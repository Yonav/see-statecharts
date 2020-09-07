package com.yakindu.sct.se.rules;

import com.yakindu.sct.se.engine.strategy.SETContext;
import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.rules.cycleElimination.LocalReactionCycleEliminationRule;
import com.yakindu.sct.se.rules.cycleElimination.TransitionCycleEliminationRule;
import com.yakindu.sct.se.rules.statement.pathConstraints.AssignmentConditionalRule;
import com.yakindu.sct.se.rules.statement.pathConstraints.AssignmentDivisionRule;
import com.yakindu.sct.se.rules.statement.simplifying.ConditionalRule;
import com.yakindu.sct.se.rules.transition.TransitionRule;

/**
 * Interface for Rules.
 * 
 * @author jwielage
 *
 */
public interface IRule {

	default boolean setIfApplicable(Node node, SETContext globalContext) {
		boolean result = isApplicable(node, globalContext);
		if (result) {
			setIsApplicableFor(node);
		}
		return result;
	}

	public boolean isApplicable(Node node, SETContext globalContext);

	public void apply(Node node, SETContext globalContext);

	default void sanityCheckApply(Node node) {
		if (node == null || !getIsApplicableFor().equals(node)) {
			throw new NullPointerException("Applicable Rule: " + this.getClass().getSimpleName()
					+ " should be applicable, some side effects must have happened!");
		}
		if (node.getAppliedRule() != null) {
			throw new NullPointerException("Some Rule is already applied!");
		}
		if (node.getChildren() != null) {
			throw new NullPointerException("Node already has children before applying rule!");
		}
	}

	public Node getIsApplicableFor();

	public void setIsApplicableFor(Node node);

	default void resetIsApplicableFor() {
		setIsApplicableFor(null);
		resetRuleSpecifics();
	}

	public void resetRuleSpecifics();

	// ===============================================================
	// Performs some checks on instances of rules
	// ===============================================================
	static boolean isOfRuleType(Node node, Class<? extends IRule> clazz) {
		return clazz.isInstance(node.getAppliedRule());
	}

	@SafeVarargs
	static boolean isOfRuleType(Node node, Class<? extends IRule>... clazzList) {
		for (Class<? extends IRule> clazz : clazzList) {
			if (isOfRuleType(node, clazz)) {
				return true;
			}
		}
		return false;
	}

	static boolean isOfRuleType(IRule rule, Class<? extends IRule> clazz) {
		return clazz.isInstance(rule);
	}

	@SafeVarargs
	static boolean isOfRuleType(IRule rule, Class<? extends IRule>... clazzList) {
		for (Class<? extends IRule> clazz : clazzList) {
			if (isOfRuleType(rule, clazz)) {
				return true;
			}
		}
		return false;
	}

	static boolean isBranchingRuleType(Node node) {
		if (node.getAppliedRule() == null) {
			return false;
		}
		return isBranchingRuleType(node.getAppliedRule());
	}

	static boolean isBranchingRuleType(IRule rule) {
		return isOfRuleType(rule, TransitionRule.class, AssignmentConditionalRule.class,
				TransitionCycleEliminationRule.class, LocalReactionCycleEliminationRule.class, ConditionalRule.class,
				AssignmentDivisionRule.class);
	}

	/*
	 * static boolean isBranchingRuleType(Node node) { boolean result =
	 * isOfRuleType(node, CycleRule.class, AssignmentConditionalRule.class,
	 * TransitionCycleEliminationRule.class,
	 * LocalReactionCycleEliminationRule.class) || (isOfRuleType(node,
	 * ConditionalRule.class) && !node.getChildren().getNext().isEmpty()); return
	 * result; }
	 * 
	 * static boolean isBranchingRuleType(IRule rule) { boolean result =
	 * isOfRuleType(rule, CycleRule.class, AssignmentConditionalRule.class,
	 * TransitionCycleEliminationRule.class,
	 * LocalReactionCycleEliminationRule.class, ConditionalRule.class,
	 * AssignmentDivisionRule.class); return result; }
	 */

}
