package com.yakindu.sct.se.yakinduIntegration;

import org.yakindu.base.types.validation.IValidationIssueAcceptor;
import org.yakindu.sct.model.sgraph.Statechart;

public interface ISMTSolver {

	public void verifyStateMachine(Statechart statemachine, IValidationIssueAcceptor acceptor);

}
