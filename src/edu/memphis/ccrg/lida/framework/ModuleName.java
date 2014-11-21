/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.memphis.ccrg.lida.attentioncodelets.AttentionCodeletModule;
import edu.memphis.ccrg.lida.episodicmemory.EpisodicMemory;
import edu.memphis.ccrg.lida.sensorymemory.SensoryMemory;
import edu.memphis.ccrg.lida.workspace.workspacebuffers.WorkspaceBuffer;

/**
 * Encapsulation of the name of a {@link FrameworkModule}.  Provides several public-static instances by default. 
 * @author Javier Snaider
 * @author Ryan J. McCall
 */
public class ModuleName {

	/**
	 * String representation of {@link ModuleName}
	 */
	public final String name;
	private static Map<String, ModuleName> moduleNames = new HashMap<String, ModuleName>();

	/**
	 * Returns ModuleName of specified name. 
	 * @param name String
	 * @return ModuleName
	 */
	public static ModuleName getModuleName(String name) {
		return moduleNames.get(name);
	}

	/**
	 * Creates and adds a new module name if name is not already defined.
	 * Returns new ModuleName or existing {@link ModuleName} associated with the name.
	 * @param name String
	 * @return ModuleName
	 */
	public static ModuleName addModuleName(String name) {
		if (!moduleNames.containsKey(name)) {
			new ModuleName(name);
		}
		return moduleNames.get(name);
	}

	private ModuleName(String name) {
		this.name = name;
		moduleNames.put(name, this);
	}
	
	@Override
	public String toString(){
		return name;
	}

	/**
	 * Returns a {@link Collection} of all {@link ModuleName}s
	 * @return all module names
	 */
	public static Collection<ModuleName> values() {
		return Collections.unmodifiableCollection(moduleNames.values());
	}

	/**
	 * Name of an {@link edu.memphis.ccrg.lida.environment.Environment} module
	 */
	public final static ModuleName Environment = new ModuleName("Environment");
	/**
	 * Name of a {@link SensoryMemory} module
	 */
	public final static ModuleName SensoryMemory = new ModuleName(
			"SensoryMemory");
	/**
	 * Name of a {@link edu.memphis.ccrg.lida.pam.PerceptualAssociativeMemory} module
	 */
	public final static ModuleName PerceptualAssociativeMemory = new ModuleName(
			"PerceptualAssociativeMemory");
	/**
	 * Name of an {@link EpisodicMemory} module
	 */
	public final static ModuleName TransientEpisodicMemory = new ModuleName(
			"TransientEpisodicMemory");
	/**
	 * Name of an {@link EpisodicMemory} module
	 */
	public final static ModuleName DeclarativeMemory = new ModuleName(
			"DeclarativeMemory");
	/**
	 * Name of a {@link edu.memphis.ccrg.lida.workspace.Workspace} module
	 */
	public final static ModuleName Workspace = new ModuleName("Workspace");
	/**
	 * Name of a {@link WorkspaceBuffer} module
	 */
	public final static ModuleName PerceptualBuffer = new ModuleName(
			"PerceptualBuffer");
	/**
	 * Name of a {@link WorkspaceBuffer} module
	 */
	public final static ModuleName EpisodicBuffer = new ModuleName(
			"EpisodicBuffer");
	/**
	 * Name of a {@link edu.memphis.ccrg.lida.workspace.workspacebuffers.BroadcastQueue} module
	 */
	public final static ModuleName BroadcastQueue = new ModuleName(
			"BroadcastQueue");
	/**
	 * Name of a {@link WorkspaceBuffer} module
	 */
	public final static ModuleName CurrentSituationalModel = new ModuleName(
			"CurrentSituationalModel");
	/**
	 * Name of an {@link AttentionCodeletModule} 
	 */
	public final static ModuleName AttentionModule = new ModuleName(
			"AttentionModule");
	/**
	 * Name of a {@link edu.memphis.ccrg.lida.workspace.structurebuildingcodelets.StructureBuildingCodeletModule} 
	 */
	public final static ModuleName StructureBuildingCodeletModule = new ModuleName(
			"StructureBuildingCodeletModule");
	/**
	 * Name of a {@link edu.memphis.ccrg.lida.globalworkspace.GlobalWorkspace} module
	 */
	public final static ModuleName GlobalWorkspace = new ModuleName(
			"GlobalWorkspace");
	/**
	 * Name of a {@link edu.memphis.ccrg.lida.proceduralmemory.ProceduralMemory} module
	 */
	public final static ModuleName ProceduralMemory = new ModuleName(
			"ProceduralMemory");
	/**
	 * Name of an {@link edu.memphis.ccrg.lida.actionselection.ActionSelection} module
	 */
	public final static ModuleName ActionSelection = new ModuleName(
			"ActionSelection");
	/**
	 * Name of a {@link edu.memphis.ccrg.lida.sensorymotormemory.SensoryMotorMemory} module
	 */
	public final static ModuleName SensoryMotorMemory = new ModuleName(
			"SensoryMotorMemory");
	/**
	 * Name of an {@link Agent} module
	 */
	public final static ModuleName Agent = new ModuleName("Agent");
	/**
	 * Name of an unnamed module
	 */
	public final static ModuleName UnnamedModule = new ModuleName("UnnamedModule");
}
