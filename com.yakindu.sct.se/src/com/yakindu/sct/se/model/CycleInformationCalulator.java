package com.yakindu.sct.se.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.jgrapht.alg.connectivity.GabowStrongConnectivityInspector;
import org.jgrapht.graph.DirectedPseudograph;
import org.yakindu.base.types.Expression;
import org.yakindu.sct.model.sgraph.Effect;
import org.yakindu.sct.model.sgraph.Reaction;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.sgraph.Vertex;
import org.yakindu.sct.model.stext.stext.ReactionEffect;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import com.yakindu.sct.se.util.ExpressionUtil;

/**
 * Calculates all information regarding strongly connected components and
 * bundles them in a map.
 * 
 * @author jwielage
 *
 */
public class CycleInformationCalulator {

	private final Map<Vertex, BundledSCCInformation> vertexToSCCInfo = new HashMap<>();

	public static CycleInformationCalulator calculateFromStatechart(Statechart statechart) {
		return new CycleInformationCalulator(statechart);
	}

	private CycleInformationCalulator(Statechart statechart) {
		calculateSCCInformation(statechart);
	}

	// ======================================================================================
	// calculate all relevant information for a scc and add to map
	// ======================================================================================
	private void calculateSCCInformation(Statechart statechart) {
		List<Set<Vertex>> sCCs = calculateReducedSCCs(statechart);

		for (Set<Vertex> scc : sCCs) {
			if (isCycleInducing(scc)) {
				List<Reaction> reactionsForScc = calculateReactions(scc);
				BundledSCCInformation sccBundleInfo = new BundledSCCInformation(scc, reactionsForScc,
						calulateVariables(reactionsForScc));

				for (Vertex vertex : scc) {
					vertexToSCCInfo.put(vertex, sccBundleInfo);
				}
			} else if (!scc.isEmpty()) {
				Vertex vertex = scc.iterator().next();
				BundledSCCInformation sccBundleInfo = new BundledSCCInformation(null, null, null);
				vertexToSCCInfo.put(vertex, sccBundleInfo);
			}
		}

		// test();
	}

	// ======================================================================================
	// calculate reachables...
	// ======================================================================================

	/*
	 * public void test() { for (Entry<Vertex, BundledSCCInformation> entry :
	 * vertexToSCCInfo.entrySet()) { // calculate all dependent vertices
	 * entry.getValue().addReachableVertices(entry.getKey()); for (Transition
	 * outTrans : entry.getKey().getOutgoingTransitions()) {
	 * 
	 * if (entry.getValue().getVerticesInCycle() != null &&
	 * !entry.getValue().getVerticesInCycle().contains(outTrans.getTarget())) {
	 * entry.getValue().addDependentVertex(outTrans.getTarget()); } }
	 * 
	 * }
	 * 
	 * boolean shouldContinueSearching = true; while(shouldContinueSearching) {
	 * shouldContinueSearching = false;
	 * 
	 * for (Entry<Vertex, BundledSCCInformation> entry : vertexToSCCInfo.entrySet())
	 * {
	 * 
	 * if(entry.getValue().getDependentVertices() != null &&
	 * !entry.getValue().getDependentVertices().isEmpty()) { shouldContinueSearching
	 * = true; Iterator<Vertex> iterator =
	 * entry.getValue().getDependentVertices().iterator(); while
	 * (iterator.hasNext()) { Vertex vertex = iterator.next();
	 * if(entry.getValue().getReachableVertices().contains(vertex)) {
	 * iterator.remove(); } else
	 * if(vertexToSCCInfo.get(vertex).getDependentVertices() == null ||
	 * vertexToSCCInfo.get(vertex).getDependentVertices().isEmpty()){
	 * entry.getValue().addReachableVertices(vertexToSCCInfo.get(vertex));
	 * iterator.remove(); } } } else { // is finished!! } } } }
	 */

