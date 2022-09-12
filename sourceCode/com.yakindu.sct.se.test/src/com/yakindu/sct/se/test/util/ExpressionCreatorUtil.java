package com.yakindu.sct.se.test.util;


import org.yakindu.base.expressions.expressions.AdditiveOperator;
import org.yakindu.base.expressions.expressions.AssignmentExpression;
import org.yakindu.base.expressions.expressions.AssignmentOperator;
import org.yakindu.base.expressions.expressions.BinaryExpression;
import org.yakindu.base.expressions.expressions.ConditionalExpression;
import org.yakindu.base.expressions.expressions.ElementReferenceExpression;
import org.yakindu.base.expressions.expressions.ExpressionsFactory;
import org.yakindu.base.expressions.expressions.LogicalAndExpression;
import org.yakindu.base.expressions.expressions.LogicalNotExpression;
import org.yakindu.base.expressions.expressions.LogicalOperator;
import org.yakindu.base.expressions.expressions.LogicalOrExpression;
import org.yakindu.base.expressions.expressions.LogicalRelationExpression;
import org.yakindu.base.expressions.expressions.MultiplicativeOperator;
import org.yakindu.base.expressions.expressions.NumericalAddSubtractExpression;
import org.yakindu.base.expressions.expressions.NumericalMultiplyDivideExpression;
import org.yakindu.base.expressions.expressions.NumericalUnaryExpression;
import org.yakindu.base.expressions.expressions.ParenthesizedExpression;
import org.yakindu.base.expressions.expressions.PrimitiveValueExpression;
import org.yakindu.base.expressions.expressions.RelationalOperator;
import org.yakindu.base.expressions.expressions.UnaryOperator;
import org.yakindu.base.types.Expression;
import org.yakindu.sct.model.sgraph.Scope;
import org.yakindu.sct.model.stext.stext.VariableDefinition;
import org.yakindu.sct.model.stext.stext.impl.StextFactoryImpl;

import com.yakindu.sct.se.model.VarType;

public class ExpressionCreatorUtil {

	//-----------------------------------------------------
	// Ready To Use
	//-----------------------------------------------------
	
	public static AssignmentExpression setVarToInt(String varName, int number) {
		return setVarToInt(createVarDef(varName, "integer"), number);
	}
		
	public static AssignmentExpression setVarToInt(VariableDefinition varDef, int number) {
		return createAssignment(varDef, createIntExpression(number));
	}
	
	public static AssignmentExpression addOneToVar(String varName) {
		return addOneToVar(createVarDef(varName, "integer"));
	}
	
	public static AssignmentExpression addOneToVar(VariableDefinition varDef) {
		return addIntToVar(varDef, 1);
	}
	
	public static AssignmentExpression addIntToVar(String varName, int number) {
		return addIntToVar(createVarDef(varName, "integer"), number);
	}
	
	public static AssignmentExpression addIntToVar(VariableDefinition varDef, int number) {
		NumericalAddSubtractExpression right = createNumPlusMinusExpression(createElRef(varDef), createIntExpression(number),
				AdditiveOperator.PLUS);
		return createAssignment(varDef, right);
	}
	
	public static LogicalRelationExpression checkRelOfVar(String varName, int number, RelationalOperator operator) {
		return checkRelOfVar(createVarDef(varName, "integer"), number, operator);
	}
	
	public static LogicalRelationExpression checkRelOfVar(VariableDefinition varDef, int number, RelationalOperator operator) {
		return createLogRelExpression(createElRef(varDef), createIntExpression(number), operator);
	}
	
	//-----------------------------------------------------
	// Conditional
	//-----------------------------------------------------
	
	public static ConditionalExpression createCondExpression(Expression condition, Expression trueCase, Expression falseCase) {
		ConditionalExpression result = ExpressionsFactory.eINSTANCE.createConditionalExpression();
		result.setCondition(condition);
		result.setTrueCase(trueCase);
		result.setFalseCase(falseCase);
		return result;
	}
	
	//------------------------------------------------------------------
	// Logical Operations
	//-------------------------------------------------
	public static LogicalRelationExpression createLogRelExpression(Expression left, Expression right, RelationalOperator operator) {
		LogicalRelationExpression result = ExpressionsFactory.eINSTANCE.createLogicalRelationExpression();
		result.setLeftOperand(left);
		result.setRightOperand(right);
		result.setOperator(operator);
		return result;
	}
	
	public static Expression createLogExpression(Expression left, Expression right, LogicalOperator operator) {
		switch(operator.getValue()) {
		case LogicalOperator.AND_VALUE : return createLogAndExpression(left, right);
		case LogicalOperator.OR_VALUE : return createLogOrExpression(left, right);
		case LogicalOperator.NOT_VALUE : return createLogNotExpression(left);
		}
		return null;
	}
	
