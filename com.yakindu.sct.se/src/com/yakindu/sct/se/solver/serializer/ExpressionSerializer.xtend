package com.yakindu.sct.se.solver.serializer

import org.yakindu.base.expressions.expressions.AssignmentExpression
import org.yakindu.base.expressions.expressions.BitwiseOperator
import org.yakindu.base.types.Expression
import org.yakindu.base.types.Enumerator
import org.yakindu.base.expressions.expressions.ParenthesizedExpression
import org.yakindu.base.expressions.expressions.impl.PrimitiveValueExpressionImpl
import org.yakindu.base.expressions.expressions.Literal
import org.yakindu.base.expressions.expressions.ElementReferenceExpression
import org.yakindu.sct.model.stext.stext.VariableDefinition
import org.yakindu.base.expressions.expressions.ArgumentExpression
import org.yakindu.base.expressions.expressions.ConditionalExpression
import org.yakindu.base.expressions.expressions.UnaryExpression
import org.yakindu.base.expressions.expressions.BinaryExpression
import org.yakindu.base.expressions.expressions.NullLiteral
import org.yakindu.base.expressions.expressions.StringLiteral
import org.yakindu.base.expressions.expressions.HexLiteral
import org.yakindu.base.expressions.expressions.FloatLiteral
import org.yakindu.base.expressions.expressions.DoubleLiteral
import org.yakindu.base.expressions.expressions.IntLiteral
import org.yakindu.base.expressions.expressions.BoolLiteral
import org.yakindu.base.expressions.expressions.ShiftOperator
import org.yakindu.base.expressions.expressions.AssignmentOperator
import org.yakindu.base.expressions.expressions.MultiplicativeOperator
import org.yakindu.base.expressions.expressions.AdditiveOperator
import org.yakindu.base.expressions.expressions.LogicalOperator
import org.yakindu.base.expressions.expressions.RelationalOperator
import com.yakindu.sct.se.symbolicExecutionExtension.SequenceBlock
import java.util.List
import org.eclipse.xtend.lib.annotations.Accessors
import org.yakindu.sct.model.stext.stext.EventDefinition
import org.yakindu.base.expressions.expressions.UnaryOperator

class ExpressionSerializer {

	private boolean customFunctions;
	
	  new(boolean customFunctions) {
    	this.customFunctions = customFunctions
 	 }
 	 
	
	def dispatch String serialize(Expression expression) {
		'''«expression.serializeExpression.prettyString»'''
	}
	


	def dispatch String serializeExpression(Expression expression){
		'''test'''
	}
	
	def dispatch String serializeExpression(EventDefinition expression){
		'''test'''
	}
	
	def dispatch String serializeExpression(SequenceBlock it){
		'''
		«FOR e: expressions»
		«e.serializeExpression»;
		«ENDFOR»
		'''
	}
	
	
	//TODO: could not be working properly...test it
	def dispatch String serializeExpression(AssignmentExpression it){
		'''(«operator.serializeOperator» «varRef.serializeExpression» «expression.serializeExpression»)'''
	}

	def dispatch String serializeExpression(ParenthesizedExpression it){
		'''«expression.serializeExpression»'''
	}
	
	def dispatch String serializeExpression(PrimitiveValueExpressionImpl it){
		'''«value.serializeLiteral»'''
	}
	
	def dispatch String serializeExpression(ElementReferenceExpression it){
		'''«reference.serializeExpression»'''
	}
	
	def dispatch String serializeExpression(VariableDefinition it){
		'''«name»'''
	}
	
	def dispatch String serializeExpression(ArgumentExpression it){
		throw new IllegalArgumentException("Isn't implemented")
	}
	
	def dispatch String serializeExpression(ConditionalExpression it){
		'''(ite «condition.serializeExpression» «trueCase.serializeExpression» «falseCase.serializeExpression»)'''
	}
	
	def dispatch String serializeExpression(UnaryExpression it){
		'''(«operator.serializeOperator» «operand.serializeExpression»)'''
	}
	
