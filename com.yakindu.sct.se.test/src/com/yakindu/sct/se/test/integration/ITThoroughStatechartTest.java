package com.yakindu.sct.se.test.integration;

import static com.yakindu.sct.se.test.util.SGraphTestFactory.createEntry;
import static com.yakindu.sct.se.test.util.SGraphTestFactory.createRegion;
import static com.yakindu.sct.se.test.util.SGraphTestFactory.createState;
import static com.yakindu.sct.se.test.util.SGraphTestFactory.createStatechart;
import static com.yakindu.sct.se.test.util.SGraphTestFactory.createTransition;
import static com.yakindu.sct.se.test.util.StextTestFactory._createEventDefinition;
import static com.yakindu.sct.se.test.util.StextTestFactory._createReactionTrigger;
import static com.yakindu.sct.se.test.util.StextTestFactory.getOrCreateInternalScope;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Test;
import org.yakindu.base.expressions.expressions.AssignmentExpression;
import org.yakindu.base.expressions.expressions.LogicalAndExpression;
import org.yakindu.base.expressions.expressions.LogicalRelationExpression;
import org.yakindu.base.expressions.expressions.MultiplicativeOperator;
import org.yakindu.base.expressions.expressions.RelationalOperator;
import org.yakindu.base.types.Expression;
import org.yakindu.sct.model.sgraph.Entry;
import org.yakindu.sct.model.sgraph.Region;
import org.yakindu.sct.model.sgraph.Scope;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.sgraph.Vertex;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.EventSpec;
import org.yakindu.sct.model.stext.stext.Guard;
import org.yakindu.sct.model.stext.stext.InternalScope;
import org.yakindu.sct.model.stext.stext.LocalReaction;
import org.yakindu.sct.model.stext.stext.ReactionEffect;
import org.yakindu.sct.model.stext.stext.ReactionTrigger;
import org.yakindu.sct.model.stext.stext.RegularEventSpec;
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
import com.yakindu.sct.se.model.VarType;
import com.yakindu.sct.se.test.util.ExpressionCreatorUtil;
import com.yakindu.sct.se.test.util.StextTestFactory;
import com.yakindu.sct.se.test.util.TransitionCreatorUtil;

public class ITThoroughStatechartTest {

	private List<Vertex> nonReachableVerticesExpected = new ArrayList<>();
	private List<Transition> nonReachableTransitionsExpected = new ArrayList<>();

