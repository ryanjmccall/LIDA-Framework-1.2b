/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.initialization;

import java.util.Map;

import edu.memphis.ccrg.lida.framework.Agent;
import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTask;

/**
 * An initializer configures a {@link FullyInitializable}.  
 * 
 * @author Ryan J. McCall 
 */
public interface Initializer {
	
	/**
	 * Receives a particular {@link FullyInitializable} to configure. 
	 * The {@link Agent} object and a map of parameters can be used in the specific initialization code.
	 * Named 'initModule' historically, however an initializer need not initialize a {@link FrameworkModule}, 
	 * for example a {@link FrameworkTask} is also valid.
	 * 
	 * @param obj the {@link FullyInitializable} object being initialized
	 * @param a the {@link Agent} object
	 * @param params parameters to configure the {@link FullyInitializable}
	 *
	 */
	public void initModule(FullyInitializable obj, Agent a, Map<String, ?> params); 

}
