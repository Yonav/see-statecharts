package com.yakindu.sct.se.model;

import org.yakindu.base.types.Expression;
import org.yakindu.sct.model.sgraph.Reaction;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.sgraph.Vertex;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import com.yakindu.sct.se.collection.ImmutableList;
import com.yakindu.sct.se.engine.strategy.SETContext;
import com.yakindu.sct.se.rules.IRule;
import com.yakindu.sct.se.solver.model.MaybeBool;

/**
 * Node of the SET
 * 
 * @author jwielage
 *
 */
public class Node {
	// BigInt
	private final int nodeCounter;
	private MaybeBool satisfiability;

	private ImmutableList<Transition> lastTransitions;
	private ImmutableList<Expression> pathConstraint;
	private ImmutableList<VariableDefinition> symbolicMemoryStore;

	private ImmutableList<Expression> activeStatements;

	private ImmutableList<Reaction> blockedReactions;

	private IRule appliedRule;

	private ImmutableList<Node> children;
	// parent
	private Node parent;

	//
	public Node(int nodeCounter) {
		this.nodeCounter = nodeCounter;
	}

	public int getNodeCounter() {
		return nodeCounter;
	}

	// ----------------------
	// satisfiability
	// ----------------------

	public MaybeBool getSatisfiability() {
		return satisfiability;
	}

	public boolean registeredSatisfiability() {
		return satisfiability != null;
	}

	public void setSatisfiability(MaybeBool satisfiability) {
		this.satisfiability = satisfiability;
	}

	// ----------------------
	// activeStates
	// ----------------------
	public ImmutableList<Transition> prependTransition(Transition transition) {
		return prependIMList(lastTransitions, transition);
	}

	public void prependTransitionAndSet(Transition transition) {
		lastTransitions = prependIMList(lastTransitions, transition);
	}

	public ImmutableList<Transition> removeLastTransitionValue() {
		return removeIMListValue(lastTransitions);
	}

	public ImmutableList<Transition> getLastTransitions() {
		return lastTransitions;
	}

	public void setLastTransitions(ImmutableList<Transition> lastTransition) {
		this.lastTransitions = lastTransition;
	}

	// ----------------------
	// pathConstraint
	// ----------------------
	public ImmutableList<Expression> prependConstraint(Expression expression) {
		return prependIMList(pathConstraint, expression);
	}

	public void prependConstraintAndSet(Expression expression) {
		pathConstraint = prependIMList(pathConstraint, expression);
	}

	public ImmutableList<Expression> removeLastPathConstraintValue() {
		return removeIMListValue(pathConstraint);
	}

	public ImmutableList<Expression> getPathConstraint() {
		return pathConstraint;
	}

	public void setPathConstraint(ImmutableList<Expression> pathConstraint) {
		this.pathConstraint = pathConstraint;
	}

	// ----------------------
	// symbolicMemoryStore
	// ----------------------

	public ImmutableList<VariableDefinition> prependSymbolicMemoryEntry(VariableDefinition entry) {
		return prependIMList(symbolicMemoryStore, entry);
	}

	public void prependSymbolicMemoryEntryAndSet(VariableDefinition entry) {
		symbolicMemoryStore = prependIMList(symbolicMemoryStore, entry);
	}

	public ImmutableList<VariableDefinition> removeLastSymbolicMemoryEntry() {
		return removeIMListValue(symbolicMemoryStore);
	}

	public ImmutableList<VariableDefinition> getSymbolicMemoryStore() {
		return symbolicMemoryStore;
	}

	public void setSymbolicMemoryStore(ImmutableList<VariableDefinition> symbolicMemoryStore) {
		this.symbolicMemoryStore = symbolicMemoryStore;
	}

	// ----------------------
	// activeStatements
	// ----------------------
	public ImmutableList<Expression> prependActiveStatement(Expression expression) {
		return prependIMList(activeStatements, expression);
	}

	public void prependActiveStatementAndSet(Expression expression) {
		activeStatements = prependIMList(activeStatements, expression);
	}

	public ImmutableList<Expression> removeTopActiveStatement() {
		return removeIMListValue(activeStatements);
	}

