package com.yakindu.sct.se.test.util;

import static com.yakindu.sct.se.test.util.StextTestFactory._createReactionTrigger;
import static com.yakindu.sct.se.test.util.StextTestFactory._createRegularEventSpec;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.yakindu.base.types.Expression;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.sgraph.Vertex;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.Guard;
import org.yakindu.sct.model.stext.stext.ReactionEffect;
import org.yakindu.sct.model.stext.stext.ReactionTrigger;

public class TransitionCreatorUtil {

	
	//------------------------------------
	// Transition
	//----------------------------------------------------
	public static Transition createTransition(Vertex source, Vertex goal) {
		return createTransition(source, goal, null, null);
	}

	public static Transition createTransition(Vertex source, Vertex goal,
			Expression guardExpression) {
		return createTransition(source, goal, guardExpression, null);
	}

	public static Transition createTransition(Vertex source, Vertex goal, List<Expression> effect) {
		return createTransition(source, goal, null, effect);
	}

	public static Transition createTransition(Vertex source, Vertex goal,
			Expression guardExpression, List<Expression> effect) {
		Transition result = SGraphTestFactory.createTransition(source, goal);

		ReactionTrigger reactionTrigger = _createReactionTrigger(null);
		
		if (guardExpression != null) {
			// guard
			Guard guard = StextTestFactory.createGuardExpression(EcoreUtil.copy(guardExpression));
			reactionTrigger.setGuard(guard);

		}
		
		result.setTrigger(reactionTrigger);

		// effect
		if (effect != null) {
			ReactionEffect reactionEffect = StextTestFactory._createReactionEffect(null);
			EList<Expression> actionList = reactionEffect.getActions();
			for (Expression expr : effect) {
				actionList.add(EcoreUtil.copy(expr));
			}

			result.setEffect(reactionEffect);
		}
		result.setSpecification(source.getName() + " -> "+ goal.getName());
		return result;

	}
	
	public static void addEventTrigger(Transition transition, EventDefinition event) {
		ReactionTrigger trigger = ((ReactionTrigger) transition.getTrigger());
		_createRegularEventSpec(event, trigger);
	}
	

	public static Transition changeGuardOfTransition(Transition trans, Expression guardExpression) {
		Guard guard = StextTestFactory.createGuardExpression(EcoreUtil.copy(guardExpression));
		((ReactionTrigger)trans.getTrigger()).setGuard(guard);
		return trans;
	}
	
	public static Transition addEffectToTransition(Transition trans, Expression expression) {
		((ReactionEffect)trans.getEffect()).getActions().add(EcoreUtil.copy(expression));
		return trans;
	}
	
	public static Transition changeEffectOfTransition(Transition trans, List<Expression> expressionList) {
		((ReactionEffect)trans.getEffect()).getActions().clear();
		for (Expression expr : expressionList) {
			((ReactionEffect)trans.getEffect()).getActions().add(EcoreUtil.copy(expr));
		}
		return trans;
	}
	
	/*
	 * public static Transition copy(Transition trans) {
	 * 
	 * createTransition(trans.getSource(), trans.getTarget(), guardExpression,
	 * effect) }
	 */
	//------------------------------------
	// Guard
	//----------------------------------------------------
	
	
	
	
	
	
	
}