package com.yakindu.sct.se.rules.statement.pathConstraints;

import org.eclipse.emf.common.util.EList;
import org.yakindu.base.expressions.expressions.AssignmentExpression;
import org.yakindu.base.expressions.expressions.MultiplicativeOperator;
import org.yakindu.base.expressions.expressions.NumericalMultiplyDivideExpression;
import org.yakindu.base.types.Expression;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import com.yakindu.sct.se.engine.strategy.SETContext;
import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.rules.IRule;
import com.yakindu.sct.se.rules.statement.pathConstraints.transformer.SSATransformator;
import com.yakindu.sct.se.solver.model.MaybeBool;
import com.yakindu.sct.se.symbolicExecutionExtension.SequenceBlock;
import com.yakindu.sct.se.util.ExpressionUtil;
import com.yakindu.sct.se.util.NodeUtil;
import com.yakindu.sct.se.util.ExpressionCreatorUtil;

/**
 * Implements AssignmentDivisionRule.
 * 
 * @author jwielage
 *
 */
public class AssignmentDivisionRule implements IRule {

	public static final AssignmentDivisionRule INSTANCE(boolean allowZeroDivision) {
		return new AssignmentDivisionRule(allowZeroDivision);
	}

	private Node isApplicableFor;

	private final boolean allowZeroDivision;
	private SSATransformator transformator = SSATransformator.INSTANCE;

	private AssignmentDivisionRule(boolean allowZeroDivision) {
		this.allowZeroDivision = allowZeroDivision;

	}

	@Override
	public boolean isApplicable(Node node, SETContext globalContext) {
		if (node.getActiveStatements() != null && node.getActiveStatements().getValue() instanceof AssignmentExpression
				&& ((AssignmentExpression) node.getActiveStatements().getValue())
						.getExpression() instanceof NumericalMultiplyDivideExpression
				&& ((NumericalMultiplyDivideExpression) ((AssignmentExpression) node.getActiveStatements().getValue())
						.getExpression()).getOperator().equals(MultiplicativeOperator.DIV)) {
			return true;
		}

		return false;
	}

	@Override
	public void apply(Node node, SETContext globalContext) {
		sanityCheckApply(node);

		node.setAppliedRule(this);

		AssignmentExpression expressionToHandle = ((AssignmentExpression) node.getActiveStatements().getValue());

		SequenceBlock transformedExpressionAndExtras = transformator.transformToSSA(expressionToHandle,
				node.getSymbolicMemoryStore(), globalContext);

		// create new child
		Node childDivisorNonNull = NodeUtil.createChildFromParent(globalContext.generateNextNodeCount(), node, null,
				null, node.removeTopActiveStatement(), null);

		Node childDivisorNull = NodeUtil.createChildFromParent(globalContext.generateNextNodeCount(), node, null,
				null, node.removeTopActiveStatement(), null);

		EList<Expression> expressionsInBlock = transformedExpressionAndExtras.getExpressions();

		// i=0: transformed assignment expression, add newly generated variable to
		// symbolicMemory
		// i>0 :adds newly generated defaultValue Expressions and newly generated
		// variable mappings to the childnode
		for (int i = 0; i < expressionsInBlock.size(); i++) {

			if (i == 0) {
				AssignmentExpression assignment = ((AssignmentExpression) expressionsInBlock.get(i));
				Expression rightOp = ((NumericalMultiplyDivideExpression) assignment.getExpression()).getRightOperand();

				VariableDefinition varRight = ExpressionUtil.extractVarDefofElementReference(rightOp);
				if (varRight != null) {
					AssignmentExpression eqNull = ExpressionCreatorUtil.createAssignment(varRight,
							ExpressionCreatorUtil.createIntExpression(0));

					childDivisorNull.prependConstraintAndSet(eqNull);
					childDivisorNonNull.prependConstraintAndSet(ExpressionCreatorUtil.createLogNotExpression(eqNull));

				}

			}
			Expression expression = expressionsInBlock.get(i);

			// add mapping to symbolicMemory
			VariableDefinition createdVarDef = ExpressionUtil.extractVarDefOfAssignment(expression);
			if (createdVarDef != null) {
				childDivisorNonNull.prependSymbolicMemoryEntryAndSet(createdVarDef);
				childDivisorNull.prependSymbolicMemoryEntryAndSet(createdVarDef);
			}

			// adds default values to uninitialized variables
			childDivisorNonNull.prependConstraintAndSet(expression);
			childDivisorNull.prependConstraintAndSet(expression);
		}

		if (!allowZeroDivision) {
			childDivisorNull.setSatisfiability(MaybeBool.UNSAT);
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
