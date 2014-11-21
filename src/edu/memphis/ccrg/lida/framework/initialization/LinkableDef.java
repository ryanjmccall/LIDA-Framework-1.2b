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

import edu.memphis.ccrg.lida.framework.shared.Linkable;

/**
 * Definition of a {@link Linkable} object.
 * 
 * @see AgentXmlFactory
 * @author Javier Snaider
 */
public class LinkableDef {
	
	private String name;
	private String className;
	private Map<String,String> defaultStrategies;
	private Map<String,Object> params;
	
	/**
	 * 
	 */
	public LinkableDef() {
		defaultStrategies = new HashMap<String, String>();
		params = new HashMap<String,Object>();
	}

	/**
	 * @param className Qualified name
	 * @param defaultStrategies Map of strategies this linkable will have
	 * @param name label for object 
	 * @param params optional parameters
	 */
	public LinkableDef(String className, Map<String,String> defaultStrategies,
			String name, Map<String,Object> params) {
		this.className = className;
		this.defaultStrategies = defaultStrategies;
		this.name = name;
		this.params = params;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
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
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	/**
	 * @return {@link Map} of this def's default strategies
	 */
	public Map<String,String> getDefaultStrategies() {
		return defaultStrategies;
	}
	/**
	 * @param defaultStrategies the default strategies to set
	 */
	public void setDefaultStrategies(Map<String,String> defaultStrategies) {
		this.defaultStrategies = defaultStrategies;
	}
	/**
	 * @return the params
	 */
	public Map<String,Object> getParams() {
		return params;
	}
	/**
	 * @param params the params to set
	 */
	public void setParams(Map<String,Object> params) {
		this.params = params;
	}

}
