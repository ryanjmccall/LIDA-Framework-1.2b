/*******************************************************************************
 * Copyright (c) 2009, 2010 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/

package edu.memphis.ccrg.lida.actionselection.behaviornetwork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.actionselection.ActionSelection;
import edu.memphis.ccrg.lida.actionselection.ActionSelectionListener;
import edu.memphis.ccrg.lida.actionselection.Behavior;
import edu.memphis.ccrg.lida.actionselection.PreafferenceListener;
import edu.memphis.ccrg.lida.framework.FrameworkModuleImpl;
import edu.memphis.ccrg.lida.framework.ModuleListener;
import edu.memphis.ccrg.lida.framework.initialization.Initializable;
import edu.memphis.ccrg.lida.framework.shared.ConcurrentHashSet;
import edu.memphis.ccrg.lida.framework.shared.ElementFactory;
import edu.memphis.ccrg.lida.framework.strategies.DecayStrategy;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTaskImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.proceduralmemory.Condition;
import edu.memphis.ccrg.lida.proceduralmemory.ProceduralMemoryListener;

/**
 * A Behavior Network implementation of {@link ActionSelection} based on the
 * ideas of Maes' original. This implementation is integrated into the
 * framework, operating asynchronously and also taking the conscious broadcast
 * as input. Maes' called the input "environment".
 * 
 * @author Ryan J. McCall
 * @author Javier Snaider
 */
