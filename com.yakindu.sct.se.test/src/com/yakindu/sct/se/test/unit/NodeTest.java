package com.yakindu.sct.se.test.unit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.yakindu.base.types.Expression;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import com.yakindu.sct.se.collection.ImmutableList;
import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.rules.IRule;

@RunWith(MockitoJUnitRunner.class)
public class NodeTest {

	@Mock
	Transition transition;
	@Mock
	Expression pathConstraint;
	@Mock
	Node childNode;
	@Mock
	Expression statement;
	@Mock
	VariableDefinition symbolicMemoryEntry;

	@Mock
	ImmutableList<Transition> transitionIMList;
	@Mock
	ImmutableList<Expression> pathConstraintIMList;
	@Mock
	ImmutableList<Node> childNodeIMList;
	@Mock
	ImmutableList<Expression> statementIMList;
	@Mock
	ImmutableList<VariableDefinition> symbolicMemoryStoreIMList;

	@Mock
	IRule ruleOne, ruleTwo, ruleThree;

	// here are some tests missing for statementBlock
	// TODO: With a list that has Empty behind it
	// TODO: NOT WORKING!!!
	@Test
	public void removeValue_allSet() {
		// arrange
		Node node = new Node(0);
		ImmutableList<Node> childList = ImmutableList.create(childNode);
		ImmutableList<Expression> constraintList = ImmutableList.create(pathConstraint);
		ImmutableList<Transition> stateList = ImmutableList.create(transition);
		ImmutableList<Expression> statementList = ImmutableList.create(statement);
		ImmutableList<VariableDefinition> symbolicMemoryStore = ImmutableList.create(symbolicMemoryEntry);

		node.setChildren(childList.prepend(childNode));
		node.setPathConstraint(constraintList.prepend(pathConstraint));
		node.setLastTransitions(stateList.prepend(transition));
		node.setActiveStatements(statementList.prepend(statement));
		node.setSymbolicMemoryStore(symbolicMemoryStore.prepend(symbolicMemoryEntry));

		// act
		ImmutableList<Node> removedChild = node.removeLastChildValue();
		ImmutableList<Expression> removedConstraint = node.removeLastPathConstraintValue();
		ImmutableList<Expression> removedStatement = node.removeTopActiveStatement();
		ImmutableList<Transition> removedState = node.removeLastTransitionValue();
		ImmutableList<VariableDefinition> removedSEntry = node.removeLastSymbolicMemoryEntry();

		// assert
		assertThat(removedChild).isEqualTo(childList);
		assertThat(removedConstraint).isEqualTo(constraintList);
		assertThat(removedStatement).isEqualTo(statementList);
		assertThat(removedState).isEqualTo(stateList);
		assertThat(removedSEntry).isEqualTo(symbolicMemoryStore);
	}

	@Test
	public void removeValue_whenNull() {
		// arrange
		Node node = new Node(0);

		// act
		ImmutableList<Node> removedChild = node.removeLastChildValue();
		ImmutableList<Expression> removedConstraint = node.removeLastPathConstraintValue();
		ImmutableList<Expression> removedStatement = node.removeTopActiveStatement();
		ImmutableList<Transition> removedState = node.removeLastTransitionValue();
		ImmutableList<VariableDefinition> removedSEntry = node.removeLastSymbolicMemoryEntry();
		// assert
		assertThat(removedChild).isNull();
		assertThat(removedConstraint).isNull();
		assertThat(removedStatement).isNull();
		assertThat(removedState).isNull();
		assertThat(removedSEntry).isNull();
	}

