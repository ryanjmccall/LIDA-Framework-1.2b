/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.proceduralmemory;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.actionselection.Action;
import edu.memphis.ccrg.lida.framework.shared.RootableNode;
import edu.memphis.ccrg.lida.framework.shared.activation.LearnableImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.proceduralmemory.ProceduralMemoryImpl.ConditionType;

/**
 * Default implementation of {@link Scheme}.
 * @author Ryan J. McCall
 * @author Javier Snaider
 */
public class SchemeImpl extends LearnableImpl implements Scheme {
	
	private static final Logger logger = Logger.getLogger(SchemeImpl.class.getCanonicalName());
	private static int idCounter = 0;//TODO Factory support for Scheme
		
	private String label;
	private int id;
	private boolean isInnate;
	private int numExecutions;
	private int numSuccessfulExecutions;
	
	private Action action;
	private Map<Object,Condition> context = new ConcurrentHashMap<Object,Condition>();
	private Map<Object,Condition> addingList = new ConcurrentHashMap<Object,Condition>();
	private Map<Object,Condition> deletingList = new ConcurrentHashMap<Object,Condition>();
	private ProceduralMemoryImpl pm;
	
	/*
	 * The weight of the context in the calculation of scheme salience
	 */
	private static double contextWeight = 1.0;
	/*
	 * The weight of the adding list in the calculation of scheme salience
	 */
	private static double addingListWeight = 1.0;
	/*
	 * Threshold for Schemes to be reliable
	 */
	private static double reliabilityThreshold = 0.0; 

	/**
	 * Constructs a new scheme with default values
	 */
	SchemeImpl(){
		id = idCounter++;
	}
	
	/**
	 * Intended for testing only
	 * @Deprecated to be removed
	 * @param label {@link String}
	 * @param a {@link Action}
	 */
	@Deprecated
	SchemeImpl(String label, Action a){
		this.label = label;
		this.action = a;
	}
	
	/**
	 * Intended for testing only
	 * @Deprecated to be removed
	 * @param id Scheme's id
	 */
	@Deprecated
	SchemeImpl(int id){
		this();
		this.id = id;
	}
	
	/**
	 * Sets the {@link ProceduralMemoryImpl} to which this {@link SchemeImpl} belongs.
	 * @param pm a {@link ProceduralMemoryImpl}
	 * @see ProceduralMemoryImpl#getNewScheme(Action)
	 */
	void setProceduralMemory(ProceduralMemoryImpl pm){
		this.pm = pm;
	}

	/**
	 * @param w the contextWeight to set
	 */
	static void setContextWeight(double w) {
		if(w >= 0.0){
			SchemeImpl.contextWeight = w;
		}else{
			logger.log(Level.WARNING, "Context weight must be positive", TaskManager.getCurrentTick());
		}
	}

	/**
	 * @return the contextWeight
	 */
	static double getContextWeight() {
		return contextWeight;
	}

	/**
	 * @param w the addingListWeight to set
	 */
	static void setAddingListWeight(double w) {
		if(w >= 0.0){
			SchemeImpl.addingListWeight = w;
		}else{
			logger.log(Level.WARNING, "Adding list weight must be positive", TaskManager.getCurrentTick());
		}
	}

	/**
	 * @return the addingListWeight
	 */
	static double getAddingListWeight() {
		return addingListWeight;
	}
	
	//Scheme methods
	
	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public void actionExecuted() {
		numExecutions++;
	}

	@Override
	public void actionSuccessful() {
		numSuccessfulExecutions++;		
	}

	@Override
	public double getReliability() {
		return (numExecutions > 0) ? 
				((double) numSuccessfulExecutions)/numExecutions : 0.0;
	}

	@Override
	public boolean isReliable() {
		return getReliability() >= reliabilityThreshold;
	}
	
	@Override
	public void setInnate(boolean in) {
		isInnate = in;
	}

	@Override
	public boolean isInnate() {
		return isInnate;
	}

