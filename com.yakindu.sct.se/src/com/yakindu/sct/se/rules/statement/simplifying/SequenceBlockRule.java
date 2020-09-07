package com.yakindu.sct.se.rules.statement.simplifying;

import org.eclipse.emf.common.util.EList;
import org.yakindu.base.types.Expression;

import com.yakindu.sct.se.collection.ImmutableList;
import com.yakindu.sct.se.engine.strategy.SETContext;
import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.rules.IRule;
import com.yakindu.sct.se.symbolicExecutionExtension.SequenceBlock;
import com.yakindu.sct.se.util.NodeUtil;

/**
 * Is applicable if the first element in the Active Statements is a
 * sequenceBlock. Adds all elements of the sequenceBlock to the Active
 * Statements in the right order.
 * 
 * @author jwielage
 *
 */
public class SequenceBlockRule implements IRule {

	public static final SequenceBlockRule INSTANCE = new SequenceBlockRule();
	private Node isApplicableFor;

	@Override
	public boolean isApplicable(Node node, SETContext globalContext) {
		if (node.hasActiveStatements() && node.getActiveStatements().getValue() instanceof SequenceBlock) {
			return true;
		}
		return false;
	}

	@Override
	public void apply(Node node, SETContext globalContext) {
		sanityCheckApply(node);
		node.setAppliedRule(this);

		SequenceBlock sequence = ((SequenceBlock) node.getActiveStatements().getValue());

		EList<Expression> expressionList = sequence.getExpressions();

		ImmutableList<Expression> newStatementBlock = node.removeTopActiveStatement();

		if (expressionList != null && !expressionList.isEmpty()) {
			newStatementBlock = newStatementBlock.prependReverse(expressionList.toArray(new Expression[0]));
		}

		NodeUtil.createChildFromParent(globalContext.generateNextNodeCount(), node, null, null, newStatementBlock,
				null);
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
		// do nothing
	}

}
