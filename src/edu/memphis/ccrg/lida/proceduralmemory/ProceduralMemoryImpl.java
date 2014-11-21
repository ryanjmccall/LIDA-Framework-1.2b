/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.proceduralmemory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections15.collection.UnmodifiableCollection;

import edu.memphis.ccrg.lida.actionselection.Action;
import edu.memphis.ccrg.lida.actionselection.Behavior;
import edu.memphis.ccrg.lida.framework.FrameworkModuleImpl;
import edu.memphis.ccrg.lida.framework.ModuleListener;
import edu.memphis.ccrg.lida.framework.initialization.Initializable;
import edu.memphis.ccrg.lida.framework.shared.ConcurrentHashSet;
import edu.memphis.ccrg.lida.framework.shared.ElementFactory;
import edu.memphis.ccrg.lida.framework.shared.Node;
import edu.memphis.ccrg.lida.framework.shared.NodeStructure;
import edu.memphis.ccrg.lida.framework.shared.NodeStructureImpl;
import edu.memphis.ccrg.lida.framework.shared.RootableNode;
import edu.memphis.ccrg.lida.framework.shared.UnmodifiableNodeStructureImpl;
import edu.memphis.ccrg.lida.framework.strategies.DecayStrategy;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTaskImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.globalworkspace.BroadcastListener;
import edu.memphis.ccrg.lida.globalworkspace.Coalition;

/**
 * Default implementation of {@link ProceduralMemory}. Indexes scheme by context
 * elements for quick access. Assumes that the {@link Condition} of {@link Scheme} are {@link Node} only.
 * @author Ryan J. McCall
 * @author Javier Snaider
 */
public class ProceduralMemoryImpl extends FrameworkModuleImpl implements ProceduralMemory, BroadcastListener {

	private static final Logger logger = Logger.getLogger(ProceduralMemoryImpl.class.getCanonicalName());
	private static final ElementFactory factory = ElementFactory.getInstance();
	/**
	 * 
	 * The possible type of usage for a condition inside a {@link Scheme}
	 *
	 */
	public enum ConditionType{
		/**
		 * A {@link Condition} that is part of a scheme's context.
		 */
		CONTEXT, 
		/**
		 * A {@link Condition} that is part of a scheme's adding list.
		 */
		ADDINGLIST,
		/**
		 * A {@link Condition} that is part of a scheme's deleting list.
		 * Not yet supported.
		 */
		DELETINGLIST,
		/**
		 * A {@link Condition} that is part of a scheme's negated context.
		 * Not yet supported.
		 */
		NEGATEDCONTEXT
	};
	/*
	 * Schemes indexed by Nodes in their context.
	 */
	private Map<Object, Set<Scheme>> contextSchemeMap = new ConcurrentHashMap<Object, Set<Scheme>>();

	/*
	 * Schemes indexed by Nodes in their adding list.
	 */
	private Map<Object, Set<Scheme>> addingSchemeMap = new ConcurrentHashMap<Object, Set<Scheme>>();

	/*
	 * Set of all schemes current in the module. Convenient for decaying the schemes' base-level activation.
	 */
	private Set<Scheme> schemeSet = new ConcurrentHashSet<Scheme>();
	
	/*
	 * A pool of all conditions (context and adding) in all schemes in the procedural memory
	 */
	private Map<Object,Condition> conditionPool = new HashMap<Object,Condition>();
	
	/*
	 * Recent contents of consciousness that have not yet decayed away.
	 */
	private InternalNodeStructure broadcastBuffer = new InternalNodeStructure();
	
	/**
	 * Allows Nodes to be added without copying. 
	 * Warning: doing so allows the same java object of Node to exist in multiple places. 
	 * @author Ryan J. McCall
	 * @see NodeStructureImpl
	 */
	protected class InternalNodeStructure extends NodeStructureImpl {
		@Override
		public Node addNode(Node n, boolean copy) {
			return super.addNode(n, copy);
		}
	}

