package com.yakindu.sct.se.model;

import java.util.List;
import java.util.Set;

import org.yakindu.sct.model.sgraph.Reaction;
import org.yakindu.sct.model.sgraph.Vertex;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

/**
 * Stores all information regarding a strongly connected component used for cycle elimination
 * @author jwielage
 *
 */
public class BundledSCCInformation {

	private final Set<Vertex> verticesInCycle;
	private final List<Reaction> reactionsInCycle;
	private final List<VariableDefinition> variablesInCycle;

	public BundledSCCInformation(Set<Vertex> verticesInCycle, List<Reaction> reactionsInCycle,
			List<VariableDefinition> variablesInCycle) {
		super();
		this.verticesInCycle = verticesInCycle;
		this.reactionsInCycle = reactionsInCycle;
		this.variablesInCycle = variablesInCycle;
	}

	public Set<Vertex> getVerticesInCycle() {
		return verticesInCycle;
	}

	public List<Reaction> getReactionsInCycle() {
		return reactionsInCycle;
	}

	public List<VariableDefinition> getVariablesInCycle() {
		return variablesInCycle;
	}
}
