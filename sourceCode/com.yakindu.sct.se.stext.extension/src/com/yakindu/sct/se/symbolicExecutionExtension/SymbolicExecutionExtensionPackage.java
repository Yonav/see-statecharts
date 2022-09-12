/**
 */
package com.yakindu.sct.se.symbolicExecutionExtension;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.yakindu.base.types.TypesPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see com.yakindu.sct.se.symbolicExecutionExtension.SymbolicExecutionExtensionFactory
 * @model kind="package"
 * @generated
 */
public interface SymbolicExecutionExtensionPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "symbolicExecutionExtension";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.yakindu.org/symbolicExecution/extension/2.0.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "symbolicExecutionExtension";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	SymbolicExecutionExtensionPackage eINSTANCE = com.yakindu.sct.se.symbolicExecutionExtension.impl.SymbolicExecutionExtensionPackageImpl.init();

	/**
	 * The meta object id for the '{@link com.yakindu.sct.se.symbolicExecutionExtension.impl.SequenceBlockImpl <em>Sequence Block</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.yakindu.sct.se.symbolicExecutionExtension.impl.SequenceBlockImpl
	 * @see com.yakindu.sct.se.symbolicExecutionExtension.impl.SymbolicExecutionExtensionPackageImpl#getSequenceBlock()
	 * @generated
	 */
	int SEQUENCE_BLOCK = 0;

	/**
	 * The feature id for the '<em><b>Expressions</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENCE_BLOCK__EXPRESSIONS = TypesPackage.EXPRESSION_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Sequence Block</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SEQUENCE_BLOCK_FEATURE_COUNT = TypesPackage.EXPRESSION_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Sequence Block</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 * @ordered
	 */
	int SEQUENCE_BLOCK_OPERATION_COUNT = 0; //TypesPackage.EXPRESSION_OPERATION_COUNT + 0;


	/**
	 * Returns the meta object for class '{@link com.yakindu.sct.se.symbolicExecutionExtension.SequenceBlock <em>Sequence Block</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Sequence Block</em>'.
	 * @see com.yakindu.sct.se.symbolicExecutionExtension.SequenceBlock
	 * @generated
	 */
	EClass getSequenceBlock();

	/**
	 * Returns the meta object for the reference list '{@link com.yakindu.sct.se.symbolicExecutionExtension.SequenceBlock#getExpressions <em>Expressions</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Expressions</em>'.
	 * @see com.yakindu.sct.se.symbolicExecutionExtension.SequenceBlock#getExpressions()
	 * @see #getSequenceBlock()
	 * @generated
	 */
	EReference getSequenceBlock_Expressions();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	SymbolicExecutionExtensionFactory getSymbolicExecutionExtensionFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link com.yakindu.sct.se.symbolicExecutionExtension.impl.SequenceBlockImpl <em>Sequence Block</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.yakindu.sct.se.symbolicExecutionExtension.impl.SequenceBlockImpl
		 * @see com.yakindu.sct.se.symbolicExecutionExtension.impl.SymbolicExecutionExtensionPackageImpl#getSequenceBlock()
		 * @generated
		 */
		EClass SEQUENCE_BLOCK = eINSTANCE.getSequenceBlock();

		/**
		 * The meta object literal for the '<em><b>Expressions</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SEQUENCE_BLOCK__EXPRESSIONS = eINSTANCE.getSequenceBlock_Expressions();

	}

} //SymbolicExecutionExtensionPackage
