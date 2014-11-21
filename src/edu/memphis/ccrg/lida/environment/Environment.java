/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.environment;

import java.util.Map;

import edu.memphis.ccrg.lida.framework.FrameworkModule;

/**
 * Specification for domains that the framework can use as an environment.  
 * Generally speaking environments can be sensed and can receive actions.
 * 
 * @author Ryan J. McCall
 */
public interface Environment extends FrameworkModule{

	/**
	 * Resets the state of the environment
	 */
	public void resetState();
	
	/**
	 * Process the specified action updating the environment's state accordingly.
	 * @param action an algorithm to be processed by the environment
	 */
	public void processAction(Object action);
	
	/**
	 * Returns the environment's state
	 * @param params Map of optional parameters specifying the aspect of the state which will be returned
	 * @return some part of the environment's state depending on the parameters  
	 */
	public Object getState(Map<String, ?> params);
}
