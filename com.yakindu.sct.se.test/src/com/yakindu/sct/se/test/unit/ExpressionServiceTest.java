package com.yakindu.sct.se.test.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;

import org.junit.Test;
import org.yakindu.base.expressions.expressions.AdditiveOperator;
import org.yakindu.base.expressions.expressions.AssignmentExpression;
import org.yakindu.base.expressions.expressions.LogicalAndExpression;
import org.yakindu.base.expressions.expressions.LogicalNotExpression;
import org.yakindu.base.expressions.expressions.LogicalOrExpression;
import org.yakindu.base.expressions.expressions.LogicalRelationExpression;
import org.yakindu.base.expressions.expressions.MultiplicativeOperator;
import org.yakindu.base.expressions.expressions.NumericalAddSubtractExpression;
import org.yakindu.base.expressions.expressions.NumericalMultiplyDivideExpression;
import org.yakindu.base.expressions.expressions.NumericalUnaryExpression;
import org.yakindu.base.expressions.expressions.RelationalOperator;
import org.yakindu.base.expressions.expressions.UnaryOperator;
import org.yakindu.base.types.Expression;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import com.yakindu.sct.se.test.util.ExpressionCreatorUtil;
import com.yakindu.sct.se.util.ExpressionUtil;

public class ExpressionServiceTest {

	@Test
	public void _extractVarDefNamesOfExpression_WhenNull() {
		// arrange

		// act
		HashSet<VariableDefinition> resultSet = ExpressionUtil.extractVarDefOfExpression(null);
		// assert
		assertThat(resultSet).isEmpty();

	}

	@Test
	public void _extractVarDefNamesOfExpression_WhenAssignment() {
		// arrange
		AssignmentExpression expression = ExpressionCreatorUtil.addOneToVar("x");
		VariableDefinition varDef = ExpressionUtil.extractVarDefOfAssignment(expression);
		// act
		HashSet<VariableDefinition> resultSet = ExpressionUtil.extractVarDefOfExpression(expression);

		// assert
		assertThat(resultSet).contains(varDef);
		assertThat(resultSet).hasSize(1);
	}

	@Test
	public void _extractVarDefNamesOfExpression_WhenMultipleInterlaced() {
		// arrange
		NumericalUnaryExpression negA = ExpressionCreatorUtil
				.createNumUnaryExpression(ExpressionCreatorUtil.createElRef("a", "integer"), UnaryOperator.NEGATIVE);

		NumericalMultiplyDivideExpression mulE = ExpressionCreatorUtil.createNumMulDivExpression(negA,
				ExpressionCreatorUtil.createIntExpression(3), MultiplicativeOperator.MUL);
		NumericalAddSubtractExpression plusE = ExpressionCreatorUtil.createNumPlusMinusExpression(mulE,
				ExpressionCreatorUtil.createElRef("b", "integer"), AdditiveOperator.PLUS);

		LogicalRelationExpression smallerE = ExpressionCreatorUtil.createLogRelExpression(plusE,
				ExpressionCreatorUtil.createIntExpression(3), RelationalOperator.SMALLER);

		LogicalNotExpression notE = ExpressionCreatorUtil
				.createLogNotExpression(ExpressionCreatorUtil.createElRef("c", "boolean"));

		LogicalOrExpression orE = ExpressionCreatorUtil.createLogOrExpression(notE, smallerE);

		LogicalNotExpression notE2 = ExpressionCreatorUtil
				.createLogNotExpression(ExpressionCreatorUtil.createElRef("d", "integer"));

		LogicalOrExpression orE2 = ExpressionCreatorUtil.createLogOrExpression(notE2,
				ExpressionCreatorUtil.createElRef("e", "integer"));

		Expression parE = ExpressionCreatorUtil.createParenthesizedExpression(orE2);

		LogicalAndExpression expr = ExpressionCreatorUtil.createLogAndExpression(parE, orE);

		// act
		HashSet<VariableDefinition> resultSet = ExpressionUtil.extractVarDefOfExpression(expr);
		// assert
		assertThat(resultSet).contains(ExpressionUtil.extractVarDefofElementReference(negA.getOperand()));
		assertThat(resultSet).contains(ExpressionUtil.extractVarDefofElementReference(plusE.getRightOperand()));
		assertThat(resultSet).contains(ExpressionUtil.extractVarDefofElementReference(notE.getOperand()));
		assertThat(resultSet).contains(ExpressionUtil.extractVarDefofElementReference(notE2.getOperand()));
		assertThat(resultSet).contains(ExpressionUtil.extractVarDefofElementReference(orE2.getRightOperand()));

	}

}
