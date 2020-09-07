package com.yakindu.sct.se.solver;

import java.util.ArrayDeque;
import java.util.Deque;

import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.solver.model.MaybeBool;

/**
 * Implements the context solving optimization.
 * 
 * @author jwielage
 *
 */
public class ContextSolver {

	public static ContextSolver INSTANCE = new ContextSolver();

	private Deque<Node> queue = new ArrayDeque<>();

	private ContextSolver() {

	}

	public MaybeBool contextSolve(Node node) {
		resetQueue();
		// sanity check
		if (node.registeredSatisfiability()) {
			return node.getSatisfiability();
		}

		// empty path constraint
		if (node.getPathConstraint() == null || node.getPathConstraint().isEmpty()) {
			return MaybeBool.SAT;
		}

		// look for ancestor with sat result
		Node ancestorWithSatResult = node.getParent();
		while (!ancestorWithSatResult.registeredSatisfiability()) {
			ancestorWithSatResult = ancestorWithSatResult.getParent();
		}

		// ancestor is unsat
		if (ancestorWithSatResult.getSatisfiability().equals(MaybeBool.UNSAT)) {
			return MaybeBool.UNSAT;
		}

		// path constraint is equal to ancestor
		if (node.getPathConstraint().equals(ancestorWithSatResult.getPathConstraint())) {
			return ancestorWithSatResult.getSatisfiability();
		}

		// search for SAT in descendants
		while (node != null) {
			if (node.registeredSatisfiability() && !node.getSatisfiability().equals(MaybeBool.UNSAT)) {
				// found descendant with SAT or UNKNOWN
				return node.getSatisfiability();
			}

			if (!node.registeredSatisfiability() || !node.getSatisfiability().equals(MaybeBool.UNSAT))
				// add children to queue
				if (node.getChildren() != null) {
					node.getChildren().consumeUnderlyingList(child -> {
						queue.add(child);
					});
				}
			node = queue.pollFirst();
		}

		// no result from context solving
		return null;
	}

	// clear queue
	private void resetQueue() {
		queue.clear();
	}

}
