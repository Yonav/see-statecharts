package com.yakindu.sct.se.solver.model;

public enum MaybeBool {
	SAT, UNSAT, UNKNOWN;

	public boolean isSatisfiable() {
		return SAT.equals(this);
	}

	public boolean isUnknown() {
		return UNKNOWN.equals(this);
	}

	public boolean isUnsatisfiable() {
		return UNSAT.equals(this);
	}
}
