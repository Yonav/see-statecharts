package com.yakindu.sct.se.util;

import java.util.HashSet;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.yakindu.base.expressions.expressions.AssignmentExpression;
import org.yakindu.base.expressions.expressions.ElementReferenceExpression;
import org.yakindu.base.types.Expression;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

/**
 * Contains utility methods for expressions
 * 
 * @author jwielage
 *
 */
public class ExpressionUtil {

	public static final String ORIGINAL_VARIABLE_DECLARATION = "OriginalVariableDeclaration";
	public static final String UNINITIALIZED_VARIABLE = "UnitializedVariable";

	public static VariableDefinition extractVarDefOfAssignment(AssignmentExpression expression) {
		if (expression.getVarRef() instanceof ElementReferenceExpression) {
			return extractVarDefofElementReference(expression.getVarRef());
		}
		return null;
	}

	public static VariableDefinition extractVarDefOfAssignment(Expression expression) {
		if (expression instanceof AssignmentExpression) {
			return extractVarDefOfAssignment(((AssignmentExpression) expression));
		}
		return null;
	}

	public static VariableDefinition extractVarDefofElementReference(Expression expression) {
		if (expression instanceof ElementReferenceExpression) {
			EObject reference = ((ElementReferenceExpression) expression).getReference();
			if (reference instanceof VariableDefinition) {
				return ((VariableDefinition) reference);
			}
		}
		return null;
	}

	public static VariableDefinition getAnnotationFromVarDef(VariableDefinition varDef, String annotationName) {
		try {
			Expression capsulatingElementReference = varDef.getAnnotationOfType(annotationName).getArguments().get(0);
			return ((VariableDefinition) ((ElementReferenceExpression) capsulatingElementReference).getReference());
		} catch (NullPointerException e) {
			return null;
		}
	}

	public static HashSet<VariableDefinition> extractVarDefOfExpression(Expression expression) {
		HashSet<VariableDefinition> result = new HashSet<>();
		if (expression == null) {
			return result;
		}

		TreeIterator<EObject> expressionIterator = expression.eAllContents();
		while (expressionIterator.hasNext()) {
			EObject next = expressionIterator.next();

			if (next instanceof ElementReferenceExpression) {
				next = ((ElementReferenceExpression) next).getReference();
			}
			if (next instanceof VariableDefinition) {
				if (!result.contains((VariableDefinition) next)) {
					result.add((VariableDefinition) next);
				}
			}
		}
		return result;

	}

}
