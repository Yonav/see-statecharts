package com.yakindu.sct.se.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.yakindu.sct.generator.core.console.IConsoleLogger;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;

import com.google.inject.Inject;
import com.yakindu.sct.se.engine.SymbolicExecutionEngine;
import com.yakindu.sct.se.engine.strategy.ConstructorStrategy;
import com.yakindu.sct.se.engine.strategy.SETContext;
import com.yakindu.sct.se.engine.strategy.Strategy;
import com.yakindu.sct.se.model.Node;
import com.yakindu.sct.se.rules.IRule;
import com.yakindu.sct.se.rules.cycleElimination.LocalReactionCycleEliminationRule;
import com.yakindu.sct.se.rules.cycleElimination.TransitionCycleEliminationRule;
import com.yakindu.sct.se.rules.statement.pathConstraints.AssignmentConditionalRule;
import com.yakindu.sct.se.rules.statement.pathConstraints.AssignmentDefaultRule;
import com.yakindu.sct.se.rules.statement.pathConstraints.AssignmentDivisionRule;
import com.yakindu.sct.se.rules.statement.pathConstraints.DefaultStatementRule;
import com.yakindu.sct.se.rules.statement.simplifying.ConditionalRule;
import com.yakindu.sct.se.rules.statement.simplifying.PrimitiveValueRule;
import com.yakindu.sct.se.rules.statement.simplifying.SequenceBlockRule;
import com.yakindu.sct.se.rules.transition.TransitionRule;
import com.yakindu.sct.se.util.SELogger;

/**
 * Analysis of the Engine supports reachability analysis
 * 
 * @author jwielage
 *
 */
public class Analysis {

	@Inject
	private IConsoleLogger logger = new SELogger();

	private final Strategy strategy;
	private final AnalysisResult analysisResult;

	private final SymbolicExecutionEngine engine;
	private final ConstructorStrategy constructorStrategy;
	private final SETContext globalContext;

	private final Statechart statechart;
	private final IProgressMonitor monitor;

	public static Analysis analyse(Statechart statechart) {
		return new Analysis(statechart, Strategy.DEFAULT, new NullProgressMonitor());
	}

	public static Analysis analyse(Statechart statechart, Strategy mode) {
		return new Analysis(statechart, mode, new NullProgressMonitor());
	}

	public static Analysis analyse(Statechart statechart, IProgressMonitor monitor) {
		return new Analysis(statechart, Strategy.DEFAULT, monitor);
	}

	public static Analysis analyse(Statechart statechart, Strategy mode, IProgressMonitor monitor) {
		return new Analysis(statechart, mode, monitor);
	}

	private Analysis(Statechart statechart, Strategy mode, IProgressMonitor monitor) {
		super();
		this.monitor = monitor;
		this.statechart = statechart;
		this.strategy = mode;
		this.globalContext = SETContext.createFor(this.statechart, mode);
		this.engine = new SymbolicExecutionEngine(this.statechart, this.globalContext, initializeRulesForEngine());

		this.analysisResult = AnalysisResult.initWithStatechartAndStartTimer(this.statechart, globalContext);
		this.constructorStrategy = mode.getConstructionStrategy();

		startAnalysis();
	}

	// ==========================================================================================

	private void startAnalysis() {
		// analyse
		analyzeNonReachableElements();

		// finish analysis
		analysisResult.completeAnalysis();
		globalContext.getSolver().close();
	}

	private void interruptAnalysis() {
		analysisResult.interruptAnalysis();
		globalContext.getSolver().close();
	}

