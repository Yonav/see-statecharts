package com.yakindu.sct.se.rules.cycleElimination;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.yakindu.base.expressions.expressions.ConditionalExpression;
import org.yakindu.base.types.Expression;
import org.yakindu.sct.model.sgraph.Effect;
import org.yakindu.sct.model.sgraph.Reaction;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.stext.stext.EntryEvent;
import org.yakindu.sct.model.stext.stext.ExitEvent;
import org.yakindu.sct.model.stext.stext.ReactionEffect;
import org.yakindu.sct.model.stext.stext.ReactionTrigger;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import com.yakindu.sct.se.collection.ImmutableList;
import com.yakindu.sct.se.engine.strategy.SETContext;
import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.rules.IRule;
import com.yakindu.sct.se.rules.statement.simplifying.ConditionalRule;
import com.yakindu.sct.se.rules.transition.TransitionRule;
import com.yakindu.sct.se.util.ExpressionUtil;
import com.yakindu.sct.se.util.NodeUtil;

/**
 * Elimination lokaler Reaktionen in Zuständen
 * 
 * @author jwielage
 *
 */
public class LocalReactionCycleEliminationRule implements IRule {

	public static LocalReactionCycleEliminationRule INSTANCE = new LocalReactionCycleEliminationRule();
	private Node isApplicableFor;
	private Node nodeToRebaseOn;

	@Override
	public boolean isApplicable(Node node, SETContext globalContext) {

		// no state -> no local reactions
		if (!(node.getActiveState(globalContext) instanceof State)) {
			return false;
		}

		Node currentParent = node.getParent();
		// should trigger directly after CycleRule, when oncycle is already taken in
		// path
		if (currentParent == null || !IRule.isOfRuleType(currentParent, TransitionRule.class)
				|| !node.getLastTransitions().equals(currentParent.getLastTransitions())) {
			return false;
		}

		currentParent = currentParent.getParent();

		while (currentParent != null) {
			// search for previousCycleRule..maybe others? LoopBreakingRule behandeln
			if (IRule.isOfRuleType(currentParent, TransitionRule.class)) {
				if (!node.getLastTransitions().equals(currentParent.getLastTransitions())) {
					return false;
				} else {
					nodeToRebaseOn = currentParent;
					return true;
				}
			}
			currentParent = currentParent.getParent();
		}
		return false;

	}

	public boolean searchForSuccesFullAction(Node node, Node ancestor) {

		boolean result = false;
		Node previousNode = null;
		while (!node.equals(ancestor)) {

			if (IRule.isOfRuleType(node, ConditionalRule.class)) {
				ConditionalExpression condExpr = ((ConditionalExpression) node.getActiveStatements().getValue());
				Expression guard = condExpr.getCondition();

				if (previousNode != null && previousNode.getActiveStatements().getValue().equals(guard)) {
					result = true;
				}

			}

			previousNode = node;
			node = node.getParent();
		}

		return result;

	}

	@Override
	public void apply(Node node, SETContext globalContext) {
		sanityCheckApply(node);
		node.setAppliedRule(this);

		// only forget variables if there was a previously successfull reaction in this
		// state
		boolean shouldForgetVariables = searchForSuccesFullAction(node, nodeToRebaseOn);

		// calculate changed variables
		ImmutableList<VariableDefinition> curSStore = node.getSymbolicMemoryStore();
		if (curSStore == null) {
			curSStore = ImmutableList.create();
		}
		if (shouldForgetVariables) {
			curSStore = curSStore.prepend(extractEliminatedVariables(node, globalContext));
		}

		// calculate blocked reactions
		Reaction[] reactionsToBlock = ((State) node.getActiveState(globalContext)).getLocalReactions()
				.toArray(new Reaction[0]);

		ImmutableList<Reaction> curBlockedReactions = node.getBlockedReactions();
		if (reactionsToBlock != null && reactionsToBlock.length != 0) {
			if (curBlockedReactions == null) {
				curBlockedReactions = ImmutableList.create();
			}
			curBlockedReactions = curBlockedReactions.prepend(reactionsToBlock);
		}

		Node child = NodeUtil.createChildFromParent(globalContext.generateNextNodeCount(), node, null, null, null,
				null);
		child.setSymbolicMemoryStore(curSStore);
		child.setBlockedReactions(curBlockedReactions);
	}

	private VariableDefinition[] extractEliminatedVariables(Node node, SETContext globalContext) {
		List<VariableDefinition> uniqueVariables = calulateVariables(
				((State) node.getActiveState(globalContext)).getLocalReactions());

		return globalContext.getSEntryGenerator().breakMe(uniqueVariables);
	}

	private List<VariableDefinition> calulateVariables(List<Reaction> reactionsForScc) {

		// filter those reactions, that only trigger on entry and or exit
		reactionsForScc = reactionsForScc.stream().filter(reaction -> {
			if (reaction.getTrigger() instanceof ReactionTrigger) {
				if (((ReactionTrigger) reaction.getTrigger()).getTriggers().stream()
						.allMatch(t -> (t instanceof ExitEvent) || (t instanceof EntryEvent))) {
					return false;
				}
			}
			return true;
		}).collect(Collectors.toList());

		List<VariableDefinition> uniqueVariables = new ArrayList<>();

		// for keeping created objects at a minimum
		boolean shouldBeAdded = true;
		Effect effect = null;
		EList<Expression> effectList = null;
		VariableDefinition assignee = null;

		for (Reaction reaction : reactionsForScc) {
			effect = reaction.getEffect();
			effectList = null;

			if (effect != null && effect instanceof ReactionEffect) {
				effectList = ((ReactionEffect) effect).getActions();
			}

			if (effectList != null) {
				for (Expression expr : effectList) {
					assignee = ExpressionUtil.extractVarDefOfAssignment(expr);

					if (assignee != null) {
						// expr is assignment
						shouldBeAdded = true;

						// check if variable is already in list regarding EcoreUtil.equals
						for (VariableDefinition variable : uniqueVariables) {
							if (EcoreUtil.equals(variable, assignee)) {
								shouldBeAdded = false;
								break;
							}
						}

						if (shouldBeAdded) {
							uniqueVariables.add(assignee);
						}
					}
				}
			}

		}

		return uniqueVariables;
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
		nodeToRebaseOn = null;
	}

}
