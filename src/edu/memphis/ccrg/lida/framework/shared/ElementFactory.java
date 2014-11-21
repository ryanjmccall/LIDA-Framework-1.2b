/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.actionselection.Behavior;
import edu.memphis.ccrg.lida.actionselection.BehaviorImpl;
import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.ModuleName;
import edu.memphis.ccrg.lida.framework.initialization.FrameworkTaskDef;
import edu.memphis.ccrg.lida.framework.initialization.LinkableDef;
import edu.memphis.ccrg.lida.framework.initialization.StrategyDef;
import edu.memphis.ccrg.lida.framework.shared.activation.Activatible;
import edu.memphis.ccrg.lida.framework.strategies.DecayStrategy;
import edu.memphis.ccrg.lida.framework.strategies.DefaultTotalActivationStrategy;
import edu.memphis.ccrg.lida.framework.strategies.ExciteStrategy;
import edu.memphis.ccrg.lida.framework.strategies.LinearDecayStrategy;
import edu.memphis.ccrg.lida.framework.strategies.LinearExciteStrategy;
import edu.memphis.ccrg.lida.framework.strategies.NoDecayStrategy;
import edu.memphis.ccrg.lida.framework.strategies.NoExciteStrategy;
import edu.memphis.ccrg.lida.framework.strategies.Strategy;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTask;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.pam.PamLinkImpl;
import edu.memphis.ccrg.lida.pam.PamNodeImpl;
import edu.memphis.ccrg.lida.proceduralmemory.Scheme;

/**
 * Standard factory for the basic elements of the framework. Support for
 * {@link Node}, {@link Link}, {@link FrameworkTask}, and {@link NodeStructure}
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 */
public class ElementFactory {

	private static final Logger logger = Logger
			.getLogger(ElementFactory.class.getCanonicalName());

	/*
	 * Used to assign unique IDs to nodes.
	 */
	private static int nodeIdCount;

	/*
	 * Used to assign unique IDs to schemes.
	 */
	private static int behaviorIdCount;

	/*
	 * Used to retrieve default decay strategy from 'decayStrategies' map.
	 */
	private String defaultDecayType = "defaultDecay";

	/*
	 * Used to retrieve default excite strategy from 'exciteStrategies' map.
	 */
	private String defaultExciteType = "defaultExcite";
	
	private String defaultTotalValueStrategyType = DefaultTotalActivationStrategy.class.getSimpleName();

	/*
	 * Used to retrieve default link class from 'linkClasses' map. e.g.
	 * edu.memphis.ccrg.lida.framework.shared.LinkImpl
	 */
	private String defaultLinkClassName = LinkImpl.class.getCanonicalName();

	/*
	 * Specifies default link type used by the factory. e.g. "LinkImpl"
	 */
	private String defaultLinkType = LinkImpl.class.getSimpleName();

	/*
	 * Used to retrieve default node class from 'nodeClasses' map. e.g.
	 * edu.memphis.ccrg.lida.framework.shared.NodeImpl
	 */
	private String defaultNodeClassName = NodeImpl.class.getCanonicalName();

	/*
	 * Specifies default node type used by the factory. e.g. "NodeImpl"
	 */
	private String defaultNodeType = NodeImpl.class.getSimpleName();
	
	//TODO a Definition for behavior is needed
	private String defaultBehaviorClassName = BehaviorImpl.class.getCanonicalName();
	
	/*
	 * Map of all the ExciteStrategies available to this factory
	 */
	private Map<String, StrategyDef> exciteStrategies = new HashMap<String, StrategyDef>();

	/*
	 * Map of all the DecayStrategies available to this factory
	 */
	private Map<String, StrategyDef> decayStrategies = new HashMap<String, StrategyDef>();

	/*
	 * Map of all the strategies (of any type) available to this factory
	 */
	private Map<String, StrategyDef> strategies = new HashMap<String, StrategyDef>();

	/*
	 * Map of LinkableDefs for the Link types available to this factory indexed
	 * by their linkFactoryName.
	 */
	private Map<String, LinkableDef> linkClasses = new HashMap<String, LinkableDef>();

	/*
	 * Map of LinkableDefs for the Node types available to this factory indexed
	 * by their nodeFactoryName.
	 */
	private Map<String, LinkableDef> nodeClasses = new HashMap<String, LinkableDef>();

	/*
	 * Map of {@link FrameworkTaskDef} for the {@link FrameworkTask} types available to this factory
	 * indexed by name as specified in factories data.
	 */
	private Map<String, FrameworkTaskDef> tasks = new HashMap<String, FrameworkTaskDef>();

	/*
	 * Sole instance of this class that will be used.
	 */
	private static final ElementFactory instance = new ElementFactory();

	/*
	 * Name of decay strategy type
	 * 
	 * @see LidaFactories.xsd
	 */
	private static final String decayStrategyType = "decay";

	/*
	 * Name of excite strategy type
	 * 
	 * @see LidaFactories.xsd
	 */
	private static final String exciteStrategyType = "excite";
	
	//TODO Implement PropagationStrategy in Pam in a generic way
	/*
	 * Name of propagation strategy type
	 * 
	 * @see LidaFactories.xsd
	 */
	@SuppressWarnings("unused")
	private static final String propagationStrategyType = "propagation";