	private void analyzeNonReachableElements() {

		monitor.beginTask("Construct SET", IProgressMonitor.UNKNOWN);
		Node momentaryNode = constructorStrategy.getNextNodeToTreat(engine);

		List<Node> nodesToAnalyzeForReachability = new ArrayList<>();

		while (momentaryNode != null) {
			if (monitor.isCanceled()) {
				interruptAnalysis();
				throw new OperationCanceledException();
			}

			constructorStrategy.oneStep(engine);

			if (IRule.isOfRuleType(momentaryNode, TransitionRule.class, TransitionCycleEliminationRule.class,
					LocalReactionCycleEliminationRule.class)) {
				// those nodes have to be analyzed for reachability
				nodesToAnalyzeForReachability.add(momentaryNode);
			}

			if (nodesToAnalyzeForReachability.size() >= 15) {
				checkForAnalyzableNodes(nodesToAnalyzeForReachability);
			}

			if (analysisResult.isEverythingReachable()) {
				// everything is reachable, construction of set can be aborted in context of
				// reachability analysis
				return;
			}

			momentaryNode = constructorStrategy.getNextNodeToTreat(engine);

			monitor.worked(1);

			// to update time
			globalContext.getTimer().getOverallTime().stopTime();
			globalContext.getTimer().getOverallTime().startTime();

			if (globalContext.getTimer().getOverallTime().getPassedTimeAsSeconds() > strategy.getTimeConstraint()) {
				checkForAnalyzableNodes(nodesToAnalyzeForReachability);
				interruptAnalysis();
				return;
			}

		}
		checkForAnalyzableNodes(nodesToAnalyzeForReachability);
		monitor.done();

	}

	private void checkForAnalyzableNodes(List<Node> nodesToHandleRegardingSatisfiability) {
		Iterator<Node> iterator = nodesToHandleRegardingSatisfiability.iterator();

		while (iterator.hasNext()) {
			Node currentNode = iterator.next();

			// setzt voraus, dass überall satisfiability bekannt ist...
			if (!currentNode.registeredSatisfiability()) {
				continue;
			}

			if (!currentNode.getSatisfiability().isUnsatisfiable()) {

				// for cycleElimination
				if (IRule.isOfRuleType(currentNode, TransitionCycleEliminationRule.class,
						LocalReactionCycleEliminationRule.class)) {
					if (currentNode.getChildren() != null && !currentNode.getChildren().isEmpty()) {
						currentNode.getChildren().getValue().getBlockedReactions()
								.consumeValuesUntilEqual(currentNode.getBlockedReactions(), reaction -> {
									if (reaction instanceof Transition) {
										analysisResult.addReachableTransition(((Transition) reaction));
									}
								});
					}
				}
				if (currentNode.getLastTransitions() == null || currentNode.getLastTransitions().isEmpty()) {
					// kann entfernt werden
					continue;
				}

				Transition lastTransition = currentNode.getLastTransitions().getValue();
				analysisResult.addReachableTransition(lastTransition);
				String aSD = "";

			} else {
				logger.log("is unsat in analysis: " + currentNode.getNodeCounter());
			}

			iterator.remove();

		}

	}

	// ============================================================================================================
	// Set the right rules
	// ============================================================================================================
	private List<IRule> initializeRulesForEngine() {
		TransitionRule transitionRule = TransitionRule.INSTANCE;
		LocalReactionCycleEliminationRule lrEliminiationR = LocalReactionCycleEliminationRule.INSTANCE;
		AssignmentConditionalRule assignmentConditionalRule = AssignmentConditionalRule.INSTANCE;
		AssignmentDivisionRule assignmentDivisionRule = AssignmentDivisionRule.INSTANCE(false);
		AssignmentDefaultRule assignmentDefaultRule = AssignmentDefaultRule.INSTANCE;
		ConditionalRule conditionalRule = ConditionalRule.INSTANCE;
		DefaultStatementRule defaultStatementRule = DefaultStatementRule.INSTANCE;
		SequenceBlockRule sequenceRule = SequenceBlockRule.INSTANCE;
		PrimitiveValueRule primRule = PrimitiveValueRule.INSTANCE;

		ArrayList<IRule> result = new ArrayList<IRule>(
				Arrays.asList(transitionRule, lrEliminiationR, assignmentConditionalRule, assignmentDefaultRule,
						conditionalRule, defaultStatementRule, primRule, sequenceRule, assignmentDivisionRule));

		TransitionCycleEliminationRule cycleEliminationRule = this.strategy.getAffiliatedCycleEliminationRule();
		if (cycleEliminationRule != null) {
			result.add(cycleEliminationRule);
		}
		return result;
	}

	// ====================================
	public SymbolicExecutionEngine getEngine() {
		return engine;
	}

	public AnalysisResult getFinishedResult() {
		if (!analysisResult.isAnalysisCompleted()) {
			return null;
		}
		return analysisResult;
	}
}
