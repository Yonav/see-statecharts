package com.yakindu.sct.se.engine.strategy;

import com.yakindu.sct.se.rules.cycleElimination.TransitionCycleEliminationRule;
import com.yakindu.sct.se.solver.NodeSolver;
import com.yakindu.sct.se.solver.SMTLIBConstraintSolver;

/**
 * Handles the Strategy of the Eninge.
 * 
 * @author jwielage
 *
 */
public class Strategy {

	public enum CycleElimination {
		CONSERVATIVE_FIVE, CONSERVATIVE_ONE, PREVENTIVE, OFF
	}

	public enum SatDistribution {
		BINARY_SEARCH, OLDEST_SEARCH
	}

	public enum ContextSolving {
		ON, OFF
	}

	public enum IndependenceOptimization {
		ON, OFF
	}

	public enum Solver {
		JCONSTRAINTS, NATIVE_Z3
	}

	public enum Construction {
		BFS, DFS
	}

	private final CycleElimination cycleElimination;
	private final SatDistribution satDistribution;
	private final ContextSolving contextSolving;
	private final IndependenceOptimization independenceOptimization;
	private final Construction construction;
	private final Solver solver;
	private final int satCost;
	private final int timeConstraint;

	public static Strategy DEFAULT = new Strategy(CycleElimination.CONSERVATIVE_ONE, SatDistribution.OLDEST_SEARCH,
			ContextSolving.ON, IndependenceOptimization.ON, Construction.BFS, Solver.NATIVE_Z3, 30, 1800);

	public static Strategy FASTEST = new Strategy(CycleElimination.PREVENTIVE, SatDistribution.OLDEST_SEARCH,
			ContextSolving.ON, IndependenceOptimization.ON, Construction.BFS, Solver.NATIVE_Z3, 30, 1800);

	public Strategy(CycleElimination cycleElimination, SatDistribution satDistribution, ContextSolving contextSolving,
			IndependenceOptimization independenceOptimization, Construction construction, Solver solver, int satCost,
			int timeConstraint) {
		this.cycleElimination = cycleElimination;
		this.satDistribution = satDistribution;
		this.contextSolving = contextSolving;
		this.independenceOptimization = independenceOptimization;
		this.construction = construction;
		this.solver = solver;
		if (timeConstraint <= 0) {
			this.timeConstraint = Integer.MAX_VALUE;
		} else {
			this.timeConstraint = timeConstraint;
		}
		if (satCost <= 1) {
			this.satCost = 1;
		} else {
			this.satCost = satCost;
		}
	}

	public ConstructorStrategy getConstructionStrategy() {
		return new ConstructorStrategy(construction.equals(Construction.DFS), satCost);
	}

	public TransitionCycleEliminationRule getAffiliatedCycleEliminationRule() {
		TransitionCycleEliminationRule lBRule = null;
		switch (cycleElimination) {
		case PREVENTIVE:
			lBRule = TransitionCycleEliminationRule.of(0);
			break;
		case CONSERVATIVE_FIVE:
			lBRule = TransitionCycleEliminationRule.of(5);
			break;
		case CONSERVATIVE_ONE:
			lBRule = TransitionCycleEliminationRule.of(1);
			break;
		default:
			break;
		}
		return lBRule;
	}

	public SMTLIBConstraintSolver getAffiliatedSMTSolver() {
		switch (solver) {
		case JCONSTRAINTS:
			return SMTLIBConstraintSolver.INSTANCE(true);
		case NATIVE_Z3:
			return SMTLIBConstraintSolver.INSTANCE(false);
		}
		return null;

	}

	public NodeSolver getAffiliatedNodeSolver(SETContext globalContext) {
		boolean b_binarySearch = satDistribution.equals(SatDistribution.BINARY_SEARCH);
		boolean b_independeOptimization = independenceOptimization.equals(IndependenceOptimization.ON);
		boolean b_contextSolving = contextSolving.equals(ContextSolving.ON);
		boolean b_jConstraints = solver.equals(Solver.JCONSTRAINTS);

		return NodeSolver.of(globalContext.getTimer(), b_binarySearch, b_independeOptimization, b_contextSolving,
				b_jConstraints);
	}

	public CycleElimination getCycleElimination() {
		return cycleElimination;
	}

	public SatDistribution getSatDistribution() {
		return satDistribution;
	}

	public ContextSolving getContextSolving() {
		return contextSolving;
	}

	public IndependenceOptimization getIndependenceOptimization() {
		return independenceOptimization;
	}

	public Construction getConstruction() {
		return construction;
	}

	public Solver getSolver() {
		return solver;
	}

	public int getSatCost() {
		return satCost;
	}

	public int getTimeConstraint() {
		return timeConstraint;
	}

	@Override
	public String toString() {
		return "Strategy [cycleElimination=" + cycleElimination + ", satDistribution=" + satDistribution
				+ ", contextSolving=" + contextSolving + ", independenceOptimization=" + independenceOptimization
				+ ", construction=" + construction + ", solver=" + solver + ", satCost=" + satCost + ", timeConstraint="
				+ timeConstraint + "]";
	}

}
