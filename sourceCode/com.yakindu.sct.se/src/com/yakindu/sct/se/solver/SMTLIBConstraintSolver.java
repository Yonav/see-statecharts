package com.yakindu.sct.se.solver;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.smtlib.IParser.ParserException;
import org.yakindu.sct.generator.core.console.IConsoleLogger;

import com.google.inject.Inject;
import com.yakindu.sct.se.solver.model.MaybeBool;
import com.yakindu.sct.se.solver.z3.Z3Solver;
import com.yakindu.sct.se.util.SELogger;

import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.smtlibUtility.SMTProblem;
import gov.nasa.jpf.constraints.smtlibUtility.parser.SMTLIBParser;
import gov.nasa.jpf.constraints.smtlibUtility.parser.SMTLIBParserException;
import gov.nasa.jpf.constraints.solvers.ConstraintSolverFactory;
import gov.nasa.jpf.constraints.util.ExpressionUtil;

/**
 * 
 * Bridge to a native Z3Solver or jConstraints solver
 * @author jwielage
 *
 */
public class SMTLIBConstraintSolver {

	@Inject
	private IConsoleLogger logger = new SELogger();

	private final boolean jConstraints;

	private Future<ConstraintSolver> futurejConstraints_Solver;
	private ConstraintSolver solver_jConstraints;

	private Future<Z3Solver> futureZ3Solver;
	private Z3Solver solver_z3;

	private ExecutorService pool;

	public static SMTLIBConstraintSolver INSTANCE(boolean jConstraints) {
		return new SMTLIBConstraintSolver(jConstraints);
	}

	private SMTLIBConstraintSolver(boolean jConstraints) {
		this.jConstraints = jConstraints;
		this.pool = Executors.newSingleThreadExecutor();

		createSolver();

	}

	public MaybeBool solve(String smtlib2) {
		printSMTLIBToConsole(smtlib2);
		MaybeBool result = null;
		if (jConstraints) {
			result = solveWithJConstraints(smtlib2);
		} else {
			result = solveWithNativeZ3(smtlib2);
		}
		return result;
	}

	private MaybeBool solveWithNativeZ3(String smtlib2) {
		try {
			return getNativeZ3Solver().checkSat(smtlib2);
		} catch (InterruptedException | ExecutionException e) {
			logger.logError(e);
			return MaybeBool.UNKNOWN;
		}
	}

	private MaybeBool solveWithJConstraints(String smtlib2) {
		try {
			SMTProblem problem = SMTLIBParser.parseSMTProgram(smtlib2);
			Expression<Boolean> expr = problem.getAllAssertionsAsConjunction();
			// @SuppressWarnings("unchecked")
			// expr = NumericSimplificationUtil.simplify(expr);

			if (ExpressionUtil.freeVariables(expr).isEmpty()) {
				// Z3 is not needed
				boolean evaluationResult = expr.evaluate(new Valuation());
				logger.log("evaluationResult without z3: " + evaluationResult);
				return evaluationResult ? MaybeBool.SAT : MaybeBool.UNSAT;
			}

			Valuation val = new Valuation();
			ConstraintSolver.Result z3Result = getJConstraintsSolver().solve(expr, val);

			return jConstraintResultToMaybeBool(z3Result);

		} catch (IOException | ParserException | SMTLIBParserException | InterruptedException | ExecutionException e) {
			logger.logError(e);
			return MaybeBool.UNKNOWN;
		}

	}

	private void printSMTLIBToConsole(String smtLIBConstraint) {
		if (smtLIBConstraint == null || smtLIBConstraint.isEmpty()) {
			return;
		}
		int bracketCounter = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < smtLIBConstraint.length(); i++) {
			char momChar = smtLIBConstraint.charAt(i);
			if (momChar == '(') {
				bracketCounter++;
			} else if (momChar == ')') {
				bracketCounter--;
			}
			sb.append(momChar);
			if (bracketCounter == 0) {
				logger.log(sb.toString() + "\n");
				sb.setLength(0);
			}
		}
	}

	public MaybeBool jConstraintResultToMaybeBool(Result result) {
		switch (result) {
		case SAT:
			return MaybeBool.SAT;
		case UNSAT:
			return MaybeBool.UNSAT;
		default:
			return MaybeBool.UNKNOWN;
		}

	}

	private void createSolver() {
		if (jConstraints) {
			this.futurejConstraints_Solver = pool.submit(new JConstraintsSolverCreationTask());
		} else {
			this.futureZ3Solver = pool.submit(new NativeZ3SolverCreationTask());
		}
	}

	private Z3Solver getNativeZ3Solver() throws InterruptedException, ExecutionException {
		if (solver_z3 == null) {
			solver_z3 = this.futureZ3Solver.get();
		}
		return solver_z3;
	}

	private ConstraintSolver getJConstraintsSolver() throws InterruptedException, ExecutionException {
		if (solver_jConstraints == null) {
			solver_jConstraints = this.futurejConstraints_Solver.get();
		}
		return solver_jConstraints;
	}

	private class JConstraintsSolverCreationTask implements Callable<ConstraintSolver> {
		public JConstraintsSolverCreationTask() {
		}

		@Override
		public ConstraintSolver call() {
			Properties conf = new Properties();
			conf.setProperty("symbolic.dp", "z3");
			ConstraintSolverFactory factory = new ConstraintSolverFactory(conf);
			return factory.createSolver();
		}
	}

	private class NativeZ3SolverCreationTask implements Callable<Z3Solver> {
		public NativeZ3SolverCreationTask() {
		}

		@Override
		public Z3Solver call() {
			return Z3Solver.INSTANCE;
		}
	}

	public void close() {
		if (pool != null) {
			pool.shutdownNow();
		}
	}
}
