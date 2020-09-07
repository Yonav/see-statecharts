package com.yakindu.sct.se.solver.serializer;

import java.util.ArrayList;
import java.util.List;

import org.yakindu.sct.model.stext.stext.VariableDefinition;

import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.model.VarType;
import com.yakindu.sct.se.solver.OptimizedPathConstraint;

/**
 * Serializes PathConstraints to SMT-LIB2.
 * 
 * @author jwielage
 *
 */
public class SETModel2SMTSolverSerializer {

	public static SETModel2SMTSolverSerializer INSTANCE(boolean customFunctions) {
		return new SETModel2SMTSolverSerializer(customFunctions);
	}

	private final boolean customFunctions;

	private final ExpressionSerializer exprSerializer;

	private final StringBuilder smtLibBuilder = new StringBuilder();

	private SETModel2SMTSolverSerializer(boolean customFunctions) {
		this.customFunctions = customFunctions;
		this.exprSerializer = new ExpressionSerializer(customFunctions);
	}

	private void clearBuilder() {
		smtLibBuilder.setLength(0);
	}

	public String convertToSMTLIB(OptimizedPathConstraint simplified) {
		clearBuilder();

		if (customFunctions) {
			smtLibBuilder.append(yakDivDefinition());
		}
		// add variables
		simplified.getVariables().forEach(var -> {
			smtLibBuilder.append(declareUnaryVarType(var));
		});

		// add assertions
		for (int i = simplified.getAssertions().size() - 1; i >= 0; i--) {
			smtLibBuilder.append(addAsAssert(exprSerializer.serialize(simplified.getAssertions().get(i))));
		}

		smtLibBuilder.append("(check-sat)");

		return smtLibBuilder.toString();
	}

	public String convertToSMTLIB(Node node) {
		clearBuilder();

		List<String> tempSMTStringStorage = new ArrayList<>();

		if (customFunctions) {
			smtLibBuilder.append(yakDivDefinition());
		}

		node.getPathConstraint().consumeUnderlyingList(expr -> {
			tempSMTStringStorage.add(addAsAssert(exprSerializer.serialize(expr)));
		});

		node.getSymbolicMemoryStore().consumeUnderlyingList(varDef -> {
			tempSMTStringStorage.add(declareUnaryVarType(varDef));
		});

		// strings are written in wrong order, dont know if this helps z3
		for (int i = tempSMTStringStorage.size() - 1; i >= 0; i--) {
			smtLibBuilder.append(tempSMTStringStorage.get(i));
		}

		smtLibBuilder.append("(check-sat)");
		return smtLibBuilder.toString();

	}

	private String declareUnaryVarType(VariableDefinition varDef) {
		return "(declare-fun " + varDef.getName() + " () " + VarType.ofYSCT(varDef.getType()).smtLibFormat() + ")";
	}

	private String addAsAssert(String expression) {
		return "(assert " + expression + ")";
	}

	private String yakDivDefinition() {
		StringBuilder yakDiv = new StringBuilder();
		yakDiv.append("(");
		yakDiv.append("define-fun yakDiv ((x Int) (y Int)) Int ");
		yakDiv.append("(");
		yakDiv.append("ite ");
		yakDiv.append("(or (>= x 0) (= 0 (mod x y))) ");
		yakDiv.append("(div x y) ");
		yakDiv.append("(");
		yakDiv.append("ite (> y 0) (+ (div x y) 1) (- (div x y) 1)");
		yakDiv.append(")");
		yakDiv.append(")");
		yakDiv.append(")");

		return yakDiv.toString();
	}

}