	public static LogicalOrExpression createLogOrExpression(Expression left, Expression right) {
		LogicalOrExpression result = ExpressionsFactory.eINSTANCE.createLogicalOrExpression();
		result.setLeftOperand(left);
		result.setRightOperand(right);
		return result;
	}
	
	
	public static LogicalAndExpression createLogAndExpression(Expression left, Expression right) {
		LogicalAndExpression result = ExpressionsFactory.eINSTANCE.createLogicalAndExpression();
		result.setLeftOperand(left);
		result.setRightOperand(right);
		return result;
	}
	
	
	public static LogicalNotExpression createLogNotExpression(Expression left) {
		LogicalNotExpression result = ExpressionsFactory.eINSTANCE.createLogicalNotExpression();
		result.setOperand(left);
		return result;
	}
	

	
	//------------------------------------------------------------------
	// VarDef
	//-------------------------------------------------
	public static VariableDefinition createVarDef(String varName, String type) {
		return createVarDef(varName, VarType.ofYSCT(type));
	}
	
	public static VariableDefinition createVarDef(String varName, VarType type) {
		return StextTestFactory._createVariableDefinition(varName, type.convertToType(), null);
	}
	
	public static VariableDefinition createVarDef(String varName, VarType type, Scope scope) {
		return StextTestFactory._createVariableDefinition(varName, type.convertToType(), scope);
	}
	
	public static VariableDefinition createVarDef(String varName, int number) {
		return createVarDef(varName, null, number);
	}
	
	public static VariableDefinition createVarDef(String varName, Scope scope, int number) {
		return StextTestFactory._createVariableDefinition(varName, VarType.INT.convertToType(), scope, createIntExpression(number));
	}
	
	public static VariableDefinition createVarDef(String varName, boolean b) {
		return createVarDef(varName, null, b);
	}
	
	public static VariableDefinition createVarDef(String varName, Scope scope, boolean b) {
		return StextTestFactory._createVariableDefinition(varName, VarType.BOOL.convertToType(), scope, createBoolExpression(b));
	}
	
	public static ElementReferenceExpression createElRef(String varName, String type) {
		return createElRef(createVarDef(varName, type));
	}
	
	public static ElementReferenceExpression createElRef(VariableDefinition varDef) {
		return StextTestFactory._createElementReferenceExpression(varDef);
	}
	
	//------------------------------------------------------------------
	// Assignment
	//-------------------------------------------------
	public static AssignmentExpression createAssignment(VariableDefinition varDef, Expression assignment) {
		return createAssignment(varDef, assignment, AssignmentOperator.ASSIGN);
	}
	
	public static AssignmentExpression createAssignment(VariableDefinition varDef, Expression assignment, AssignmentOperator operator) {
		AssignmentExpression result = ExpressionsFactory.eINSTANCE.createAssignmentExpression();
		ElementReferenceExpression elRef = createElRef(varDef);
		result.setVarRef(elRef);
		result.setExpression(assignment);
		result.setOperator(operator);
		return result;
	}
	
	
	//------------------------------------------------------------------
	// Numerical Operations
	//-------------------------------------------------
	public static NumericalAddSubtractExpression createNumPlusMinusExpression(Expression left, Expression right, AdditiveOperator operator) {
		NumericalAddSubtractExpression result = ExpressionsFactory.eINSTANCE.createNumericalAddSubtractExpression();
		result.setOperator(operator);
		result.setLeftOperand(left);
		result.setRightOperand(right);
		return result;
	}
	
	public static NumericalMultiplyDivideExpression createNumMulDivExpression(Expression left, Expression right, MultiplicativeOperator operator) {
		NumericalMultiplyDivideExpression result = ExpressionsFactory.eINSTANCE.createNumericalMultiplyDivideExpression();
		result.setOperator(operator);
		result.setLeftOperand(left);
		result.setRightOperand(right);
		return result;
	}
	
	public static NumericalUnaryExpression createNumUnaryExpression(Expression left, UnaryOperator operator) {
		NumericalUnaryExpression result = ExpressionsFactory.eINSTANCE.createNumericalUnaryExpression();
		result.setOperator(operator);
		result.setOperand(left);
		return result;
	}
	

	//------------------------------------------------------------------
	// Literals
	//-------------------------------------------------
	public static PrimitiveValueExpression createIntExpression(int number) {
		return StextTestFactory._createValue(number);
	}
	
	public static PrimitiveValueExpression createBoolExpression(boolean b) {
		return StextTestFactory._createValue(b);
	}
	
	//------------------------------------------------------------------
	// Copy Expression
	//-------------------------------------------------
	public static Expression copy(Expression expression) {
		return null;
	}

	//------------------------------------------------------------------
	// Parenthesized
	//-------------------------------------------------
	public static Expression createParenthesizedExpression(Expression expression) {
		ParenthesizedExpression par = ExpressionsFactory.eINSTANCE.createParenthesizedExpression();
		par.setExpression(expression);
		return par;

		
	}
	
}