	/**
	 * Returns the sole instance of this factory. Implements the Singleton
	 * pattern.
	 * 
	 * @return the sole {@link ElementFactory} instance of this class
	 */
	public static ElementFactory getInstance() {
		return instance;
	}

	/*
	 * Creates the Factory and adds default Node, Link and Strategies to the
	 * Maps in the Factory.
	 */
	private ElementFactory() {
		//Default decay type
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("m", 0.1);
		addDecayStrategy(defaultDecayType, new StrategyDef(
				LinearDecayStrategy.class.getCanonicalName(), defaultDecayType,
				params, decayStrategyType, true));

		//Default excite type
		params = new HashMap<String, Object>();
		params.put("m", 1.0);
		addExciteStrategy(defaultExciteType, new StrategyDef(
				LinearExciteStrategy.class.getCanonicalName(),
				defaultExciteType, params,
				exciteStrategyType, true));
		
		//No-decay strategy type
		String strategyName="noDecay";
		addDecayStrategy(strategyName, new StrategyDef(
				NoDecayStrategy.class.getCanonicalName(), strategyName,
				new HashMap<String, Object>(), decayStrategyType, true));
		
		//No-excite strategy type
		strategyName="noExcite";
		addExciteStrategy(strategyName, new StrategyDef(
				NoExciteStrategy.class.getCanonicalName(), strategyName,
				new HashMap<String, Object>(), exciteStrategyType, true));
		
		//Default TotalActivation strategy
		strategyName=defaultTotalValueStrategyType;
		StrategyDef strategyDef = new StrategyDef(DefaultTotalActivationStrategy.class.getCanonicalName(), 
				strategyName, new HashMap<String, Object>(), "other", true);
		addStrategy(strategyName, strategyDef);
		
		//Nodes, Links
		Map<String, String> defaultStrategies = new HashMap<String, String>();
		defaultStrategies.put("decay", defaultDecayType);
		defaultStrategies.put("excite", defaultExciteType);
		
		params= new HashMap<String, Object>();
		params.put("learnable.baseLevelActivation", 0.0);
		params.put("learnable.baseLevelRemovalThreshold", -1.0);
		params.put("learnable.baseLevelDecayStrategy", "noDecay");
		params.put("learnable.baseLevelExciteStrategy", "noExcite");
		params.put("learnable.totalActivationStrategy", strategyName);
		
		//Nodes
		//Default node type
		addNodeType(defaultNodeType, defaultNodeClassName);
		
		addNodeType("RootableNodeImpl","edu.memphis.ccrg.lida.framework.shared.RootableNodeImpl");
		
		//PamNodeImpl type
		LinkableDef newNodeDef = new LinkableDef(PamNodeImpl.class.getCanonicalName(), new HashMap<String, String>(), PamNodeImpl.class.getSimpleName(), params);
		addNodeType(newNodeDef);
		
		//Non-decaying PamNode
		newNodeDef = new LinkableDef(PamNodeImpl.class.getCanonicalName(), defaultStrategies, "NoDecayPamNode", params);
		addNodeType(newNodeDef);

		//Links
		//Default link type
		addLinkType(defaultLinkType, defaultLinkClassName);
		
		//PamLinkImpl type
		LinkableDef newLinkDef = new LinkableDef(PamLinkImpl.class.getCanonicalName(), new HashMap<String, String>(), PamLinkImpl.class.getSimpleName(), params);
		addLinkType(newLinkDef);
		
		newLinkDef = new LinkableDef(PamLinkImpl.class.getCanonicalName(), defaultStrategies, "NoDecayPamLink", params);
		addLinkType(newLinkDef);
	}
	
	/**
	 * Adds a {@link DecayStrategy} indexed by specified name.
	 * 
	 * @param name
	 *            the name used to refer to the strategy
	 * @param decayDef
	 *            the decay strategy's {@link StrategyDef}
	 */
	public void addDecayStrategy(String name, StrategyDef decayDef) {
		decayStrategies.put(name, decayDef);
		strategies.put(name, decayDef);
	}

	/**
	 * Adds an excite strategy indexed by specified name.
	 * 
	 * @param name
	 *            the name used to reference the strategy 
	 * @param exciteDef
	 *            the excite strategy {@link StrategyDef}
	 */
	public void addExciteStrategy(String name, StrategyDef exciteDef) {
		exciteStrategies.put(name, exciteDef);
		strategies.put(name, exciteDef);
	}

	/**
	 * Adds a strategy to this factory indexed by specified name.
	 * 
	 * @param name
	 *            the name
	 * @param strategyDef
	 *            the {@link StrategyDef}
	 */
	public void addStrategy(String name, StrategyDef strategyDef) {
		strategies.put(name, strategyDef);
	}

	/**
	 * Adds a Link type to this factory
	 * 
	 * @param linkDef
	 *            the link def
	 */
	public void addLinkType(LinkableDef linkDef) {
		linkClasses.put(linkDef.getName(), linkDef);
	}

