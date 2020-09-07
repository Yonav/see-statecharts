package com.yakindu.sct.se.engine;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.yakindu.sct.generator.core.console.IConsoleLogger;
import org.yakindu.sct.model.sgraph.Statechart;

import com.yakindu.sct.se.engine.strategy.SETContext;
import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.rules.IRule;
import com.yakindu.sct.se.util.NodeUtil;
import com.yakindu.sct.se.util.SELogger;

/**
 * Engine of the symbolic execution.
 * 
 * @author jwielage
 */
public class SymbolicExecutionEngine {

	private IConsoleLogger logger = new SELogger();

	private final Statechart statechart;
	private final Node initialNode;
	private final SETContext globalContext;
	private final List<IRule> rules;
	private final List<Node> goals = new LinkedList<>();

	public SymbolicExecutionEngine(Statechart statechart, SETContext globalContext, List<IRule> rules) {
		this.globalContext = globalContext;
		this.statechart = statechart;
		this.rules = rules;

		// sets entryState in globalContext!
		initialNode = SETInitializer.INSTANCE.createInitialSETNodeForStatechart(this.statechart, this.globalContext);
		if (initialNode != null) {
			goals.add(initialNode);
		}
	}

	/**
	 * Applies the rule and adds children as goal
	 * 
	 * @param goal to apply rule to
	 * @param rule to apply
	 */
	public void applyRule(Node goal, IRule rule) {
		if ((goal.registeredSatisfiability() && goal.getSatisfiability().isUnsatisfiable())
				|| !globalContext.getSolver().elligibleForRuleApplicabilityBla(goal)) {
			rule = null;
		}

		if (rule != null) {
			rule.apply(goal, this.globalContext);
		}

		boolean isLeaf = !addChildrenAsGoals(goal);
		if (isLeaf) {
			handleLeaf(goal);
		}

		goals.remove(goal);
		resetRuleSpecifics();

		// if you want to print information about the node to logger
		logNode(goal);
	}

	// note: adds children in reverse...
	private boolean addChildrenAsGoals(Node node) {
		if (node.getChildren() != null && !node.getChildren().isEmpty()) {
			node.getChildren().consumeUnderlyingList(child -> {
				goals.add(child);
			});
			return true;
		}
		return false;
	}

	// if no rule is applicable
	public void applyNoRule(Node goal) {
		applyRule(goal, null);
	}

	// leafs have to be solved to be analyzed, can be done mostly with fastSolving
	private void handleLeaf(Node leaf) {
		if (!leaf.registeredSatisfiability()) {
			globalContext.getSolver().solveLeaf(leaf);
		}
	}

	/**
	 * reset all information stored in rules
	 */
	private void resetRuleSpecifics() {
		for (IRule rule : rules) {
			rule.resetIsApplicableFor();
		}
	}

	/**
	 * Check which rules are applicable for the node and sets the node as applicable
	 * 
	 * @param node
	 * @return
	 */
	public List<IRule> inferApplicableRules(Node node) {
		List<IRule> result = new ArrayList<IRule>();

		for (IRule rule : rules) {
			if (rule.setIfApplicable(node, this.globalContext)) {
				result.add(rule);
			}
		}
		return result;
	}

	/**
	 * Returns the first element in the list, that is an instance of the class
	 * 
	 * @param ruleList  to search for class
	 * @param ruleClazz class to search
	 * @return found rule
	 */
	public IRule searchForSpecificRule(List<IRule> ruleList, Class<? extends IRule> ruleClazz) {
		for (IRule ruleInList : ruleList) {
			if (ruleClazz.isInstance(ruleInList)) {
				return ruleInList;
			}
		}
		return null;
	}

	// prints information about the node to logger
	private void logNode(Node node) {
		logger.log("------------");
		NodeUtil.printNode(node);
		if (node.getChildren() == null || node.getChildren().isEmpty()) {
			logger.log("Keine Kinder!");
			if (node.registeredSatisfiability()) {
				logger.log("Knoten war: " + node.getSatisfiability().toString());
			}
			logger.log("------------");
		} else {
			logger.log("\n" + "Kinder:");
			node.getChildren().consumeUnderlyingList(NodeUtil::printNode);
			logger.log("------------");
		}
	}

	// ========================================================
	// Getter & Setter
	// ========================================================
	public SETContext getGlobalContext() {
		return globalContext;
	}

	public List<Node> getGoals() {
		return goals;
	}

	public Node getInitialNode() {
		return initialNode;
	}
}
