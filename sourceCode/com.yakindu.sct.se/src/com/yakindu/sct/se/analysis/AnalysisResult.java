package com.yakindu.sct.se.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.yakindu.sct.generator.core.console.IConsoleLogger;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.sgraph.Vertex;

import com.google.inject.Inject;
import com.yakindu.sct.se.engine.strategy.SETContext;
import com.yakindu.sct.se.util.SELogger;
import com.yakindu.sct.se.util.StaticAnalyserUtil;
import com.yakindu.sct.se.util.performance.PerformanceUtil;

/**
 * Stores the result of the reachability analysis. Can be interrupted.
 * 
 * @author jwielage
 *
 */
public class AnalysisResult {

	@Inject
	private IConsoleLogger logger = new SELogger();

	private final List<Transition> nonReachableTransitions = new ArrayList<>();
	private final List<Vertex> nonReachableVertices = new ArrayList<>();
	private final Statechart statechart;

	private boolean analysisCompleted = false;
	private boolean interruptedAnalysis = false;

	private final PerformanceUtil timeTracker;

	public static AnalysisResult initWithStatechartAndStartTimer(Statechart statechart, SETContext globalContext) {
		return new AnalysisResult(statechart, globalContext);
	}

	private AnalysisResult(Statechart statechart, SETContext globalContext) {
		this.statechart = statechart;
		this.timeTracker = globalContext.getTimer();
		beginAnalysis(statechart, globalContext);

	}

	private void beginAnalysis(Statechart statechart, SETContext globalContext) {
		startTimer();

		StaticAnalyserUtil.multiplefindAllInTree(statechart.getRegions().get(0),
				Arrays.asList(Vertex.class, Transition.class), false, Arrays.asList(v -> {
					nonReachableVertices.add(((Vertex) v));
				}, t -> {
					nonReachableTransitions.add(((Transition) t));
				}));

		nonReachableVertices.remove(globalContext.getEntryState());
	}

	private boolean addReachableVertex(Vertex vertex) {
		return nonReachableVertices.remove(vertex);
	}

	public void addReachableTransition(Transition transition) {
		if (nonReachableTransitions.remove(transition)) {
			addReachableVertex(transition.getTarget());
		}
	}

	public boolean isEverythingReachable() {
		return nonReachableTransitions.isEmpty() && nonReachableVertices.isEmpty();
	}

	// returns empty list of the analysis isn't completed
	public List<Transition> getNonReachableTransitions() {
		if (!isAnalysisCompleted()) {
			printAnalyzationNotCompleted();
			return new ArrayList<>();
		}
		return nonReachableTransitions;
	}

	public List<Vertex> getNonReachableVertices() {
		if (!isAnalysisCompleted()) {
			printAnalyzationNotCompleted();
			return new ArrayList<>();
		}
		return nonReachableVertices;
	}

	public boolean isInterruptedAnalysis() {
		return interruptedAnalysis;
	}

	public boolean isAnalysisCompleted() {
		return analysisCompleted;
	}

	public void interruptAnalysis() {
		interruptedAnalysis = true;
		completeAnalysis();
	}

	public void completeAnalysis() {
		analysisCompleted = true;
		stopTimer();
	}

	public void startTimer() {
		timeTracker.getOverallTime().startTime();
	}

	public void stopTimer() {
		timeTracker.getOverallTime().stopTime();
	}

	public void printAnalyzationNotCompleted() {
		logger.log("Analysis wasn't finished; Result is not accurate!");
	}

	public Statechart getStatechart() {
		return statechart;
	}

	public String printResult(boolean detailed) {
		StringBuilder sb = new StringBuilder();
		sb.append("----------------------------------\n");
		sb.append("AnalysisResult von: ");
		sb.append(statechart.getName());
		sb.append("\n");
		sb.append("Anzahl nicht erreichbarer Zustände: ");
		sb.append(nonReachableVertices.size());
		sb.append("\n");

		if (detailed) {
			nonReachableVertices.forEach(v -> {
				sb.append("[");
				sb.append(v.getName());
				sb.append("], ");
			});
			sb.append("\n");
		}
		sb.append("Anzahl nicht erreichbarer Transitionen: ");
		sb.append(nonReachableTransitions.size());
		sb.append("\n");

		if (detailed) {
			nonReachableTransitions.forEach(t -> {
				sb.append("[");
				sb.append(t.getSource().getName());
				sb.append(" -> ");
				sb.append(t.getTarget().getName());
				sb.append("], ");
			});
			sb.append("\n");
		}

		sb.append("Zeit der kompletten Analyse: ");
		sb.append(timeTracker.getOverallTime().getPassedTimeAsSeconds());
		sb.append(" sec\n");

		sb.append("Zeit des Solvers: ");
		sb.append(timeTracker.getSolverTime().getPassedTimeAsSeconds());
		sb.append(" sec\n");
		sb.append("Anzahl Solving Aufrufe: ");
		sb.append(timeTracker.getSolvingCount());
		sb.append("\n");
		sb.append("Anzahl ContextSolving Erfolge: ");
		sb.append(timeTracker.getContextSolvingSuccessCount());
		sb.append("\n");
		sb.append("----------------------------------\n");
		return sb.toString();

	}

	public PerformanceUtil getTimeTracker() {
		return timeTracker;
	}

}
