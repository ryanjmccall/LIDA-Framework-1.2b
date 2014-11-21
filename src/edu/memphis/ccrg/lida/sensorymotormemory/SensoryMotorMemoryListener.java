/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.sensorymotormemory;

import edu.memphis.ccrg.lida.framework.ModuleListener;

/**
 * Listener of {@link SensoryMotorMemory}
 * @author Ryan J. McCall
 */
public interface SensoryMotorMemoryListener extends ModuleListener{
	
	/**
	 * @param command Current command being executed.
	 */
	public void receiveActuatorCommand(Object command);

}
