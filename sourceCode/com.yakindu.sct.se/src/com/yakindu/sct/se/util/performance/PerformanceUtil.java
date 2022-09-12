package com.yakindu.sct.se.util.performance;

/**
 * used to track the performance of the engine
 * @author jwielage
 *
 */
public class PerformanceUtil {

	public static PerformanceUtil INSTANCE() {
		return new PerformanceUtil();
	}
	
	private int solvingCount = 0;
	private int contextSolvingSuccessCount = 0;
	
	
	private StopWatchUtil overallTime = StopWatchUtil.INSTANCE();
	private StopWatchUtil solverTime = StopWatchUtil.INSTANCE();

	public void countSolving() {
		solvingCount++;
	}
	
	public void countContextSolvingSuccess() {
		contextSolvingSuccessCount++;
	}

	public int getSolvingCount() {
		return solvingCount;
	}

	public int getContextSolvingSuccessCount() {
		return contextSolvingSuccessCount;
	}

	public StopWatchUtil getOverallTime() {
		return overallTime;
	}

	public StopWatchUtil getSolverTime() {
		return solverTime;
	}
	
}