public class BehaviorNetwork extends FrameworkModuleImpl implements
		ActionSelection, ProceduralMemoryListener {

	private static final Logger logger = Logger.getLogger(BehaviorNetwork.class.getCanonicalName());
	private static final double DEFAULT_INITIAL_CANDIDATE_THRESHOLD = 0.9;
	private static final double DEFAULT_BROADCAST_EXCITATION_FACTOR = 0.05;
	private static final double DEFAULT_SUCCESSOR_EXCITATION_FACTOR = 0.04;
	private static final double DEFAULT_PREDECESSOR_EXCITATION_FACTOR = 0.1;
	private static final double DEFAULT_CONFLICTOR_EXCITATION_FACTOR = 0.04;
	private static final double DEFAULT_CONTEXT_SATISFACTION_THRESHOLD = 0.0;
	private static final String DEFAULT_CANDIDATE_THRESHOLD_DECAY = "defaultDecay";
	private static final String DEFAULT_BEHAVIOR_DECAY_NAME = "behaviorDecay";

	/*
	 * Current threshold for a behavior to be a candidate for selection (or to
	 * be 'active') (akin to THETA from Maes' Implementation)
	 */
	private double candidateThreshold;

	/*
	 * The initial value for candidate threshold. candidate threshold is reset
	 * to this initial value after every action selection.
	 */
	private double initialCandidateThreshold = DEFAULT_INITIAL_CANDIDATE_THRESHOLD;

	/*
	 * Amount of excitation from broadcast (environment) (akin to PHI from Maes'
	 * Implementation)
	 */
	private double broadcastExcitationFactor = DEFAULT_BROADCAST_EXCITATION_FACTOR;

	/*
	 * Scales the strength of activation passed from behavior to successor.
	 */
	private double successorExcitationFactor = DEFAULT_SUCCESSOR_EXCITATION_FACTOR;

	/*
	 * Scales the strength of activation passed from behavior to predecessor.
	 */
	private double predecessorExcitationFactor = DEFAULT_PREDECESSOR_EXCITATION_FACTOR;

	/*
	 * Scales the strength of activation passed from behavior to conflictor.
	 */
	private double conflictorExcitationFactor = DEFAULT_CONFLICTOR_EXCITATION_FACTOR;
	
	/* 
	 * Amount of activation a context condition must have to be satisfied
	 */
	private double contextSatisfactionThreshold = DEFAULT_CONTEXT_SATISFACTION_THRESHOLD;

	/*
	 * Function by which the behavior activation threshold is reduced
	 */
	private DecayStrategy thresholdReductionStrategy;

	/*
	 * Listeners of this action selection
	 */
	private List<ActionSelectionListener> actionSelectionListeners = new ArrayList<ActionSelectionListener>();

	/*
	 * Preafference listeners of this action selection
	 */
	private List<PreafferenceListener> preafferenceListeners = new ArrayList<PreafferenceListener>();

	/*
	 * All the behaviors currently in this behavior network.
	 */
	private ConcurrentMap<Integer, Behavior> behaviors = new ConcurrentHashMap<Integer, Behavior>();

	/*
	 * Map of behaviors indexed by the elements appearing in their context
	 * conditions.
	 */
	private ConcurrentMap<Condition, Set<Behavior>> behaviorsByContextCondition = new ConcurrentHashMap<Condition, Set<Behavior>>();

	/*
	 * Map of behaviors indexed by the elements appearing in their add list.
	 */
	private ConcurrentMap<Condition, Set<Behavior>> behaviorsByAddingItem = new ConcurrentHashMap<Condition, Set<Behavior>>();

	/*
	 * Map of behaviors indexed by the elements appearing in their delete list.
	 */
	private ConcurrentMap<Condition, Set<Behavior>> behaviorsByDeletingItem = new ConcurrentHashMap<Condition, Set<Behavior>>();
	/* 
	 * DecayStrategy used to decay all behaviors in the network.
	 * TODO make this part of a new "BehaviorDef" in the element factory
	 */
	private DecayStrategy behaviorDecayStrategy;

	/**
	 * Default constructor
	 */
	public BehaviorNetwork() {
		super();
	}

	/**
	 * This module can initialize the following parameters:<br><br/>
	 * 
	 * <b>actionselection.broadcastExcitationFactor</b> - double, the percentage of the activation that broadcast elements send to behaviors whose context and/or result intersect with said elements<br/>
	 * <b>actionselection.successorExcitationFactor</b> - double, the percentage of the activation that behaviors receive from their successors<br/>
	 * <b>actionselection.predecessorExcitationFactor</b> - double, the percentage of activation that behaviors receive from their predecessors<br/>
	 * <b>actionselection.conflictorExcitationFactor</b> - double, the percent of activation behaviors receive from conflicting behaviors<br/>
	 * <b>actionselection.contextSatisfactionThreshold</b> - double, amount of activation a context condition must have to be satisfied<br/>
	 * <b>actionselection.initialCandidateThreshold</b> - double, the initial value for candidate threshold. candidate threshold is reset to this initial value after every action selection<br/>
	 * <b>actionselection.candidateThresholdDecayName</b> - string, factory name of the DecayStrategy used to decay the candidate threshold.
	 * <b>actionselection.behaviorDecayName</b> - string, factory name of the DecayStrategy used to decay all behaviors.
	 *  
	 * If default LinearDecayStrategy is used its slope determine the amount the selection threshold will be reduced in each cycle.<br/>
	 * 
	 * @see Initializable
	 */
	@Override
	public void init() {
		broadcastExcitationFactor = getParam(
				"actionselection.broadcastExcitationFactor",
				DEFAULT_BROADCAST_EXCITATION_FACTOR);
		successorExcitationFactor = getParam(
				"actionselection.successorExcitationFactor",
				DEFAULT_SUCCESSOR_EXCITATION_FACTOR);
		predecessorExcitationFactor = getParam(
				"actionselection.predecessorExcitationFactor",
				DEFAULT_PREDECESSOR_EXCITATION_FACTOR);
		conflictorExcitationFactor = getParam(
				"actionselection.conflictorExcitationFactor",
				DEFAULT_CONFLICTOR_EXCITATION_FACTOR);
		contextSatisfactionThreshold = getParam("actionselection.contextSatisfactionThreshold",
					DEFAULT_CONTEXT_SATISFACTION_THRESHOLD);		
		initialCandidateThreshold = getParam(
				"actionselection.initialCandidateThreshold",
				DEFAULT_INITIAL_CANDIDATE_THRESHOLD);
		candidateThreshold = initialCandidateThreshold;
		ElementFactory factory = ElementFactory.getInstance();
		String name = getParam(
				"actionselection.candidateThresholdDecayName",
				DEFAULT_CANDIDATE_THRESHOLD_DECAY);
		thresholdReductionStrategy = factory.getDecayStrategy(name);
		
		name = getParam(
				"actionselection.behaviorDecayName",
				DEFAULT_BEHAVIOR_DECAY_NAME);
		behaviorDecayStrategy = factory.getDecayStrategy(name);

		taskSpawner.addTask(new BehaviorNetworkBackgroundTask());
	}
	
	@Override
	public void addListener(ModuleListener l) {
		if (l instanceof ActionSelectionListener) {
			addActionSelectionListener((ActionSelectionListener) l);
		}else{
			logger.log(Level.WARNING, "Cannot add listener {1}", 
					new Object[]{TaskManager.getCurrentTick(), l});
		}
	}

	@Override
	public void addActionSelectionListener(ActionSelectionListener l) {
		actionSelectionListeners.add(l);
	}

	@Override
	public void addPreafferenceListener(PreafferenceListener l) {
		preafferenceListeners.add(l);
	}

	@Override
	public void receiveBehavior(Behavior b) {
		if(b == null){
			logger.log(Level.WARNING, "Received null behavior.", 
					TaskManager.getCurrentTick());
		}else{
			b.setDecayStrategy(behaviorDecayStrategy);
			behaviors.put(b.getId(), b);
			indexBehaviorByElements(b, b.getContextConditions(),
					behaviorsByContextCondition);
			indexBehaviorByElements(b, b.getAddingList(), behaviorsByAddingItem);
			indexBehaviorByElements(b, b.getDeletingList(), behaviorsByDeletingItem);
		}
	}

	/*
	 * Utility method to index the behaviors into a map by elements.
	 */
	private static void indexBehaviorByElements(Behavior b,
			Collection<Condition> elements, Map<Condition, Set<Behavior>> map) {
		for (Condition element : elements) {
			synchronized (element) {
				Set<Behavior> values = map.get(element);
				if (values == null) {
					values = new ConcurrentHashSet<Behavior>();
					map.put(element, values);
				}
				values.add(b);
			}
		}
	}

	private class BehaviorNetworkBackgroundTask extends FrameworkTaskImpl {
		@Override
		public void runThisFrameworkTask() {
			passActivationFromSchemes();
			passActivationAmongBehaviors();
			attemptActionSelection();
			logger.log(Level.FINEST, "BehaviorNetwork completes one execution cycle.",
					TaskManager.getCurrentTick());
		}
	}

	/**
	 * Intended for testing only.
	 * Passes activation from Schemes to Behaviors.
	 */
	void passActivationFromSchemes() {
		for (Behavior b : behaviors.values()) {
			double amount = b.getScheme().getActivation()
					* broadcastExcitationFactor;
			b.excite(amount);
		}
	}

	/**
	 * Intended for testing only.
	 * Passes activation from successors to predecessors and vice versa also to conflictors.
	 * This implementation is different to the original Maes' code. Here the
	 * activation is updated directly and the new value is used to compute the
	 * passing activation.
	 */
	void passActivationAmongBehaviors() {
		Object[] keyPermutation = getRandomPermutation();
		for (Object key : keyPermutation) {
			Behavior b = behaviors.get(key);
			if (isAllContextConditionsSatisfied(b)) {
				passActivationToSuccessors(b);
			} else {
				passActivationToPredecessors(b);
			}
			passActivationToConflictors(b);
		}
	}
	
	/*
	 * Returns a random permutation of the keys of the behaviors in the network.
	 */
	private Object[] getRandomPermutation() {
		Object[] keys = (Object[]) behaviors.keySet().toArray();
		for (int i = 0; i < keys.length - 1; i++) {
			int swapPosition = (int) (Math.random() * (keys.length - i)) + i;
			Object stored = keys[i];
			keys[i] = keys[swapPosition];
			keys[swapPosition] = stored;
		}
		return keys;
	}

	/*
	 * Only excite successor if precondition is not yet satisfied.
	 */
	private void passActivationToSuccessors(Behavior b) {
		// For successors with positive conditions
		for (Condition addCondition : b.getAddingList()) {
			if(isContextConditionSatisfied(addCondition) == false){
				Set<Behavior> successors = getSuccessors(addCondition);
				if (successors != null) {
					auxPassActivationToSuccessors(b, addCondition, successors);
				}
			}
		}
	}

	private Set<Behavior> getSuccessors(Condition addingCondition) {
		return behaviorsByContextCondition.get(addingCondition);
	}

	/*
	 * Passes activation from b to its successors for a particular condition.
	 * Condition has been determined to be satisfied.
	 */
	private void auxPassActivationToSuccessors(Behavior b,Condition c,Set<Behavior> successors) {
		for (Behavior successor : successors) {
			if (successor != b) { //perhaps a behavior can use up one of its context condition
				double amount = b.getActivation()/getUnsatisfiedContextCount(successor)*successorExcitationFactor;
				successor.excite(amount);
				logger.log(Level.FINEST,
						   "Behavior {1} excites successor, {2}, amount: {3} because of {4}",
						   new Object[]{TaskManager.getCurrentTick(),b,successor,amount,c});
			}
		}
	}

	/*
	 * Don't bother exciting a predecessor for a precondition that is already
	 * satisfied.
	 */
	private void passActivationToPredecessors(Behavior b) {
		// For positive conditions
		for (Condition contextCond : b.getContextConditions()) {
			if (isContextConditionSatisfied(contextCond) == false) {
				Set<Behavior> predecessors = behaviorsByAddingItem.get(contextCond); 
				if (predecessors != null) {
					auxPassActivationPredecessors(b, contextCond, predecessors);
				}
			}
		}
	}

	private void auxPassActivationPredecessors(Behavior b, Condition c,
			Set<Behavior> predecessors) {
		for (Behavior predecessor : predecessors) {
			//think about being your own predecessor
			double amount = b.getActivation()/getUnsatisfiedContextCount(b)*predecessorExcitationFactor;
			predecessor.excite(amount);
			logger.log(Level.FINEST,
							"Behavior {1} excites predecessor, {2}, amount: {3} because of {4}",
							new Object[] { TaskManager.getCurrentTick(), b,
									predecessor, amount, c });
		}
	}

	private void passActivationToConflictors(Behavior b) {
		boolean isMutualConflict = false;
		for (Condition condition : b.getContextConditions()) {
			Set<Behavior> conflictors = behaviorsByDeletingItem.get(condition);
			if (conflictors != null) {
				for (Behavior conflictor : conflictors) {
					isMutualConflict = false;
					if ((b != conflictor)
							&& (b.getActivation() < conflictor.getActivation())) {
						for (Condition conflictorPreCondition : conflictor
								.getContextConditions()) {
							Set<Behavior> conflictorConflictors = behaviorsByDeletingItem
									.get(conflictorPreCondition);
							if (conflictorConflictors != null) {
								// check if there is a mutual conflict
								isMutualConflict = conflictorConflictors
										.contains(b);
								if (isMutualConflict) {
									break;
								}
							}
						}
					}
					// No mutual conflict then inhibit the conflictor of behavior
					if (!isMutualConflict) {
						auxPassActivationToConflictor(b, conflictor);
					}
				}//for each conflictor
			}
		}// for each context condition
	}

	private void auxPassActivationToConflictor(Behavior b, Behavior conflictor) {
		double inhibitionAmount = -(b.getActivation() * conflictorExcitationFactor)
				/ b.getContextConditions().size();
		conflictor.excite(inhibitionAmount);
		logger.log(Level.FINEST, "{1} inhibits conflictor {2} amount {3}",
				new Object[] { TaskManager.getCurrentTick(), b.getLabel(),
						conflictor.getLabel(), inhibitionAmount });
	}

	/**
	 * Tries to select one behavior to be executed. The chosen behavior's action is
	 * executed, its activation is set to 0.0 and the candidate threshold is
	 * reset. If no behavior is selected, the candidate threshold is reduced.
	 */
	void attemptActionSelection() {
		Behavior winningBehavior = selectBehavior(getSatisfiedBehaviors(),
				candidateThreshold);
		if (winningBehavior != null) {
			sendAction(winningBehavior);
			resetCandidateThreshold();
			winningBehavior.setActivation(0.0);
			logger.log(Level.FINER, "Behavior: {1} with action: {2} selected.",
					new Object[] { TaskManager.getCurrentTick(),
							winningBehavior, winningBehavior.getAction()});
		} else {
			reduceCandidateThreshold();
		}
	}

	private Set<Behavior> getSatisfiedBehaviors() {
		Set<Behavior> result = new HashSet<Behavior>();
		for (Behavior b : behaviors.values()) {
			if (isAllContextConditionsSatisfied(b)) {
				result.add(b);
			}
		}
		return result;
	}
	private boolean isAllContextConditionsSatisfied(Behavior b) {
		for(Condition c: b.getContextConditions()){
			if(c.getActivation() < contextSatisfactionThreshold){
				return false;
			}
		}
		return true;
	}
	
	private boolean isContextConditionSatisfied(Condition c) {
		return c.getActivation() >= contextSatisfactionThreshold;
	}

	private int getUnsatisfiedContextCount(Behavior b) {
		int count = 0;
		for(Condition c: b.getContextConditions()){
			if(c.getActivation() < contextSatisfactionThreshold){
				count++;
			}
		}
		return count;
	}

	/**
	 * Selects a behavior (containing an action) for execution. This
	 * implementation picks a behavior over candidate threshold and that has
	 * maximum activation (alpha) among all behaviors. If there is a tie then
	 * one behavior is selection randomly.
	 * 
	 * @param behaviors
	 *            {@link Collection} of behaviors currently available in the
	 *            module
	 * @param candidateThreshold
	 *            threshold for a behavior to be a candidate
	 * @return winning Behavior or null if none was chosen
	 */
	@Override
	public Behavior selectBehavior(Collection<Behavior> behaviors,
			double candidateThreshold) {
		double maxActivation = 0.0;
		List<Behavior> winners = new ArrayList<Behavior>();
		for (Behavior b : behaviors) {
			double currentActivation = b.getTotalActivation();
			if (currentActivation > candidateThreshold) {
				if (currentActivation > maxActivation) {
					winners.clear();
					winners.add(b);
					maxActivation = currentActivation;
				} else if (currentActivation == maxActivation) {
					winners.add(b);
				}
			}
		}
		Behavior winner = null;
		switch (winners.size()) {
		case 0:
			winner = null;
			break;
		case 1:
			winner = winners.get(0);
			logger.log(Level.FINER, "Winner: {1}  activation: {2}",
					new Object[] { TaskManager.getCurrentTick(),
							winner.getLabel(), maxActivation });
			break;
		default:
			winner = winners.get((int) (Math.random() * winners.size()));
			logger.log(Level.FINER, "Winner: {1}  activation: {2}",
					new Object[] { TaskManager.getCurrentTick(),
							winner.getLabel(), maxActivation });
		}
		return winner;
	}

	private void reduceCandidateThreshold() {
		//a value of 1 is passed so that the threshold is decayed one time 
		//based on the "slope" of the decay strategy
		candidateThreshold = thresholdReductionStrategy.decay(
				candidateThreshold, 1);
		logger.log(Level.FINEST, "Candidate threshold REDUCED to {1}",
						new Object[] { TaskManager.getCurrentTick(),
								candidateThreshold });
	}

	private void resetCandidateThreshold() {
		candidateThreshold = initialCandidateThreshold;
		logger.log(Level.FINEST, "Candidate threshold RESET to {1}",
						new Object[] { TaskManager.getCurrentTick(),
								candidateThreshold });
	}

	private void sendAction(Behavior b) {
		for (ActionSelectionListener l : actionSelectionListeners) {
			l.receiveAction(b.getAction());
		}
	}

	@Override
	public void decayModule(long t) {
		for (Behavior b : behaviors.values()) {
			b.decay(t);
			if (b.isRemovable()) {
				removeBehavior(b);
			}
		}
	}

	/*
	 * Removes specified behavior from the behavior net, severing all links to
	 * other behaviors and removing it from the specified stream which contained
	 * it.
	 */
	private void removeBehavior(Behavior b) {
		for (Condition c : b.getContextConditions()) {
			behaviorsByContextCondition.get(c).remove(b);
		}
		for (Condition c : b.getAddingList()) {
			behaviorsByAddingItem.get(c).remove(b);
		}
		for (Condition c : b.getDeletingList()) {
			behaviorsByDeletingItem.get(c).remove(b);
		}
		behaviors.remove(b.getId());
		logger.log(Level.FINEST, "Behavior {1} was removed from BehaviorNet.",
				new Object[] { TaskManager.getCurrentTick(), b });
	}

	@Override
	public Collection<Behavior> getBehaviors() {
		Collection<Behavior> aux = behaviors.values();
		return (aux == null)? null:Collections.unmodifiableCollection(aux);
	}
	
	@Override
	public Object getModuleContent(Object... params) {
		if("behaviors".equals(params[0])){
			return getBehaviors();
		}
		return null;
	}

	/**
	 * Gets candidate threshold.
	 * @return the current threshold a behavior must have to be a candidate for selection
	 */
	double getCandidateThreshold() {
		return candidateThreshold;
	}
}