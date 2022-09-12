package com.yakindu.sct.se.rules.transition;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.yakindu.base.types.Expression;
import org.yakindu.sct.model.sgraph.Effect;
import org.yakindu.sct.model.sgraph.Reaction;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.sgraph.Vertex;
import org.yakindu.sct.model.stext.stext.AlwaysEvent;
import org.yakindu.sct.model.stext.stext.BuiltinEventSpec;
import org.yakindu.sct.model.stext.stext.EntryEvent;
import org.yakindu.sct.model.stext.stext.EventSpec;
import org.yakindu.sct.model.stext.stext.ExitEvent;
import org.yakindu.sct.model.stext.stext.ReactionEffect;
import org.yakindu.sct.model.stext.stext.ReactionTrigger;

import com.yakindu.sct.se.collection.ImmutableList;
import com.yakindu.sct.se.engine.strategy.SETContext;
import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.rules.IRule;
import com.yakindu.sct.se.symbolicExecutionExtension.SequenceBlock;
import com.yakindu.sct.se.util.NodeUtil;
import com.yakindu.sct.se.util.ExpressionCreatorUtil;

/**
 * Implements the TransitionRule that executes transitions or localReactions
 */
public class TransitionRule implements IRule {

	public static final TransitionRule INSTANCE = new TransitionRule();

	private Node isApplicableFor;
	private TransitionPriorityHelper transitionPriorityHelper = TransitionPriorityHelper.INSTANCE;
	private LocalReactionHelper localReactionHelper = LocalReactionHelper.INSTANCE;

	// ====================================================================================================================================
	// Applicability
	// ====================================================================================================================================
	@Override
	public boolean isApplicable(Node node, SETContext globalContext) {
		return !node.hasActiveStatements();
	}

	// ====================================================================================================================================
	// Apply Rule
	// ====================================================================================================================================
	@Override
	public void apply(Node node, SETContext globalContext) {
		sanityCheckApply(node);

		node.setAppliedRule(this);
		Vertex state = node.getActiveState(globalContext);

		for (Transition outTrans : state.getOutgoingTransitions()) {
			executeTransitionWithEntryAndExitEffects(globalContext, node, outTrans);
		}

		// handle localReactions
		executeLocalReactions(globalContext, node, state);

	}

	// ====================================================================================================================================
	// handle Transitions
	// ====================================================================================================================================
	private void executeTransitionWithEntryAndExitEffects(SETContext globalContext, Node node, Transition transition) {

		List<Expression> expressions = new ArrayList<>();

		Expression[] transitionExpressions = executeTransition(transition);

		// check if transition should be aborted!!
		if (transitionExpressions == null) {
			// abort transition
			return;
		}

		// transition guard
		if (transitionExpressions[0] != null) {
			expressions.add(transitionExpressions[0]);
		}

		// handle exit effects
		if (transition.getSource() instanceof State) {
			for (Reaction reaction : ((State) transition.getSource()).getLocalReactions()) {
				if (isOfType(reaction, ExitEvent.class)) {
					Expression exitExpression = handleOtherReactions(reaction);
					if (exitExpression != null) {
						expressions.add(exitExpression);
					}
				}
			}
		}

		// transition effects
		if (transitionExpressions[1] != null) {
			expressions.add(transitionExpressions[1]);
		}

		// handle entry effects
		if (transition.getTarget() instanceof State) {
			for (Reaction reaction : ((State) transition.getTarget()).getLocalReactions()) {
				if (isOfType(reaction, EntryEvent.class)) {
					Expression entryExpression = handleOtherReactions(reaction);
					if (entryExpression != null) {
						expressions.add(entryExpression);
					}
				}
			}
		}

		// check here, because the priority map has to be build...
		if (node.isBlocked(transition)) {
			return;
		}
		NodeUtil.createChildFromParent(globalContext.generateNextNodeCount(), node, transition, null,
				ImmutableList.createReverse(expressions.toArray(new Expression[0])), null);

	}

