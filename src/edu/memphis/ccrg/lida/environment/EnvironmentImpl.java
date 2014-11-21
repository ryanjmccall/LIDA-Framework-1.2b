/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.environment;

import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.FrameworkModuleImpl;
import edu.memphis.ccrg.lida.framework.initialization.AgentXmlFactory;

/**
 * Abstract implementation of {@link Environment}
 * Environments should not be a listener of anything besides GUIs.  
 * Rather, SensoryMemory and SensoryMotorMemory should
 * add environments as associated modules in the XML configuration file. 
 * @author Ryan J. McCall
 */
public abstract class EnvironmentImpl extends FrameworkModuleImpl implements Environment{
	
	/**
	 * Default constructor will be invoked by {@link AgentXmlFactory} 
	 * to create this {@link FrameworkModule}
	 */
	public EnvironmentImpl(){
	}
	
	/**
	 * override to implement Environment's decay.
	 * @see FrameworkModule#decayModule(long)
	 */
	@Override
	public void decayModule(long ticks) {
	}
	
}