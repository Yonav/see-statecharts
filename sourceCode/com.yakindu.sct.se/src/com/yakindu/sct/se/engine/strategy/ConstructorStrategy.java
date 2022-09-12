package com.yakindu.sct.se.engine.strategy;

import java.util.List;

import com.yakindu.sct.se.engine.SymbolicExecutionEngine;
import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.rules.IRule;
import com.yakindu.sct.se.rules.cycleElimination.LocalReactionCycleEliminationRule;
import com.yakindu.sct.se.rules.cycleElimination.TransitionCycleEliminationRule;

/**
 * responsible for picking the next node, triggering satisfiability check and
 * applying the rules
 * 
 * @author jwielage
 *
 */
public class ConstructorStrategy {

	private final boolean dfs;
	private final int satCost;

	public ConstructorStrategy(boolean dfs, int satCost) {
		this.dfs = dfs;
		this.satCost = satCost;
	}

	public Node getNextNodeToTreat(SymbolicExecutionEngine engine) {
		if (!engine.getGoals().isEmpty()) {
			if (dfs) {
				engine.getGoals().get(engine.getGoals().size() - 1);
			}
			return engine.getGoals().get(0);
		}
		return null;
	}

	public void applyStrategySoughtRule(Node goal, SymbolicExecutionEngine engine) {
		if (goal.registeredSatisfiability() && goal.getSatisfiability().isUnsatisfiable()) {
			engine.applyNoRule(goal);
			return;
		}

		List<IRule> possibleRules = engine.inferApplicableRules(goal);
		if (!possibleRules.isEmpty()) {
			IRule ruleToApply = null;

			ruleToApply = engine.searchForSpecificRule(possibleRules, TransitionCycleEliminationRule.class);
			if (ruleToApply == null) {
				ruleToApply = engine.searchForSpecificRule(possibleRules, LocalReactionCycleEliminationRule.class);
			}
			if (ruleToApply == null) {
				ruleToApply = possibleRules.get(0);
			}

			// solving gets triggered here
			if (IRule.isBranchingRuleType(ruleToApply) && shouldBeSolved(goal)) {
				solveNode(engine, goal);
			}

			engine.applyRule(goal, ruleToApply);

			return;
		}

		engine.applyNoRule(goal);
	}

	public void oneStep(SymbolicExecutionEngine engine) {
		Node goal = getNextNodeToTreat(engine);
		if (goal != null) {
			applyStrategySoughtRule(goal, engine);
			return;
		}
	}

	public void solveNode(SymbolicExecutionEngine engine, Node node) {
		engine.getGlobalContext().getSolver().solveNode(node);
	}

	public boolean shouldBeSolved(Node goal) {
		if (goal.registeredSatisfiability()) {
			return false;
		}
		int stepsTaken = 0;

		Node possibleFather = goal.getParent();

		while (possibleFather != null) {
			if (possibleFather.registeredSatisfiability()) {
				return false;
			}
			if (IRule.isBranchingRuleType(possibleFather)) {
				stepsTaken++;
			}
			if (stepsTaken >= satCost) {
				return true;
			}
			possibleFather = possibleFather.getParent();
		}
		return false;
	}

}
