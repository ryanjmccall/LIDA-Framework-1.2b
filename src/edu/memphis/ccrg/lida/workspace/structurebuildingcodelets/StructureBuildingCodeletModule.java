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

import edu.memphis.ccrg.lida.framework.CodeletManagerModule;
import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.FrameworkModuleImpl;
import edu.memphis.ccrg.lida.framework.ModuleName;
import edu.memphis.ccrg.lida.framework.shared.ElementFactory;
import edu.memphis.ccrg.lida.framework.tasks.Codelet;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.workspace.Workspace;

/**
 * A module which maintains Codelets of workspace. This module manages Codelets
 * and responds for sending events to framework GUI.
 * 
 * @author Ryan J. McCall
 *
 */
public class StructureBuildingCodeletModule extends FrameworkModuleImpl implements CodeletManagerModule {

	private static final Logger logger = Logger
			.getLogger(StructureBuildingCodeletModule.class.getCanonicalName());
	private static final ElementFactory factory = ElementFactory
			.getInstance();

	private static final double DEFAULT_CODELET_ACTIVATION = 1.0;
	private double codeletActivation = DEFAULT_CODELET_ACTIVATION;
	
	private static final double DEFAULT_CODELET_REMOVAL_THRESHOLD = -1.0;
	private double codeletRemovalThreshold = DEFAULT_CODELET_REMOVAL_THRESHOLD;

	private static final String DEFAULT_CODELET_TYPE = "BasicStructureBuildingCodelet";
	private String defaultCodeletType = DEFAULT_CODELET_TYPE;

	private Map<ModuleName, FrameworkModule> modulesMap = new HashMap<ModuleName, FrameworkModule>();

	// /*
	// * Pool keeping all recycled codelets.
	// * Key = CodeletType
	// * Value = finished, reset codelets of that type
	// */
	// private Map<CodeletType, List<StructureBuildingCodelet>> codeletPool;

	/**
	 * Default Constructor. Sets up the initial 
	 * default {@link StructureBuildingCodelet} for the module.
	 */
	public StructureBuildingCodeletModule() {
	}

	/**
     * Will set parameters with the following names:<br/><br/>
     * 
     * <b>sbcModule.defaultCodeletType</b> type of attention codelets obtained from this module<br/>
     * <b>sbcModule.codeletActivation</b> initial activation of codelets obtained from this module<br/>
     * <b>sbcModule.codeletRemovalThreshold</b> initial removal threshold for codelets obtained from this module<br/>
     */
	@Override
	public void init() {
		defaultCodeletType = (String) getParam("sbcModule.defaultCodeletType", DEFAULT_CODELET_TYPE);
		codeletActivation = (Double) getParam("sbcModule.codeletActivation", DEFAULT_CODELET_ACTIVATION);
		codeletRemovalThreshold = (Double) getParam("sbcModule.codeletRemovalThreshold", DEFAULT_CODELET_REMOVAL_THRESHOLD);
	}
	
	@Override
	public void setDefaultCodeletType(String type){
		if(factory.containsTaskType(type)){
			defaultCodeletType = type;
		}else{
			logger.log(Level.WARNING, 
					"Cannot set default codelet type, factory does not have type: {1}", 
					new Object[]{TaskManager.getCurrentTick(),type});
		}
	}

	@Override
	public void setAssociatedModule(FrameworkModule module, String moduleUsage) {
		if (module instanceof Workspace) {
			modulesMap.put(module.getModuleName(), module);
			Map<ModuleName, FrameworkModule> submodules = module.getSubmodules();
			if(!submodules.isEmpty()){
				modulesMap.putAll(submodules);
			}else {
				logger.log(Level.WARNING, "Cannot add submodules",TaskManager.getCurrentTick());
			}
		} else {
			logger.log(Level.WARNING, "Cannot associate module {1}",
					new Object[]{TaskManager.getCurrentTick(),module});
		}
	}

	@Override
	public StructureBuildingCodelet getDefaultCodelet() {
		return getCodelet(defaultCodeletType, null);
	}
	@Override
	public StructureBuildingCodelet getDefaultCodelet(Map<String, Object> params) {
		return getCodelet(defaultCodeletType, params);		
	}

	@Override
	public StructureBuildingCodelet getCodelet(String codeletType) {
		return getCodelet(codeletType, null);
	}

	@Override
	public StructureBuildingCodelet getCodelet(String type, Map<String, Object> params) {
		StructureBuildingCodelet codelet = (StructureBuildingCodelet) factory.getFrameworkTask(type, params, modulesMap);
		if (codelet == null) {
			logger.log(Level.WARNING,
					"Codelet type not supported: {1}",
					new Object[]{TaskManager.getCurrentTick(),type});
			return null;
		}
		codelet.setActivation(codeletActivation);
		codelet.setActivatibleRemovalThreshold(codeletRemovalThreshold);

		return codelet;
	}
	
	@Override
	public void addCodelet(Codelet cod) {
		if (cod instanceof StructureBuildingCodelet) {
			logger.log(Level.FINER, "New codelet {1} spawned",
					new Object[]{TaskManager.getCurrentTick(),cod});
			taskSpawner.addTask(cod);
		} else {
			logger.log(Level.WARNING,
					"codelet must be a structure-buidling codelet",
					TaskManager.getCurrentTick());
		}
	}

	@Override
	public Object getModuleContent(Object... params) {
		return null;
	}

	@Override
	public void decayModule(long ticks) {
	}

}