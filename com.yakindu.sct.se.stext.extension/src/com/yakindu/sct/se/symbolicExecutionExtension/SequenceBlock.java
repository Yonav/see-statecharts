/**
 */
package com.yakindu.sct.se.symbolicExecutionExtension;

import org.eclipse.emf.common.util.EList;

import org.yakindu.base.types.Expression;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sequence Block</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.yakindu.sct.se.symbolicExecutionExtension.SequenceBlock#getExpressions <em>Expressions</em>}</li>
 * </ul>
 *
 * @see com.yakindu.sct.se.symbolicExecutionExtension.SymbolicExecutionExtensionPackage#getSequenceBlock()
 * @model
 * @generated
 */
public interface SequenceBlock extends Expression {
	/**
	 * Returns the value of the '<em><b>Expressions</b></em>' reference list.
	 * The list contents are of type {@link org.yakindu.base.types.Expression}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Expressions</em>' reference list.
	 * @see com.yakindu.sct.se.symbolicExecutionExtension.SymbolicExecutionExtensionPackage#getSequenceBlock_Expressions()
	 * @model
	 * @generated
	 */
	EList<Expression> getExpressions();

} // SequenceBlock