	/**
	 * Adds a link type indexed by specified typeName
	 * 
	 * @param typeName
	 *            the link type
	 * @param className
	 *            the class name
	 */
	public void addLinkType(String typeName, String className) {
		linkClasses.put(typeName, new LinkableDef(className,
				new HashMap<String, String>(), typeName,
				new HashMap<String, Object>()));
	}

	/**
	 * Adds a {@link Node} type to this factory
	 * 
	 * @param nodeDef
	 *            the node def
	 */
	public void addNodeType(LinkableDef nodeDef) {
		nodeClasses.put(nodeDef.getName(), nodeDef);
	}

	/**
     * Adds a Node type indexed by specified typeName
	 * 
	 * @param typeName
	 *            the simple node name
	 * @param className
	 *            the canonical node name
	 */
	public void addNodeType(String typeName, String className) {
		nodeClasses.put(typeName, new LinkableDef(className,
				new HashMap<String, String>(), typeName,
				new HashMap<String, Object>()));
	}

	/**
	 * Adds the {@link FrameworkTask} type.
	 * 
	 * @param taskDef
	 *            {@link FrameworkTaskDef}
	 */
	public void addFrameworkTaskType(FrameworkTaskDef taskDef) {
		tasks.put(taskDef.getName(), taskDef);
	}


	/**
	 * Gets default link type.
	 * 
	 * @return the default link type
	 */
	public String getDefaultLinkType() {
		return defaultLinkType;
	}

	/**
	 * Gets default node type.
	 * 
	 * @return the default node type
	 */
	public String getDefaultNodeType() {
		return defaultNodeType;
	}
	
	/**
	 * Returns whether this factory contains specified {@link Strategy} type.
	 * @param strategyTypeName name of strategy type
	 * @return true if factory contains type or false if not
	 */
	public boolean containsStrategy(String strategyTypeName){
		return strategies.containsKey(strategyTypeName);
	}

	/**
	 * Returns whether this factory contains specified {@link Node} type.
	 * @param nodeTypeName name of node type
	 * @return true if factory contains type or false if not
	 */
	public boolean containsNodeType(String nodeTypeName) {
		return nodeClasses.containsKey(nodeTypeName);
	}

	/**
	 * Returns whether this factory contains specified {@link Link} type.
	 * @param linkTypeName name of Link type
	 * @return true if factory contains type or false if not
	 */
	public boolean containsLinkType(String linkTypeName) {
		return linkClasses.containsKey(linkTypeName);
	}
	
	/**
	 * Returns whether this factory contains specified {@link FrameworkTask} type.
	 * @param typeName String
	 * @return true if factory contains type or false if not
	 */
	public boolean containsTaskType(String typeName) {
		return tasks.containsKey(typeName);
	}

	/**
	 * Gets decay strategy.
	 * 
	 * @param strategyTypeName
	 *            name of DecayStrategy type
	 * @return the decay strategy
	 */
	public DecayStrategy getDecayStrategy(String strategyTypeName) {
		DecayStrategy d = null;
		StrategyDef sd = decayStrategies.get(strategyTypeName);
		if (sd == null) {
			sd = decayStrategies.get(defaultDecayType);
			logger.log(Level.WARNING, "Decay strategy type {1} does not exist. Default type will be returned.", 
					new Object[]{TaskManager.getCurrentTick(),strategyTypeName});
		}
		d = (DecayStrategy) sd.getInstance();
		return d;
	}

	/**
	 * Gets excite strategy.
	 * 
	 * @param strategyTypeName name of excite strategy type
	 * @return the excite strategy
	 */
	public ExciteStrategy getExciteStrategy(String strategyTypeName) {
		ExciteStrategy d = null;
		StrategyDef sd = exciteStrategies.get(strategyTypeName);
		if (sd == null) {
			sd = exciteStrategies.get(defaultExciteType);
			logger.log(Level.WARNING, "Excite strategy type {1} does not exist. Default type will be returned.", 
					new Object[]{TaskManager.getCurrentTick(),strategyTypeName});
		}
		d = (ExciteStrategy) sd.getInstance();
		return d;
	}

	/**
	 * Get a strategy by type.
	 * 
	 * @param typeName
	 *            Name of sought strategy.
	 * @return Strategy if found or null.
	 */
	public Strategy getStrategy(String typeName) {
		Strategy d = null;
		StrategyDef sd = strategies.get(typeName);
		if (sd != null) {
			d = sd.getInstance();
		}else{
			logger.log(Level.WARNING, "Factory does not contain strategy of type {1}",
					new Object[]{TaskManager.getCurrentTick(),typeName});
		}
		return d;
	}

	/**
	 * Creates and returns a new Link with specified source, sink, category, and
	 * activation.
	 * 
	 * @param source
	 *            Node that is link's source
	 * @param sink
	 *            Linkable that is link's sink
	 * @param category
	 *            LinkCategory
	 * @param activation
	 *            initial activation
	 * @param removalThreshold threshold to remain in {@link NodeStructure}
	 * @return new Link
	 */
	public Link getLink(Node source, Linkable sink, LinkCategory category,double activation, double removalThreshold) {
		return getLink(defaultLinkType, source, sink, category,
				defaultDecayType, defaultExciteType, activation, removalThreshold);
	}

