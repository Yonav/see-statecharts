package com.yakindu.sct.se.rules.cycleElimination;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.yakindu.sct.model.sgraph.Reaction;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.sgraph.Vertex;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import com.yakindu.sct.se.collection.ImmutableList;
import com.yakindu.sct.se.engine.strategy.SETContext;
import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.rules.IRule;
import com.yakindu.sct.se.rules.transition.TransitionRule;
import com.yakindu.sct.se.util.NodeUtil;

/**
 * Eliminates cycles that are based on transitions
 * 
 * @author jwielage
 *
 */
public class TransitionCycleEliminationRule implements IRule {

	private final int cycleTraversalsForIdentification;

	private Node isApplicableFor;

	public static TransitionCycleEliminationRule of(int cycleTraversalsForIdentification) {
		// TODO Auto-generated
		return new TransitionCycleEliminationRule(cycleTraversalsForIdentification);
	}

	public TransitionCycleEliminationRule(int cycleTraversalsForIdentification) {
		super();
		this.cycleTraversalsForIdentification = cycleTraversalsForIdentification;
	}

	// =========================================================================
	// applicability detection
	// =========================================================================
	@Override
	public boolean isApplicable(Node node, SETContext globalContext) {
		if (node.getParent() == null || IRule.isOfRuleType(node.getParent(), TransitionCycleEliminationRule.class)) {
			return false;
		}

		if (cycleTraversalsForIdentification == 0) {
			return sccCycleDetection(node, globalContext);
		}
		return traversalCycleDetection(node, globalContext);
	}

	/**
	 * This method is used to detect a cycle pre traversal via the scc analysis
	 */
	private boolean sccCycleDetection(Node node, SETContext globalContext) {
		if (node.hasActiveStatements()) {
			return false;
		}
		return globalContext.getCycleCalulator().getVerticesInCycle(node.getActiveState(globalContext)) != null;
	}

	/**
	 * This method is used to detect a cycle via number of traversals of transitions
	 */
	private boolean traversalCycleDetection(Node node, SETContext globalContext) {
		Node curAncestor = node.getParent();
		if (!IRule.isOfRuleType(curAncestor, TransitionRule.class)) {
			// last Rule has to be a CycleRule
			return false;
		}
		if (curAncestor.getLastTransitions().equals(node.getLastTransitions())) {
			// triggers, if this child wasn't produced with a transition (ex. oncycle
			// reaction)
			// doesn't check if the transitions are equal, but whether the whole list is
			// equal
			return false;
		}

		int additionaltraversalCounter = 0;
		// can't be null due to prior evaluations
		Transition transitionToCheck = node.getLastTransitions().getValue();

		// determine if the transition was already blocked
		if (node.getBlockedReactions() != null && node.getBlockedReactions().consumeUnderlyingList(reaction -> {
			if (reaction.equals(transitionToCheck)) {
				return true;
			}
			return false;
		}) != null) {
			return false;
		}

		ImmutableList<Transition> transitionList = curAncestor.getLastTransitions();

		while (transitionList != null && !transitionList.isEmpty()) {
			if (transitionList.getValue().equals(transitionToCheck)) {
				additionaltraversalCounter++;

				if (additionaltraversalCounter >= cycleTraversalsForIdentification) {
					// found it, save anything?
					return true;
				}
			}

			transitionList = transitionList.getNext();
		}

		return false;

	}

	// =========================================================================
	// apply Rule
	// =========================================================================
	@Override
	public void apply(Node node, SETContext globalContext) {
		sanityCheckApply(node);
		node.setAppliedRule(this);

		// calculate changed variables
		ImmutableList<VariableDefinition> curSStore = node.getSymbolicMemoryStore();
		if (curSStore == null) {
			curSStore = ImmutableList.create();
		}
		curSStore = curSStore.prepend(extractEliminatedVariables(node, globalContext));

		// calculate blocked reactions
		List<Reaction> reactionsToBlock = extractReactionsToBlock(node, globalContext);

		ImmutableList<Reaction> curBlockedReactions = node.getBlockedReactions();
		if (reactionsToBlock != null) {
			if (curBlockedReactions == null) {
				curBlockedReactions = ImmutableList.create();
			}
			curBlockedReactions = curBlockedReactions.prepend(reactionsToBlock.toArray(new Reaction[0]));
		}

		// calculate transitions to spawn children
		for (Transition spawningTrans : extractChildrenSpawningTransitions(node, globalContext)) {
			Node child = NodeUtil.createChildFromParent(globalContext.generateNextNodeCount(), node, spawningTrans,
					null, null, null);
			child.setSymbolicMemoryStore(curSStore);
			child.setBlockedReactions(curBlockedReactions);
		}
	}

	private List<Transition> extractChildrenSpawningTransitions(Node node, SETContext globalContext) {
		Set<Vertex> verticesInCycle = globalContext.getCycleCalulator()
				.getVerticesInCycle(node.getActiveState(globalContext));

		if (verticesInCycle == null) {
			throw new IllegalStateException("There should be vertices in induced cycle!");
		}

		List<Transition> transitionsToSpawnChildren = new ArrayList<>();

		for (Vertex vertex : verticesInCycle) {
			boolean shouldSpawnChild = false;
			for (Transition outTrans : vertex.getOutgoingTransitions()) {
				if (!verticesInCycle.contains(outTrans.getTarget())) {
					// vertex should be added
					shouldSpawnChild = true;
					break;
				}
			}
			if (shouldSpawnChild) {
				for (Transition inTrans : vertex.getIncomingTransitions()) {
					if (verticesInCycle.contains(inTrans.getSource())) {
						transitionsToSpawnChildren.add(inTrans);
						break;
					}
				}
			}
		}
		return transitionsToSpawnChildren;
	}

	// =========================================================================
	// extract eliminated VarDefs
	// =========================================================================
	public VariableDefinition[] extractEliminatedVariables(Node node, SETContext globalContext) {
		List<VariableDefinition> uniqueVariables = globalContext.getCycleCalulator()
				.getVariablesInCycle(node.getActiveState(globalContext));
		if (uniqueVariables == null) {
			throw new IllegalStateException("active state should have a list of corresponding variable definitions");
		}
		return globalContext.getSEntryGenerator().breakMe(uniqueVariables);
	}

	// =========================================================================
	// extract corresponding reactions
	// =========================================================================
	public List<Reaction> extractReactionsToBlock(Node node, SETContext globalContext) {
		return globalContext.getCycleCalulator().getReactionsInCycle(node.getActiveState(globalContext));
	}

	@Override
	public Node getIsApplicableFor() {
		return isApplicableFor;
	}

	@Override
	public void setIsApplicableFor(Node node) {
		this.isApplicableFor = node;
	}

	@Override
	public void resetRuleSpecifics() {
		// nothing to do here
	}

}
