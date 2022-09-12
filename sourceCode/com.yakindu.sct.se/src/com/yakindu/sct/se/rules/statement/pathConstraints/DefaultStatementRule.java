package com.yakindu.sct.se.rules.statement.pathConstraints;

import org.eclipse.emf.common.util.EList;
import org.yakindu.base.expressions.expressions.AssignmentExpression;
import org.yakindu.base.expressions.expressions.BitwiseAndExpression;
import org.yakindu.base.expressions.expressions.BitwiseOrExpression;
import org.yakindu.base.expressions.expressions.BitwiseXorExpression;
import org.yakindu.base.expressions.expressions.ConditionalExpression;
import org.yakindu.base.expressions.expressions.PostFixUnaryExpression;
import org.yakindu.base.expressions.expressions.PrimitiveValueExpression;
import org.yakindu.base.expressions.expressions.ShiftExpression;
import org.yakindu.base.expressions.expressions.TypeCastExpression;
import org.yakindu.base.types.Expression;
import org.yakindu.sct.model.stext.stext.EventRaisingExpression;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import com.yakindu.sct.se.engine.strategy.SETContext;
import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.rules.IRule;
import com.yakindu.sct.se.rules.statement.pathConstraints.transformer.SSATransformator;
import com.yakindu.sct.se.symbolicExecutionExtension.SequenceBlock;
import com.yakindu.sct.se.util.ExpressionUtil;
import com.yakindu.sct.se.util.NodeUtil;

/**
 * Is applicable if the first element in the Active Statements is a specific
 * expression. Adds the expression with transformed variable names to
 * pathConstraints. If there are uninitialized variables an expression assigning
 * the variable it's default value is added to the path constraints
 * 
 * @author jwielage
 *
 */
public class DefaultStatementRule implements IRule {

	public static final DefaultStatementRule INSTANCE = new DefaultStatementRule();

	private Node isApplicableFor;
	private final SSATransformator transformator = SSATransformator.INSTANCE;

	private DefaultStatementRule() {
	}

	@Override
	public boolean isApplicable(Node node, SETContext globalContext) {
		if (!node.hasActiveStatements()) {
			return false;
		}

		Expression expr = node.getActiveStatements().getValue();
		if (expr instanceof PrimitiveValueExpression || expr instanceof AssignmentExpression
				|| expr instanceof ConditionalExpression || expr instanceof SequenceBlock
				|| expr instanceof BitwiseAndExpression || expr instanceof BitwiseOrExpression
				|| expr instanceof BitwiseXorExpression || expr instanceof TypeCastExpression
				|| expr instanceof ShiftExpression || expr instanceof PostFixUnaryExpression) {
			return false;
		}

		return true;
	}

	@Override
	public void apply(Node node, SETContext globalContext) {
		sanityCheckApply(node);
		node.setAppliedRule(this);

		Expression exprToHandle = node.getActiveStatements().getValue();

		// aren't supported atm
		if (exprToHandle instanceof EventRaisingExpression) {
			NodeUtil.createChildFromParent(globalContext.generateNextNodeCount(), node, null, null,
					node.removeTopActiveStatement(), null);
			return;
		}

		SequenceBlock transformedExpressionAndExtras = transformator.transformToSSA(exprToHandle,
				node.getSymbolicMemoryStore(), globalContext);

		EList<Expression> expressionsInBlock = transformedExpressionAndExtras.getExpressions();
		Expression transformedExpression = expressionsInBlock.get(0);

		Node child = NodeUtil.createChildFromParent(globalContext.generateNextNodeCount(), node, null,
				transformedExpression, node.removeTopActiveStatement(), null);

		// adds newly generated defaultValue Expressions and newly generated variable
		// mappings to the childnode
		for (int i = 1; i < expressionsInBlock.size(); i++) {

			Expression defaultValueExpression = expressionsInBlock.get(i);

			// add mapping to symbolicMemory
			VariableDefinition createdVarDef = ExpressionUtil.extractVarDefOfAssignment(defaultValueExpression);
			if (createdVarDef != null) {
				child.prependSymbolicMemoryEntryAndSet(createdVarDef);
			}

			// adds default values to uninitialized variables
			child.prependConstraintAndSet(defaultValueExpression);
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
		// do nothing...
	}

}
