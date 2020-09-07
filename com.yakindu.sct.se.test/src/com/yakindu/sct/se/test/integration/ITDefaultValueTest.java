package com.yakindu.sct.se.test.integration;

import static com.yakindu.sct.se.test.util.SGraphTestFactory.createEntry;
import static com.yakindu.sct.se.test.util.SGraphTestFactory.createRegion;
import static com.yakindu.sct.se.test.util.SGraphTestFactory.createState;
import static com.yakindu.sct.se.test.util.SGraphTestFactory.createStatechart;
import static com.yakindu.sct.se.test.util.StextTestFactory.getOrCreateInternalScope;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.yakindu.base.expressions.expressions.LogicalRelationExpression;
import org.yakindu.base.expressions.expressions.RelationalOperator;
import org.yakindu.sct.model.sgraph.Entry;
import org.yakindu.sct.model.sgraph.Region;
import org.yakindu.sct.model.sgraph.Scope;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.sgraph.Vertex;
import org.yakindu.sct.model.stext.stext.InternalScope;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import com.yakindu.sct.se.analysis.Analysis;
import com.yakindu.sct.se.analysis.AnalysisResult;
import com.yakindu.sct.se.engine.strategy.Strategy;
import com.yakindu.sct.se.model.VarType;
import com.yakindu.sct.se.test.util.ExpressionCreatorUtil;
import com.yakindu.sct.se.test.util.StextTestFactory;
import com.yakindu.sct.se.test.util.TransitionCreatorUtil;

public class ITDefaultValueTest {

	private List<Vertex> nonReachableVerticesExpected = new ArrayList<>();
	private List<Transition> nonReachableTransitionsExpected = new ArrayList<>();

	@Test
	public void test() {
		// arrange
		Statechart testChart = createTestChart();

		// act
		Analysis analysis = Analysis.analyse(testChart, Strategy.DEFAULT);
		AnalysisResult analysisResult = analysis.getFinishedResult();

		// assert
		assertThat(analysisResult.getNonReachableVertices())
				.containsExactlyInAnyOrderElementsOf(nonReachableVerticesExpected);
		assertThat(analysisResult.getNonReachableTransitions())
				.containsExactlyInAnyOrderElementsOf(nonReachableTransitionsExpected);
	}

	private Statechart createTestChart() {
		Statechart statechart = createStatechart("Statechart");
		Region region = createRegion(statechart, "Region");
		Entry entry = createEntry(null, "entry", region);
		State stateA = createState(region, "defaultValueInt");
		State stateB = createState(region, "defaultValueBool");

		Scope scope = createScope(statechart);

		VariableDefinition varA = ExpressionCreatorUtil.createVarDef("a", VarType.INT, scope);
		VariableDefinition varX = ExpressionCreatorUtil.createVarDef("x", VarType.BOOL, scope);

		LogicalRelationExpression unEq0 = ExpressionCreatorUtil.createLogRelExpression(ExpressionCreatorUtil.createElRef(varA),
				ExpressionCreatorUtil.createIntExpression(0), RelationalOperator.NOT_EQUALS);

		Transition eToA = TransitionCreatorUtil.createTransition(entry, stateA, unEq0);
		Transition eToB = TransitionCreatorUtil.createTransition(entry, stateB, ExpressionCreatorUtil.createElRef(varX));

		addAsNR(stateA);
		addAsNR(stateB);
		addAsNR(eToA);
		addAsNR(eToB);

		return statechart;
	}

	private void addAsNR(Vertex vertex) {
		nonReachableVerticesExpected.add(vertex);
	}

	private void addAsNR(Transition transition) {
		nonReachableTransitionsExpected.add(transition);
	}

	public static Scope createScope(Statechart statechart) {
		InternalScope scope = getOrCreateInternalScope(statechart);
		StextTestFactory._createEventDefinition("operate", scope);
		return scope;
	}

}
