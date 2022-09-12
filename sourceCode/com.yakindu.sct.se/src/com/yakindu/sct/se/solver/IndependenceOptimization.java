package com.yakindu.sct.se.solver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.yakindu.base.expressions.expressions.ElementReferenceExpression;
import org.yakindu.base.types.Expression;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import com.yakindu.sct.se.collection.ImmutableList;
import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.util.ExpressionUtil;
import com.yakindu.sct.se.util.StaticAnalyserUtil;

/**
 * Implements the Independence Optimization.
 * 
 * @author jwielage
 *
 */
public class IndependenceOptimization {

	public static IndependenceOptimization INSTANCE = new IndependenceOptimization();

	public OptimizedPathConstraint simplify(Node node) {
		Node youngestWithKnownSatisfiability = node.getParent();

		while (!youngestWithKnownSatisfiability.registeredSatisfiability()) {
			youngestWithKnownSatisfiability = youngestWithKnownSatisfiability.getParent();
		}

		List<Expression> difference = new ArrayList<>();

		// add all pathconstraints to difference that are new since youngestKnown
		node.getPathConstraint().consumeValuesUntilEqual(youngestWithKnownSatisfiability.getPathConstraint(), pc -> {
			difference.add(pc);
		});

		return buildGraphAndCalculateSimplifiedVersion(node.getPathConstraint(), difference);
	}

	// calculate simplified version with a graph
	private OptimizedPathConstraint buildGraphAndCalculateSimplifiedVersion(
			ImmutableList<Expression> completePathConstraints, List<Expression> newConstraintsSinceLastSolving) {
		List<Expression> result = new ArrayList<>();

		// build graph
		SimpleGraph<VariableDefinition, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
		List<VariableDefinition> foundVarDefs = new ArrayList<>();
		completePathConstraints.consumeUnderlyingList(expr -> {

			StaticAnalyserUtil.findAllInTree(expr, ElementReferenceExpression.class, false, elRef -> {
				VariableDefinition currentVarDef = ExpressionUtil.extractVarDefofElementReference(elRef);
				foundVarDefs.add(currentVarDef);
				graph.addVertex(currentVarDef);
			});

			if (foundVarDefs.size() > 1) {
				for (int i = 1; i < foundVarDefs.size(); i++) {
					if (!foundVarDefs.get(0).equals(foundVarDefs.get(i))) {
						graph.addEdge(foundVarDefs.get(0), foundVarDefs.get(i));
					}
				}
			}
			foundVarDefs.clear();

		});

		// calculate connected Sets
		ConnectivityInspector<VariableDefinition, DefaultEdge> connectivityInspector = new ConnectivityInspector<>(
				graph);
		connectivityInspector.connectedSets();

		// add all conntected sets that contain a variable from the newly added
		// constraints to a list
		List<Set<VariableDefinition>> gatherSets = new ArrayList<>();
		for (Expression constraint : newConstraintsSinceLastSolving) {
			StaticAnalyserUtil.findAllInTree(constraint, ElementReferenceExpression.class, true, elRef -> {
				VariableDefinition currentVarDef = ExpressionUtil.extractVarDefofElementReference(elRef);
				Set<VariableDefinition> currentSet = connectivityInspector.connectedSetOf(currentVarDef);
				if (!gatherSets.contains(currentSet)) {
					gatherSets.add(currentSet);
				}
			});
		}

		// add all variables to set
		final Set<VariableDefinition> allDependentVariables = new HashSet<>();
		gatherSets.forEach(allDependentVariables::addAll);

		// add all expressions to set that contain such a variable
		completePathConstraints.consumeUnderlyingList(expr -> {
			StaticAnalyserUtil.findAllInTree(expr, ElementReferenceExpression.class, true, elRef -> {
				VariableDefinition currentVarDef = ExpressionUtil.extractVarDefofElementReference(elRef);
				foundVarDefs.add(currentVarDef);
			});

			if (foundVarDefs.isEmpty() || allDependentVariables.contains(foundVarDefs.get(0))) {
				result.add(expr);
			} else if (foundVarDefs.isEmpty() && newConstraintsSinceLastSolving.contains(expr)) {
				// add all expressions without variables of newly added constraints to list
				result.add(expr);
			}
			foundVarDefs.clear();
		});

		return OptimizedPathConstraint.create(result, allDependentVariables);
	}
}