	private Expression[] executeTransition(Transition transition) {
		// result[0] is guard, result[1] is effectList
		Expression[] result = new Expression[2];

		if (transition.getTrigger() == null || !(transition.getTrigger() instanceof ReactionTrigger)) {
			return result;
		}

		// add guard to result
		ReactionTrigger trigger = ((ReactionTrigger) transition.getTrigger());

		if (trigger.getTriggers() == null && trigger.getGuard() == null) {
			// TODO: abort whole transition, except entry
			return null;
		}

		// add expression from previous encounters
		result[0] = transitionPriorityHelper.getExpressionsThroughTriggers(trigger.getTriggers());

		Expression guardExpr = null;
		if (trigger.getGuard() != null) {
			// add as guard expression if it exists
			guardExpr = EcoreUtil.copy(trigger.getGuard().getExpression());

			// create new guard from previous encounters
			if (result[0] != null) {
				result[0] = ExpressionCreatorUtil.createLogAndExpression(result[0], guardExpr);
			} else {
				result[0] = guardExpr;
			}
		}

		// add for next transition
		transitionPriorityHelper.addToMap(trigger.getTriggers(), guardExpr);

		// add effect to result
		Effect effect = transition.getEffect();
		EList<Expression> effectList = null;
		if (effect != null && effect instanceof ReactionEffect) {
			effectList = ((ReactionEffect) effect).getActions();
		}

		if (effectList != null && !effectList.isEmpty()) {
			result[1] = ExpressionCreatorUtil.createSequenceBlockOfEffects(effectList);
		}

		return result;
	}

	// ====================================================================================================================================
	// handle LocalReactions
	// ====================================================================================================================================
	private void executeLocalReactions(SETContext globalContext, Node node, Vertex state) {
		if (!(state instanceof State)) {
			return;
		}

		EList<Reaction> localReactions = ((State) state).getLocalReactions();

		if (localReactions.isEmpty()) {
			return;
		}
		for (Reaction localReaction : localReactions) {
			if (node.isBlocked(localReaction)) {
				continue;
			}
			// the reactions dont need to compete for priorities anymore
			if (localReaction.getTrigger() == null || !(localReaction.getTrigger() instanceof ReactionTrigger)) {
				continue;
			}

			localReactionHelper.addLReaction(((ReactionTrigger) localReaction.getTrigger()).getTriggers(),
					handleOtherReactions(localReaction));
		}

		// hier müssen expression von transition rein..
		List<Expression> allExpressionPermutations = localReactionHelper.generateAllPermutations();

		if (allExpressionPermutations == null || allExpressionPermutations.isEmpty()) {
			return;
		}

		for (Expression expression : allExpressionPermutations) {
			NodeUtil.createChildFromParent(globalContext.generateNextNodeCount(), node, null, null,
					ImmutableList.createReverse(expression), null);
		}
	}

	// ====================================================================================================================================
	// handle All Reactions
	// ====================================================================================================================================
	// guard = null && effect = null -> null
	// guard != null && effect = null -> guard
	// guard = null && effect != null -> effect
	// guard != null && effect != null -> guard? effect : true
	private Expression handleOtherReactions(Reaction reaction) {

		// return as conditional, or normal expression
		// result[0] is guard, result[1] is effectList
		Expression result;

		if (reaction.getTrigger() == null || !(reaction.getTrigger() instanceof ReactionTrigger)) {
			return null;
		}

		// add guard to result
		ReactionTrigger trigger = ((ReactionTrigger) reaction.getTrigger());

		if (trigger.getTriggers() == null && trigger.getGuard() == null) {
			return null;
		}

		// add expression from previous encounters
		result = transitionPriorityHelper.getExpressionsThroughTriggers(trigger.getTriggers());

		if (trigger.getGuard() != null) {
			// add as guard expression if it exists
			Expression guardExpr = EcoreUtil.copy(trigger.getGuard().getExpression());
			// create new guard from previous encounters
			if (result == null) {
				result = guardExpr;
			} else {
				result = ExpressionCreatorUtil.createLogAndExpression(result, guardExpr);
			}
		}

		// add effect to result
		Effect effect = reaction.getEffect();
		EList<Expression> effectList = null;
		if (effect != null && effect instanceof ReactionEffect) {
			effectList = ((ReactionEffect) effect).getActions();
		}

		if (effectList != null && !effectList.isEmpty()) {
			SequenceBlock effectExpr = ExpressionCreatorUtil.createSequenceBlockOfEffects(effectList);

			if (result == null) {
				result = effectExpr;
			} else {
				result = ExpressionCreatorUtil.createCondExpression(result, effectExpr,
						ExpressionCreatorUtil.createBoolExpression((true)));
			}
		}

		return result;
	}

	// ====================================================================================================================================
	// others
	// ====================================================================================================================================
	private boolean isOfType(Reaction reaction, Class<? extends BuiltinEventSpec> builtinEventClass) {
		if (!(reaction.getTrigger() instanceof ReactionTrigger)) {
			return false;
		}
		EList<EventSpec> triggerList = ((ReactionTrigger) reaction.getTrigger()).getTriggers();
		if (triggerList == null) {
			return false;
		}
		if (triggerList.isEmpty()) {
			return builtinEventClass.equals(AlwaysEvent.class);
		}

		return triggerList.stream().anyMatch(eventSpec -> builtinEventClass.isInstance(eventSpec));
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
		transitionPriorityHelper.reset();
		localReactionHelper.reset();
	}
}