	@Test
	public void removeValue_whenOneValue() {
		// arrange
		Node node = new Node(0);
		node.prependChildAndSet(childNode);
		node.prependConstraintAndSet(pathConstraint);
		node.prependTransitionAndSet(transition);
		node.prependActiveStatementAndSet(statement);
		node.prependSymbolicMemoryEntryAndSet(symbolicMemoryEntry);
		// act
		ImmutableList<Node> removedChild = node.removeLastChildValue();
		ImmutableList<Expression> removedConstraint = node.removeLastPathConstraintValue();
		ImmutableList<Expression> removedStatement = node.removeTopActiveStatement();
		ImmutableList<Transition> removedState = node.removeLastTransitionValue();
		ImmutableList<VariableDefinition> removedSEntry = node.removeLastSymbolicMemoryEntry();
		// assert
		assertThat(removedChild.isEmpty()).isTrue();
		assertThat(removedConstraint.isEmpty()).isTrue();
		assertThat(removedStatement.isEmpty()).isTrue();
		assertThat(removedState.isEmpty()).isTrue();
		assertThat(removedSEntry.isEmpty()).isTrue();
	}

	@Test
	public void prependAndSet_onNull() {
		// arrange
		Node node = new Node(0);
		// act
		node.prependChildAndSet(childNode);
		node.prependConstraintAndSet(pathConstraint);
		node.prependTransitionAndSet(transition);
		node.prependActiveStatementAndSet(statement);
		node.prependSymbolicMemoryEntryAndSet(symbolicMemoryEntry);

		// assert
		assertThat(node.getChildren().getValue()).isEqualTo(childNode);
		assertThat(node.getChildren().getNext().isEmpty()).isTrue();

		assertThat(node.getPathConstraint().getValue()).isEqualTo(pathConstraint);
		assertThat(node.getPathConstraint().getNext().isEmpty()).isTrue();

		assertThat(node.getLastTransitions().getValue()).isEqualTo(transition);
		assertThat(node.getLastTransitions().getNext().isEmpty()).isTrue();

		assertThat(node.getActiveStatements().getValue()).isEqualTo(statement);
		assertThat(node.getActiveStatements().getNext().isEmpty()).isTrue();

		assertThat(node.getSymbolicMemoryStore().getValue()).isEqualTo(symbolicMemoryEntry);
		assertThat(node.getSymbolicMemoryStore().getNext().isEmpty()).isTrue();
	}

	@Test
	public void prependAndSet_WithNull() {
		// arrange
		Node node = new Node(0);
		node.setChildren(childNodeIMList);
		node.setPathConstraint(pathConstraintIMList);
		node.setLastTransitions(transitionIMList);
		node.setActiveStatements(statementIMList);
		node.setSymbolicMemoryStore(symbolicMemoryStoreIMList);
		// act
		node.prependChildAndSet(null);
		node.prependConstraintAndSet(null);
		node.prependTransitionAndSet(null);
		node.prependActiveStatementAndSet(null);
		node.prependSymbolicMemoryEntryAndSet(null);

		// assert
		assertThat(node.getChildren()).isEqualTo(childNodeIMList);
		assertThat(node.getPathConstraint()).isEqualTo(pathConstraintIMList);
		assertThat(node.getLastTransitions()).isEqualTo(transitionIMList);
		assertThat(node.getActiveStatements()).isEqualTo(statementIMList);
		assertThat(node.getSymbolicMemoryStore()).isEqualTo(symbolicMemoryStoreIMList);
	}

	@Test
	public void _addChildAndSetAsFather() {
		// arrange
		Node father = new Node(0);
		Node child = new Node(0);
		// act
		father.addChildAndSetAsFather(child);

		// assert
		assertThat(father.getChildren().getValue()).isEqualTo(child);
		assertThat(father.getChildren().getNext().isEmpty()).isTrue();

		assertThat(child.getParent()).isEqualTo(father);
	}

	@Test
	public void _addChildAndSetAsFatherWhenNull() {
		// arrange
		Node father = new Node(0);

		// act
		father.addChildAndSetAsFather(null);

		// assert
		assertThat(father.getChildren()).isNull();
	}

	@Test
	public void _addChildAndSetAsFatherWhenNullAndOneChildExists() {
		// arrange
		Node father = new Node(0);
		Node child = new Node(0);
		// act
		father.addChildAndSetAsFather(child);
		father.addChildAndSetAsFather(null);

		// assert
		assertThat(father.getChildren().getValue()).isEqualTo(child);
		assertThat(father.getChildren().getNext().isEmpty()).isTrue();
	}

}
