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
import com.yakindu.sct.se.engine.strategy.Strategy.Construction;
import com.yakindu.sct.se.engine.strategy.Strategy.ContextSolving;
import com.yakindu.sct.se.engine.strategy.Strategy.CycleElimination;
import com.yakindu.sct.se.engine.strategy.Strategy.IndependenceOptimization;
import com.yakindu.sct.se.engine.strategy.Strategy.SatDistribution;
import com.yakindu.sct.se.engine.strategy.Strategy.Solver;
import com.yakindu.sct.se.test.util.ExpressionCreatorUtil;
import com.yakindu.sct.se.test.util.StextTestFactory;
import com.yakindu.sct.se.test.util.TransitionCreatorUtil;

public class ITCycleEliminationTest {

	private List<Vertex> v_nr_lb_off = new ArrayList<>();
	private List<Transition> t_nr_lb_off = new ArrayList<>();

	private List<Vertex> v_nr_lb_0 = new ArrayList<>();
	private List<Transition> t_nr_lb_0 = new ArrayList<>();

	private List<Vertex> v_nr_lb_1 = new ArrayList<>();
	private List<Transition> t_nr_lb_1 = new ArrayList<>();

	private List<Vertex> v_nr_lb_5 = new ArrayList<>();
	private List<Transition> t_nr_lb_5 = new ArrayList<>();

	@Test
	public void test() {
		List<List<Vertex>> listV = new ArrayList<>();
		listV = Arrays.asList(v_nr_lb_off, v_nr_lb_0, v_nr_lb_1, v_nr_lb_5);
		List<List<Transition>> listR = new ArrayList<>();
		listR = Arrays.asList(t_nr_lb_off, t_nr_lb_0, t_nr_lb_1, t_nr_lb_5);
		Statechart testChart = createTestChart();

		List<CycleElimination> listMode = Arrays.asList(CycleElimination.OFF, CycleElimination.PREVENTIVE,
				CycleElimination.CONSERVATIVE_ONE, CycleElimination.CONSERVATIVE_FIVE);
		for (int i = 0; i < listV.size(); i++) {

			if(i!=3) {
				continue;
			}
			// arrange
			Strategy strategy = new Strategy(listMode.get(i), SatDistribution.BINARY_SEARCH, ContextSolving.ON,
					IndependenceOptimization.ON, Construction.DFS, Solver.NATIVE_Z3, 1, 1800);

			// act
			Analysis analysis = Analysis.analyse(testChart, strategy);
			AnalysisResult analysisResult = analysis.getFinishedResult();

			System.out.println(analysisResult.printResult(true));
			// assert
			assertThat(analysisResult.getNonReachableVertices()).containsExactlyInAnyOrderElementsOf(listV.get(i));
			assertThat(analysisResult.getNonReachableTransitions()).containsExactlyInAnyOrderElementsOf(listR.get(i));

		}

	}

	private Statechart createTestChart() {
		// Nicht erreichbar: State D,
		//
		// finde heraus, nur ein Pfad existiert

		Statechart statechart = createStatechart("Statechart");
		Region region = createRegion(statechart, "Region");
		Entry entry = createEntry(null, "entry", region);

		State stateA = createState(region, "State_Circle_1");
		State stateB = createState(region, "State_Circle_2");
		State stateC = createState(region, "State_Circle_3");

		State stateD = createState(region, "State Exit_A");
		State stateE = createState(region, "State Exit_B");
		State stateF = createState(region, "State Exit_C");

		Scope scope = createScope(statechart);

		createTransition(entry, stateA);

		VariableDefinition varX = ExpressionCreatorUtil.createVarDef("x", scope, 0);
		VariableDefinition varY = ExpressionCreatorUtil.createVarDef("y", scope, 0);
		EventDefinition event1 = _createEventDefinition("evt_1", scope);

		LogicalRelationExpression xG5 = ExpressionCreatorUtil.checkRelOfVar(varX, 5, RelationalOperator.GREATER_EQUAL);
		LogicalRelationExpression xL5 = ExpressionCreatorUtil.checkRelOfVar(varX, 5, RelationalOperator.SMALLER);
		LogicalRelationExpression xL0 = ExpressionCreatorUtil.checkRelOfVar(varX, 0, RelationalOperator.SMALLER);
		LogicalRelationExpression yL5 = ExpressionCreatorUtil.checkRelOfVar(varY, 5, RelationalOperator.GREATER);

		AssignmentExpression xAddOne = ExpressionCreatorUtil.addIntToVar(varX, 1);
		AssignmentExpression xAddOne_2 = ExpressionCreatorUtil.addIntToVar(varX, 1);

		// Transitions
		Transition t1 = TransitionCreatorUtil.createTransition(stateA, stateB, Arrays.asList(xAddOne));
		Transition t2 = TransitionCreatorUtil.createTransition(stateB, stateC, xL5, Arrays.asList(xAddOne_2));
		Transition t3 = TransitionCreatorUtil.createTransition(stateC, stateA);

		Transition aToD = TransitionCreatorUtil.createTransition(stateA, stateD, xL0);
		Transition bToE = TransitionCreatorUtil.createTransition(stateB, stateE, xG5);
		Transition cToF = TransitionCreatorUtil.createTransition(stateC, stateF, yL5);

		TransitionCreatorUtil.addEventTrigger(t1, event1);
		TransitionCreatorUtil.addEventTrigger(t2, event1);
		TransitionCreatorUtil.addEventTrigger(t3, event1);

		addAsNR(stateD, true, false, false, true);
		addAsNR(aToD, true, false, false, true);

		addAsNR(stateF, true, true, true, true);
		addAsNR(cToF, true, true, true, true);

		return statechart;
	}

	private void addAsNR(Vertex vertex, boolean off, boolean zero, boolean one, boolean ten) {
		if (off) {
			v_nr_lb_off.add(vertex);
		}
		if (zero) {
			v_nr_lb_0.add(vertex);
		}
		if (one) {
			v_nr_lb_1.add(vertex);

		}
		if (ten) {
			v_nr_lb_5.add(vertex);

		}
	}

	private void addAsNR(Transition transition, boolean off, boolean zero, boolean one, boolean ten) {
		if (off) {
			t_nr_lb_off.add(transition);
		}
		if (zero) {
			t_nr_lb_0.add(transition);
		}
		if (one) {
			t_nr_lb_1.add(transition);

		}
		if (ten) {
			t_nr_lb_5.add(transition);

		}
	}

	public static Scope createScope(Statechart statechart) {
		InternalScope scope = getOrCreateInternalScope(statechart);
		StextTestFactory._createEventDefinition("operate", scope);
		return scope;
	}

}
