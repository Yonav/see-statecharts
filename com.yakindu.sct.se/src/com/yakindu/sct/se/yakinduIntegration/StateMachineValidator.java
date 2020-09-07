package com.yakindu.sct.se.yakinduIntegration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.CheckType;
import org.yakindu.base.expressions.expressions.ExpressionsPackage;
import org.yakindu.base.types.validation.IValidationIssueAcceptor;
import org.yakindu.sct.model.sgraph.Statechart;

import com.google.inject.Inject;

public class StateMachineValidator extends AbstractDeclarativeValidator implements IValidationIssueAcceptor {

	@Inject
	private ISMTSolver solver;

	@Check(CheckType.NORMAL)
	public void verifyStatechart(Statechart statechart) {
		solver.verifyStateMachine(statechart, this);
	}

	@Override
	protected List<EPackage> getEPackages() {
		List<EPackage> result = new ArrayList<>();
		result.add(EPackage.Registry.INSTANCE.getEPackage("http://www.yakindu.org/sct/statechart/SText"));
		result.add(EPackage.Registry.INSTANCE.getEPackage("http://www.yakindu.org/sct/sgraph/2.0.0"));
		result.add(EPackage.Registry.INSTANCE.getEPackage("http://www.yakindu.org/base/types/2.0.0"));
		result.add(EPackage.Registry.INSTANCE.getEPackage("http://www.yakindu.org/base/expressions/Expressions"));
		result.add(ExpressionsPackage.eINSTANCE);
		return result;
	}

	@Override
	protected boolean isResponsible(Map<Object, Object> context, EObject eObject) {
		return true;
	}

	@Override
	public boolean isLanguageSpecific() {
		return false;
	}

	public void accept(ValidationIssue issue) {
		switch (issue.getSeverity()) {
		case ERROR: {
			error(issue.getMessage(), issue.getTarget(), null, issue.getIssueCode());
			break;
		}
		case WARNING: {
			warning(issue.getMessage(), issue.getTarget(), null, issue.getIssueCode());
			break;
		}
		default:
			break;
		}
	}
}