	// ======================================================================================
	// calculate reactions for a scc
	// ======================================================================================
	private List<Reaction> calculateReactions(Set<Vertex> scc) {
		List<Reaction> result = new ArrayList<>();

		for (Vertex vertex : scc) {
			// add transitions
			for (Transition outTrans : vertex.getOutgoingTransitions()) {
				if (scc.contains(outTrans.getTarget())) {
					result.add(outTrans);
				}
			}
			// add local reactions
			if (vertex instanceof State) {
				for (Reaction reaction : ((State) vertex).getLocalReactions()) {
					result.add(reaction);
				}
			}
		}
		return result;
	}

	// ======================================================================================
	// calculate variables for a scc
	// only assigness in assignments get in this list, they are the ones that get
	// changed
	// ======================================================================================
	private List<VariableDefinition> calulateVariables(List<Reaction> reactionsForScc) {
		List<VariableDefinition> uniqueVariables = new ArrayList<>();

		// for keeping created objects at a minimum
		boolean shouldBeAdded = true;
		Effect effect = null;
		EList<Expression> effectList = null;
		VariableDefinition assignee = null;

		for (Reaction reaction : reactionsForScc) {
			effect = reaction.getEffect();
			effectList = null;

			if (effect != null && effect instanceof ReactionEffect) {
				effectList = ((ReactionEffect) effect).getActions();
			}

			if (effectList != null) {
				for (Expression expr : effectList) {
					assignee = ExpressionUtil.extractVarDefOfAssignment(expr);

					if (assignee != null) {
						// expr is assignment
						shouldBeAdded = true;

						// check if variable is already in list regarding EcoreUtil.equals
						for (VariableDefinition variable : uniqueVariables) {
							if (EcoreUtil.equals(variable, assignee)) {
								shouldBeAdded = false;
								break;
							}
						}

						if (shouldBeAdded) {
							uniqueVariables.add(assignee);
						}
					}
				}
			}

		}

		return uniqueVariables;
	}

	// ======================================================================================
	// calculate reduced SCCs of statechart
	// ======================================================================================
	private List<Set<Vertex>> calculateReducedSCCs(Statechart statechart) {
		List<Transition> transitionsInGraph = new ArrayList<>();

		DirectedPseudograph<Vertex, Transition> graphOfChart = new DirectedPseudograph<>(Transition.class);
		// DefaultDirectedGraph<Vertex, Transition> graphOfChart = new
		// DefaultDirectedGraph<>(Transition.class);

		// search only in main region
		TreeIterator<EObject> iterator = statechart.getRegions().get(0).eAllContents();

		while (iterator.hasNext()) {
			EObject next = iterator.next();
			if (next instanceof Vertex) {

				graphOfChart.addVertex(((Vertex) next));
				transitionsInGraph.addAll(((Vertex) next).getOutgoingTransitions());
			}
		}

		for (Transition trans : transitionsInGraph) {
			graphOfChart.addEdge(trans.getSource(), trans.getTarget(), trans);
		}

		GabowStrongConnectivityInspector<Vertex, Transition> gabowScCalulator = new GabowStrongConnectivityInspector<>(
				graphOfChart);
		List<Set<Vertex>> sCCs = gabowScCalulator.stronglyConnectedSets();

		return sCCs;
	}

	private boolean isCycleInducing(Set<Vertex> scc) {
		if (scc.size() > 1) {
			return true;
		} else if (scc.size() == 0) {
			return false;
		}

		Vertex vertex = scc.iterator().next();
		for (Transition outTrans : vertex.getOutgoingTransitions()) {
			if (outTrans.getTarget().equals(vertex)) {
				return true;
			}
		}
		return false;
	}

	// ======================================================================================
	// getter && setter
	// ======================================================================================
	public Set<Vertex> getVerticesInCycle(Vertex vertex) {
		BundledSCCInformation info = vertexToSCCInfo.get(vertex);
		return info != null ? info.getVerticesInCycle() : null;
	}

	public List<Reaction> getReactionsInCycle(Vertex vertex) {
		BundledSCCInformation info = vertexToSCCInfo.get(vertex);
		return info != null ? info.getReactionsInCycle() : null;
	}

	public List<VariableDefinition> getVariablesInCycle(Vertex vertex) {
		BundledSCCInformation info = vertexToSCCInfo.get(vertex);
		return info != null ? info.getVariablesInCycle() : null;
	}

}
