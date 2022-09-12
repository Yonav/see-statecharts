package com.yakindu.sct.se.test.integration;

import static com.yakindu.sct.se.test.util.SGraphTestFactory.createEntry;
import static com.yakindu.sct.se.test.util.SGraphTestFactory.createRegion;
import static com.yakindu.sct.se.test.util.SGraphTestFactory.createState;
import static com.yakindu.sct.se.test.util.SGraphTestFactory.createStatechart;
import static com.yakindu.sct.se.test.util.SGraphTestFactory.createTransition;
import static com.yakindu.sct.se.test.util.StextTestFactory._createEventDefinition;
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
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.InternalScope;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import com.yakindu.sct.se.analysis.Analysis;
import com.yakindu.sct.se.analysis.AnalysisResult;
import com.yakindu.sct.se.engine.strategy.Strategy;
import com.yakindu.sct.se.test.util.ExpressionCreatorUtil;
import com.yakindu.sct.se.test.util.StextTestFactory;
import com.yakindu.sct.se.test.util.TransitionCreatorUtil;

public class ITPriorityTransitionTest {

	private List<Vertex> nonReachableVerticesExpected = new ArrayList<>();
	private List<Transition> nonReachableTransitionsExpected = new ArrayList<>();

	@Test
	public void test() {
		// arrange
		Statechart testChart = createTestChart();

		// act
		Analysis analysis = Analysis.analyse(testChart, Strategy.DEFAULT);
		AnalysisResult analysisResult = analysis.getFinishedResult();

		System.out.println(analysisResult.printResult(true));
		// assert
		assertThat(analysisResult.getNonReachableVertices()).containsExactlyElementsOf(nonReachableVerticesExpected);
		assertThat(analysisResult.getNonReachableTransitions())
				.containsExactlyElementsOf(nonReachableTransitionsExpected);

	}

	private Statechart createTestChart() {
		Statechart statechart = createStatechart("Statechart");
		Region region = createRegion(statechart, "Region");
		Entry entry = createEntry(null, "entry", region);
		State stateInit = createState(region, "init");
		State stateA = createState(region, "prio_1");
		State stateB = createState(region, "prio_2");
		State stateC = createState(region, "prio_3");
		State stateD = createState(region, "prio_4");
		State stateE = createState(region, "prio_5");

		Scope scope = createScope(statechart);
		createTransition(entry, stateInit);

		VariableDefinition varA = ExpressionCreatorUtil.createVarDef("a", scope, 2);

		EventDefinition event1 = _createEventDefinition("evt_1", null);
		EventDefinition event2 = _createEventDefinition("evt_2", null);

		LogicalRelationExpression iTo1 = ExpressionCreatorUtil.createLogRelExpression(ExpressionCreatorUtil.createElRef(varA),
				ExpressionCreatorUtil.createIntExpression(0), RelationalOperator.GREATER);

		LogicalRelationExpression iTo2 = ExpressionCreatorUtil.createLogRelExpression(ExpressionCreatorUtil.createElRef(varA),
				ExpressionCreatorUtil.createIntExpression(1), RelationalOperator.GREATER);

		LogicalRelationExpression iTo3 = ExpressionCreatorUtil.createLogRelExpression(ExpressionCreatorUtil.createElRef(varA),
				ExpressionCreatorUtil.createIntExpression(0), RelationalOperator.GREATER);

		LogicalRelationExpression iTo4 = ExpressionCreatorUtil.createLogRelExpression(ExpressionCreatorUtil.createElRef(varA),
				ExpressionCreatorUtil.createIntExpression(0), RelationalOperator.GREATER);

		LogicalRelationExpression iTo5 = ExpressionCreatorUtil.createLogRelExpression(ExpressionCreatorUtil.createElRef(varA),
				ExpressionCreatorUtil.createIntExpression(0), RelationalOperator.GREATER);

		Transition eToA = TransitionCreatorUtil.createTransition(stateInit, stateA, iTo1);
		Transition eToB = TransitionCreatorUtil.createTransition(stateInit, stateB, iTo2);
		Transition eToC = TransitionCreatorUtil.createTransition(stateInit, stateC, iTo3);
		Transition eToD = TransitionCreatorUtil.createTransition(stateInit, stateD, iTo4);
		Transition eToE = TransitionCreatorUtil.createTransition(stateInit, stateE, iTo5);

		TransitionCreatorUtil.addEventTrigger(eToA, event1);
		TransitionCreatorUtil.addEventTrigger(eToC, event2);
		TransitionCreatorUtil.addEventTrigger(eToB, event1);

		addAsNR(stateB);
		addAsNR(stateE);
		addAsNR(eToB);
		addAsNR(eToE);

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
