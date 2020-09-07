package com.yakindu.sct.se.rules.statement.pathConstraints.transformer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.yakindu.base.expressions.expressions.AssignmentExpression;
import org.yakindu.base.expressions.expressions.ElementReferenceExpression;
import org.yakindu.base.types.Expression;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import com.yakindu.sct.se.collection.ImmutableList;
import com.yakindu.sct.se.engine.strategy.SETContext;
import com.yakindu.sct.se.symbolicExecutionExtension.SequenceBlock;
import com.yakindu.sct.se.util.ExpressionUtil;
import com.yakindu.sct.se.util.ExpressionCreatorUtil;
import com.yakindu.sct.se.util.StaticAnalyserUtil;

/**
 * Transform an Expression to it's SSA Form based on the SymbolicMemory.
 * Initializes Variables that are uninitialized atm.
 * 
 * @author jwielage
 */
public class SSATransformator {

	public static final String ORIGINAL_VARIABLE_DECLARATION = "OriginalVariableDeclaration";

	public static SSATransformator INSTANCE = new SSATransformator();

	private SSATransformator() {

	}

	public SequenceBlock transformToSSA(Expression expr, ImmutableList<VariableDefinition> sStore,
			SETContext globalContext) {
		// stores all expressions that should be returned
		List<Expression> resultList = new ArrayList<>();

		// copy expression to prevent Econtainer issues
		Expression result = EcoreUtil.copy(expr);

		// collect all ElementReferenceExpressions
		List<ElementReferenceExpression> elRefsInExpr = new ArrayList<>();
		StaticAnalyserUtil.findAllInTree(result, ElementReferenceExpression.class, false, elRefsInExpr::add);

		// would'nt be catched otherwise
		if (expr instanceof ElementReferenceExpression) {
			elRefsInExpr.add(((ElementReferenceExpression) result));
		}

		// search for latest mapping in symbolicMemory of Variables
		if (sStore != null) {
			sStore.consumeUnderlyingList(sEntry -> {
				// get annotation(=unspoiledVarDef) of entry
				VariableDefinition unspoiledVarDef = ExpressionUtil.getAnnotationFromVarDef(sEntry,
						ORIGINAL_VARIABLE_DECLARATION);
				if (unspoiledVarDef == null) {
					throw new NullPointerException("Found symbolic memory entry without annotation.");
				}

				// search for annotation in list
				Iterator<ElementReferenceExpression> elRefIterator = elRefsInExpr.iterator();
				while (elRefIterator.hasNext()) {
					ElementReferenceExpression elRef = elRefIterator.next();

					if (!(elRef.getReference() instanceof VariableDefinition)) {
						// remove ElementReference if it doesn't hold a Variable
						elRefIterator.remove();
					} else if (unspoiledVarDef.equals(ExpressionUtil.extractVarDefofElementReference(elRef))) {
						// if found, update reference and remove it to not update it
						elRef.setReference(sEntry);
						elRefIterator.remove();
					}
				}

				// continues of list is not empty
				return elRefsInExpr.isEmpty();

			});
		}

		// handle AssignmentExpression
		ElementReferenceExpression uninitializedAssignee = null;
		if (result instanceof AssignmentExpression) {
			ElementReferenceExpression elRefOfAssignee = ((ElementReferenceExpression) ((AssignmentExpression) result)
					.getVarRef());
			if (elRefsInExpr.remove(elRefOfAssignee)) {
				// varDef wasn't previously initialized, is handled after other uninitialized
				// variables are handled
				uninitializedAssignee = elRefOfAssignee;
			} else {
				// VarDef was previously initialized
				VariableDefinition varDefOfAssigne = ExpressionUtil.extractVarDefofElementReference(elRefOfAssignee);
				elRefOfAssignee.setReference(globalContext.getSEntryGenerator()
						.generateAndAddNextNumberedAndAnnotatedVarDef(varDefOfAssigne));
			}
		}

		List<VariableDefinition> newInitializedVariables = new ArrayList<>();
		// handles all uninitialized VarDefs, except the assigne in case of
		// AssignmentExpression
		if (!elRefsInExpr.isEmpty()) {
			// notifyContextAboutUninitializedElRefs(expr, globalContext, elRefsInExpr);
			newInitializedVariables = handleUnitializedVariables(elRefsInExpr, globalContext);
		}

		// assignee was previously uninitialized
		if (uninitializedAssignee != null) {
			VariableDefinition varDefToGenerateAssigneeFrom = ExpressionUtil
					.extractVarDefofElementReference(uninitializedAssignee);
			VariableDefinition posAlreadyGenerated = isAlreadyGenerated(newInitializedVariables,
					varDefToGenerateAssigneeFrom);

			if (posAlreadyGenerated != null) {
				// variable was initialized during initialization of other variables
				// generate new Assignee on top of new initialized var def
				varDefToGenerateAssigneeFrom = posAlreadyGenerated;
			}

			uninitializedAssignee.setReference(globalContext.getSEntryGenerator()
					.generateAndAddNextNumberedAndAnnotatedVarDef(varDefToGenerateAssigneeFrom));
		}

		// add transformed expression to result
		resultList.add(result);

		// generates an expression assigning the default value to all newly generated
		// variables, adds them to result
		for (VariableDefinition newInitializedVariable : newInitializedVariables) {
			Expression generatedExpr = initializeWithDefaultValueExpression(newInitializedVariable);
			if (generatedExpr != null) {
				resultList.add(generatedExpr);
			}
		}

		return ExpressionCreatorUtil.createSequenceBlock(resultList);
	}