	/*
	 * Listeners of this Procedural Memory
	 */
	private List<ProceduralMemoryListener> proceduralMemoryListeners = new ArrayList<ProceduralMemoryListener>();

	private static final double DEFAULT_SCHEME_SELECTION_THRESHOLD = 0.1;	
	/*
	 * Determines how much activation a scheme should have to be instantiated
	 */
	private double schemeSelectionThreshold = DEFAULT_SCHEME_SELECTION_THRESHOLD;
	
	private static final double DEFAULT_CONDITION_WEIGHT = 1.0;//for Javier
	
	private static final String DEFAULT_SCHEME_CLASS = "edu.memphis.ccrg.lida.proceduralmemory.SchemeImpl";
	/*
	 * Qualified name of the {@link Scheme} class used by this module 
	 */
	private String schemeClass = DEFAULT_SCHEME_CLASS;
	
	/*
	 * DecayStrategy used by all conditions in the condition pool (and broadcast buffer). 
	 */
	private DecayStrategy conditionDecay;
	
	/**
	 * This module can initialize the following parameters:<br><br/>
	 * 
	 * <b>proceduralMemory.schemeSelectionThreshold type=double</b> amount of activation schemes must have to be instantiated, default is 0.0<br/>
	 * <b>proceduralMemory.contextWeight type=double</b> The weight of context conditions for the calculation of scheme activation. Should be positive<br/>
	 * <b>proceduralMemory.addingListWeight type=double</b> The weight of adding list conditions for the calculation of scheme activation. Should be positive<br/>
	 * <b>proceduralMemory.conditionDecayStrategy type=string</b> The DecayStrategy used by all conditions in the condition pool (and broadcast buffer).<br/> 
	 * <b>proceduralMemory.schemeClass type=string</b> qualified name of the {@link Scheme} class used by this module <br/>
	 * 
	 * @see Initializable
	 */
	@Override
	public void init() {	
		schemeSelectionThreshold = getParam("proceduralMemory.schemeSelectionThreshold", DEFAULT_SCHEME_SELECTION_THRESHOLD);
		SchemeImpl.setContextWeight(getParam("proceduralMemory.contextWeight",DEFAULT_CONDITION_WEIGHT));
		SchemeImpl.setAddingListWeight(getParam("proceduralMemory.addingListWeight",DEFAULT_CONDITION_WEIGHT));
		String decayName = getParam("proceduralMemory.conditionDecayStrategy", factory.getDefaultDecayType());
		conditionDecay = factory.getDecayStrategy(decayName);		
		schemeClass = getParam("proceduralMemory.schemeClass",DEFAULT_SCHEME_CLASS);
	}

	@Override
	public void addListener(ModuleListener l) {
		if (l instanceof ProceduralMemoryListener) {
			proceduralMemoryListeners.add((ProceduralMemoryListener) l);
		}else{
			logger.log(Level.WARNING, "Requires ProceduralMemoryListener but received {1}", 
					new Object[]{TaskManager.getCurrentTick(), l});
		}
	}
	
	@Override
	public Scheme getNewScheme(Action a){
		if(a == null){
			logger.log(Level.WARNING, "Action is null, cannot create scheme.",
					TaskManager.getCurrentTick());
			return null;
		}
		SchemeImpl s = null;
		try {
			s = (SchemeImpl) Class.forName(schemeClass).newInstance();
			s.setAction(a);
			s.setProceduralMemory(this);
		} catch (InstantiationException e) {
			logger.log(Level.WARNING, "Error creating Scheme.", TaskManager
					.getCurrentTick());
		} catch (IllegalAccessException e) {
			logger.log(Level.WARNING, "Error creating Scheme.", TaskManager
					.getCurrentTick());
		} catch (ClassNotFoundException e) {
			logger.log(Level.WARNING, "Error creating Scheme.", TaskManager
					.getCurrentTick());
		}
		schemeSet.add(s);
		return s;
	}
	
