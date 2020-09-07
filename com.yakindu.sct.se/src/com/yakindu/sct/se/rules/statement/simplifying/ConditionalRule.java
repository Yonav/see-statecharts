package com.yakindu.sct.se.rules.statement.simplifying;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.yakindu.base.expressions.expressions.ConditionalExpression;
import org.yakindu.base.expressions.expressions.LogicalNotExpression;
import org.yakindu.base.types.Expression;

import com.yakindu.sct.se.collection.ImmutableList;
import com.yakindu.sct.se.engine.strategy.SETContext;
import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.rules.IRule;
import com.yakindu.sct.se.util.NodeUtil;
import com.yakindu.sct.se.util.ExpressionCreatorUtil;

/**
 * Is applicable if the first element in the Active Statements is a
 * {@link ConditionalExpression}. Generates a child for each case of the
 * condition and adds the guard expression (or negated guard expression) to the
 * Active Statements, followed by the respective expression.
 * 
 * @author jwielage
 *
 */
public class ConditionalRule implements IRule {

	public static final ConditionalRule INSTANCE = new ConditionalRule();

	private Node isApplicableFor;

	@Override
	public boolean isApplicable(Node node, SETContext globalContext) {
		if (node.hasActiveStatements() && node.getActiveStatements().getValue() instanceof ConditionalExpression) {
			return true;
		}
		return false;
	}

	@Override
	public void apply(Node node, SETContext globalContext) {
		sanityCheckApply(node);
		node.setAppliedRule(this);

		ConditionalExpression condExpr = ((ConditionalExpression) node.getActiveStatements().getValue());

		// true case
		ImmutableList<Expression> trueCaseRTCExpressions = node.getActiveStatements().getNext()
				.prepend(condExpr.getTrueCase(), condExpr.getCondition());

		NodeUtil.createChildFromParent(globalContext.generateNextNodeCount(), node, null, null,
				trueCaseRTCExpressions, null);

		// false case
		LogicalNotExpression negatedCondition = ExpressionCreatorUtil
				.createLogNotExpression(EcoreUtil.copy(condExpr.getCondition()));

		ImmutableList<Expression> falseCaseRTCExpressions = node.getActiveStatements().getNext()
				.prepend(condExpr.getFalseCase(), negatedCondition);

		NodeUtil.createChildFromParent(globalContext.generateNextNodeCount(), node, null, null,
				falseCaseRTCExpressions, null);
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
