package com.yakindu.sct.se.engine;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.yakindu.base.types.Property;
import org.yakindu.sct.model.sgraph.Scope;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import com.yakindu.sct.se.collection.ImmutableList;
import com.yakindu.sct.se.engine.strategy.SETContext;
import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.solver.model.MaybeBool;
import com.yakindu.sct.se.util.ExpressionCreatorUtil;

/**
 * Initializes a SET Tree by creating its root.
 * 
 * @author jwielage
 *
 */
public class SETInitializer {

	public static SETInitializer INSTANCE = new SETInitializer();

	public Node createInitialSETNodeForStatechart(Statechart statechart, SETContext globalContext) {
		if (globalContext.getEntryState() == null) {
			return null;
		}

		Node root = new Node(globalContext.generateNextNodeCount());

		// preventing NullPointer Exceptions
		root.setLastTransitions(ImmutableList.create());

		// collect all variables in scope and add them to statementBlock if they have an
		// initial value
		for (Scope scope : statechart.getScopes()) {
			for (Property property : scope.getVariables()) {
				if (property instanceof VariableDefinition) {
					VariableDefinition variable = ((VariableDefinition) property);
					if (variable.getInitialValue() != null) {
						root.prependActiveStatementAndSet(ExpressionCreatorUtil.createAssignment(variable,
								EcoreUtil.copy(variable.getInitialValue())));
					}
				}
			}
		}

		// pathConstraints is empty, so this is clear
		root.setSatisfiability(MaybeBool.SAT);

		return root;
	}

}
