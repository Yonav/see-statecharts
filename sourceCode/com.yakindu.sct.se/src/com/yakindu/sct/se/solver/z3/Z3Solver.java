package com.yakindu.sct.se.solver.z3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;

import com.yakindu.sct.se.solver.model.MaybeBool;

/**
 * Bridge to Z3 that solves smtlib2 strings
 * @author jwielage
 *
 */
public class Z3Solver {

	// TODO: Drüber schauen
	private Process z3Process;
	private PrintStream z3Input;

	private BufferedReader z3OutputNew;

	public static Z3Solver INSTANCE = new Z3Solver();

	private Z3Solver() {
		init();
	}

	private void init() {
		List<String> commands = new ArrayList<>();
		// commands.add(z3Location);
		commands.add("z3");

		commands.add("-in");
		String commando = String.join(" ", commands);

		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("cmd.exe");
		processBuilder.redirectInput(Redirect.PIPE);
		processBuilder.redirectOutput(Redirect.PIPE);
		processBuilder.redirectError(Redirect.PIPE);

		commands.clear();
		commands.add(commando);

		try {
			z3Process = processBuilder.start();

			OutputStream procOut = z3Process.getOutputStream();
			z3Input = new PrintStream(procOut);
			z3OutputNew = new BufferedReader(new InputStreamReader(z3Process.getInputStream()));
			// z3Output = z3Process.getInputStream();

			// PrintStream printOut = new PrintStream(procOut);
			commands.forEach(z3Input::println);

		} catch (Exception e) {
			System.out.println("est");

		}

	}

	public MaybeBool checkSat(String smtLIBConstraint) {
		// return MaybeBool.valueOf(startSolver(smtLIBConstraint).toUpperCase());
		return solveSMTLIB(smtLIBConstraint);
	}

	private MaybeBool solveSMTLIB(String smtString) {
		z3Input.println("(push)");
		z3Input.println(smtString);
		z3Input.println("(pop)");

		z3Input.flush();

		String line = "";
		try {
			while ((line = z3OutputNew.readLine()) != null) {
				if (line.equals("sat") || line.equals("unsat") || line.equals("unknown")) {
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String resultString = line;

		if (resultString.equals("sat")) {
			return MaybeBool.SAT;
		}
		if (resultString.equals("unsat")) {
			return MaybeBool.UNSAT;
		}
		if (resultString.equals("unknown")) {
			return MaybeBool.UNKNOWN;
		}

		return MaybeBool.UNKNOWN;

	}
}
