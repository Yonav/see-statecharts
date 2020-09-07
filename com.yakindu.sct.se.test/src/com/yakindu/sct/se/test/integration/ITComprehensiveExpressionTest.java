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
import org.yakindu.base.expressions.expressions.LogicalAndExpression;
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
import com.yakindu.sct.se.engine.strategy.Strategy.Construction;
import com.yakindu.sct.se.engine.strategy.Strategy.ContextSolving;
import com.yakindu.sct.se.engine.strategy.Strategy.CycleElimination;
import com.yakindu.sct.se.engine.strategy.Strategy.IndependenceOptimization;
import com.yakindu.sct.se.engine.strategy.Strategy.SatDistribution;
import com.yakindu.sct.se.engine.strategy.Strategy.Solver;
import com.yakindu.sct.se.test.util.ExpressionCreatorUtil;
import com.yakindu.sct.se.test.util.StextTestFactory;
import com.yakindu.sct.se.test.util.TransitionCreatorUtil;

public class ITComprehensiveExpressionTest {

	private List<Vertex> nonReachableVerticesExpected = new ArrayList<>();
	private List<Transition> nonReachableTransitionsExpected = new ArrayList<>();

	@Test
	public void test() {
		List<Strategy> strategies = strategiesToBenchmark();

		for (Strategy strategy : strategies) {
			testWithStrategy(strategy);
		}
	}

	public void testWithStrategy(Strategy strategy) {
		// arrange
		nonReachableTransitionsExpected.clear();
		nonReachableVerticesExpected.clear();
		Statechart testChart = createTestChart();

		// act
		Analysis analysis = Analysis.analyse(testChart, strategy);
		AnalysisResult analysisResult = analysis.getFinishedResult();

		System.out.println(analysisResult.printResult(true));
		// assert
		assertThat(analysisResult.getNonReachableVertices())
				.containsExactlyInAnyOrderElementsOf(nonReachableVerticesExpected);
		assertThat(analysisResult.getNonReachableTransitions())
				.containsExactlyInAnyOrderElementsOf(nonReachableTransitionsExpected);

	}

