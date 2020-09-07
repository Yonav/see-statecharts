package com.yakindu.sct.se.yakinduIntegration;

import org.eclipse.emf.ecore.EValidator;
import org.eclipse.xtext.Constants;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;

public class SolverModule implements Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(ISMTSolver.class).to(SEStartPoint.class);
		binder.bind(String.class).annotatedWith(Names.named(Constants.LANGUAGE_NAME))
				.toInstance("org.yakindu.base.expressions.Expressions");
		binder.bind(EValidator.Registry.class).toInstance(EValidator.Registry.INSTANCE);

	}

}
