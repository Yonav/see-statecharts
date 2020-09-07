package com.yakindu.sct.se.rules.statement.pathConstraints;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.yakindu.base.expressions.expressions.AssignmentExpression;
import org.yakindu.base.expressions.expressions.ConditionalExpression;
import org.yakindu.base.expressions.expressions.LogicalNotExpression;
import org.yakindu.base.types.Expression;

import com.yakindu.sct.se.collection.ImmutableList;
import com.yakindu.sct.se.engine.strategy.SETContext;
import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.rules.IRule;
import com.yakindu.sct.se.rules.statement.pathConstraints.transformer.SSATransformator;
import com.yakindu.sct.se.symbolicExecutionExtension.SequenceBlock;
import com.yakindu.sct.se.util.NodeUtil;
import com.yakindu.sct.se.util.ExpressionCreatorUtil;

/**
 * Implements AssignmentConditionalRule.
 * 
 * @author jwielage
 *
 */
public class AssignmentConditionalRule implements IRule {

	public static final AssignmentConditionalRule INSTANCE = new AssignmentConditionalRule();

	private SSATransformator transformator = SSATransformator.INSTANCE;

	private Node isApplicableFor;

	private AssignmentConditionalRule() {

	}

	@Override
	public boolean isApplicable(Node node, SETContext globalContext) {
		if (node.getActiveStatements() != null && node.getActiveStatements().getValue() instanceof AssignmentExpression
				&& ((AssignmentExpression) node.getActiveStatements().getValue())
						.getExpression() instanceof ConditionalExpression) {
			return true;
		}
		return false;
	}

	@Override
	public void apply(Node node, SETContext globalContext) {
		sanityCheckApply(node);

		node.setAppliedRule(this);

		ImmutableList<Expression> newStatementBlockWithoutCondition = node.removeTopActiveStatement();
		AssignmentExpression assignment = ((AssignmentExpression) node.getActiveStatements().getValue());

		ConditionalExpression conditional = ((ConditionalExpression) assignment.getExpression());

		// true case
		AssignmentExpression newAssignmentTrue = ExpressionCreatorUtil.createAssignment(assignment.getVarRef(),
				conditional.getTrueCase());

		SequenceBlock tempSeqBlock = transformator.transformToSSA(conditional.getCondition(),
				node.getSymbolicMemoryStore(), globalContext);

		if (tempSeqBlock.getExpressions().size() == 0) {
			throw new IllegalStateException();
		}
		Expression conditionTrueWithRuntimeNames = tempSeqBlock.getExpressions().get(0);

		// true child
		NodeUtil.createChildFromParent(globalContext.generateNextNodeCount(), node, null,
				conditionTrueWithRuntimeNames, newStatementBlockWithoutCondition.prepend(newAssignmentTrue), null);

		// false case
		AssignmentExpression newAssignmentFalse = ExpressionCreatorUtil.createAssignment(assignment.getVarRef(),
				conditional.getFalseCase());

		LogicalNotExpression conditionFalseWithRuntimeNames = ExpressionCreatorUtil
				.createLogNotExpression(EcoreUtil.copy(conditionTrueWithRuntimeNames));

		// false child
		NodeUtil.createChildFromParent(globalContext.generateNextNodeCount(), node, null,
				conditionFalseWithRuntimeNames, newStatementBlockWithoutCondition.prepend(newAssignmentFalse), null);
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
		// TODO Auto-generated method stub

	}

}
