/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.initialization;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.strategies.Strategy;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * Definition of a {@link Strategy} object.
 * 
 * @see AgentXmlFactory
 * @author Javier Snaider
 *
 */
public class StrategyDef {

	private static final Logger logger = Logger.getLogger(StrategyDef.class.getCanonicalName());
	private String name;
	private String className;
	private String type;
	private Map<String, Object> params;
	private boolean flyWeight=true;
	private Strategy instance = null;

	/**
	 * @param className Qualified name
	 * @param name Strategy name
	 * @param params optional parameters
	 * @param type kind of strategy, e.g. decay, excite
	 * @param flyWeight Will there be multiple copies of this strategy or just one shared?
	 */
	public StrategyDef(String className, String name,
			Map<String, Object> params, String type, boolean flyWeight) {
		this.className = className;
		this.name = name;
		this.params = params;
		this.type = type;
		this.flyWeight=flyWeight;
	}

	/**
	 * Default constructor
	 */
	public StrategyDef() {
		params = new HashMap<String, Object>();
	}
	
	/**
	 * if this strategy is flyweight returns the only one instance, a new instance otherwise.
	 * 
	 * @return the instance
	 */
	public Strategy getInstance() {
		if (flyWeight) {
			if (instance == null) {
				synchronized (this) {
					if (instance == null)
						instance = getNewInstance();
				}
				instance = getNewInstance();
			}
			return instance;
		} else {
			return getNewInstance();
		}
	}

	private Strategy getNewInstance() {
		Strategy st = null;
		try {
			st = (Strategy) Class.forName(className).newInstance();
			st.init(params);

		} catch (InstantiationException e) {
			logger.log(Level.WARNING, "Exception {1} creating Strategy.",
					new Object[]{TaskManager.getCurrentTick(), e.getMessage()});
		} catch (IllegalAccessException e) {
			logger.log(Level.WARNING, "Exception {1} creating Strategy.",
					new Object[]{TaskManager.getCurrentTick(), e.getMessage()});
		} catch (ClassNotFoundException e) {
			logger.log(Level.WARNING, "Exception {1} creating Strategy.",
					new Object[]{TaskManager.getCurrentTick(), e.getMessage()});
		}
		return st;
	}

	/**
	 * @return the flyWeight
	 */
	public boolean isFlyWeight() {
		return flyWeight;
	}

	/**
	 * @param flyWeight
	 *            the flyWeight to set
	 */
	public void setFlyWeight(boolean flyWeight) {
		this.flyWeight = flyWeight;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param className
	 *            the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the params
	 */
	public Map<String, Object> getParams() {
		return params;
	}

	/**
	 * @param params
	 *            the params to set
	 */
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

}
