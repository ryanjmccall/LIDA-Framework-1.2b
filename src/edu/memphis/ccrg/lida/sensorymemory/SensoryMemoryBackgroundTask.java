/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.sensorymemory;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTaskImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * Task which operates a Sensory Memory. This class provides a general way to control various type of
 * sensory memory -- It is the meaning of "background" here.
 * @author Javier Snaider
 */
public class SensoryMemoryBackgroundTask extends FrameworkTaskImpl {

	private static final Logger logger = Logger.getLogger(SensoryMemoryBackgroundTask.class.getCanonicalName());
	private SensoryMemory sm;
	
	/**
	 * This method overrides setAssociatedModule() from class FrameworkTaskImpl
	 * It sets a module passing parameter to SensoryMemory sm
	 * 
	 * @param module The module to be associated
	 * @param moduleUsage It is not used here
	 */
	@Override
	public void setAssociatedModule(FrameworkModule module, String moduleUsage) {
		if (module instanceof SensoryMemory) {
			sm = (SensoryMemory) module;
		}else{
			logger.log(Level.WARNING, "Cannot add module {1}",
					new Object[]{TaskManager.getCurrentTick(),module});
		}
	}

	/**
	 * This method overrides runThisFrameworkTask() from class FrameworkTaskImpl
	 * It executes method runSensors()of SensoryMemory sm
	 * 
	 */
	@Override
	protected void runThisFrameworkTask() {
		sm.runSensors();		
	}
}
