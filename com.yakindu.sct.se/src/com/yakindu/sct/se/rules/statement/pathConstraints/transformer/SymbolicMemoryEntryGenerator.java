package com.yakindu.sct.se.rules.statement.pathConstraints.transformer;

import java.util.List;
import java.util.Random;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import com.yakindu.sct.se.util.ExpressionUtil;
import com.yakindu.sct.se.util.ExpressionCreatorUtil;
import com.yakindu.sct.se.util.StaticAnalyserUtil;

/**
 * Used to generate names for symbolic representations
 * 
 * @author jwielage
 *
 */
public class SymbolicMemoryEntryGenerator {

	private int breakLoopCounter = 0;
	private String randomInfix = "a";
	private String suffixForVariableGeneration = "_" + randomInfix + "_see_bLc_" + breakLoopCounter + "_";

	private boolean extraSafetyCheck = true;

	public SymbolicMemoryEntryGenerator(Statechart statechart) {
		checkForSuffixOverlappingInStatechart(statechart);
		updateSuffix();
	}

	/**
	 * Tests if there is a variable in the statechart that has a name containing the
	 * used suffix to indicate iterations of variables in ssa form. Randomizes the
	 * String as long as there is a matching
	 * 
	 * @param statechart
	 */
	private void checkForSuffixOverlappingInStatechart(Statechart statechart) {

		int leftLimit = 97; // letter 'a'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 1;
		Random random = new Random();

		boolean[] foundOccurence = new boolean[1];
		foundOccurence[0] = true;

		while (foundOccurence[0]) {
			foundOccurence[0] = false;

			randomInfix = random.ints(leftLimit, rightLimit + 1)
					.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength)
					.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();

			StaticAnalyserUtil.findAllInTree(statechart, VariableDefinition.class, false, var -> {
				if (var.getName().contains("_" + randomInfix + "_see_bLc_")) {
					foundOccurence[0] = true;
				}
			});

			// every loop the length gets higher to increase the chance of finding random
			// string
			targetStringLength++;
		}
	}

	public VariableDefinition[] breakMe(List<VariableDefinition> eliminatedVarDefs) {
		breakLoop();

		if (eliminatedVarDefs == null) {
			return null;
		}
		return eliminatedVarDefs.stream().map(varDef -> generateAndAddNextNumberedAndAnnotatedVarDef(varDef))
				.toArray(VariableDefinition[]::new);
	}

	public VariableDefinition generateAndAddNextNumberedAndAnnotatedVarDef(VariableDefinition varDefToGenerateFrom) {

		if (varDefToGenerateFrom == null) {
			return null;
		}

		VariableDefinition possibleAnnotation = ExpressionUtil.getAnnotationFromVarDef(varDefToGenerateFrom,
				ExpressionUtil.ORIGINAL_VARIABLE_DECLARATION);

		if (possibleAnnotation == null) {
			possibleAnnotation = varDefToGenerateFrom;
		}

		VariableDefinition result = EcoreUtil.copy(varDefToGenerateFrom);

		result.setName(generateNextVarDefNameFromCurrent(varDefToGenerateFrom.getName()));
		ExpressionCreatorUtil.addAnnotation(result, ExpressionUtil.ORIGINAL_VARIABLE_DECLARATION,
				ExpressionCreatorUtil.createElRef(possibleAnnotation));

		return result;
	}

	private String generateNextVarDefNameFromCurrent(String currentVarName) {
		if (currentVarName == null) {
			return null;
		}
		if (currentVarName.isEmpty()) {
			return suffixForVariableGeneration + "1";
		}

		String[] array = currentVarName.split(suffixForVariableGeneration);

		if (array.length <= 1) {
			// originalVarDef
			return currentVarName + suffixForVariableGeneration + "1";
		} else if (array.length >= 3) {
			throw new NullPointerException("error creating next VariableName");
		}
		String result = array[0] + suffixForVariableGeneration + String.valueOf(Integer.valueOf(array[1]) + 1);
		return result;

	}

	private void breakLoop() {
		breakLoopCounter++;
		updateSuffix();
	}

	private void updateSuffix() {
		this.suffixForVariableGeneration = "_" + randomInfix + "_see_bLc_" + breakLoopCounter + "_";
	}

	public VariableDefinition generateFromUnitializedVarDef(VariableDefinition unspoiledVarDef) {
		safetyCheck(unspoiledVarDef, true);
		VariableDefinition result = generateAndAddNextNumberedAndAnnotatedVarDef(unspoiledVarDef);

		ExpressionCreatorUtil.addAnnotation(result, ExpressionUtil.UNINITIALIZED_VARIABLE,
				ExpressionCreatorUtil.createElRef(unspoiledVarDef));
		return result;
	}

	private void safetyCheck(VariableDefinition varDef, boolean shouldBeUnspoiled) {
		if (extraSafetyCheck && (shouldBeUnspoiled ^ ExpressionUtil.getAnnotationFromVarDef(varDef,
				ExpressionUtil.ORIGINAL_VARIABLE_DECLARATION) == null)) {

			StringBuilder errorMessage = new StringBuilder();

			errorMessage.append("VaribleDefinition ");
			errorMessage.append(varDef.getName());
			errorMessage.append(" should be ");
			if (shouldBeUnspoiled) {
				errorMessage.append("un");
			}
			errorMessage.append("spoiled");
			errorMessage.append(", but is not!");

			throw new NullPointerException(errorMessage.toString());
		}
	}

}