	/**
	 * Creates and returns a new Link with specified source, sink, and category.
	 * Zero initial activation.
	 * 
	 * @param source
	 *            Node that is link's source
	 * @param sink
	 *            Linkable that is link's sink
	 * @param category
	 *            LinkCategory
	 * @return new Link
	 */
	public Link getLink(Node source, Linkable sink, LinkCategory category) {
		return getLink(defaultLinkType, source, sink, category,
				defaultDecayType, defaultExciteType, 
				Activatible.DEFAULT_ACTIVATION, Activatible.DEFAULT_ACTIVATIBLE_REMOVAL_THRESHOLD);
	}

	/**
	 * Checks if desiredType is-a requiredType.  Creates and returns
	 * a Link of desiredType with specified parameters.
	 * @param requiredType Required Link type for {@link NodeStructure}
	 * @param desiredType Desired Link type for returned Link. Must be a subtype of required type.
	 * @param source Link's source
	 * @param sink Link's sink
	 * @param category Link's {@link LinkCategory}
	 * @return new {@link Link} with specified attributes.
	 */
	public Link getLink(String requiredType, String desiredType, 
						Node source, Linkable sink, LinkCategory category) {
		LinkableDef requiredDef = linkClasses.get(requiredType);
		if(requiredDef == null){
			logger.log(Level.WARNING, "Factory does not contain link type: {1}", 
					new Object[]{TaskManager.getCurrentTick(),requiredType});
			return null;
		}
		LinkableDef desiredDef = linkClasses.get(desiredType);
		if(desiredDef == null){
			logger.log(Level.WARNING, "Factory does not contain link type: {1}", 
					new Object[]{TaskManager.getCurrentTick(),desiredType});
			return null;
		}
		
		Link l = null;
		try {
			Class<?> required = Class.forName(requiredDef.getClassName());
			Class<?> desired = Class.forName(desiredDef.getClassName());
			
			if(required != null && required.isAssignableFrom(desired)){
				l = getLink(desiredType, source, sink, category);
			}
		} catch (ClassNotFoundException exc) {
			exc.printStackTrace();
		}
		return l;
	}
	
	
	/**
	 * Creates and returns a new Link with specified type, source, sink, and
	 * category.
	 * 
	 * @param linkType
	 *            the link type
	 * @param source
	 *            Node that is link's source
	 * @param sink
	 *            Linkable that is link's sink
	 * @param category
	 *            LinkCategory
	 * @return new Link
	 */
	public Link getLink(String linkType, Node source, Linkable sink,
						LinkCategory category) {
		LinkableDef linkDef = linkClasses.get(linkType);
		if (linkDef == null) {
			logger.log(Level.WARNING, "Link type {1} does not exist.",
					new Object[]{TaskManager.getCurrentTick(),linkType});
			return null;
		}
		
		String decayB = linkDef.getDefaultStrategies().get(decayStrategyType);
		if (decayB == null) {
			decayB = defaultDecayType;
		}
		String exciteB = linkDef.getDefaultStrategies().get(exciteStrategyType);
		if (exciteB == null) {
			exciteB = defaultExciteType;
		}

		return getLink(linkType, source, sink, category, decayB, exciteB, 
				Activatible.DEFAULT_ACTIVATION, Activatible.DEFAULT_ACTIVATIBLE_REMOVAL_THRESHOLD);
	}

	/**
	 * Creates and returns a new Link of specified type with specified source,
	 * sink, LinkCategory, DecayStrategy, ExciteStrategy, and category.
	 * 
	 * @param linkType
	 *            Link type
	 * @param source
	 *            Link's source
	 * @param sink
	 *            Link's sink
	 * @param category
	 *            Link's category
	 * @param decayStrategy
	 *            Link's {@link DecayStrategy}
	 * @param exciteStrategy
	 *            Link's {@link ExciteStrategy}
	 * @param activation
	 *            initial activation
	 * @param removalThreshold threshold of activation required to remain active
	 * @return new Link
	 */
	public Link getLink(String linkType, Node source, Linkable sink,
			LinkCategory category, String decayStrategy, String exciteStrategy,
			double activation, double removalThreshold) {
		
		if(source == null){
			logger.log(Level.WARNING, "Cannot create a link with a null source.",
					TaskManager.getCurrentTick());
			return null;
		}
		if(sink == null){
			logger.log(Level.WARNING, "Cannot create a link with a null sink.",
					TaskManager.getCurrentTick());
			return null;
		}
		if(category == null){
			logger.log(Level.WARNING, "Cannot create a link with a null category.",
					TaskManager.getCurrentTick());
			return null;
		}
		
		Link link = null;
		try {
			LinkableDef linkDef = linkClasses.get(linkType);
			if (linkDef == null) {
				logger.log(Level.WARNING, "Link type {1} does not exist.", 
						new Object[]{TaskManager.getCurrentTick(),linkType});
				return null;
			}

			String className = linkDef.getClassName();
			link = (Link) Class.forName(className).newInstance();
			link.setFactoryType(linkType);
			link.setSource(source);
			link.setSink(sink);
			link.setCategory(category);
			link.setActivation(activation);
			link.setActivatibleRemovalThreshold(removalThreshold);
			setActivatibleStrategies(link, decayStrategy, exciteStrategy);
			link.init(linkDef.getParams());

		} catch (InstantiationException e) {
			logger.log(Level.WARNING, "InstantiationException creating Link.", TaskManager
					.getCurrentTick());
		} catch (IllegalAccessException e) {
			logger.log(Level.WARNING, "IllegalAccessException creating Link.", TaskManager
					.getCurrentTick());
		} catch (ClassNotFoundException e) {
			logger.log(Level.WARNING, "ClassNotFoundException creating Link.", TaskManager
					.getCurrentTick());
		}
		return link;
	}
	
