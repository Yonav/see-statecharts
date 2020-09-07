package com.yakindu.sct.se.util;

import org.yakindu.sct.generator.core.console.IConsoleLogger;

public class SELogger implements IConsoleLogger {

	boolean logToConsole = true;

	@Override
	public void logError(Throwable t) {
		if (logToConsole) {
			System.err.println(t);
		}
	}

	@Override
	public void log(String line) {
		if (logToConsole) {
			System.out.println(line);
		}
	}

	@Override
	public void close() {
		// Nothing to do
	}

}
