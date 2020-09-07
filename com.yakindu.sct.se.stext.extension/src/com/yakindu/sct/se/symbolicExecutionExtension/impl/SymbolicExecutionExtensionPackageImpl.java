/**
 */
package com.yakindu.sct.se.symbolicExecutionExtension.impl;

import com.yakindu.sct.se.symbolicExecutionExtension.SequenceBlock;
import com.yakindu.sct.se.symbolicExecutionExtension.SymbolicExecutionExtensionFactory;
import com.yakindu.sct.se.symbolicExecutionExtension.SymbolicExecutionExtensionPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.yakindu.base.base.BasePackage;

import org.yakindu.base.types.TypesPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SymbolicExecutionExtensionPackageImpl extends EPackageImpl implements SymbolicExecutionExtensionPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass sequenceBlockEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see com.yakindu.sct.se.symbolicExecutionExtension.SymbolicExecutionExtensionPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private SymbolicExecutionExtensionPackageImpl() {
		super(eNS_URI, SymbolicExecutionExtensionFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link SymbolicExecutionExtensionPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static SymbolicExecutionExtensionPackage init() {
		if (isInited) return (SymbolicExecutionExtensionPackage)EPackage.Registry.INSTANCE.getEPackage(SymbolicExecutionExtensionPackage.eNS_URI);

		// Obtain or create and register package
		Object registeredSymbolicExecutionExtensionPackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		SymbolicExecutionExtensionPackageImpl theSymbolicExecutionExtensionPackage = registeredSymbolicExecutionExtensionPackage instanceof SymbolicExecutionExtensionPackageImpl ? (SymbolicExecutionExtensionPackageImpl)registeredSymbolicExecutionExtensionPackage : new SymbolicExecutionExtensionPackageImpl();

		isInited = true;

		// Initialize simple dependencies
		BasePackage.eINSTANCE.eClass();
		TypesPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theSymbolicExecutionExtensionPackage.createPackageContents();

		// Initialize created meta-data
		theSymbolicExecutionExtensionPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theSymbolicExecutionExtensionPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(SymbolicExecutionExtensionPackage.eNS_URI, theSymbolicExecutionExtensionPackage);
		return theSymbolicExecutionExtensionPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSequenceBlock() {
		return sequenceBlockEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSequenceBlock_Expressions() {
		return (EReference)sequenceBlockEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SymbolicExecutionExtensionFactory getSymbolicExecutionExtensionFactory() {
		return (SymbolicExecutionExtensionFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		sequenceBlockEClass = createEClass(SEQUENCE_BLOCK);
		createEReference(sequenceBlockEClass, SEQUENCE_BLOCK__EXPRESSIONS);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		TypesPackage theTypesPackage = (TypesPackage)EPackage.Registry.INSTANCE.getEPackage(TypesPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		sequenceBlockEClass.getESuperTypes().add(theTypesPackage.getExpression());

		// Initialize classes, features, and operations; add parameters
		initEClass(sequenceBlockEClass, SequenceBlock.class, "SequenceBlock", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getSequenceBlock_Expressions(), theTypesPackage.getExpression(), null, "expressions", null, 0, -1, SequenceBlock.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} //SymbolicExecutionExtensionPackageImpl
