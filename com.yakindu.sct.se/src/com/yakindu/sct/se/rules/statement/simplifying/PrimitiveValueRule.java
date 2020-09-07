package com.yakindu.sct.se.rules.statement.simplifying;

import org.yakindu.base.expressions.expressions.BoolLiteral;
import org.yakindu.base.expressions.expressions.PrimitiveValueExpression;

import com.yakindu.sct.se.engine.strategy.SETContext;
import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.rules.IRule;
import com.yakindu.sct.se.solver.model.MaybeBool;
import com.yakindu.sct.se.util.NodeUtil;

/**
 * Implements PrimitiveValueRule.
 * 
 * @author jwielage
 *
 */
public class PrimitiveValueRule implements IRule {

	public static final PrimitiveValueRule INSTANCE = new PrimitiveValueRule();

	private Node isApplicableFor;

	private PrimitiveValueRule() {
	}

	@Override
	public boolean isApplicable(Node node, SETContext globalContext) {
		if (node.hasActiveStatements() && node.getActiveStatements().getValue() instanceof PrimitiveValueExpression) {
			return true;
		}
		return false;
	}

	@Override
	public void apply(Node node, SETContext globalContext) {
		sanityCheckApply(node);
		node.setAppliedRule(this);

		PrimitiveValueExpression exprToHandle = ((PrimitiveValueExpression) node.getActiveStatements().getValue());

		if (exprToHandle.getValue() instanceof BoolLiteral && !(((BoolLiteral) exprToHandle.getValue()).isValue())) {
			// holds a 'false', cancel this path
			node.setSatisfiability(MaybeBool.UNSAT);
			return;
		}

		NodeUtil.createChildFromParent(globalContext.generateNextNodeCount(), node, null, null,
				node.removeTopActiveStatement(), null);

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
		// do nothing...
	}

}
