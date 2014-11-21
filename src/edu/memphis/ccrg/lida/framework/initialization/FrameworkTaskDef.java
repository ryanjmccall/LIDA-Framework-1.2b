/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
/**
 * 
 */
package edu.memphis.ccrg.lida.framework.initialization;

import java.util.HashMap;
import java.util.Map;

import edu.memphis.ccrg.lida.framework.ModuleName;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTask;

/**
 * Definition of a {@link FrameworkTask} object
 * 
 * @see AgentXmlFactory
 * @author Javier Snaider
 * @author Ryan J. McCall
 */
public class FrameworkTaskDef {

	private String name;
	private String className;
	private int ticksPerRun;
	private Map<ModuleName, String> associatedModules;
	private Map<String, String> defaultStrategies;
	private Map<String, Object> params;

	/**
	 * 
	 */
	public FrameworkTaskDef() {
		defaultStrategies = new HashMap<String, String>();
		associatedModules = new HashMap<ModuleName,String>();
		params = new HashMap<String, Object>();
	}

	/**
	 * @param className
	 *            Class name of {@link FrameworkTask}
	 * @param ticksPerRun
	 *            the default ticks per run of this task
	 * @param defaultStrategies
	 *            map of strategies
	 * @param name    
	 *            label for task
	 * @param params
	 *            optional parameters
	 * @param associatedModules
	 *            the default associated modules of this task
	 */
	public FrameworkTaskDef(String className, int ticksPerRun,
			Map<String, String> defaultStrategies, String name,
			Map<String, Object> params,
			Map<ModuleName, String> associatedModules) {
		this();
		this.className = className;
		if (defaultStrategies != null) {
			this.defaultStrategies = defaultStrategies;
		}
		this.name = name;
		if (params != null) {
			this.params = params;
		}
		this.ticksPerRun = ticksPerRun;
		if (associatedModules != null) {
			this.associatedModules = associatedModules;
		}
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @return a {@link Map} of the default strategies
	 */
	public Map<String, String> getDefaultStrategies() {
		return defaultStrategies;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the params
	 */
	public Map<String, Object> getParams() {
		return params;
	}

	/**
	 * @param className
	 *            the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @param defaultStrategies
	 *            the default strategies to set
	 */
	public void setDefaultStrategies(Map<String, String> defaultStrategies) {
		if (defaultStrategies != null) {
			this.defaultStrategies = defaultStrategies;
		}
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param params
	 *            the params to set
	 */
	public void setParams(Map<String, Object> params) {
		if (params != null) {
			this.params = params;
		}
	}

	/**
	 * @return the ticksPerRun
	 */
	public int getTicksPerRun() {
		return ticksPerRun;
	}

	/**
	 * @param ticksPerRun
	 *            the ticksPerRun to set
	 */
	public void setTicksPerRun(int ticksPerRun) {
		this.ticksPerRun = ticksPerRun;
	}

	/**
	 * @return the associatedModules
	 */
	public Map<ModuleName, String> getAssociatedModules() {
		return associatedModules;
	}

	/**
	 * @param associatedModules
	 *            the associatedModules to set
	 */
	public void setAssociatedModules(Map<ModuleName, String> associatedModules) {
		if (associatedModules != null) {
			this.associatedModules = associatedModules;
		}
	}

}