	@Test
	public void testWithStrategy() {
		// arrange
		Strategy strategy = new Strategy(CycleElimination.OFF, SatDistribution.OLDEST_SEARCH, ContextSolving.ON,
				IndependenceOptimization.ON, Construction.BFS, Solver.NATIVE_Z3, 30, 1800);

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
		// finde heraus, nur ein Pfad existiert

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
		State stateN = createState(region, "State N");

		Scope scope = createScope(statechart);

		createTransition(entry, stateA);

		VariableDefinition varA = ExpressionCreatorUtil.createVarDef("a", scope, 0);
		VariableDefinition varB = ExpressionCreatorUtil.createVarDef("b", VarType.INT, scope);
		EventDefinition event1 = _createEventDefinition("evt_1", scope);

		LogicalRelationExpression bGE0 = ExpressionCreatorUtil.checkRelOfVar(varB, 0, RelationalOperator.GREATER_EQUAL);
		LogicalRelationExpression bGE0_2 = ExpressionCreatorUtil.checkRelOfVar(varB, 0, RelationalOperator.GREATER_EQUAL);
		LogicalRelationExpression bL0 = ExpressionCreatorUtil.checkRelOfVar(varB, 0, RelationalOperator.SMALLER);
		LogicalRelationExpression bL0_2 = ExpressionCreatorUtil.checkRelOfVar(varB, 0, RelationalOperator.SMALLER);
		LogicalRelationExpression bG0 = ExpressionCreatorUtil.checkRelOfVar(varB, 0, RelationalOperator.GREATER);
		LogicalRelationExpression bGE10 = ExpressionCreatorUtil.checkRelOfVar(varB, 10, RelationalOperator.GREATER_EQUAL);
		LogicalRelationExpression bL10 = ExpressionCreatorUtil.checkRelOfVar(varB, 10, RelationalOperator.SMALLER);
		LogicalRelationExpression bL10_2 = ExpressionCreatorUtil.checkRelOfVar(varB, 10, RelationalOperator.SMALLER);
		LogicalRelationExpression bNE0 = ExpressionCreatorUtil.checkRelOfVar(varB, 0, RelationalOperator.NOT_EQUALS);
		LogicalRelationExpression bG100 = ExpressionCreatorUtil.checkRelOfVar(varB, 100, RelationalOperator.GREATER);
		LogicalRelationExpression bG5 = ExpressionCreatorUtil.checkRelOfVar(varB, 5, RelationalOperator.GREATER);
		
		
		LogicalRelationExpression aG100 = ExpressionCreatorUtil.checkRelOfVar(varA, 100, RelationalOperator.GREATER);
		LogicalRelationExpression aL0 = ExpressionCreatorUtil.checkRelOfVar(varA, 0, RelationalOperator.SMALLER);

		
		AssignmentExpression bSetTo1 = ExpressionCreatorUtil.setVarToInt(varB, 1);
		AssignmentExpression aSetTo1 = ExpressionCreatorUtil.setVarToInt(varA, 1);
		AssignmentExpression bAdd1 = ExpressionCreatorUtil.addIntToVar(varB, 1);
		AssignmentExpression bAdd1_2 = ExpressionCreatorUtil.addIntToVar(varB, 1);
		AssignmentExpression bDiv = ExpressionCreatorUtil.createAssignment(varB,
				ExpressionCreatorUtil.createNumMulDivExpression(ExpressionCreatorUtil.createIntExpression(-1),
						ExpressionCreatorUtil.createIntExpression(3), MultiplicativeOperator.DIV));

		
		TransitionCreatorUtil.createTransition(stateA, stateB, bGE0);
		Transition aToF = TransitionCreatorUtil.createTransition(stateA, stateF, bL0);
		
		TransitionCreatorUtil.createTransition(stateB, stateC, bL10, Arrays.asList(bAdd1));

		Transition cToN = TransitionCreatorUtil.createTransition(stateC, stateN, bL0_2);
		TransitionCreatorUtil.createTransition(stateC, stateB, bL10_2, Arrays.asList(bAdd1_2));
		

		TransitionCreatorUtil.createTransition(stateB, stateD, bGE10);
		
		TransitionCreatorUtil.createTransition(stateD, stateE, bG5);
		Transition dToF = TransitionCreatorUtil.createTransition(stateD, stateF, bG0);
		
		TransitionCreatorUtil.createTransition(stateE, stateG, bG100, Arrays.asList(bDiv));
		
		Transition gToF_1 = TransitionCreatorUtil.createTransition(stateG, stateF, aG100);
		Transition gToF_2 = TransitionCreatorUtil.createTransition(stateG, stateF, bNE0);
		
		// lR for E
		ReactionTrigger rt_E = _createReactionTrigger(null);
		Guard guard_E = StextTestFactory.createGuardExpression(EcoreUtil.copy(bGE0_2));
		rt_E.setGuard(guard_E);
		StextTestFactory._createAlwaysEventSpec(rt_E);
		
		
		ReactionEffect reactionEffect_E = StextTestFactory._createReactionEffect(null);
		EList<Expression> actionList_E = reactionEffect_E.getActions();
		actionList_E.add(EcoreUtil.copy(bSetTo1));

		
		RegularEventSpec triggerEvt_E = StextTestFactory._createRegularEventSpec(event1, rt_E);
		LocalReaction lr_E = StextTestFactory._createLocalReaction(stateE, triggerEvt_E);
		lr_E.setEffect(reactionEffect_E);
		lr_E.setTrigger(rt_E);
		
		// lR for G
		ReactionTrigger rt_G = _createReactionTrigger(null);
		Guard guard_G = StextTestFactory.createGuardExpression(EcoreUtil.copy(aL0));
		rt_G.setGuard(guard_G);
		StextTestFactory._createAlwaysEventSpec(rt_G);
				
				
		ReactionEffect reactionEffect_G = StextTestFactory._createReactionEffect(null);
		EList<Expression> actionList_G = reactionEffect_G.getActions();
		actionList_G.add(EcoreUtil.copy(aSetTo1));

				
		RegularEventSpec triggerEvt_G = StextTestFactory._createRegularEventSpec(event1, rt_G);
		LocalReaction lr_G = StextTestFactory._createLocalReaction(stateG, triggerEvt_G);
		lr_G.setEffect(reactionEffect_G);
		lr_G.setTrigger(rt_G);
		//TransitionCreatorUtil.addEventTrigger(cToN, event1);

		addAsNR(stateF);
		addAsNR(stateN);

		addAsNR(aToF);
		addAsNR(cToN);
		addAsNR(dToF);
		addAsNR(gToF_1);
		addAsNR(gToF_2);

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
