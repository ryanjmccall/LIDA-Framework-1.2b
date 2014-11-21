/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.workspace.structurebuildingcodelets;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.ModuleName;
import edu.memphis.ccrg.lida.framework.initialization.ModuleUsage;
import edu.memphis.ccrg.lida.framework.tasks.CodeletImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.workspace.workspacebuffers.WorkspaceBuffer;

/**
 * Basic implementation of {@link StructureBuildingCodelet}
 * @author Ryan J. McCall
 *
 */
public abstract class StructureBuildingCodeletImpl extends CodeletImpl implements StructureBuildingCodelet{
	
	private static final Logger logger=Logger.getLogger(StructureBuildingCodeletImpl.class.getCanonicalName());
	
	/**
	 * Map of workspace buffers this codelet reads from.
	 */
	protected Map<ModuleName, WorkspaceBuffer> readableBuffers;
	
	/**
	 *  {@link WorkspaceBuffer} to be written to.
	 */
	protected WorkspaceBuffer writableBuffer;
	
	/**
	 * Expected results of this codelets
	 */
	protected Object runResults;
	
	/**
	 * Default Constructor
	 */
	public StructureBuildingCodeletImpl(){
		super();
		readableBuffers = new HashMap<ModuleName, WorkspaceBuffer>();
		runResults = new HashMap<String, Object>();
	}
	
	@Override
	public void setAssociatedModule(FrameworkModule module, String usage) {
		if(module instanceof WorkspaceBuffer){
			if(ModuleUsage.TO_READ_FROM.equals(usage)){
				readableBuffers.put(module.getModuleName(), (WorkspaceBuffer) module);		
			}else if(ModuleUsage.TO_WRITE_TO.equals(usage)){
				writableBuffer = (WorkspaceBuffer) module;
			}else{
				logger.log(Level.WARNING, "Specified usage is not supported.  See ModuleUsage", TaskManager.getCurrentTick());
			}
		}else{
			logger.log(Level.WARNING, "Expected module to be a WorkspaceBuffer but it was not.  Module not added.", TaskManager.getCurrentTick());
		}
	}
    	
	@Override
	protected abstract void runThisFrameworkTask();
	
	@Override
	public void reset() {
		setTicksPerRun(1);
		setActivation(0.0);
		
		readableBuffers.clear();
		writableBuffer = null;
		super.soughtContent = null;
	}
	
	@Override
	public Object getCodeletRunResult(){
		return runResults;
	}

} 