	/**
	 * Gets the average activation of this unit's context conditions.
	 * @return average activation of unit's context
	 */
	protected double getAverageContextActivation(){
		if(context.size() == 0){
			return 0.0;
		}
		double aggregateActivation = 0.0;
		for(Condition c: context.values()){
			//if required to use Condition weight, use it here
			aggregateActivation += c.getActivation();
		}
		return aggregateActivation / context.size();
	}
		
	/**
	 * Gets the average net desirability of this unit's adding list
	 * @return average net desirability of this unit's adding list
	 */
	protected double getAverageAddingListNetDesirability(){
		int numConditions = 0;
		double aggregateNetDesirability = 0.0;
		for(Condition c: addingList.values()){
			if(c instanceof RootableNode){
				//if required to use Condition weight, use it here
				aggregateNetDesirability += ((RootableNode) c).getNetDesirability(); 
				numConditions++;
			}
		}
		if(numConditions == 0){
			return 0.0;
		}else{
			return aggregateNetDesirability / numConditions;
		}
	}
	
	
	//Learnable override
	@Override
	public void decayBaseLevelActivation(long ticks){
		if(!isInnate){
			super.decayBaseLevelActivation(ticks);
		}
	}
	
	@Override
	public double getActivation(){
		double overallSalience = contextWeight * getAverageContextActivation() +
									addingListWeight * getAverageAddingListNetDesirability();
		return (overallSalience > 1.0)? 1.0:overallSalience;		
	}

	@Override
	public int getNumExecutions() {
		return numExecutions;
	}
	
	/**
	 * Gets reliabilityThreshold
	 * @return threshold of reliability
	 */
	static double getReliabilityThreshold() {
		return reliabilityThreshold;
	}

	/**
	 * Sets reliabilityThreshold
	 * @param t threshold of reliability
	 */
	static void setReliabilityThreshold(double t) {
		SchemeImpl.reliabilityThreshold = t;
	}

	@Override
	public boolean addCondition(Condition c,ConditionType type) {
		boolean wasAdded = false;
		Condition condition = pm.addCondition(c);
		if(condition != null){
			Map<Object,Condition> map = null;
			switch(type){
				case CONTEXT:
					map = context;
					break;
				case ADDINGLIST:
					map = addingList;
					break;
				case DELETINGLIST:
					map = deletingList;
					break;
				case NEGATEDCONTEXT:
					break;
			}
			if(map != null){
				wasAdded = (map.put(condition.getConditionId(),condition) == null);
			}
			if(!wasAdded){
				logger.log(Level.WARNING, "Error adding condition {1}. Condition added to ProceduralMemory but not to Scheme.", 
						new Object[]{TaskManager.getCurrentTick(),c});
			}else{
				pm.indexScheme(this,condition,type);
			}
		}
		return wasAdded;
	}
	
	//ProceduralUnit methods
	@Override
	public void setAction(Action a) {
		action = a;
	}

	@Override
	public Collection<Condition> getContextConditions() {
		Collection<Condition> aux = context.values();
		return (aux == null)? null : Collections.unmodifiableCollection(aux);
	}

	@Override
	public Collection<Condition> getAddingList() {
		Collection<Condition> aux = addingList.values();
		return (aux == null)? null : Collections.unmodifiableCollection(aux);
	}

	@Override
	public Collection<Condition> getDeletingList() {
		Collection<Condition> aux = deletingList.values();
		return (aux == null)? null : Collections.unmodifiableCollection(aux);
	}

	@Override
	public Action getAction() {
		return action;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * Gets context condition specified by id
	 * @param id condition id
	 * @return a {@link Condition} or null
	 */
	Condition getContextCondition(Object id) {
		return context.get(id);
	}
	
	//Object methods
	@Override
	public boolean equals(Object o) {
		if (o instanceof Scheme) {
			return ((Scheme) o).getId() == id;
		}
		return false;		
	}
	
	@Override
	public int hashCode() {
		return (int) id;
	}	
	
	@Override
	public String toString(){
		return getLabel() + "-" + getId();
	}
}