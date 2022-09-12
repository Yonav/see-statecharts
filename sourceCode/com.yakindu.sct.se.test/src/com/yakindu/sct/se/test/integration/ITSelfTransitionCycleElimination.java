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
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.yakindu.base.expressions.expressions.AssignmentExpression;
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

public class ITSelfTransitionCycleElimination {

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

	public Statechart createTestChart() {

		Statechart statechart = createStatechart("Statechart");
		Region region = createRegion(statechart, "Region");
		Entry entry = createEntry(null, "entry", region);
		State stateA = createState(region, "State A");
		State stateB = createState(region, "State B");

		State stateD = createState(region, "State D");
		State stateF = createState(region, "State F");

		Scope scope = createScope(statechart);
		statechart.getScopes().add(scope);

		createTransition(entry, stateA);

		VariableDefinition varX = ExpressionCreatorUtil.createVarDef("x", scope, 0);
		VariableDefinition varY = ExpressionCreatorUtil.createVarDef("y", scope, 10);

		EventDefinition event1 = _createEventDefinition("evt_1", scope);

		AssignmentExpression addOneToX = ExpressionCreatorUtil.createAssignment(varX,
				ExpressionCreatorUtil.addIntToVar(varX, 1));
		AssignmentExpression addOneToY = ExpressionCreatorUtil.createAssignment(varY,
				ExpressionCreatorUtil.addIntToVar(varY, 1));

		LogicalRelationExpression yL0 = ExpressionCreatorUtil.createLogRelExpression(ExpressionCreatorUtil.createElRef(varY),
				ExpressionCreatorUtil.createIntExpression(0), RelationalOperator.SMALLER);

		Transition aToA = TransitionCreatorUtil.createTransition(stateA, stateA, Arrays.asList(addOneToX, addOneToY));
		TransitionCreatorUtil.createTransition(stateA, stateB, yL0);

		Transition bToD = TransitionCreatorUtil.createTransition(stateB, stateD,
				ExpressionCreatorUtil.createLogRelExpression(ExpressionCreatorUtil.createElRef(varX),
						ExpressionCreatorUtil.createIntExpression(0), RelationalOperator.SMALLER));

		TransitionCreatorUtil.createTransition(stateB, stateF,
				ExpressionCreatorUtil.createLogRelExpression(ExpressionCreatorUtil.createElRef(varX),
						ExpressionCreatorUtil.createIntExpression(0), RelationalOperator.GREATER_EQUAL));

		TransitionCreatorUtil.addEventTrigger(aToA, event1);

		TransitionCreatorUtil.addEventTrigger(bToD, event1);

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
