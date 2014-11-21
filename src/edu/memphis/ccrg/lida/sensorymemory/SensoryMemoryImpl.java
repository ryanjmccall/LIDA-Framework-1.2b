/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.sensorymemory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.environment.Environment;
import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.FrameworkModuleImpl;
import edu.memphis.ccrg.lida.framework.ModuleListener;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.pam.tasks.DetectionAlgorithm;
import edu.memphis.ccrg.lida.sensorymotormemory.SensoryMotorMemory;

/**
 * Default <i> abstract </i> (i.e. must be overridden to be used) implementation of the {@link SensoryMemory} module. This module should
 * sense the environment, store the sensed data and processing it. It should expect access 
 * to its content from {@link DetectionAlgorithm}s via method {@link SensoryMemory#getSensoryContent(String, Map)} and it may transmit content to
 * {@link SensoryMotorMemory}.
 * @author Ryan J. McCall
 */
public abstract class SensoryMemoryImpl extends FrameworkModuleImpl implements SensoryMemory {

	private static Logger logger = Logger.getLogger(SensoryMemoryImpl.class.getCanonicalName());
	
    /**
     * The {@link SensoryMemoryListener} references associated with this module.
     */
    protected List<SensoryMemoryListener> sensoryMemoryListeners
            = new ArrayList<SensoryMemoryListener>();
    
    /**
     * The {@link Environment} associated with this module.
     */
    protected Environment environment;

    /**
     * Default Constructor.
     */
    public SensoryMemoryImpl() {
    	super();
    }

    @Override
    public void addListener(ModuleListener l) {
        if (l instanceof SensoryMemoryListener) {
            addSensoryMemoryListener((SensoryMemoryListener) l);
        }else{
        	logger.log(Level.WARNING, "Cannot add listener {1}",
					new Object[]{TaskManager.getCurrentTick(),l});
        }
    }
    
    @Override
    public void addSensoryMemoryListener(SensoryMemoryListener l) {
        sensoryMemoryListeners.add(l);
    }

    @Override
    public void setAssociatedModule(FrameworkModule m, String usage) {
        if (m instanceof Environment){
             environment = (Environment) m;
        }else{
        	logger.log(Level.WARNING, "Cannot add module {1}",
					new Object[]{TaskManager.getCurrentTick(),m});
        }
    }

    /* 
     * Override with your implementation.
     */
	@Override
	public abstract void runSensors();
	
    /* 
     * Override with your implementation.
     */
    @Override
	public abstract Object getSensoryContent(String modality, Map<String, Object> params);

	/* 
     * Override with your implementation.
     */
	@Override
	public abstract void decayModule(long ticks);
    
}