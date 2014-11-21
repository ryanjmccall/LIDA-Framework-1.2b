/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework;

import java.util.Map;

import edu.memphis.ccrg.lida.framework.initialization.FullyInitializable;
import edu.memphis.ccrg.lida.framework.tasks.TaskSpawner;

/**
 * Interface for the modules of an agent.
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 *
 */
public interface FrameworkModule extends FullyInitializable{

	/**
	 * Gets moduleName.
	 * 
	 * @return {@link ModuleName} of this FrameworkModule
	 */
	public ModuleName getModuleName();
	
	/**
	 * Sets ModuleName.
	 * 
	 * @param moduleName {@link ModuleName} of this FrameworkModule
	 */
	public void setModuleName(ModuleName moduleName);
	
	/**
	 * Returns whether this {@link FrameworkModule} contains a submodule with 
	 * specified {@link ModuleName}.
	 * @param name {@link ModuleName} of submodule
	 * @return true if there is a {@link FrameworkModule} with specified {@link ModuleName}
	 * in this {@link FrameworkModule}
	 */
	public boolean containsSubmodule(ModuleName name);
	
	/**
	 * Returns whether this {@link FrameworkModule} contains a submodule with 
	 * specified name.
	 * @param name {@link ModuleName} of submodule
	 * @return true if there is a {@link FrameworkModule} with specified name
	 * in this {@link FrameworkModule}
	 */
	public boolean containsSubmodule(String name);
	
	/**
	 * Gets specified submodule subModule.
	 * 
	 * @param name name of the desired submodule.
	 * @return the submodule.
	 */
	public FrameworkModule getSubmodule(ModuleName name);
	
	/**
	 * Gets specified submodule.
	 * 
	 * @param name name of the desired submodule.
	 * @return the submodule.
	 */
	public FrameworkModule getSubmodule(String name);
	
	/**
	 * Adds submodule as a component of this FrameworkModule.
	 * @param lm submodule to add
	 */
	public void addSubModule(FrameworkModule lm);
	
	/**
	 * Returns module content specified by params. Intended for use by the GUI only.
	 * 
	 * @param params parameters specifying what content will be returned
	 * @return Parameter-specified content of this module.
	 */
	public Object getModuleContent(Object... params);

	/**
	 * Decay this module and all its submodules. 
	 * Framework users should not call this method. It will be called by the TaskManager.
	 * Decays this module and all its submodules. 
	 * 
	 * @param ticks number of ticks to decay.
	 */
	public void taskManagerDecayModule(long ticks);
	
	/**
	 * Decay only this Module.   
	 * @param ticks number of ticks to decay.
	 */
	public void decayModule(long ticks);

	/**
	 * Generic way to add various kinds of listeners.  
	 * @param listener - listener of this FrameworkModule
	 */
	public void addListener(ModuleListener listener);
	
	/**
	 * Specify the {@link TaskSpawner} which this FrameworkModule will use to spawn tasks.
	 * 
	 * @param ts the TaskSpawner
	 */
	public void setAssistingTaskSpawner(TaskSpawner ts);

	/**
	 * Returns the {@link TaskSpawner} which this FrameworkModule uses to spawn tasks.
	 * 
	 * @return the assisting task spawner
	 */
	public TaskSpawner getAssistingTaskSpawner();

	/**
	 * Convenience method to get submodules
	 * @return map of submodules by {@link ModuleName}
	 */
	public Map<ModuleName, FrameworkModule> getSubmodules();

}
