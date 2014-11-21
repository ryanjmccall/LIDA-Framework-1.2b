/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.initialization.AgentXmlFactory;
import edu.memphis.ccrg.lida.framework.initialization.InitializableImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.framework.tasks.TaskSpawner;

/**
 * Abstract implementation of {@link FrameworkModule} Implementations should add
 * themselves to the agent.xml configuration file
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 */
public abstract class FrameworkModuleImpl extends InitializableImpl implements
		FrameworkModule {

	private static final Logger logger = Logger
			.getLogger(FrameworkModuleImpl.class.getCanonicalName());
	private ModuleName moduleName;
	private Map<ModuleName, FrameworkModule> submodules = new ConcurrentHashMap<ModuleName, FrameworkModule>();

	/**
	 * {@link TaskSpawner} used by this module
	 */
	protected TaskSpawner taskSpawner;

	/**
	 * Default constructor
	 */
	public FrameworkModuleImpl() {
		moduleName = ModuleName.UnnamedModule;
	}

	/**
	 * Creates a FrameworkModule with specified module name. It is generally
	 * preferable to use the default constructor instead of this one since the
	 * ModuleName is typically specified by agent.xml and set by
	 * {@link AgentXmlFactory}
	 * 
	 * @see AgentXmlFactory
	 * @param name
	 *            {@link ModuleName} of this {@link FrameworkModule}
	 */
	FrameworkModuleImpl(ModuleName name) {
		moduleName = name;
	}

	@Override
	public void setAssistingTaskSpawner(TaskSpawner ts) {
		taskSpawner = ts;
	}

	@Override
	public TaskSpawner getAssistingTaskSpawner() {
		return taskSpawner;
	}

	@Override
	public FrameworkModule getSubmodule(ModuleName name) {
		return (name==null)? null:submodules.get(name);
	}

	@Override
	public FrameworkModule getSubmodule(String name) {
		if(name==null){
			return null;
		}
	    return getSubmodule(ModuleName.getModuleName(name));
	}

	@Override
	public boolean containsSubmodule(ModuleName name) {
		return (getSubmodule(name) != null);
	}

	@Override
	public boolean containsSubmodule(String name) {
		return (getSubmodule(name) != null);
	}

	@Override
	public void addSubModule(FrameworkModule module) {
		if (module == null){
			logger.log(Level.WARNING,
					"Cannot add null submodule", 
					TaskManager.getCurrentTick());
		}else if(module.getModuleName() == null){
			logger.log(Level.WARNING,
					"Cannot add a  submodule with null ModuleName", TaskManager
							.getCurrentTick());
		} else {
			submodules.put(module.getModuleName(), module);
		}
	}

	@Override
	public Map<ModuleName, FrameworkModule> getSubmodules() {
		return Collections.unmodifiableMap(submodules);
	}

	/**
	 * Framework users should not call this method. 
	 * It will be called by the {@link TaskManager}. Decays this module and all its submodules. 
	 * Subclasses overriding this method must call this method first in order to have all
	 * submodules decayed.
	 * 
	 * @param ticks
	 *            number of ticks to decay.
	 */
	@Override
	public void taskManagerDecayModule(long ticks) {
		try{
			decayModule(ticks); //First call this FrameworkModule's decayModule method.
		}catch(Exception e){
			logger.log(Level.WARNING, 
					"Exception occurred during the execution of the 'decayModule(long ticks)' method in module: {1}. \n{2}",
					new Object[]{TaskManager.getCurrentTick(),moduleName,e});
			e.printStackTrace();
		}
		for (FrameworkModule lm : submodules.values()) {
			try{
				lm.taskManagerDecayModule(ticks); //Then call all submodule's taskManagerDecayModule.
			}catch(Exception e){
				logger.log(Level.WARNING, 
						"Exception occurred during the execution of the 'taskManagerDecayModule(long ticks)' method in module: {1}. \n{2}",
						new Object[]{TaskManager.getCurrentTick(),moduleName,e});
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setModuleName(ModuleName name) {
		moduleName = name;
	}

	@Override
	public ModuleName getModuleName() {
		return moduleName;
	}

	@Override
	public String toString() {
		return (moduleName == null)? null:moduleName.name;
	}

	@Override
	public void setAssociatedModule(FrameworkModule module, String moduleUsage) {
	}

	/**
	 * Override this method to add a listener to the module
	 * 
	 * @param listener
	 *            - listener of this FrameworkModule
	 */
	@Override
	public void addListener(ModuleListener listener) {
	}

	/**
	 * Intended to be called from the GUI. Override this method to return
	 * particular module content based on params.
	 */
	@Override
	public Object getModuleContent(Object... params) {
		return null;
	}

}