	private Statechart createTestChart() {
		// Nicht erreichbar: State D,
		//

		Statechart statechart = createStatechart("Statechart");
		Region region = createRegion(statechart, "Region");
		Entry entry = createEntry(null, "entry", region);
		State stateA = createState(region, "State A");
		State stateB = createState(region, "State B");
		State stateC = createState(region, "State C");
		State stateD = createState(region, "State D");
		State stateE = createState(region, "State E");
		State stateF = createState(region, "State F");
		State stateG = createState(region, "State G");
		State stateH = createState(region, "State H");
		State stateJ = createState(region, "State J");

		Scope scope = createScope(statechart);

		createTransition(entry, stateA);

		VariableDefinition varX = ExpressionCreatorUtil.createVarDef("x", scope, 2);
		VariableDefinition varY = ExpressionCreatorUtil.createVarDef("y", scope, 5);
		EventDefinition event1 = _createEventDefinition("evt_1", scope);

		LogicalRelationExpression xG0 = ExpressionCreatorUtil.checkRelOfVar(varX, 0, RelationalOperator.GREATER);
		LogicalRelationExpression xG0_2 = ExpressionCreatorUtil.checkRelOfVar(varX, 0, RelationalOperator.GREATER);
		LogicalRelationExpression xL0 = ExpressionCreatorUtil.checkRelOfVar(varX, 0, RelationalOperator.SMALLER);
		LogicalRelationExpression xL0_2 = ExpressionCreatorUtil.checkRelOfVar(varX, 0, RelationalOperator.SMALLER);
		LogicalRelationExpression xL20 = ExpressionCreatorUtil.checkRelOfVar(varX, 20, RelationalOperator.SMALLER);
		LogicalRelationExpression yL10 = ExpressionCreatorUtil.checkRelOfVar(varY, 10, RelationalOperator.SMALLER);
		LogicalRelationExpression yE10 = ExpressionCreatorUtil.checkRelOfVar(varY, 10, RelationalOperator.EQUALS);
		LogicalRelationExpression yE11 = ExpressionCreatorUtil.checkRelOfVar(varY, 11, RelationalOperator.EQUALS);
		LogicalRelationExpression xE42 = ExpressionCreatorUtil.checkRelOfVar(varX, 42, RelationalOperator.EQUALS);
		LogicalRelationExpression xNE42 = ExpressionCreatorUtil.checkRelOfVar(varX, 42, RelationalOperator.NOT_EQUALS);

		LogicalAndExpression xAnd = ExpressionCreatorUtil.createLogAndExpression(xG0, xL20);

		AssignmentExpression xSetTo0 = ExpressionCreatorUtil.setVarToInt(varX, 0);
		AssignmentExpression xSetTo1 = ExpressionCreatorUtil.setVarToInt(varX, 1);
		AssignmentExpression xSetTo2 = ExpressionCreatorUtil.setVarToInt(varX, 2);
		AssignmentExpression xSetTo4 = ExpressionCreatorUtil.setVarToInt(varX, 4);

		// AssignmentExpression xSetTo10 = PremadeExpression.setVarToInt(varX, 10);
		AssignmentExpression xSetTo10 = ExpressionCreatorUtil.createAssignment(varX,
				ExpressionCreatorUtil.createNumMulDivExpression(ExpressionCreatorUtil.createIntExpression(40),
						ExpressionCreatorUtil.createElRef(varX), MultiplicativeOperator.DIV));

		AssignmentExpression xSetTo42 = ExpressionCreatorUtil.setVarToInt(varX, 42);

		AssignmentExpression xMinOne = ExpressionCreatorUtil.addIntToVar(varX, -1);

		AssignmentExpression ySetTo0 = ExpressionCreatorUtil.setVarToInt(varY, 0);
		AssignmentExpression ySetTo12 = ExpressionCreatorUtil.setVarToInt(varY, 12);
		AssignmentExpression ySetTo42 = ExpressionCreatorUtil.setVarToInt(varY, 42);

		AssignmentExpression yPlusOne = ExpressionCreatorUtil.addIntToVar(varY, 1);
		AssignmentExpression yPlusTwo = ExpressionCreatorUtil.addIntToVar(varY, 2);

		AssignmentExpression yMul = ExpressionCreatorUtil.createAssignment(varY,
				ExpressionCreatorUtil.createNumMulDivExpression(ExpressionCreatorUtil.createIntExpression(40),
						ExpressionCreatorUtil.createElRef(varX), MultiplicativeOperator.DIV));

		AssignmentExpression yMul2 = ExpressionCreatorUtil.createAssignment(varY,
				ExpressionCreatorUtil.createNumMulDivExpression(ExpressionCreatorUtil.createElRef(varX),
						ExpressionCreatorUtil.createIntExpression(2), MultiplicativeOperator.MUL));

		Transition aToE = TransitionCreatorUtil.createTransition(stateA, stateE, xG0_2);
		TransitionCreatorUtil.createTransition(stateA, stateB, xG0);
		TransitionCreatorUtil.createTransition(stateB, stateC, Arrays.asList(xSetTo0, ySetTo0));

		Transition cToD = TransitionCreatorUtil.createTransition(stateC, stateD, yE10, Arrays.asList(xSetTo10));
		TransitionCreatorUtil.createTransition(stateC, stateG, yL10, Arrays.asList(xSetTo2, yPlusTwo));

		Transition dToA = TransitionCreatorUtil.createTransition(stateD, stateA, xL0_2, Arrays.asList(xMinOne));
		Transition dToF = TransitionCreatorUtil.createTransition(stateD, stateF, xAnd, Arrays.asList(yMul));

		TransitionCreatorUtil.createTransition(stateE, stateF, xNE42, Arrays.asList(xSetTo4, ySetTo12));
		Transition eToH = TransitionCreatorUtil.createTransition(stateE, stateH, xE42, Arrays.asList(ySetTo42));

		TransitionCreatorUtil.createTransition(stateF, stateH);

		TransitionCreatorUtil.createTransition(stateG, stateH, yE11, Arrays.asList(yMul2));
		Transition gToJ = TransitionCreatorUtil.createTransition(stateG, stateJ, xL0);
		TransitionCreatorUtil.createTransition(stateG, stateC, yL10, Arrays.asList(yPlusOne, xSetTo1));

		Transition jToH = TransitionCreatorUtil.createTransition(stateJ, stateH, Arrays.asList(xSetTo42, ySetTo42));

		TransitionCreatorUtil.addEventTrigger(aToE, event1);

		addAsNR(stateD);
		addAsNR(stateJ);

		addAsNR(cToD);
		addAsNR(dToA);
		addAsNR(dToF);
		addAsNR(gToJ);
		addAsNR(jToH);
		addAsNR(eToH);

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

	private List<Strategy> strategiesToBenchmark() {
		List<Strategy> result = new ArrayList<>();

		result.add(new Strategy(CycleElimination.OFF, SatDistribution.OLDEST_SEARCH, ContextSolving.ON,
				IndependenceOptimization.ON, Construction.BFS, Solver.NATIVE_Z3, 30, 1800));

		result.add(new Strategy(CycleElimination.OFF, SatDistribution.BINARY_SEARCH, ContextSolving.ON,
				IndependenceOptimization.ON, Construction.BFS, Solver.NATIVE_Z3, 30, 1800));

		result.add(new Strategy(CycleElimination.OFF, SatDistribution.OLDEST_SEARCH, ContextSolving.OFF,
				IndependenceOptimization.ON, Construction.BFS, Solver.NATIVE_Z3, 30, 1800));

		result.add(new Strategy(CycleElimination.OFF, SatDistribution.OLDEST_SEARCH, ContextSolving.ON,
				IndependenceOptimization.OFF, Construction.BFS, Solver.NATIVE_Z3, 30, 1800));

		result.add(new Strategy(CycleElimination.OFF, SatDistribution.OLDEST_SEARCH, ContextSolving.ON,
				IndependenceOptimization.ON, Construction.BFS, Solver.NATIVE_Z3, 30, 1800));

		result.add(new Strategy(CycleElimination.OFF, SatDistribution.OLDEST_SEARCH, ContextSolving.ON,
				IndependenceOptimization.ON, Construction.DFS, Solver.JCONSTRAINTS, 30, 1800));

		return result;

	}

}
