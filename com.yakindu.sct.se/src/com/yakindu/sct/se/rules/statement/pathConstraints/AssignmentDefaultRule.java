package com.yakindu.sct.se.rules.statement.pathConstraints;

import org.eclipse.emf.common.util.EList;
import org.yakindu.base.expressions.expressions.AssignmentExpression;
import org.yakindu.base.expressions.expressions.ConditionalExpression;
import org.yakindu.base.expressions.expressions.MultiplicativeOperator;
import org.yakindu.base.expressions.expressions.NumericalMultiplyDivideExpression;
import org.yakindu.base.types.Expression;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import com.yakindu.sct.se.engine.strategy.SETContext;
import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.rules.IRule;
import com.yakindu.sct.se.rules.statement.pathConstraints.transformer.SSATransformator;
import com.yakindu.sct.se.symbolicExecutionExtension.SequenceBlock;
import com.yakindu.sct.se.util.ExpressionUtil;
import com.yakindu.sct.se.util.NodeUtil;

/**
 * Implements AssignmentDefaultRule.
 * 
 * @author jwielage
 *
 */
public class AssignmentDefaultRule implements IRule {

	public static final AssignmentDefaultRule INSTANCE = new AssignmentDefaultRule();

	private Node isApplicableFor;

	private SSATransformator transformator = SSATransformator.INSTANCE;

	private AssignmentDefaultRule() {

	}

	@Override
	public boolean isApplicable(Node node, SETContext globalContext) {
		if (!node.hasActiveStatements()) {
			return false;
		}
		if (!(node.getActiveStatements().getValue() instanceof AssignmentExpression)) {
			return false;
		}
		Expression assignee = ((AssignmentExpression) node.getActiveStatements().getValue()).getExpression();

		if (assignee instanceof ConditionalExpression) {
			return false;
		}

		if (assignee instanceof NumericalMultiplyDivideExpression
				&& ((NumericalMultiplyDivideExpression) assignee).getOperator().equals(MultiplicativeOperator.DIV)) {
			return false;
		}
		return true;
	}

	@Override
	public void apply(Node node, SETContext globalContext) {
		sanityCheckApply(node);

		node.setAppliedRule(this);

		AssignmentExpression expressionToHandle = ((AssignmentExpression) node.getActiveStatements().getValue());

		SequenceBlock transformedExpressionAndExtras = transformator.transformToSSA(expressionToHandle,
				node.getSymbolicMemoryStore(), globalContext);

		// create new child
		Node child = NodeUtil.createChildFromParent(globalContext.generateNextNodeCount(), node, null, null,
				node.removeTopActiveStatement(), null);

		EList<Expression> expressionsInBlock = transformedExpressionAndExtras.getExpressions();

		// i=0: transformed assignment expression, add newly generated variable to
		// symbolicMemory
		// i>0 :adds newly generated defaultValue Expressions and newly generated
		// variable mappings to the childnode
		for (int i = 0; i < expressionsInBlock.size(); i++) {

			Expression expression = expressionsInBlock.get(i);

			// add mapping to symbolicMemory
			VariableDefinition createdVarDef = ExpressionUtil.extractVarDefOfAssignment(expression);
			if (createdVarDef != null) {
				child.prependSymbolicMemoryEntryAndSet(createdVarDef);
			}

			// adds default values to uninitialized variables
			child.prependConstraintAndSet(expression);
		}
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
