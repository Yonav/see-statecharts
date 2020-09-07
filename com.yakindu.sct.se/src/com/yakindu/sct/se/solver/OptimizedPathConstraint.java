package com.yakindu.sct.se.solver;

import java.util.List;
import java.util.Set;

import org.yakindu.base.types.Expression;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

/**
 * Container holding optimied path constraints with corresponding variables
 * @author jwielage
 *
 */
public class OptimizedPathConstraint {

	private final List<Expression> assertions;
	private final Set<VariableDefinition> variables;
	
	public static OptimizedPathConstraint create(List<Expression> assertions, Set<VariableDefinition> variables) {
		return new OptimizedPathConstraint(assertions, variables);
	}
	
	private OptimizedPathConstraint(List<Expression> assertions, Set<VariableDefinition> variables) {
		this.variables = variables;
		this.assertions = assertions;
		
	}

	public List<Expression> getAssertions() {
		return assertions;
	}

	public Set<VariableDefinition> getVariables() {
		return variables;
	}
}