	/**
	 * Creates a default node with the default strategies and default activation.
	 * @see Activatible
	 * 
	 * @return the node
	 */
	public Node getNode() {
		return getNode(defaultNodeType, defaultDecayType, defaultExciteType,
				"Node", Activatible.DEFAULT_ACTIVATION, Activatible.DEFAULT_ACTIVATIBLE_REMOVAL_THRESHOLD);
	}

	/**
	 * Creates a copy of the supplied node with the default strategies. Note that
	 * the new node is of a default type regardless of the node passed in the
	 * parameter.
	 * 
	 * @param oNode
	 *            supplied node
	 * @return the node
	 */
	public Node getNode(Node oNode) {
		return getNode(oNode, defaultNodeType, defaultDecayType,
				defaultExciteType);
	}

	/**
	 * Creates a copy of specified {@link Node}. The second argument specifies the type of
	 * the new node. The {@link Activatible} strategies of the
	 * new node are based on those specified by the {@link Node} type's {@link LinkableDef} 
	 * (specified by factoriesData.xml) If the {@link Node} type
	 * does not specify default {@link Activatible} strategies then the default strategies are used.
	 * All other values of the specified {@link Node} are copied to the new {@link Node}, e.g. activation.
	 * 
	 * @param oNode
	 *            supplied node
	 * @param nodeType
	 *            type of returned node
	 * @return the node
	 */
	public Node getNode(Node oNode, String nodeType) {
		if(oNode == null){
			logger.log(Level.WARNING, "Supplied node is null", TaskManager.getCurrentTick());
			return null;
		}		
		LinkableDef nodeDef = nodeClasses.get(nodeType);
		if (nodeDef == null) {
			logger.log(Level.WARNING, "Node type {1} does not exist.", 
					new Object[]{TaskManager.getCurrentTick(),nodeType});
			return null;
		}
		String decayB = nodeDef.getDefaultStrategies().get(decayStrategyType);
		String exciteB = nodeDef.getDefaultStrategies().get(exciteStrategyType);
		if (decayB == null) {
			decayB = defaultDecayType;
		}
		if (exciteB == null) {
			exciteB = defaultExciteType;
		}
		return getNode(oNode, nodeType, decayB, exciteB);
	}

	/**
	 * Creates new node of specified type with specified label. Uses strategies
	 * based on specified node type, or the default strategies if the node type
	 * has no strategies defined.
	 * 
	 * @param type
	 *            type of new node
	 * @param label
	 *            label of new node
	 * @return the node
	 */
	public Node getNode(String type, String label) {
		LinkableDef nodeDef = nodeClasses.get(type);
		if (nodeDef == null) {
			logger.log(Level.WARNING, "Node type {1} does not exist.", 
					new Object[]{TaskManager.getCurrentTick(),type});
			return null;
		}
		String decayB = nodeDef.getDefaultStrategies().get(decayStrategyType);
		String exciteB = nodeDef.getDefaultStrategies().get(exciteStrategyType);
		if (decayB == null) {
			decayB = defaultDecayType;
		}
		if (exciteB == null) {
			exciteB = defaultExciteType;
		}

		Node n = getNode(type, decayB, exciteB, label, 
				Activatible.DEFAULT_ACTIVATION, Activatible.DEFAULT_ACTIVATIBLE_REMOVAL_THRESHOLD);
		return n;
	}

	/**
	 * Creates a copy of specified node of desired type.  Desired type
	 * must pass is-a test with requireType.
	 * @param requiredType Default node type of {@link NodeStructure} 
	 * @param oNode {@link Node} to be copied.
	 * @param desiredType type of copied node
	 * @return copy of oNode of desired type, or a new node of desired type, or null
	 */
	public Node getNode(String requiredType, Node oNode, String desiredType) {
		LinkableDef requiredDef = nodeClasses.get(requiredType);
		if(requiredDef == null){
			logger.log(Level.WARNING, "Factory does not contain node type: {1}", 
					new Object[]{TaskManager.getCurrentTick(),requiredType});
			return null;
		}
		LinkableDef desiredDef = nodeClasses.get(desiredType);
		if(desiredDef == null){
			logger.log(Level.WARNING, "Factory does not contain node type: {1}", 
					new Object[]{TaskManager.getCurrentTick(),desiredType});
			return null;
		}
		
		Node newNode = null;
		try {
			Class<?> required = Class.forName(requiredDef.getClassName());
			Class<?> desired = Class.forName(desiredDef.getClassName());
			
			if(required != null && required.isAssignableFrom(desired)){
				if(oNode == null){//Get a new Node from scratch
					newNode = getNode(desiredType, "Node");
				}else{ //Get a new Node based on oNode
					newNode = getNode(oNode, desiredType);
				}
			}
		} catch (ClassNotFoundException exc) {
			logger.log(Level.SEVERE, "Cannot find Class type.", TaskManager.getCurrentTick());
			exc.printStackTrace();
		} 
		return newNode;
	}