	/**
	 * Initializes all variables in the list
	 * 
	 * @param uninitializedReferences list of elementReferences that hold variables
	 * @param globalContext
	 * @return list of newly generated variables
	 */
	public List<VariableDefinition> handleUnitializedVariables(List<ElementReferenceExpression> uninitializedReferences,
			SETContext globalContext) {
		List<VariableDefinition> newInitializedVariables = new ArrayList<>();

		// for every uninitialized variable create new variable
		for (ElementReferenceExpression unInElRef : uninitializedReferences) {
			VariableDefinition posVarDef = ExpressionUtil.extractVarDefofElementReference(unInElRef);
			if (posVarDef != null) {

				// search list if same variable was already generated
				VariableDefinition posAlreadyGen = isAlreadyGenerated(newInitializedVariables, posVarDef);

				if (posAlreadyGen == null) {
					// generate new one
					posAlreadyGen = globalContext.getSEntryGenerator().generateFromUnitializedVarDef(posVarDef);
					newInitializedVariables.add(posAlreadyGen);

				}
				// set reference to newly generated variable
				unInElRef.setReference(posAlreadyGen);
			}
		}

		return newInitializedVariables;
	}

	/**
	 * Returns an expressions, that assigns the variable to its default value Only
	 * integer and boolean are supported!
	 * 
	 * @param toInitialize variable to assign
	 * @return the expression
	 */
	private Expression initializeWithDefaultValueExpression(VariableDefinition toInitialize) {
		String typeName = toInitialize.getType().getName();
		Expression assignment = null;
		switch (typeName) {
		case "integer":
			assignment = ExpressionCreatorUtil.createIntExpression(0);
			break;
		case "boolean":
			assignment = ExpressionCreatorUtil.createBoolExpression(false);
			break;
		default:
			break;
		}

		if (assignment != null) {
			return ExpressionCreatorUtil.createAssignment(toInitialize, assignment);
		}

		return null;
	}

	/**
	 * Checks if the variable to search for was already generated and is in the list
	 * 
	 * @param alreadyGenerated  list to search in
	 * @param varDefToSearchFor variable to search in list
	 * @return generated variable
	 */
	private VariableDefinition isAlreadyGenerated(List<VariableDefinition> alreadyGenerated,
			VariableDefinition varDefToSearchFor) {
		if (alreadyGenerated == null || alreadyGenerated.isEmpty()) {
			return null;
		}
		// search in list if one exists already
		for (VariableDefinition initVarDef : alreadyGenerated) {
			VariableDefinition unspoiledVarDef = ExpressionUtil.getAnnotationFromVarDef(initVarDef,
					ORIGINAL_VARIABLE_DECLARATION);
			if (unspoiledVarDef.equals(varDefToSearchFor)) {
				return initVarDef;
			}
		}
		return null;
	}
}
