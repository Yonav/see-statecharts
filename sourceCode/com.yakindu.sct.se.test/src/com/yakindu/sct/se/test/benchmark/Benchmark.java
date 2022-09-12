package com.yakindu.sct.se.test.benchmark;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;
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

public class Benchmark {

	boolean warmup = true;
	boolean writeToFile = false;
	String fileLocation = "C:\\users\\jwielage\\benchmark.txt";

	int numberOfAnalysisPerMode = 3;

	@Test
	public void benchmark() {

		List<Strategy> strategies = strategiesToBenchmark();
		List<Integer> sizes = sizesToBenchmark();

		List<List<AnalysisResult>> resultList = benchmarkConcrete(strategies, sizes, warmup);

		StringBuilder sb = new StringBuilder();

		if (writeToFile) {

			for (int j = 0; j < strategies.size(); j++) {
				sb.append("---------   Strategie Nr: " + j + "    -----------\n");
				sb.append("---------   j_o" + "    -----------\n");
				for (int i = 0; i < sizes.size(); i++) {
					double overallTime = calculateAverage(resultList.get(j), i * numberOfAnalysisPerMode, true);

					overallTime = Math.round(overallTime * 10000.0) / 10000.0;

					sb.append("(" + sizes.get(i) + ", " + overallTime + ")\n");
				}

				sb.append("---------   j_s" + "    -----------\n");
				for (int i = 0; i < sizes.size(); i++) {
					double solvingTime = calculateAverage(resultList.get(j), i * numberOfAnalysisPerMode, false);

					solvingTime = Math.round(solvingTime * 10000.0) / 10000.0;
					sb.append("(" + sizes.get(i) + ", " + solvingTime + ")\n");
				}

			}

			try {
				writeToFile(sb.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			for (int i = 0; i < resultList.size(); i++) {
				System.out.println("Strategie: " + strategies.get(i).toString());

				for (int j = 0; j < resultList.get(i).size(); j = j + numberOfAnalysisPerMode) {

					System.out.println("Größe: " + sizes.get(j / numberOfAnalysisPerMode));

					double solvingTime = calculateAverage(resultList.get(i), j, false);
					double overallTime = calculateAverage(resultList.get(i), j, true);
					System.out.println("Solving: " + solvingTime + "s");
					System.out.println("Gesamt: " + overallTime + "s");

					System.out.println("-------------------------------------");
				}
				System.out.println("==========================================");
			}

		}

	}

	private List<Integer> sizesToBenchmark() {
		List<Integer> sizes = new ArrayList<>();
		// IntStream.of(2,4,6,8,10,12,14,16,18,20).forEach(i -> sizes.add(i));
		IntStream.of(2, 4, 8, 16, 32, 64).forEach(i -> sizes.add(i));
		// IntStream.of(85).forEach(i -> sizes.add(i));
		return sizes;

	}

	private List<Strategy> oneStrategy() {
		List<Strategy> result = new ArrayList<>();

		result.add(Strategy.DEFAULT);
		return result;
	}

	private List<Strategy> strategiesToBenchmark() {
		if (false) {
			return oneStrategy();
		}
		List<Strategy> result = new ArrayList<>();

		// 1 default
		result.add(Strategy.DEFAULT);

		// 2 conservative five
		result.add(new Strategy(CycleElimination.CONSERVATIVE_FIVE, SatDistribution.OLDEST_SEARCH, ContextSolving.ON,
				IndependenceOptimization.ON, Construction.BFS, Solver.NATIVE_Z3, 30, 1800));

		// 3 conversative one
		result.add(new Strategy(CycleElimination.PREVENTIVE, SatDistribution.OLDEST_SEARCH, ContextSolving.ON,
				IndependenceOptimization.ON, Construction.BFS, Solver.NATIVE_Z3, 30, 1800));

		// 4 cycleElim off
		result.add(new Strategy(CycleElimination.OFF, SatDistribution.BINARY_SEARCH, ContextSolving.ON,
				IndependenceOptimization.ON, Construction.BFS, Solver.NATIVE_Z3, 30, 1800));

		// 5 context solving off
		result.add(new Strategy(CycleElimination.CONSERVATIVE_ONE, SatDistribution.OLDEST_SEARCH, ContextSolving.OFF,
				IndependenceOptimization.ON, Construction.BFS, Solver.NATIVE_Z3, 30, 1800));

		// 6 indepOpt off
		result.add(new Strategy(CycleElimination.CONSERVATIVE_ONE, SatDistribution.OLDEST_SEARCH, ContextSolving.ON,
				IndependenceOptimization.OFF, Construction.BFS, Solver.NATIVE_Z3, 30, 1800));

		// 7 sat distrib binary
		result.add(new Strategy(CycleElimination.CONSERVATIVE_ONE, SatDistribution.BINARY_SEARCH, ContextSolving.ON,
				IndependenceOptimization.ON, Construction.BFS, Solver.NATIVE_Z3, 30, 1800));

		// 8 dfs
		result.add(new Strategy(CycleElimination.CONSERVATIVE_ONE, SatDistribution.OLDEST_SEARCH, ContextSolving.ON,
				IndependenceOptimization.ON, Construction.DFS, Solver.NATIVE_Z3, 30, 1800));

		// 9 jcons
		result.add(new Strategy(CycleElimination.CONSERVATIVE_ONE, SatDistribution.OLDEST_SEARCH, ContextSolving.ON,
				IndependenceOptimization.ON, Construction.BFS, Solver.JCONSTRAINTS, 30, 1800));

		// 10 sat cost 1
		result.add(new Strategy(CycleElimination.CONSERVATIVE_ONE, SatDistribution.OLDEST_SEARCH, ContextSolving.ON,
				IndependenceOptimization.ON, Construction.BFS, Solver.NATIVE_Z3, 1, 1800));
		// 11 sat cost 10
		result.add(new Strategy(CycleElimination.CONSERVATIVE_ONE, SatDistribution.OLDEST_SEARCH, ContextSolving.ON,
				IndependenceOptimization.ON, Construction.BFS, Solver.NATIVE_Z3, 10, 1800));
		// 12 sat cost 50
		result.add(new Strategy(CycleElimination.CONSERVATIVE_ONE, SatDistribution.OLDEST_SEARCH, ContextSolving.ON,
				IndependenceOptimization.ON, Construction.BFS, Solver.NATIVE_Z3, 50, 1800));

		// 13 context solving & indepOpt off
		result.add(new Strategy(CycleElimination.CONSERVATIVE_ONE, SatDistribution.OLDEST_SEARCH, ContextSolving.OFF,
				IndependenceOptimization.OFF, Construction.BFS, Solver.NATIVE_Z3, 30, 1800));

		// 14 context solving & indepOpt off & binary
		result.add(new Strategy(CycleElimination.CONSERVATIVE_ONE, SatDistribution.BINARY_SEARCH, ContextSolving.OFF,
				IndependenceOptimization.OFF, Construction.BFS, Solver.NATIVE_Z3, 30, 1800));

		// 15 context solving off & cycleOff
		result.add(new Strategy(CycleElimination.OFF, SatDistribution.OLDEST_SEARCH, ContextSolving.OFF,
				IndependenceOptimization.ON, Construction.BFS, Solver.NATIVE_Z3, 30, 1800));

		// 16 indepOpt off & cycleOff
		result.add(new Strategy(CycleElimination.OFF, SatDistribution.OLDEST_SEARCH, ContextSolving.ON,
				IndependenceOptimization.OFF, Construction.BFS, Solver.NATIVE_Z3, 30, 1800));

		// 17 context solving & indepOpt off & cycleOff
		result.add(new Strategy(CycleElimination.OFF, SatDistribution.OLDEST_SEARCH, ContextSolving.OFF,
				IndependenceOptimization.OFF, Construction.BFS, Solver.NATIVE_Z3, 30, 1800));

		return result;

	}

	private List<List<AnalysisResult>> benchmarkConcrete(List<Strategy> strategies, List<Integer> sizes,
			boolean warmup) {

		List<List<AnalysisResult>> resultList = new ArrayList<>();

		for (Strategy strategy : strategies) {
			List<AnalysisResult> internalResult = new ArrayList<>();
			for (Integer size : sizes) {

				Statechart benchmarkChart;
				boolean shouldContinue = true;
				if (warmup) {
					benchmarkChart = BenchmarkStatechart.createBenchmarkChart(size);
					// act
					Analysis analysis = Analysis.analyse(benchmarkChart, strategy);
					AnalysisResult anaResult = analysis.getFinishedResult();
					if (anaResult.isInterruptedAnalysis()) {

						for (int times = 1; times <= numberOfAnalysisPerMode; times++) {
							internalResult.add(anaResult);
						}
						shouldContinue = false;
					}

				}

				if (!shouldContinue) {
					continue;
				}

				for (int times = 1; times <= numberOfAnalysisPerMode; times++) {
					benchmarkChart = BenchmarkStatechart.createBenchmarkChart(size);
					// act
					Analysis analysis = Analysis.analyse(benchmarkChart, strategy);
					AnalysisResult anaResult = analysis.getFinishedResult();
					internalResult.add(anaResult);
					System.out.println(strategy.toString());
					System.out.println("Größe: " + size);
					System.out.println(anaResult.printResult(true));

				}
			}

			resultList.add(internalResult);

		}

		return resultList;

	}

	public void writeToFile(String data) throws IOException {
		File file = new File(fileLocation);
		file.createNewFile();
		OutputStream fos = Files.newOutputStream(file.toPath());
		DataOutputStream outStream = new DataOutputStream(new BufferedOutputStream(fos));
		outStream.writeBytes(data);
		outStream.close();
	}

	public double calculateAverage(List<AnalysisResult> list, int startIndex, boolean overallTime) {
		double result = 0;
		for (int i = 0; i < numberOfAnalysisPerMode; i++) {
			if (overallTime) {
				result += list.get(startIndex + i).getTimeTracker().getOverallTime().getPassedTimeAsSeconds();
			} else {
				result += list.get(startIndex + i).getTimeTracker().getSolverTime().getPassedTimeAsSeconds();
			}
		}
		return result / numberOfAnalysisPerMode;

	}

}