	/**
	 * Creates a copy of oNode with the specified decay and excite strategies.
	 * The type of the new node will be the default node type.
	 * 
	 * @param oNode
	 *            supplied node
	 * @param decayStrategy
	 *            new node's decay strategy
	 * @param exciteStrategy
	 *            new node's excite strategy
	 * @return the node
	 */
	public Node getNode(Node oNode, String decayStrategy, String exciteStrategy) {
		return getNode(oNode, defaultNodeType, decayStrategy, exciteStrategy);
	}

	/*
	 * Creates a copy of oNode with specified node type. Copy will have
	 * Decay and Excite as specified in this method's parameters, not according
	 * to the default for the Node type.
	 * 
	 * @param oNode
	 *            supplied node
	 * @param nodeType
	 *            type for new node
	 * @param decayStrategy
	 *            decayStrategy new node's decay strategy
	 * @param exciteStrategy
	 *            exciteStrategy new node's excite strategy
	 * @return the node
	 */
	private Node getNode(Node oNode, String nodeType, String decayStrategy, String exciteStrategy) {
		if(oNode == null){
			logger.log(Level.WARNING, "Specified node is null", TaskManager.getCurrentTick());
			return null;
		}
		Node n = getNode(nodeType,  decayStrategy, exciteStrategy, oNode.getLabel(),oNode.getActivation(), oNode.getActivatibleRemovalThreshold());
		n.setGroundingPamNode(oNode.getGroundingPamNode());
		n.setId(oNode.getId());	//sets extended id as well.
		n.updateNodeValues(oNode);
		return n;
	}

	/**
	 * Creates new node of specified type. Uses strategies based on specified
	 * node type, or the default strategies if the node type has no strategies
	 * defined.
	 * 
	 * @param nodeType
	 *            type of desired node
	 * @return the node
	 */
	public Node getNode(String nodeType) {
		return getNode(nodeType, "Node");
	}

	/**
	 * Creates a new node of specified type, strategies, label, and initial
	 * activation.
	 * 
	 * @param nodeType
	 *            type of new node
	 * @param decayStrategy
	 *            decay strategy of new node
	 * @param exciteStrategy
	 *            excite strategy of new node
	 * @param nodeLabel
	 *            label of new node
	 * @param activation
	 *            activation of new node
	 * @param removalThreshold threshold node needs to remain in containing {@link NodeStructure}s
	 * @return the node
	 */
	public Node getNode(String nodeType, String decayStrategy, String exciteStrategy, 
					    String nodeLabel, double activation, double removalThreshold) {
		Node n = null;
		try {
			LinkableDef nodeDef = nodeClasses.get(nodeType);
			if (nodeDef == null) {
				logger.log(Level.WARNING, "Node type {1} does not exist.", 
						new Object[]{TaskManager.getCurrentTick(),nodeType});
				return null;
			}

			String className = nodeDef.getClassName();
			n = (Node)Class.forName(className).newInstance();

			n.setId(nodeIdCount++);
			n.setFactoryType(nodeType);
			n.setLabel(nodeLabel);
			n.setActivation(activation);
			n.setActivatibleRemovalThreshold(removalThreshold);
			setActivatibleStrategies(n, decayStrategy, exciteStrategy);
			n.init(nodeDef.getParams());
		} catch (InstantiationException e) {
			logger.log(Level.WARNING, "InstantiationException creating Node.", TaskManager
					.getCurrentTick());
		} catch (IllegalAccessException e) {
			logger.log(Level.WARNING, "IllegalAccessException creating Node.", TaskManager
					.getCurrentTick());
		} catch (ClassNotFoundException e) {
			logger.log(Level.WARNING, "ClassNotFoundException creating Node.", TaskManager
					.getCurrentTick());
		}
		return n;
	}

	/*
	 * Assigns specified decay and excite strategies to supplied Activatible
	 * 
	 */
	private void setActivatibleStrategies(Activatible activatible, String decayStrategy, 
										  String exciteStrategy) {
		DecayStrategy decayB = getDecayStrategy(decayStrategy);
		activatible.setDecayStrategy(decayB);
		ExciteStrategy exciteB = getExciteStrategy(exciteStrategy);
		activatible.setExciteStrategy(exciteB);
	}

	/**
	 * Set the default Link type used by this factory.
	 * 
	 * @param linkTypeName
	 *            type of links created by this factory
	 */
	public void setDefaultLinkType(String linkTypeName) {
		if (linkClasses.containsKey(linkTypeName)){
			defaultLinkType = linkTypeName;
		}else{
			logger.log(Level.WARNING, "Factory does not contain Link type {1} so it cannot be used as default.", 
					new Object[]{TaskManager.getCurrentTick(),linkTypeName});
		}
	}

