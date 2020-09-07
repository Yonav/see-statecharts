package com.yakindu.sct.se.yakinduIntegration;

import org.eclipse.emf.ecore.EValidator;
import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.yakindu.sct.model.sgraph.SGraphPackage;
import org.yakindu.sct.model.stext.stext.StextPackage;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class VerifyActivator implements BundleActivator, IStartup {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		VerifyActivator.context = bundleContext;
		Injector injector = getInjector();

		StateMachineValidator validator = injector.getInstance(StateMachineValidator.class);
		EValidator.Registry.INSTANCE.put(SGraphPackage.eINSTANCE, validator);
		EValidator.Registry.INSTANCE.put(StextPackage.eINSTANCE, validator);
	}

	private Injector getInjector() {
		return Guice.createInjector(new SolverModule());
	}

	public void stop(BundleContext bundleContext) throws Exception {
		VerifyActivator.context = null;
	}

	@Override
	public void earlyStartup() {

	}

}
