/**
 */
package com.yakindu.sct.se.symbolicExecutionExtension.impl;

import com.yakindu.sct.se.symbolicExecutionExtension.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SymbolicExecutionExtensionFactoryImpl extends EFactoryImpl implements SymbolicExecutionExtensionFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static SymbolicExecutionExtensionFactory init() {
		try {
			SymbolicExecutionExtensionFactory theSymbolicExecutionExtensionFactory = (SymbolicExecutionExtensionFactory)EPackage.Registry.INSTANCE.getEFactory(SymbolicExecutionExtensionPackage.eNS_URI);
			if (theSymbolicExecutionExtensionFactory != null) {
				return theSymbolicExecutionExtensionFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new SymbolicExecutionExtensionFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SymbolicExecutionExtensionFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case SymbolicExecutionExtensionPackage.SEQUENCE_BLOCK: return createSequenceBlock();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SequenceBlock createSequenceBlock() {
		SequenceBlockImpl sequenceBlock = new SequenceBlockImpl();
		return sequenceBlock;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SymbolicExecutionExtensionPackage getSymbolicExecutionExtensionPackage() {
		return (SymbolicExecutionExtensionPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static SymbolicExecutionExtensionPackage getPackage() {
		return SymbolicExecutionExtensionPackage.eINSTANCE;
	}

} //SymbolicExecutionExtensionFactoryImpl
