package com.yakindu.sct.se.yakinduIntegration;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.yakindu.base.types.validation.IValidationIssueAcceptor;
import org.yakindu.sct.model.sgraph.Statechart;

import com.yakindu.sct.se.analysis.Analysis;
import com.yakindu.sct.se.analysis.AnalysisResult;
import com.yakindu.sct.se.engine.strategy.Strategy;
import com.yakindu.sct.se.engine.strategy.Strategy.Construction;
import com.yakindu.sct.se.engine.strategy.Strategy.ContextSolving;
import com.yakindu.sct.se.engine.strategy.Strategy.CycleElimination;
import com.yakindu.sct.se.engine.strategy.Strategy.IndependenceOptimization;
import com.yakindu.sct.se.engine.strategy.Strategy.SatDistribution;
import com.yakindu.sct.se.engine.strategy.Strategy.Solver;

/**
 * Class to start the symbolic execution engine
 * @author jwielage
 *
 */
public class SEStartPoint implements ISMTSolver {

	@Override
	public void verifyStateMachine(Statechart statemachine, IValidationIssueAcceptor acceptor) {

		IProgressMonitor monitor = new NullProgressMonitor();

		Strategy strat = new Strategy(CycleElimination.CONSERVATIVE_ONE, SatDistribution.OLDEST_SEARCH,
				ContextSolving.ON, IndependenceOptimization.OFF, Construction.BFS, Solver.NATIVE_Z3, 30, 1800);

		Analysis analysis = Analysis.analyse(statemachine, strat, monitor);
		AnalysisResult analysisResult = analysis.getFinishedResult();

		StatemachineAnalysisResultMarker.INSTANCE.calculateMarker(analysisResult, acceptor);

	}

}