	/**
	 * Add {@link Condition} c to the condition pool if it is not already stored. </br>
	 * Returns the stored condition with same id as c.</br>
	 * This method is intended to be used only by {@link SchemeImpl} to ensure that all schemes 
	 * in the {@link ProceduralMemory} share the same condition instances.
	 * @param c the condition to add to the condition pool.
	 * @return c or the stored condition with same id as c
	 */
	Condition addCondition(Condition c){
		if(c == null){
			logger.log(Level.WARNING, "Cannot add null condition", TaskManager.getCurrentTick());
			return null;
		}
		Condition stored = conditionPool.get(c.getConditionId());
		if(stored == null){
			logger.log(Level.FINEST,"New Condition {1} added to condition pool.",
					new Object[]{TaskManager.getCurrentTick(), c});
			c.setDecayStrategy(conditionDecay);
			conditionPool.put(c.getConditionId(),c);
			stored = c;
		}
		return stored;
	}
	
	/**
	 * Add a reference to specified Scheme from specified condition. Thus the presence of Condition c
	 * in the broadcast buffer will tend to activate s. 
	 * The specified scheme should be one that will have c in its conditions (e.g. in the Context or adding list)
	 * Indexes Scheme s by Condition c of ConditionType type.
	 * @param s the {@link Scheme} to index
	 * @param c the condition 
	 * @param type the type of the condition. This select the indexing map
	 */
	void indexScheme(Scheme s, Condition c, ConditionType type) {
		Map<Object, Set<Scheme>> map = null;
		switch(type){
			case CONTEXT:
				map = contextSchemeMap;
				break;
			case ADDINGLIST:
				map = addingSchemeMap;
				break;
			case DELETINGLIST:
				break;
			case NEGATEDCONTEXT:
				break;
		}
		if (map !=null){
			synchronized (c) {
				Object id = c.getConditionId();
				Set<Scheme> values = map.get(id);
				if (values == null) {
					values = new ConcurrentHashSet<Scheme>();
					map.put(id, values);
				}
				values.add(s);
			}
		}
	}
	
	/*
	 * Assumes Conditions are Nodes only 
	 */
	@Override
	public void receiveBroadcast(Coalition coal) {
		NodeStructure ns = (NodeStructure) coal.getContent();
		for(Node bNode: ns.getNodes()){		
			//For each broadcast node check if it is in the condition pool 
			//i.e. there is at least 1 scheme that has context or result condition equal to the node.
			Node condition = (Node) conditionPool.get(bNode.getConditionId());
			if(condition != null){ //won't add any nodes to broadcast buffer that aren't already in the condition pool
				if(!broadcastBuffer.containsNode(condition)){
					//Add a reference to the condition pool Node to the broadcast buffer without copying
					broadcastBuffer.addNode(condition, false);
				}
				//Update the activation of the condition-pool/broadcast-buffer node if needed 
				if(bNode.getActivation() > condition.getActivation()){
					condition.setActivation(bNode.getActivation());
				}
				//Update the desirability of the condition-pool/broadcast-buffer node if needed
				if(bNode instanceof RootableNode){
					RootableNode rootableBroadcastNode = (RootableNode) bNode;
					if(condition instanceof RootableNode){
						RootableNode rootableCondition = (RootableNode)condition;
						if(rootableBroadcastNode.getDesirability() > rootableCondition.getDesirability()){
							rootableCondition.setDesirability(rootableBroadcastNode.getDesirability());
						}	
					}else{
						logger.log(Level.WARNING, "Expected condition to be RootableNode but was {1}", 
								new Object[]{TaskManager.getCurrentTick(),condition.getClass()});
					}
				}
			}
		}	
		learn(coal);
		//Spawn a new task to activate and instantiate relevant schemes.
		//This task runs only once in the next tick
		taskSpawner.addTask(new FrameworkTaskImpl() {
			@Override
			protected void runThisFrameworkTask() {
				activateSchemes();
				cancel();
			}
		});
	}
	