	def dispatch String serializeExpression(BinaryExpression it){
		if(operator == MultiplicativeOperator.DIV) {
			if(customFunctions){
				'''(yakDiv «leftOperand.serializeExpression» «rightOperand.serializeExpression»)'''
			}else {
			//'''(ite (or (>= «leftOperand.serializeExpression» 0) (= 0 (% «leftOperand.serializeExpression» «rightOperand.serializeExpression»))) (/ «leftOperand.serializeExpression» «rightOperand.serializeExpression») (ite (> «rightOperand.serializeExpression» 0) (+ (/ «leftOperand.serializeExpression» «rightOperand.serializeExpression») 1) (- (/ «leftOperand.serializeExpression» «rightOperand.serializeExpression») 1)))'''
			'''(«operator.serializeOperator» «leftOperand.serializeExpression» «rightOperand.serializeExpression»)'''
			}
		} else if(operator.value == RelationalOperator.NOT_EQUALS_VALUE){
			'''(not (= «leftOperand.serializeExpression» «rightOperand.serializeExpression»))'''
		} else {
		'''(«operator.serializeOperator» «leftOperand.serializeExpression» «rightOperand.serializeExpression»)'''
		}
	}
	
	//--------------------------------------------------
	// Literals
	//--------------------------------------------------
	def dispatch serializeLiteral(Literal it){
		
	}
	
	def dispatch serializeLiteral(BoolLiteral it){
		'''«value»'''
	}
	
	def dispatch serializeLiteral(IntLiteral it){
		if(it.value < 0){
			'''(- «-value»)'''
		} else {
		'''«value»'''		
		}
	}
	
	def dispatch serializeLiteral(DoubleLiteral it){
		'''«value»'''		
	}
	
			//TODO: Could be working incorrectly
	def dispatch serializeLiteral(FloatLiteral it){
		'''«value»'''		
	}
	
			//TODO: Could be working incorrectly
	def dispatch serializeLiteral(HexLiteral it){
		'''«value»'''		
	}
	
	def dispatch serializeLiteral(StringLiteral it){
		'''«value»'''		
	}
	
	def dispatch serializeLiteral(NullLiteral it){	
		throw new IllegalArgumentException("Isn't implemented")	
	}
	
	
	//--------------------------------------------------
	// Operators
	//--------------------------------------------------
	
	//TODO Operatoren hier
	def dispatch serializeOperator(Enumerator it){
		throw new IllegalArgumentException("Implement me")
	}
	
	def dispatch serializeOperator(BitwiseOperator it){
		'''«toString»'''				
	}
	
	def dispatch serializeOperator(RelationalOperator it){
		switch value {
			case RelationalOperator.EQUALS_VALUE: '''='''
			case RelationalOperator.NOT_EQUALS_VALUE: throw new NullPointerException("distinct value..")
			default: '''«toString»'''	
		}			
	}
	
	def dispatch serializeOperator(LogicalOperator it){
		switch value {
		case LogicalOperator.AND_VALUE: '''and'''
		case LogicalOperator.OR_VALUE: '''or'''
		case LogicalOperator.NOT_VALUE: '''not'''
		}			
	}
	
	def dispatch serializeOperator(AdditiveOperator it){
		'''«toString»'''				
	}
	
	def dispatch serializeOperator(MultiplicativeOperator it){
		switch value {
		case MultiplicativeOperator.DIV_VALUE: '''div'''
		case MultiplicativeOperator.MOD_VALUE: 
			if(customFunctions){
				'''mod'''
			} else {
				'''%'''
			}
		default: '''«toString»'''
		}				
	}
	
	def dispatch serializeOperator(AssignmentOperator it){
		switch value {
		case AssignmentOperator.ASSIGN_VALUE: '''«it»'''
		default : throw new IllegalArgumentException("Isn't implemented")
		}				
	}
	
	def dispatch serializeOperator(UnaryOperator it){
		'''«toString»'''				
	}
	
	def dispatch serializeOperator(ShiftOperator it){
		'''«toString»'''				
	}
	
	def private String prettyString(String it){
		'''«it.replace("#", "-hash-").replace("$", "-dollar-")»'''
	}
	
}