	public ImmutableList<Expression> getActiveStatements() {
		return activeStatements;
	}

	public void setActiveStatements(ImmutableList<Expression> activeStatements) {
		this.activeStatements = activeStatements;
	}

	// ----------------------
	// appliedRule
	// ----------------------
	public IRule getAppliedRule() {
		return appliedRule;
	}

	public void setAppliedRule(IRule appliedRule) {
		this.appliedRule = appliedRule;
	}

	// ----------------------
	// children
	// ----------------------
	public ImmutableList<Node> prependChild(Node node) {
		return prependIMList(children, node);
	}

	public void prependChildAndSet(Node node) {
		children = prependIMList(children, node);
	}

	public ImmutableList<Node> removeLastChildValue() {
		return removeIMListValue(children);
	}

	public ImmutableList<Node> getChildren() {
		return children;
	}

	public void setChildren(ImmutableList<Node> children) {
		this.children = children;
	}

	public void addChildAndSetAsFather(Node child) {
		prependChildAndSet(child);
		if (child != null) {
			child.setParent(this);
		}
	}

	// ----------------------
	// parent
	// ----------------------
	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	// ----------------------
	// blocked Transitions
	// ----------------------
	public boolean isBlocked(Reaction possibleBlockedReaction) {
		if (blockedReactions == null) {
			return false;
		}
		return blockedReactions.contains(possibleBlockedReaction);
	}

	public void blockTransition(Reaction reactionToBlock) {
		blockedReactions = prependIMList(blockedReactions, reactionToBlock);
	}

	public ImmutableList<Reaction> getBlockedReactions() {
		return blockedReactions;
	}

	public void setBlockedReactions(ImmutableList<Reaction> blockedReactions) {
		this.blockedReactions = blockedReactions;
	}

	// ----------------------
	// other
	// ----------------------

	@SuppressWarnings("unchecked")
	public <T> ImmutableList<T> prependIMList(ImmutableList<T> iMList, T value) {
		if (value == null) {
			return iMList;
		}
		if (iMList == null) {
			return ImmutableList.create(value);
		} else {
			return iMList.prepend(value);
		}
	}

	public <T> ImmutableList<T> removeIMListValue(ImmutableList<T> iMList) {
		if (iMList == null) {
			return null;
		}
		return iMList.getNext();
	}

	// Shouldn't be changed to include other attributes
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lastTransitions == null) ? 0 : lastTransitions.hashCode());
		result = prime * result + nodeCounter;
		result = prime * result + ((pathConstraint == null) ? 0 : pathConstraint.hashCode());
		result = prime * result + ((activeStatements == null) ? 0 : activeStatements.hashCode());
		result = prime * result + ((symbolicMemoryStore == null) ? 0 : symbolicMemoryStore.hashCode());
		return result;
	}

	// DO NOT CHANGE! Shouldn't be changed to include other attributes
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (lastTransitions == null) {
			if (other.lastTransitions != null)
				return false;
		} else if (!lastTransitions.equals(other.lastTransitions))
			return false;
		if (nodeCounter != other.nodeCounter)
			return false;
		if (pathConstraint == null) {
			if (other.pathConstraint != null)
				return false;
		} else if (!pathConstraint.equals(other.pathConstraint))
			return false;
		if (activeStatements == null) {
			if (other.activeStatements != null)
				return false;
		} else if (!activeStatements.equals(other.activeStatements))
			return false;
		if (symbolicMemoryStore == null) {
			if (other.symbolicMemoryStore != null)
				return false;
		} else if (!symbolicMemoryStore.equals(other.symbolicMemoryStore))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.valueOf(nodeCounter);
	}

	/**
	 * @return true, if node is currently in an RTC, false otherwise
	 */
	public boolean hasActiveStatements() {
		if (activeStatements != null && !activeStatements.isEmpty()) {
			return true;
		}
		return false;
	}

	public Vertex getActiveState(SETContext globalContext) {
		if (lastTransitions.getValue() != null) {
			return lastTransitions.getValue().getTarget();
		}
		return globalContext.getEntryState();
		// TODO Auto-generated method stub

	}

}
