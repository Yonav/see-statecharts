package com.yakindu.sct.se.util;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.yakindu.base.base.NamedElement;
import org.yakindu.base.expressions.expressions.AdditiveOperator;
import org.yakindu.base.expressions.expressions.AssignmentExpression;
import org.yakindu.base.expressions.expressions.AssignmentOperator;
import org.yakindu.base.expressions.expressions.BoolLiteral;
import org.yakindu.base.expressions.expressions.ConditionalExpression;
import org.yakindu.base.expressions.expressions.ElementReferenceExpression;
import org.yakindu.base.expressions.expressions.ExpressionsFactory;
import org.yakindu.base.expressions.expressions.IntLiteral;
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
import org.yakindu.base.types.AnnotatableElement;
import org.yakindu.base.types.Annotation;
import org.yakindu.base.types.AnnotationType;
import org.yakindu.base.types.Expression;
import org.yakindu.base.types.TypesFactory;
import org.yakindu.sct.model.sgraph.Scope;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import com.yakindu.sct.se.model.VarType;
import com.yakindu.sct.se.symbolicExecutionExtension.SequenceBlock;
import com.yakindu.sct.se.symbolicExecutionExtension.SymbolicExecutionExtensionFactory;

/**
 * Utility class for creation of expressions
 * @author jwielage
 *
 */
public class ExpressionCreatorUtil {

	// -----------------------------------------------------
	// SequenceBlock
	// -----------------------------------------------------

	public static SequenceBlock createSequenceBlock(List<Expression> expressions) {
		SequenceBlock result = SymbolicExecutionExtensionFactory.eINSTANCE.createSequenceBlock();
		if (expressions == null) {
			throw new IllegalStateException();
		}
		EList<Expression> expressionList = result.getExpressions();
		for (Expression expression : expressions) {
			expressionList.add(EcoreUtil.copy(expression));
		}
		return result;
	}

	public static SequenceBlock createSequenceBlockOfEffects(List<Expression> expressions) {
		SequenceBlock result = SymbolicExecutionExtensionFactory.eINSTANCE.createSequenceBlock();
		if (expressions == null) {
			throw new IllegalStateException();
		}
		EList<Expression> expressionList = result.getExpressions();
		for (Expression expression : expressions) {
			if (shouldBeAddedToEffectSequenceBlock(expression)) {
				expressionList.add(EcoreUtil.copy(expression));
			}
		}
		return result;
	}

	public static boolean shouldBeAddedToEffectSequenceBlock(Expression expr) {
		if (expr instanceof LogicalRelationExpression || expr instanceof LogicalAndExpression
				|| expr instanceof LogicalOrExpression || expr instanceof LogicalNotExpression
				|| expr instanceof PrimitiveValueExpression || expr instanceof ElementReferenceExpression) {
			return false;
		}
		return true;
	}

	public static void addAnnotation(VariableDefinition varDef, String annotationName, Expression expressionToAdd) {
		Annotation annotation = TypesFactory.eINSTANCE.createAnnotation();
		annotation.getArguments().add(expressionToAdd);
		AnnotationType at = TypesFactory.eINSTANCE.createAnnotationType();
		at.setName(annotationName);
		annotation.setType(at);
		addAnnotation(varDef, annotation);
	}

	public static void addAnnotation(VariableDefinition varDef, Annotation annotation) {
		AnnotatableElement annotableElement = TypesFactory.eINSTANCE.createAnnotatableElement();
		EList<Annotation> annotationList = annotableElement.getAnnotations();
		annotationList.addAll(varDef.getAnnotations());
		annotationList.add(annotation);
		varDef.setAnnotationInfo(annotableElement);
	}

	// -----------------------------------------------------
	// Conditional
	// -----------------------------------------------------

	public static ConditionalExpression createCondExpression(Expression condition, Expression trueCase,
			Expression falseCase) {
		ConditionalExpression result = ExpressionsFactory.eINSTANCE.createConditionalExpression();
		result.setCondition(condition);
		result.setTrueCase(trueCase);
		result.setFalseCase(falseCase);
		return result;
	}

	// ------------------------------------------------------------------
	// Logical Operations
	// -------------------------------------------------
	public static LogicalRelationExpression createLogRelExpression(Expression left, Expression right,
			RelationalOperator operator) {
		LogicalRelationExpression result = ExpressionsFactory.eINSTANCE.createLogicalRelationExpression();
		result.setLeftOperand(left);
		result.setRightOperand(right);
		result.setOperator(operator);
		return result;
	}

	public static Expression createLogExpression(Expression left, Expression right, LogicalOperator operator) {
		switch (operator.getValue()) {
		case LogicalOperator.AND_VALUE:
			return createLogAndExpression(left, right);
		case LogicalOperator.OR_VALUE:
			return createLogOrExpression(left, right);
		case LogicalOperator.NOT_VALUE:
			return createLogNotExpression(left);
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


	public static ElementReferenceExpression createElRef(VariableDefinition varDef) {
		return _createElementReferenceExpression(varDef);
	}

	// ------------------------------------------------------------------
	// Assignment
	// -------------------------------------------------
	public static AssignmentExpression createAssignment(VariableDefinition varDef, Expression assignment) {
		return createAssignment(varDef, assignment, AssignmentOperator.ASSIGN);
	}

	public static AssignmentExpression createAssignment(VariableDefinition varDef, Expression assignment,
			AssignmentOperator operator) {
		AssignmentExpression result = ExpressionsFactory.eINSTANCE.createAssignmentExpression();
		ElementReferenceExpression elRef = createElRef(varDef);
		result.setVarRef(elRef);
		result.setExpression(assignment);
		result.setOperator(operator);
		return result;
	}

	
	public static AssignmentExpression createAssignment(Expression elRef, Expression assignment) {
		return createAssignment(elRef, assignment, AssignmentOperator.ASSIGN);
	}

	public static AssignmentExpression createAssignment(Expression elRef, Expression assignment,
			AssignmentOperator operator) {
		AssignmentExpression result = ExpressionsFactory.eINSTANCE.createAssignmentExpression();
		result.setVarRef(elRef);
		result.setExpression(assignment);
		result.setOperator(operator);
		return result;
	}

	// ------------------------------------------------------------------
	// Literals
	// -------------------------------------------------
	public static PrimitiveValueExpression createIntExpression(int number) {
		return _createValue(number);
	}

	public static PrimitiveValueExpression createBoolExpression(boolean b) {
		return _createValue(b);
	}


	
	public static ElementReferenceExpression _createElementReferenceExpression(
			NamedElement target) {
		ElementReferenceExpression referenceExpression = ExpressionsFactory.eINSTANCE
				.createElementReferenceExpression();
		referenceExpression.setReference(target);
		return referenceExpression;
	}
	
	public static PrimitiveValueExpression _createValue(int i) {
		PrimitiveValueExpression assignment = ExpressionsFactory.eINSTANCE
				.createPrimitiveValueExpression();
		IntLiteral intLit = ExpressionsFactory.eINSTANCE.createIntLiteral();
		intLit.setValue(i);
		assignment.setValue(intLit);
		return assignment;
	}

	public static PrimitiveValueExpression _createValue(boolean b) {
		PrimitiveValueExpression pve = ExpressionsFactory.eINSTANCE.createPrimitiveValueExpression();
		BoolLiteral boolLit = ExpressionsFactory.eINSTANCE.createBoolLiteral();
		boolLit.setValue(b);
		pve.setValue(boolLit);
		return pve;
	}
	
}
