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
import org.yakindu.base.expressions.expressions.MultiplicativeOperator;
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

public class ITDivisionRoundTest {

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

		State stateC = createState(region, "State pos_pos");
		State stateD = createState(region, "State pos_neg");
		State stateE = createState(region, "State neg_pos");
		State stateF = createState(region, "State neg_neg");

		State stateG = createState(region, "State NR_pos_pos");
		State stateH = createState(region, "State NR_pos_neg");
		State stateI = createState(region, "State NR_neg_pos");
		State stateJ = createState(region, "State NR_neg_neg");

		Scope scope = createScope(statechart);
		statechart.getScopes().add(scope);

		createTransition(entry, stateA);

		VariableDefinition varX_1 = ExpressionCreatorUtil.createVarDef("x_1", scope, 0);
		VariableDefinition varX_2 = ExpressionCreatorUtil.createVarDef("x_2", scope, 0);
		VariableDefinition varX_3 = ExpressionCreatorUtil.createVarDef("x_3", scope, 0);
		VariableDefinition varX_4 = ExpressionCreatorUtil.createVarDef("x_4", scope, 0);

		EventDefinition event1 = _createEventDefinition("evt_1", scope);
		EventDefinition event2 = _createEventDefinition("evt_2", scope);
		EventDefinition event3 = _createEventDefinition("evt_3", scope);
		EventDefinition event4 = _createEventDefinition("evt_4", scope);
		EventDefinition event5 = _createEventDefinition("evt_5", scope);
		EventDefinition event6 = _createEventDefinition("evt_6", scope);
		EventDefinition event7 = _createEventDefinition("evt_7", scope);

		AssignmentExpression posDPos = ExpressionCreatorUtil.createAssignment(varX_1,
				ExpressionCreatorUtil.createNumMulDivExpression(ExpressionCreatorUtil.createIntExpression(1),
						ExpressionCreatorUtil.createIntExpression(3), MultiplicativeOperator.DIV));

		AssignmentExpression posDNeg = ExpressionCreatorUtil.createAssignment(varX_2,
				ExpressionCreatorUtil.createNumMulDivExpression(ExpressionCreatorUtil.createIntExpression(1),
						ExpressionCreatorUtil.createIntExpression(-3), MultiplicativeOperator.DIV));

		AssignmentExpression negDPos = ExpressionCreatorUtil.createAssignment(varX_3,
				ExpressionCreatorUtil.createNumMulDivExpression(ExpressionCreatorUtil.createIntExpression(-1),
						ExpressionCreatorUtil.createIntExpression(3), MultiplicativeOperator.DIV));

		AssignmentExpression negDNeg = ExpressionCreatorUtil.createAssignment(varX_4,
				ExpressionCreatorUtil.createNumMulDivExpression(ExpressionCreatorUtil.createIntExpression(-1),
						ExpressionCreatorUtil.createIntExpression(-3), MultiplicativeOperator.DIV));

		LogicalRelationExpression x_1NEQ0 = ExpressionCreatorUtil.createLogRelExpression(
				ExpressionCreatorUtil.createElRef(varX_1), ExpressionCreatorUtil.createIntExpression(1),
				RelationalOperator.EQUALS);

		LogicalRelationExpression x_2NEQ0 = ExpressionCreatorUtil.createLogRelExpression(
				ExpressionCreatorUtil.createElRef(varX_2), ExpressionCreatorUtil.createIntExpression(-1),
				RelationalOperator.EQUALS);

		LogicalRelationExpression x_3NEQ0 = ExpressionCreatorUtil.createLogRelExpression(
				ExpressionCreatorUtil.createElRef(varX_3), ExpressionCreatorUtil.createIntExpression(-1),
				RelationalOperator.EQUALS);

		LogicalRelationExpression x_4NEQ0 = ExpressionCreatorUtil.createLogRelExpression(
				ExpressionCreatorUtil.createElRef(varX_4), ExpressionCreatorUtil.createIntExpression(1),
				RelationalOperator.EQUALS);

		LogicalRelationExpression x_1EQ0 = ExpressionCreatorUtil.createLogRelExpression(
				ExpressionCreatorUtil.createElRef(varX_1), ExpressionCreatorUtil.createIntExpression(0),
				RelationalOperator.EQUALS);

		LogicalRelationExpression x_2EQ0 = ExpressionCreatorUtil.createLogRelExpression(
				ExpressionCreatorUtil.createElRef(varX_2), ExpressionCreatorUtil.createIntExpression(0),
				RelationalOperator.EQUALS);

		LogicalRelationExpression x_3EQ0 = ExpressionCreatorUtil.createLogRelExpression(
				ExpressionCreatorUtil.createElRef(varX_3), ExpressionCreatorUtil.createIntExpression(0),
				RelationalOperator.EQUALS);

		LogicalRelationExpression x_4EQ0 = ExpressionCreatorUtil.createLogRelExpression(
				ExpressionCreatorUtil.createElRef(varX_4), ExpressionCreatorUtil.createIntExpression(0),
				RelationalOperator.EQUALS);

		Transition aToA = TransitionCreatorUtil.createTransition(stateA, stateB,
				Arrays.asList(posDPos, posDNeg, negDPos, negDNeg));
		Transition bToC = TransitionCreatorUtil.createTransition(stateB, stateC, x_1EQ0);
		Transition bToD = TransitionCreatorUtil.createTransition(stateB, stateD, x_2EQ0);
		Transition bToE = TransitionCreatorUtil.createTransition(stateB, stateE, x_3EQ0);
		Transition bToF = TransitionCreatorUtil.createTransition(stateB, stateF, x_4EQ0);

		Transition bToG = TransitionCreatorUtil.createTransition(stateB, stateG, x_1NEQ0);
		Transition bToH = TransitionCreatorUtil.createTransition(stateB, stateH, x_2NEQ0);
		Transition bToI = TransitionCreatorUtil.createTransition(stateB, stateI, x_3NEQ0);
		Transition bToJ = TransitionCreatorUtil.createTransition(stateB, stateJ, x_4NEQ0);

		TransitionCreatorUtil.addEventTrigger(bToC, event1);
		TransitionCreatorUtil.addEventTrigger(bToD, event2);
		TransitionCreatorUtil.addEventTrigger(bToE, event3);
		TransitionCreatorUtil.addEventTrigger(bToF, event4);
		TransitionCreatorUtil.addEventTrigger(bToG, event5);
		TransitionCreatorUtil.addEventTrigger(bToH, event6);
		TransitionCreatorUtil.addEventTrigger(bToI, event7);

		addAsNR(stateG);
		addAsNR(stateH);
		addAsNR(stateI);
		addAsNR(stateJ);
		addAsNR(bToG);
		addAsNR(bToH);
		addAsNR(bToI);
		addAsNR(bToJ);
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