	/**
	 * Set the default Node type used by this factory.
	 * 
	 * @param nodeTypeName
	 *            type of nodes created by this factory
	 */
	public void setDefaultNodeType(String nodeTypeName) {
		if (nodeClasses.containsKey(nodeTypeName)){
			defaultNodeType = nodeTypeName;
		}else{
			logger.log(Level.WARNING, "Factory does not contain Node type {1} so it cannot be used as default.", 
					new Object[]{TaskManager.getCurrentTick(),nodeTypeName});
		}
	}
	
	/**
	 * Gets default decay type.
	 * 
	 * @return the defaultDecayType
	 */
	public String getDefaultDecayType() {
		return defaultDecayType;
	}
	
	/**
	 * Returns the default {@link DecayStrategy}
	 * @return Factory's default {@link DecayStrategy} 
	 */
	public DecayStrategy getDefaultDecayStrategy(){
		return getDecayStrategy(defaultDecayType);
	}

	/**
	 * Sets default decay type.
	 * 
	 * @param decayTypeName
	 *            DecayType to be used
	 */
	public void setDefaultDecayType(String decayTypeName) {
		if (decayStrategies.containsKey(decayTypeName)) {
			defaultDecayType = decayTypeName;
		}else{
			logger.log(Level.WARNING, "Factory does not contain decay strategy type {1} so it cannot be used as default.", 
					new Object[]{TaskManager.getCurrentTick(),decayTypeName});
		}
	}

	/**
	 * Gets default excite type.
	 * 
	 * @return defaultExciteType ExciteType to be used
	 */
	public String getDefaultExciteType() {
		return defaultExciteType;
	}
	
	/**
	 * Returns the default {@link ExciteStrategy}
	 * @return Factory's default excite strategy
	 */
	public ExciteStrategy getDefaultExciteStrategy(){
		return getExciteStrategy(defaultExciteType);
	}

	/**
	 * Sets default excite type.
	 * 
	 * @param exciteTypeName
	 *            the defaultExciteType to set
	 */
	public void setDefaultExciteType(String exciteTypeName) {
		if (exciteStrategies.containsKey(exciteTypeName)){
			defaultExciteType = exciteTypeName;
		}else{
			logger.log(Level.WARNING, "Factory does not contain excite strategy type {1} so it cannot be used as default.", 
					new Object[]{TaskManager.getCurrentTick(),exciteTypeName});
		}
	}
	
	/**
	 * Returns a new {@link FrameworkTask} having specified attributes. FrameworkTask
	 *  will have strategies specified for the taskType. Task will not have any associate modules.
	 * @param taskType type of FrameworkTask
	 * @param params optional parameters to be set in object's init method
	 * @return the new {@link FrameworkTask}
	 */
	public FrameworkTask getFrameworkTask(String taskType, Map<String,?extends Object> params){		
		return getFrameworkTask(taskType,params,null);
	}
	
	/**
	 * Returns a new {@link FrameworkTask} having specified attributes. FrameworkTask
	 *  will have strategies specified for the taskType
	 * @param taskType type of FrameworkTask
	 * @param params optional parameters to be set in object's init method
	 * @param modules map of modules for association.
	 *            
	 * @return the new {@link FrameworkTask}
	 */
	public FrameworkTask getFrameworkTask(String taskType, Map<String,?extends Object> params, Map<ModuleName,FrameworkModule> modules){
		FrameworkTaskDef taskDef = tasks.get(taskType);		
		if (taskDef == null) {
			logger.log(Level.WARNING, "Factory does not contain FrameworkTask type {1}", 
					new Object[]{TaskManager.getCurrentTick(),taskType});
			return null;
		}
		String decayB = taskDef.getDefaultStrategies().get(decayStrategyType);
		String exciteB = taskDef.getDefaultStrategies().get(exciteStrategyType);
		if(decayB == null){
			decayB=defaultDecayType;
		}
		if(exciteB == null){
			exciteB=defaultExciteType;
		}
		int ticksPerRun = taskDef.getTicksPerRun();
		return getFrameworkTask(taskType,decayB,exciteB,ticksPerRun,Activatible.DEFAULT_ACTIVATION,Activatible.DEFAULT_ACTIVATIBLE_REMOVAL_THRESHOLD,params,modules);
	}
	
