package com.yakindu.sct.se.util.performance;

/**
 * Used to measure time
 * @author jwielage
 *
 */
public class StopWatchUtil {

	public static StopWatchUtil INSTANCE() {
		return new StopWatchUtil();
	}
	
	private long passedTime = 0;
	
	private long currentStartTime = -1;
	

	public void startTime() {
		currentStartTime = System.nanoTime(); 
	}
	
	public void stopTime() {
		if(currentStartTime != -1) {
			passedTime += System.nanoTime() - currentStartTime;
			currentStartTime = -1;
		}
	}

	public boolean isActive() {
		return currentStartTime != -1;
	}
	
	public long getPassedTime() {
		return passedTime;
	}
	
	public double getPassedTimeAsSeconds() {
		return (double)passedTime / 1_000_000_000.0;
	}

	public String toString() {
		return ""+getPassedTimeAsSeconds();
	}
	
}
