package com.yakindu.sct.se.engine.strategy;

import java.util.List;

import org.yakindu.base.expressions.expressions.ElementReferenceExpression;
import org.yakindu.sct.model.sgraph.Entry;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Vertex;

import com.yakindu.sct.se.model.CycleInformationCalulator;
import com.yakindu.sct.se.rules.statement.pathConstraints.transformer.SymbolicMemoryEntryGenerator;
import com.yakindu.sct.se.solver.NodeSolver;
import com.yakindu.sct.se.util.StaticAnalyserUtil;
import com.yakindu.sct.se.util.performance.PerformanceUtil;

/**
 * Stores information used during construction of a SET by all nodes
 * 
 * @author jwielage
 *
 */
public class SETContext {
	private int nodeCounter;

	private final SymbolicMemoryEntryGenerator sEntryGenerator;
	private final NodeSolver solver;
	private final CycleInformationCalulator cycleCalulator;
	private final PerformanceUtil timer = PerformanceUtil.INSTANCE();

	private final Entry entryState;
	private List<ElementReferenceExpression> uninitializedElRef;

	public static SETContext createFor(Statechart statechart, Strategy mode) {
		return new SETContext(statechart, mode);
	}

	private SETContext(Statechart statechart, Strategy mode) {
		this.nodeCounter = -1;
		this.sEntryGenerator = new SymbolicMemoryEntryGenerator(statechart);
		this.entryState = extractEntryState(statechart);

		this.cycleCalulator = CycleInformationCalulator.calculateFromStatechart(statechart);
		this.solver = mode.getAffiliatedNodeSolver(this);
	}

	private Entry extractEntryState(Statechart statechart) {
		Entry[] entry = new Entry[1];
		entry[0] = null;
		statechart.getRegions().forEach(region -> {
			StaticAnalyserUtil.findAllInTree(region, Entry.class, true, e -> entry[0] = e);
		});

		return entry[0];
	}

	public int generateNextNodeCount() {
		nodeCounter++;
		return nodeCounter;
	}

	// ==========================================================
	// Getter
	// ==========================================================
	public SymbolicMemoryEntryGenerator getSEntryGenerator() {
		return sEntryGenerator;
	}

	public CycleInformationCalulator getCycleCalulator() {
		return cycleCalulator;
	}

	public NodeSolver getSolver() {
		return solver;
	}

	public List<ElementReferenceExpression> getUninitializedElRefs() {
		return uninitializedElRef;
	}

	public Vertex getEntryState() {
		return entryState;
	}

	public PerformanceUtil getTimer() {
		// TODO Auto-generated method stub
		return this.timer;
	}

}
