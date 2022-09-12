package com.yakindu.sct.se.rules.transition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.yakindu.base.expressions.expressions.ElementReferenceExpression;
import org.yakindu.base.expressions.expressions.LogicalNotExpression;
import org.yakindu.base.expressions.expressions.LogicalOrExpression;
import org.yakindu.base.types.Expression;
import org.yakindu.sct.model.stext.stext.AlwaysEvent;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.EventSpec;
import org.yakindu.sct.model.stext.stext.RegularEventSpec;
import org.yakindu.sct.model.stext.stext.impl.AlwaysEventImpl;

import com.yakindu.sct.se.util.ExpressionCreatorUtil;

/**
 * Used for calculating Expressions based on transition priorites
 * 
 * @author jwielage
 *
 */
public class TransitionPriorityHelper {

	private final Map<Class<? extends EventSpec>, Expression> triggerMap = new HashMap<>();
	private final Map<EventDefinition, Expression> eventMap = new HashMap<>();

	public static TransitionPriorityHelper INSTANCE = new TransitionPriorityHelper();

	private TransitionPriorityHelper() {
	}

	public void addToMap(List<EventSpec> eventSpecList, Expression expression) {
		// wenn always existiert, brauchen die anderen nicht hinzugefügt werden
		if (expression == null) {
			expression = ExpressionCreatorUtil.createBoolExpression(true);
		}
		if (eventSpecList == null || eventSpecList.isEmpty()
				|| eventSpecList.stream().anyMatch(e -> e instanceof AlwaysEvent)) {
			// no trigger set
			addToMap(AlwaysEventImpl.class, expression);
		} else {
			// triggers are given
			List<EventSpec> alreadyAdded = new ArrayList<>();
			for (EventSpec eventSpec : eventSpecList) {

				// looking for duplicates
				if (alreadyAdded.stream().anyMatch(e -> EcoreUtil.equals(e, eventSpec))) {
					continue;
				}
				alreadyAdded.add(eventSpec);
				// duplicates should be handled...
				if (eventSpec instanceof RegularEventSpec) {
					addToMap(((RegularEventSpec) eventSpec), expression);
				} else {
					addToMap(eventSpec.getClass(), expression);
				}

			}
		}
	}

	public void addToMap(Class<? extends EventSpec> reactionClass, Expression expression) {
		// if there is no previous value -> add as negation
		// if there is a previous value -> add negation as conjunction
		LogicalNotExpression negatedExpr = ExpressionCreatorUtil.createLogNotExpression(EcoreUtil.copy(expression));
		triggerMap.merge(reactionClass, negatedExpr,
				(oldExpr, newExpr) -> ExpressionCreatorUtil.createLogAndExpression(EcoreUtil.copy(oldExpr), negatedExpr));
	}

	public void addToMap(RegularEventSpec event, Expression expression) {
		// if there is no previous value -> add as negation
		// if there is a previous value -> add negation as conjunction
		EventDefinition eventDef = ((EventDefinition) ((ElementReferenceExpression) event.getEvent()).getReference());
		LogicalNotExpression negatedExpr = ExpressionCreatorUtil.createLogNotExpression(EcoreUtil.copy(expression));
		eventMap.merge(eventDef, negatedExpr,
				(oldExpr, newExpr) -> ExpressionCreatorUtil.createLogAndExpression(EcoreUtil.copy(oldExpr), negatedExpr));
	}

	private EventDefinition getEventDef(EventSpec eventSpec) {
		if (eventSpec instanceof RegularEventSpec) {
			return ((EventDefinition) ((ElementReferenceExpression) ((RegularEventSpec) eventSpec).getEvent())
					.getReference());
		}
		return null;
	}

	// gathers all relevant experssions and returns as disjunction
	public Expression getExpressionsThroughTriggers(List<EventSpec> eventSpecList) {
		if (eventSpecList == null) {
			return triggerMap.get(AlwaysEventImpl.class);
		}

		List<Expression> gatheredExpressions = null;
		int duplicates = 0;

		boolean alwaysEventExistsInMap = false;

		// handle always event
		Expression alwaysExpression = triggerMap.get(AlwaysEventImpl.class);
		if (alwaysExpression != null) {
			gatheredExpressions = new ArrayList<>();
			gatheredExpressions.add(alwaysExpression);
			alwaysEventExistsInMap = true;
		}

		// add all relevant expressions to a list
		for (EventSpec eventSpec : eventSpecList) {
			Expression expr;
			if (eventSpec instanceof RegularEventSpec) {
				expr = eventMap.get(getEventDef(eventSpec));
			} else {
				expr = triggerMap.get(eventSpec.getClass());
			}

			if (expr != null) {
				if (gatheredExpressions == null) {
					gatheredExpressions = new ArrayList<>();
				}

				if (!gatheredExpressions.contains(expr)) {
					gatheredExpressions.add(expr);
				} else {
					duplicates++;
				}

			}
		}

		// generate the result from the list
		if (gatheredExpressions == null || gatheredExpressions.isEmpty()
				|| gatheredExpressions.size() + duplicates < eventSpecList.size()) {
			if (!alwaysEventExistsInMap) {
				return null;
			}
		}
		if (gatheredExpressions.size() == 1) {
			return EcoreUtil.copy(gatheredExpressions.get(0));
		}

		LogicalOrExpression result = ExpressionCreatorUtil.createLogOrExpression(EcoreUtil.copy(gatheredExpressions.get(0)),
				EcoreUtil.copy(gatheredExpressions.get(1)));

		for (int i = 2; i < gatheredExpressions.size(); i++) {
			result = ExpressionCreatorUtil.createLogOrExpression(result, EcoreUtil.copy(gatheredExpressions.get(i)));
		}
		return result;
	}

	public void reset() {
		triggerMap.clear();
		eventMap.clear();
	}

}
