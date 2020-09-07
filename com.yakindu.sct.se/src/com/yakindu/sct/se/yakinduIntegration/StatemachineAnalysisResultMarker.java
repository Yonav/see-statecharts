package com.yakindu.sct.se.yakinduIntegration;

import org.yakindu.base.types.validation.IValidationIssueAcceptor;
import org.yakindu.base.types.validation.IValidationIssueAcceptor.ValidationIssue;
import org.yakindu.base.types.validation.IValidationIssueAcceptor.ValidationIssue.Severity;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.sgraph.Vertex;

import com.yakindu.sct.se.analysis.AnalysisResult;

/**
 * Used to mark unreachably elements in the original statechart
 * @author jwielage
 *
 */
public class StatemachineAnalysisResultMarker {

	public static StatemachineAnalysisResultMarker INSTANCE = new StatemachineAnalysisResultMarker();

	private StatemachineAnalysisResultMarker() {

	}

	public void calculateMarker(AnalysisResult aResult, IValidationIssueAcceptor acceptor) {
		for (Vertex nRVer : aResult.getNonReachableVertices()) {
			if (!aResult.isInterruptedAnalysis()) {
				acceptor.accept(
						new ValidationIssue(Severity.WARNING, "Zustand kann nicht erreicht werden.", nRVer, "0000"));
			} else {
				acceptor.accept(new ValidationIssue(Severity.WARNING,
						"Zustand kann möglicherweise nicht erreicht werden.", nRVer, "0000"));
			}
		}
		for (Transition nRTrans : aResult.getNonReachableTransitions()) {
			if (!aResult.isInterruptedAnalysis()) {
				acceptor.accept(
						new ValidationIssue(Severity.WARNING, "Transition niemals durchführbar.", nRTrans, "0000"));
			} else {
				acceptor.accept(new ValidationIssue(Severity.WARNING, "Transition möglicherweise nicht durchführbar.",
						nRTrans, "0000"));
			}
		}

	}
}
