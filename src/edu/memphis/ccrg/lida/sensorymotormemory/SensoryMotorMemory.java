/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.sensorymotormemory;

import edu.memphis.ccrg.lida.framework.FrameworkModule;

/**
 * Sensory Motor Memory is a module which receives selected actions from ActionSelection and
 * content from SensoryMemory.  It contains the algorithm for a selected action.  When it executes an algorithm it
 * directly calls a method in the environment (doesn't use a listener).  
 * @author Ryan J. McCall
 * @author Javier Snaider
 *
 */
public interface SensoryMotorMemory extends FrameworkModule{

	/**
	 * Any non-environment communication should use listeners.
	 * @param l SensoryMotorMemoryListener
	 */
	public void addSensoryMotorMemoryListener(SensoryMotorMemoryListener l);
		
	/**
	 * Executes specified action algorithm 
	 * @param command algorithm to execute in the agent's actuators or directly in the environment.
	 */
	public void sendActuatorCommand(Object command);

}