	/**
	 * Returns a new {@link FrameworkTask} with specified attributes.
	 * 
	 * @param taskType
	 *            label for task
	 * @param decayStrategy
	 *            DecayStrategy used by task
	 * @param exciteStrategy
	 *            ExciteStrategy used by task
	 * @param ticksPerRun
	 *            execution frequency
	 * @param activation
	 *            initial activation
	 * @param removalThreshold activation needed to remain active
	 * @param params
	 *            optional parameters to be set in object's init method
	 * @param modules map of modules for association.
	 *            
	 * @return the new {@link FrameworkTask}
	 */
	public FrameworkTask getFrameworkTask(String taskType, String decayStrategy, String exciteStrategy, 
							  int ticksPerRun, double activation, double removalThreshold, Map<String, ? extends Object> params, Map<ModuleName,FrameworkModule> modules){
		FrameworkTask task = null;
		try {
			FrameworkTaskDef taskDef = tasks.get(taskType);
			if (taskDef == null) {
				logger.log(Level.WARNING, "Factory does not contain FrameworkTask type {1}",
						new Object[]{TaskManager.getCurrentTick(),taskType});
				return null;
			}

			String className = taskDef.getClassName();
			task = (FrameworkTask) Class.forName(className).newInstance();

			task.setTicksPerRun(ticksPerRun);
			task.setActivation(activation);
			task.setActivatibleRemovalThreshold(removalThreshold);
			setActivatibleStrategies(task, decayStrategy, exciteStrategy);
			
			//Associate specified modules to task
			if(modules !=null){
				Map<ModuleName,String> associatedModules = taskDef.getAssociatedModules();
				for(ModuleName mName:associatedModules.keySet()){
					FrameworkModule module = modules.get(mName);
					if(module!=null){
						task.setAssociatedModule(module, associatedModules.get(mName));
					}else{
						logger.log(Level.WARNING, "Could not associate module {1} to FrameworkTask {2}. Module was not found in 'modules' map", 
								new Object[]{TaskManager.getCurrentTick(),mName, task});
					}
				}
			}	
			
			//Call task's init with parameters
			Map<String, Object> mergedParams = new HashMap<String, Object>();
			Map<String, Object> defParams = taskDef.getParams();
			if(defParams != null){
				mergedParams.putAll(defParams);
			}
			if(params != null){ //Order matters! Overwrite defParams with argument parameters
				mergedParams.putAll(params);
			}
			task.init(mergedParams);		
		} catch (InstantiationException e) {
			logger.log(Level.WARNING, "{1} creating FrameworkTask of type {2}", 
					new Object[]{TaskManager.getCurrentTick(), e, taskType});
		} catch (IllegalAccessException e) {
			logger.log(Level.WARNING, "{1} creating FrameworkTask of type {2}", 
					new Object[]{TaskManager.getCurrentTick(), e, taskType});
		} catch (ClassNotFoundException e) {
			logger.log(Level.WARNING, "{1} creating FrameworkTask of type {2}", 
					new Object[]{TaskManager.getCurrentTick(), e, taskType});
		}
		return task;
	}

	/**
	 * 
	 * Returns a new default NodeStructure.
	 * 
	 * @return a new NodeStructure with default {@link Node} type and default
	 *         {@link Link} type.
	 */
	public NodeStructure getNodeStructure() {
		return getNodeStructure(defaultNodeType, defaultLinkType);
	}

	/**
	 * Returns a new NodeStructure with specified {@link Node} and {@link Link} types.
	 * 
	 * @param nodeType
	 *            type of node in returned {@link NodeStructure}
	 * @param linkType
	 *            type of Link in returned {@link NodeStructure}
	 * @return a new NodeStructure with specified node type and specified link
	 *         type or null if types do not exist in this factory.
	 */
	public NodeStructure getNodeStructure(String nodeType, String linkType) {
		if (containsNodeType(nodeType)) {
			if (containsLinkType(linkType)) {
				return new NodeStructureImpl(nodeType, linkType);
			}
			logger.log(Level.WARNING, "Cannot get NodeStructure. Factory does not contain link type {1}", 
					new Object[]{TaskManager.getCurrentTick(),linkType});
		}
		logger.log(Level.WARNING, "Cannot get NodeStructure. Factory does not contain node type {1}", 
				new Object[]{TaskManager.getCurrentTick(),nodeType});
		return null;
	}

	/**
	 * Returns a new Behavior based on specified {@link Scheme} of default behavior type.
	 * @param s a {@link Scheme} 
	 * @return a new {@link Behavior}
	 */
	public Behavior getBehavior(Scheme s) {
		return getBehavior(s, defaultBehaviorClassName);
	}
	
	/**
	 * Returns a new {@link Behavior} of specified class based on specified {@link Scheme}. 
	 * @param s the {@link Scheme} generating the behavior.
	 * @param className qualified name of the desired {@link Behavior} class
	 * @return a new {@link Behavior}
	 */
	public Behavior getBehavior(Scheme s, String className){
		if(s == null){
			logger.log(Level.WARNING, "Cannot create a Behavior with null Scheme.", TaskManager
					.getCurrentTick());
			return null;
		}
		if(className == null){
			logger.log(Level.WARNING, "Cannot create a Behavior, specified class name is null.", TaskManager
					.getCurrentTick());
			return null;
		}
		Behavior b = null;
		try {
			b = (Behavior) Class.forName(className).newInstance();
			b.setId(behaviorIdCount++);
			b.setScheme(s);
		} catch (InstantiationException e) {
			logger.log(Level.WARNING, "InstantiationException encountered creating object of class {1}.", 
					new Object[]{TaskManager.getCurrentTick(),className});
		} catch (IllegalAccessException e) {
			logger.log(Level.WARNING, "IllegalAccessException encountered creating object of class {1}.", 
					new Object[]{TaskManager.getCurrentTick(),className});
		} catch (ClassNotFoundException e) {
			logger.log(Level.WARNING, "ClassNotFoundException encountered creating object of class {1}.", 
					new Object[]{TaskManager.getCurrentTick(),className});
		}
		return b;
	}
}
