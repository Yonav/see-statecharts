package com.yakindu.sct.se.rules.transition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.yakindu.base.types.Expression;
import org.yakindu.sct.model.stext.stext.AlwaysEvent;
import org.yakindu.sct.model.stext.stext.EntryEvent;
import org.yakindu.sct.model.stext.stext.EventSpec;
import org.yakindu.sct.model.stext.stext.ExitEvent;
import org.yakindu.sct.model.stext.stext.ReactionTrigger;
import org.yakindu.sct.model.stext.stext.StextFactory;

import com.yakindu.sct.se.util.ExpressionCreatorUtil;

/**
 * Used for calculating which local reactions should spawn which expressions. Used by the transitionRule
 * @author jwielage
 *
 */
public class LocalReactionHelper {

	public static LocalReactionHelper INSTANCE = new LocalReactionHelper();

	private final Map<List<EventSpec>, Expression> triggerAndEventMap = new LinkedHashMap<>();

	private LocalReactionHelper() {

	}

	public void addLReaction(List<EventSpec> eList, Expression expression) {
		if (eList == null || eList.isEmpty()) {
			eList = Arrays.asList(createAlwaysEventSpec(null));
		}

		triggerAndEventMap.put(eList, EcoreUtil.copy(expression));
	}

	public List<Expression> generateAllPermutations() {
		List<Expression> result = new ArrayList<>();
		// collect all possible events in a list
		List<EventSpec> uniqueSpecs = new ArrayList<>();

		for (List<EventSpec> eventSpecList : triggerAndEventMap.keySet()) {
			for (EventSpec eventSpec : eventSpecList) {
				if ((eventSpec instanceof ExitEvent) || (eventSpec instanceof EntryEvent)) {
					continue;
				}
				if (!uniqueSpecs.stream().anyMatch(e -> EcoreUtil.equals(e, eventSpec))) {
					uniqueSpecs.add(eventSpec);
				}
			}
		}

		if (uniqueSpecs.isEmpty()) {
			return null;
		}

		// generate expressions for all possible subsets
		List<EventSpec> collectorList = new ArrayList<>();
		int allMasks = (1 << uniqueSpecs.size());
		for (int i = 1; i < allMasks; i++) {
			for (int j = 0; j < uniqueSpecs.size(); j++) {
				if ((i & (1 << j)) > 0) {
					// The j-th element is used
					collectorList.add(uniqueSpecs.get(j));
				}
			}

			Expression expr = getForList(collectorList);
			if (expr != null) {
				result.add(expr);
			}
			collectorList.clear();
		}

		return result;
	}

	public Expression getForList(List<EventSpec> eList) {
		List<Expression> collectorList = new ArrayList<>();
		for (Entry<List<EventSpec>, Expression> entry : triggerAndEventMap.entrySet()) {
			boolean shouldBeAdded = false;
			for (EventSpec eventSpec : eList) {
				if (entry.getKey().stream().anyMatch(ev -> EcoreUtil.equals(ev, eventSpec))) {
					shouldBeAdded = true;
					break;
				}
			}

			if (shouldBeAdded) {
				collectorList.add(entry.getValue());
			}

		}

		if (collectorList.isEmpty()) {
			return null;
		}
		return ExpressionCreatorUtil.createSequenceBlock(collectorList);
	}
	
	public static AlwaysEvent createAlwaysEventSpec(ReactionTrigger rt) {
		AlwaysEvent always = StextFactory.eINSTANCE.createAlwaysEvent();
		if (rt != null)
			rt.getTriggers().add(always);
		return always;
	}

	public void reset() {
		triggerAndEventMap.clear();
	}

}
