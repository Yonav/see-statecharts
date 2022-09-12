package com.yakindu.sct.se.test.unit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
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
import com.yakindu.sct.se.util.NodeUtil;

@RunWith(MockitoJUnitRunner.class)
public class NodeServiceTest {

	// child parameter
	@Mock
	Transition newLastTransition;
	@Mock
	Expression newPathConstraint;
	@Mock
	ImmutableList<Expression> newStatementBlock;
	@Mock
	VariableDefinition newSEntry;

	// parent parameter
	ImmutableList<Transition> transitionIMList = ImmutableList.create();
	ImmutableList<Expression> expressionIMList = ImmutableList.create();
	ImmutableList<Node> childNodeIMList = ImmutableList.create();
	ImmutableList<VariableDefinition> symbolicMemoryStore = ImmutableList.create();
	@Mock
	IRule newAppliedRule, ruleTwo;
	Set<IRule> rules;

	@Before
	public void setup() {
		rules = new HashSet<IRule>(Arrays.asList(newAppliedRule, ruleTwo));
	}

	@Test
	public void createChildFromParent_fullInput() {
		// arrange
		Node parent = new Node(0);
		parent.setChildren(childNodeIMList);
		parent.setLastTransitions(transitionIMList);
		parent.setPathConstraint(expressionIMList);
		parent.setSymbolicMemoryStore(symbolicMemoryStore);
		parent.setAppliedRule(newAppliedRule);

		// act
		Node actualChild = NodeUtil.createChildFromParent(1, parent, newLastTransition, newPathConstraint,
				newStatementBlock, newSEntry);

		// assert
		assertHelper(parent, actualChild, newLastTransition, newAppliedRule, newPathConstraint, newStatementBlock,
				newSEntry);

	}

	@Test
	public void createChildFromParent_nullInput() {
		// arrange
		Node parent = new Node(0);
		parent.setChildren(childNodeIMList);
		parent.setLastTransitions(transitionIMList);
		parent.setPathConstraint(expressionIMList);
		parent.setSymbolicMemoryStore(symbolicMemoryStore);
		parent.setAppliedRule(newAppliedRule);

		// act
		Node actualChild = NodeUtil.createChildFromParent(1, parent, null, null, null, null);

		// assert
		assertHelper(parent, actualChild, null, newAppliedRule, null, null, null);

	}

	@Test
	public void createChildFromParent_parentNull() {
		// arrange

		// act
		Node actualChild = NodeUtil.createChildFromParent(1, null, newLastTransition, newPathConstraint,
				newStatementBlock, newSEntry);

		// assert
		assertThat(actualChild).isNull();
	}

	public void assertHelper(Node parent, Node child, Transition newLastTransition, IRule newAppliedRule,
			Expression newPathConstraint, ImmutableList<Expression> newStatementBlock, VariableDefinition newSEntry) {
		if (newLastTransition != null) {
			assertThat(child.getLastTransitions().getValue()).isEqualTo(newLastTransition);
			assertThat(child.getLastTransitions().getNext()).isEqualTo(parent.getLastTransitions());
		} else {
			assertThat(child.getLastTransitions()).isEqualTo(parent.getLastTransitions());
		}

		if (newPathConstraint != null) {
			assertThat(child.getPathConstraint().getValue()).isEqualTo(newPathConstraint);
			assertThat(child.getPathConstraint().getNext()).isEqualTo(parent.getPathConstraint());
		} else {
			assertThat(child.getPathConstraint()).isEqualTo(parent.getPathConstraint());
		}

		if (newSEntry != null) {
			assertThat(child.getSymbolicMemoryStore().getValue()).isEqualTo(newSEntry);
			assertThat(child.getSymbolicMemoryStore().getNext()).isEqualTo(parent.getSymbolicMemoryStore());
		} else {
			assertThat(child.getSymbolicMemoryStore()).isEqualTo(parent.getSymbolicMemoryStore());
		}

		assertThat(child.getChildren()).isNull();

		assertThat(child.getActiveStatements()).isEqualTo(newStatementBlock);

		assertThat(parent.getChildren().getValue()).isEqualTo(child);

		assertThat(parent.getAppliedRule()).isEqualTo(newAppliedRule);

		assertThat(child.getParent()).isEqualTo(parent);
	}
}