	@Override
	public void learn(Coalition coalition) {
		//TODO implement learning
		// make sure to use the correct way of adding new schemes see addScheme
	}
	
	@Override
	public void activateSchemes() {
		//To prevent a scheme from being instantiated multiple times all schemes over threshold are stored in a set
		Set<Scheme> relevantSchemes = new HashSet<Scheme>();
		for (Node n: broadcastBuffer.getNodes()) {	//TODO consider links
			//Get all schemes that contain Node n in their context and add them to relevantSchemes
			Set<Scheme> schemes = contextSchemeMap.get(n.getConditionId());
			if (schemes != null) {
				relevantSchemes.addAll(schemes);
			}
			//If Node n has positive desirability, 
			//get the schemes that have n in their adding list and add them to relevantSchemes
			if(n instanceof RootableNode){
				RootableNode uNode = (RootableNode) n;
				if(uNode.getNetDesirability() > 0.0){//TODO think about more
					schemes = addingSchemeMap.get(uNode.getConditionId());
					if (schemes != null) {
						relevantSchemes.addAll(schemes);
					}
				}
			}
		}
		//For each relevant scheme, check if it should be instantiated, if so instantiate.
		for(Scheme s: relevantSchemes){
			if(shouldInstantiate(s, broadcastBuffer)){
				createInstantiation(s);
			}
		}
	}
	
	/**
	 * Returns true if the specified scheme's total activation is greater than the scheme selection threshold.
	 * </br>The threshold can be set in the {@link #init()} method.
	 */
	@Override
	public boolean shouldInstantiate(Scheme s, NodeStructure broadcastBuffer){
		return s.getTotalActivation() >= schemeSelectionThreshold;
	}

	@Override
	public Behavior createInstantiation(Scheme s) {
		logger.log(Level.FINE, "Instantiating scheme: {1} in ProceduralMemory",
				new Object[]{TaskManager.getCurrentTick(),s});
		Behavior b = factory.getBehavior(s);
		for (ProceduralMemoryListener listener : proceduralMemoryListeners) {
			listener.receiveBehavior(b);
		}
		return b;
	}

	@Override
	public void decayModule(long ticks){
		broadcastBuffer.decayNodeStructure(ticks);
	}
	
	@Override
	public void removeScheme(Scheme s) {
		schemeSet.remove(s);
		removeFromMap(s, s.getContextConditions(), contextSchemeMap);
		removeFromMap(s, s.getAddingList(), addingSchemeMap);
	}
	
	private static void removeFromMap(Scheme s, Collection<Condition> conditions, Map<Object, Set<Scheme>> map){
		for(Condition c: conditions){
			Set<Scheme> schemes = map.get(c.getConditionId());
			if(schemes != null){
				schemes.remove(s);
			}
		}
	}

	@Override
	public boolean containsScheme(Scheme s) {
		return schemeSet.contains(s);
	}

	@Override
	public int getSchemeCount() {
		return schemeSet.size();
	}

	@Override
	public Collection<Scheme> getSchemes() {
		return Collections.unmodifiableCollection(schemeSet);
	}	

	@Override
	public Object getModuleContent(Object... params) {
		if("schemes".equals(params[0])){
			return Collections.unmodifiableCollection(schemeSet);
		}
		return null;
	}
	
	/**
	 * Gets the condition pool. Method intended for testing only.
	 * @return an {@link UnmodifiableCollection} of the condition in the pool
	 */
	public Collection<Condition> getConditionPool(){
		return Collections.unmodifiableCollection(conditionPool.values());
	}
	
	/**
	 * Gets the broadcast buffer. Method intended for testing only.
	 * @return an {@link NodeStructure} containing recent broadcasts
	 */
	public NodeStructure getBroadcastBuffer(){
		return new UnmodifiableNodeStructureImpl(broadcastBuffer);
	}
}