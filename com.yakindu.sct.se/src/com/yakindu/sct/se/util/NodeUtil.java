package com.yakindu.sct.se.util;

import java.util.ArrayList;
import java.util.List;

import org.yakindu.base.types.Expression;
import org.yakindu.sct.generator.core.console.IConsoleLogger;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import com.google.inject.Inject;
import com.yakindu.sct.se.collection.ImmutableList;
import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.rules.IRule;
import com.yakindu.sct.se.rules.statement.pathConstraints.AssignmentDivisionRule;
import com.yakindu.sct.se.rules.statement.pathConstraints.DefaultStatementRule;
import com.yakindu.sct.se.rules.transition.TransitionRule;
import com.yakindu.sct.se.solver.serializer.ExpressionSerializer;
import com.yakindu.sct.se.symbolicExecutionExtension.SequenceBlock;

/**
 * Contains utility methods for nodes
 * 
 * @author jwielage
 *
 */
public class NodeUtil {

	@Inject
	private static IConsoleLogger logger = new SELogger();

	public static Node createChildFromParent(int nodeCounter, Node parent, Transition addedLastTransition,
			Expression addedPathConstraint, ImmutableList<Expression> newStatementBlock,
			VariableDefinition symbolicEntry) {

		if (parent == null) {
			return null;
		}
		// set the child Node
		Node childNode = new Node(nodeCounter);

		childNode.setLastTransitions(parent.prependTransition(addedLastTransition));

		childNode.setPathConstraint(parent.prependConstraint(addedPathConstraint));

		childNode.setSymbolicMemoryStore(parent.prependSymbolicMemoryEntry(symbolicEntry));

		childNode.setActiveStatements(newStatementBlock);

		childNode.setBlockedReactions(parent.getBlockedReactions());

		// set the parent Node
		parent.addChildAndSetAsFather(childNode);

		return childNode;
	}

	// ====================================================
	// debugging / logging methods
	// ====================================================
	public static void printNodeTree(Node initialNode, int limit) {
		try {
			List<Node> nodesToHandle = new ArrayList<>();
			nodesToHandle.add(initialNode);

			while (nodesToHandle.size() > 0 && limit > 0) {
				if (nodesToHandle.get(0).getChildren() != null) {
					logger.log("---------------------------------------------");
					logger.log("father:");
					printNode(nodesToHandle.get(0));
					logger.log("");

					ImmutableList<Node> children = nodesToHandle.get(0).getChildren();
					if (children != null) {
						logger.log("children:");
						while (!children.isEmpty()) {
							printNode(children.getValue());

							nodesToHandle.add(children.getValue());

							logger.log("");
							children = children.getNext();

						}
					}
				}
				limit--;
				nodesToHandle.remove(0);
			}

		} catch (Exception e) {
			logger.log("error when printing node " + initialNode.getNodeCounter());
		}

	}

	public static void printNode(Node node) {
		try {

			logger.log(("#:" + node.getNodeCounter()));
			ExpressionSerializer ser = new ExpressionSerializer(true);
			if (node.getAppliedRule() != null) {
				logger.log("Rule: " + node.getAppliedRule().getClass().getSimpleName());
				if (IRule.isOfRuleType(node, AssignmentDivisionRule.class)) {
					String assignment = ser.serialize(node.getActiveStatements().getValue()).toString();
					logger.log("AssignmentDefault: " + assignment);
				} else if (node.getAppliedRule().equals(DefaultStatementRule.INSTANCE)) {
					String assignment = ser.serialize(node.getActiveStatements().getValue()).toString();
					logger.log("DefaultStatement: " + assignment);
				}
			}
			if (node.getPathConstraint() != null) {
				Expression adap = node.getPathConstraint().getValue();

				String exor = ser.serialize(adap).toString();
				logger.log("PC: " + exor);

			}

			if (node.getLastTransitions() != null && !node.getLastTransitions().isEmpty()) {
				logger.log("State: " + node.getLastTransitions().getValue().getTarget().getName());
			}

			if (node.getChildren() == null) {
				logger.log("No Children");
			}

			if (!node.registeredSatisfiability()) {
				logger.log("Keine Satisfiability registriert!");
			} else {
				logger.log("Satisfiability: " + node.getSatisfiability().toString());
			}

			if (node.getLastTransitions() != null) {
				StringBuilder sb = new StringBuilder();
				ImmutableList<Transition> transitions = node.getLastTransitions();
				while (!transitions.isEmpty()) {
					sb.append("[" + transitions.getValue().getTarget().getName() + "] <- ");
					transitions = transitions.getNext();
				}

				logger.log(sb.toString());
			}

			if (node.getParent() != null && node.getParent().getAppliedRule().equals(TransitionRule.INSTANCE)) {
				if (node.getActiveStatements() != null && node.getActiveStatements().getValue() != null) {
					String className = node.getActiveStatements().getValue().getClass().getSimpleName();
					logger.log("Added Class to StatementBlock: " + className);
					if (className != SequenceBlock.class.getSimpleName()) {
						try {
							logger.log(
									"Expr Added: " + ser.serialize(node.getActiveStatements().getValue()).toString());
						} catch (IllegalArgumentException e) {
							logger.log("Error printing Expression");
						}
					}
				} else {
					logger.log("Kein Statement zu finden!");
				}
			}

		} catch (Exception e) {
			logger.log("Error when printing node " + node.getNodeCounter());
		}

	}

}
