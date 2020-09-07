package com.yakindu.sct.se.test.benchmark;

import static com.yakindu.sct.se.test.util.SGraphTestFactory.createEntry;
import static com.yakindu.sct.se.test.util.SGraphTestFactory.createRegion;
import static com.yakindu.sct.se.test.util.SGraphTestFactory.createState;
import static com.yakindu.sct.se.test.util.SGraphTestFactory.createStatechart;
import static com.yakindu.sct.se.test.util.StextTestFactory._createEventDefinition;
import static com.yakindu.sct.se.test.util.StextTestFactory.getOrCreateInternalScope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

import com.yakindu.sct.se.test.util.ExpressionCreatorUtil;
import com.yakindu.sct.se.test.util.StextTestFactory;
import com.yakindu.sct.se.test.util.TransitionCreatorUtil;

public class BenchmarkStatechart {

		public static Statechart createBenchmarkChart(int size) {

		// Nicht erreichbar: State D,
		//
		// finde heraus, nur ein Pfad existiert

		Statechart statechart = createStatechart("Statechart");
		Region region = createRegion(statechart, "Region");
		Entry entry = createEntry(null, "entry", region);

		Scope scope = createScope(statechart);

		List<VariableDefinition> variables = new ArrayList<>();
		EventDefinition event1 = _createEventDefinition("evt_1", scope);

		//VariableDefinition varX = PremadeExpression.createVarDef("x", scope, 0);
		for (int i = 1; i <= size; i++) {
			//variables.add(varX);
			variables.add(ExpressionCreatorUtil.createVarDef("x_" + i, scope, 0));
		}

		Vertex fromLastList = entry;
		for (int outerCounter = 1; outerCounter <= size; outerCounter++) {
			List<State> innerList = new ArrayList<>();
			for (int innerCounter = 1; innerCounter <= size; innerCounter++) {
				innerList.add(createState(region, "State_" + outerCounter + "_" + innerCounter));

			}

			for (int i = 0; i < innerList.size(); i++) {
				for (int j = 0; j < innerList.size(); j++) {
					if (i + 1 == j) {
						Transition trans = TransitionCreatorUtil.createTransition(innerList.get(i), innerList.get(j),
								Arrays.asList(ExpressionCreatorUtil.addOneToVar(variables.get(outerCounter - 1 % size))));
						TransitionCreatorUtil.addEventTrigger(trans, event1);
					} else if ((j == 0 && i == innerList.size() - 1)) {

						TransitionCreatorUtil.createTransition(innerList.get(i), innerList.get(j),
								ExpressionCreatorUtil.createLogRelExpression(
										ExpressionCreatorUtil.createElRef(variables.get((outerCounter - 1) % size)),
										ExpressionCreatorUtil.createIntExpression((size * (outerCounter)) % (7 * size)),
										//PremadeExpression.createIntExpression((size * (outerCounter))),
										RelationalOperator.SMALLER),
								Arrays.asList(ExpressionCreatorUtil.addOneToVar(variables.get(outerCounter - 1 % size))));
					}

				}

			}

			if (outerCounter != 1) {
				TransitionCreatorUtil.createTransition(fromLastList, innerList.get(0),
						ExpressionCreatorUtil.createLogRelExpression(
								ExpressionCreatorUtil.createElRef(variables.get((outerCounter - 2) % size)),
								ExpressionCreatorUtil.createIntExpression((size * (outerCounter - 1)) % (7 * size)),
								//PremadeExpression.createIntExpression((size * (outerCounter - 1))),
								RelationalOperator.GREATER_EQUAL));
			} else {
				TransitionCreatorUtil.createTransition(fromLastList, innerList.get(0));
			}

			// set random exit state
			// int random2 = ((int)(Math.random() * innerList.size()))%innerList.size();
			fromLastList = innerList.get(innerList.size() - 1);

		}

		TransitionCreatorUtil.createTransition(fromLastList, createState(region, "State_End"),
				ExpressionCreatorUtil.createLogRelExpression(ExpressionCreatorUtil.createElRef(variables.get(size - 1)),
						ExpressionCreatorUtil.createIntExpression((size * size) % (5 * size)),
						RelationalOperator.GREATER_EQUAL));
		return statechart;

	}


	public static Scope createScope(Statechart statechart) {
		InternalScope scope = getOrCreateInternalScope(statechart);
		StextTestFactory._createEventDefinition("operate", scope);
		return scope;
	}

}
