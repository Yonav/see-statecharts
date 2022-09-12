package com.yakindu.sct.se.solver;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.yakindu.sct.generator.core.console.IConsoleLogger;

import com.google.inject.Inject;
import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.solver.model.MaybeBool;
import com.yakindu.sct.se.solver.serializer.SETModel2SMTSolverSerializer;
import com.yakindu.sct.se.util.SELogger;
import com.yakindu.sct.se.util.performance.PerformanceUtil;

/**
 * Implements the Solver. Handles the complete procedure of the satisfiability
 * analysis.
 * 
 * @author jwielage
 *
 */
public class NodeSolver {

	@Inject
	private IConsoleLogger logger = new SELogger();

	private final SETModel2SMTSolverSerializer serializer;
	private final ContextSolver contextSolver = ContextSolver.INSTANCE;
	private final IndependenceOptimization independenceSimplifier = IndependenceOptimization.INSTANCE;
	private final SMTLIBConstraintSolver smtSolver;

	private final PerformanceUtil performanceTimer;

	private final boolean binarySearch;
	private final boolean independenceOptimization;
	private final boolean contextSolving;

	public static NodeSolver of(PerformanceUtil timer, boolean binarySearch, boolean independeOptimization,
			boolean contextSolving, boolean jConstraints) {
		return new NodeSolver(timer, binarySearch, independeOptimization, contextSolving, jConstraints);
	}

	private NodeSolver(PerformanceUtil timer, boolean binarySearch, boolean indepSimply, boolean contextSolving,
			boolean jConstraints) {
		this.serializer = SETModel2SMTSolverSerializer.INSTANCE(!jConstraints);
		this.smtSolver = SMTLIBConstraintSolver.INSTANCE(jConstraints);

		this.performanceTimer = timer;
		this.binarySearch = binarySearch;
		this.independenceOptimization = indepSimply;
		this.contextSolving = contextSolving;

	}

	public boolean elligibleForRuleApplicabilityBla(Node node) {
		return !node.registeredSatisfiability() || !node.getSatisfiability().equals(MaybeBool.UNSAT);
	}

	public void solveLeaf(Node leafToSolve) {
		solveNode(leafToSolve);
		leafToSolve.setChildren(null);
	}

	public void solveNode(Node nodeToSolve) {
		boolean rootSolvingProcess = !performanceTimer.getSolverTime().isActive();
		performanceTimer.getSolverTime().startTime();

		// solving
		performanceTimer.countSolving();
		solveAndSet(nodeToSolve);
		handleSolverResult(nodeToSolve);

		// take time
		if (rootSolvingProcess) {
			performanceTimer.getSolverTime().stopTime();
		}

		logger.log("Node: " + nodeToSolve.getNodeCounter() + " ist: " + nodeToSolve.getSatisfiability().toString());
	}

	private void solveAndSet(Node nodeToSolve) {
		if (nodeToSolve.registeredSatisfiability()) {
			logger.log("Didn't need to solve node, was already solved.");
			return;
		}
		MaybeBool result = null;

		if (contextSolving) {
			result = contextSolver.contextSolve(nodeToSolve);
		}

		if (result == null) {
			result = optimizeSerializeAndSolve(nodeToSolve);
		} else {
			performanceTimer.countContextSolvingSuccess();
		}

		nodeToSolve.setSatisfiability(result);

	}

	private void handleSolverResult(Node node) {
		if (node.getSatisfiability().isUnsatisfiable()) {
			handleUNSAT(node);
		} else {
			handleSATOrUNKNOWN(node);
		}
	}

	private MaybeBool optimizeSerializeAndSolve(Node node) {
		logger.log("Solving Node: " + node.getNodeCounter() + "\n");
		String smtLib;
		if (independenceOptimization) {
			smtLib = serializer.convertToSMTLIB(independenceSimplifier.simplify(node));
		} else {
			smtLib = serializer.convertToSMTLIB(node);
		}
		MaybeBool result = smtSolver.solve(smtLib);
		logger.log("Result: " + result.toString() + "\n");
		return result;
	}

	// ====================================================================================
	// UNSAT
	// ====================================================================================
	private void handleUNSAT(Node node) {
		setAllDescendantsAsUnsat(node);

		// search for oldest unsat ancestor
		Node currentAncestor = node.getParent();
		List<Node> unregisteredAncestors = new ArrayList<>();
		while (!currentAncestor.registeredSatisfiability()) {
			unregisteredAncestors.add(currentAncestor);
			currentAncestor = currentAncestor.getParent();
		}

		if (!unregisteredAncestors.isEmpty()) {
			if (binarySearch) {
				binarySearchPropagation(unregisteredAncestors);
			} else {
				oldestFirstPropagation(unregisteredAncestors);
			}
		}
	}

	private void setAllDescendantsAsUnsat(Node unsatNode) {
		Deque<Node> queue = new ArrayDeque<>();

		// add possible children to queue
		if (unsatNode.getChildren() != null) {
			unsatNode.getChildren().consumeUnderlyingList(child -> {
				queue.add(child);
			});
		}

		Node descendant = queue.pollFirst();

		while (descendant != null) {
			if (!descendant.registeredSatisfiability()) {
				descendant.setSatisfiability(MaybeBool.UNSAT);

				// add all children to queue
				if (descendant.getChildren() != null) {
					descendant.getChildren().consumeUnderlyingList(child -> {
						queue.add(child);
					});
				}
			}
			descendant = queue.pollFirst();
		}
	}

	// propagate the result in an oldest first traversal
	private void oldestFirstPropagation(List<Node> listToTraverse) {
		Node currentNode = null;

		for (int i = listToTraverse.size() - 1; i >= 0; i--) {
			currentNode = listToTraverse.get(i);
			if (currentNode.registeredSatisfiability()) {
				continue;
			}

			solveNode(currentNode);
			MaybeBool satResult = currentNode.getSatisfiability();

			if (satResult.isUnsatisfiable()) {
				break;
			}
		}

	}

	// propagate the result in a binary search propagation
	private void binarySearchPropagation(List<Node> listToTraverse) {
		int low = 0;
		int high = listToTraverse.size() - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			Node midVal = listToTraverse.get(mid);

			solveNode(midVal);

			// shorten the list range to search in
			if (midVal.getSatisfiability().isSatisfiable() || midVal.getSatisfiability().isUnknown()) {
				high = mid - 1;
			} else if (midVal.getSatisfiability().isUnsatisfiable()) {
				low = mid + 1;
			}
		}
	}

	// ====================================================================================
	// SAT or UNKNOWN
	// ====================================================================================
	private void handleSATOrUNKNOWN(Node node) {
		setAncestorsSatOrUnknown(node);
	}

	private void setAncestorsSatOrUnknown(Node satNode) {
		MaybeBool resultToSet = satNode.getSatisfiability();
		satNode = satNode.getParent();

		while (!satNode.registeredSatisfiability()) {
			satNode.setSatisfiability(resultToSet);
			satNode = satNode.getParent();
		}
	}

	// ====================================================================================
	// Other

	public void close() {
		smtSolver.close();
	}
}
