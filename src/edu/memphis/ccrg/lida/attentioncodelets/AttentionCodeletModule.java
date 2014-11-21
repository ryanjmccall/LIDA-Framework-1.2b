/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.attentioncodelets;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.actionselection.PreafferenceListener;
import edu.memphis.ccrg.lida.framework.CodeletManagerModule;
import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.FrameworkModuleImpl;
import edu.memphis.ccrg.lida.framework.ModuleName;
import edu.memphis.ccrg.lida.framework.shared.ElementFactory;
import edu.memphis.ccrg.lida.framework.shared.NodeStructure;
import edu.memphis.ccrg.lida.framework.tasks.Codelet;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.globalworkspace.BroadcastListener;
import edu.memphis.ccrg.lida.globalworkspace.Coalition;
import edu.memphis.ccrg.lida.globalworkspace.GlobalWorkspace;
import edu.memphis.ccrg.lida.workspace.Workspace;
	
/**
 * {@link FrameworkModule} which creates and manages all {@link AttentionCodelet}.
 * @author Ryan J. McCall
 *
 */
public class AttentionCodeletModule extends FrameworkModuleImpl implements
		BroadcastListener, PreafferenceListener, CodeletManagerModule {

	private static final Logger logger = Logger
			.getLogger(AttentionCodeletModule.class.getCanonicalName());
	private static ElementFactory factory = ElementFactory.getInstance();

	private static final String DEFAULT_CODELET_TYPE = NeighborhoodAttentionCodelet.class.getSimpleName();
	private String defaultCodeletType = DEFAULT_CODELET_TYPE;

	private static final double DEFAULT_CODELET_ACTIVATION = 1.0;
	private double codeletActivation = DEFAULT_CODELET_ACTIVATION;
	
	private static final double DEFAULT_CODELET_REMOVAL_THRESHOLD = -1.0;
	private double codeletRemovalThreshold = DEFAULT_CODELET_REMOVAL_THRESHOLD;
	
	private static final double DEFAULT_CODELET_REINFORCEMENT = 0.5;
	private double codeletReinforcement = DEFAULT_CODELET_REINFORCEMENT;
	
	private Map<ModuleName, FrameworkModule> modulesMap = new HashMap<ModuleName, FrameworkModule>();
		
	/**
	 * Default constructor
	 */
	public AttentionCodeletModule() {
	}
	
	/**
     * Will set parameters with the following names:<br/><br/>
     * 
     * <b>attentionModule.defaultCodeletType</b> type of attention codelets obtained from this module<br/>
     * <b>attentionModule.codeletActivation</b> initial activation of codelets obtained from this module<br/>
     * <b>attentionModule.codeletRemovalThreshold</b> initial removal threshold for codelets obtained from this module<br/>
     * <b>attentionModule.codeletReinforcement</b> amount of reinforcement codelets' base-level activation receives during learning<br/>
     */
	@Override
	public void init() {
		defaultCodeletType = (String) getParam("attentionModule.defaultCodeletType", DEFAULT_CODELET_TYPE);
		codeletActivation = (Double) getParam("attentionModule.codeletActivation", DEFAULT_CODELET_ACTIVATION);
		codeletRemovalThreshold = (Double) getParam("attentionModule.codeletRemovalThreshold", DEFAULT_CODELET_REMOVAL_THRESHOLD);
		codeletReinforcement = (Double) getParam("attentionModule.codeletReinforcement",DEFAULT_CODELET_REINFORCEMENT);		
	}
	
	@Override
	public void setAssociatedModule(FrameworkModule module, String moduleUsage) {
		if (module instanceof Workspace) {
			FrameworkModule m = module.getSubmodule(ModuleName.CurrentSituationalModel);
			modulesMap.put(m.getModuleName(), m);
		}else if (module instanceof GlobalWorkspace) {
			modulesMap.put(module.getModuleName(), module);
		}
	}
	
	@Override
	public void setDefaultCodeletType(String type){
		if(factory.containsTaskType(type)){
			defaultCodeletType = type;
		}else{
			logger.log(Level.WARNING, 
					"Cannot set default codelet type, factory does not have type {1}",
					new Object[]{TaskManager.getCurrentTick(),type});
		}
	}

	@Override
	public void receiveBroadcast(Coalition coalition) {
		learn(coalition);
	}

	@Override
	public AttentionCodelet getDefaultCodelet(Map<String, Object> params) {
		return getCodelet(defaultCodeletType, params);
	}

	@Override
	public AttentionCodelet getDefaultCodelet() {
		return getCodelet(defaultCodeletType, null);
	}
	
	@Override
	public AttentionCodelet getCodelet(String type) {
		return getCodelet(type, null);
	}	

	@Override
	public AttentionCodelet getCodelet(String type, Map<String, Object> params) {
		AttentionCodelet codelet = (AttentionCodelet) factory.getFrameworkTask(type, params, modulesMap);
		if (codelet == null) {
			logger.log(
					Level.WARNING,
					"Specified type does not exist in the factory. Attention codelet not created.",
					TaskManager.getCurrentTick());
			return null;
		}
		codelet.setActivation(codeletActivation);
		codelet.setActivatibleRemovalThreshold(codeletRemovalThreshold);
		return codelet;
	}

	@Override
	public void addCodelet(Codelet codelet) {
		if(codelet instanceof AttentionCodelet){
			taskSpawner.addTask(codelet);
			logger.log(Level.FINER, "New attention codelet: {1} added to run.", 
					new Object[]{TaskManager.getCurrentTick(),codelet});
		}else{
			logger.log(Level.WARNING, "Can only add an AttentionCodelet", TaskManager.getCurrentTick());
		}
	}

	@Override
	public void receivePreafference(NodeStructure addSet, NodeStructure deleteSet) {
		// TODO Receive results from Action Selection and create Attention
		// Codelets. We need
		// to figure out how to create coalitions and detect that something was
		// "deleted"
	}
	
	/**
	 * Performs learning based on the {@link AttentionCodelet} that created the current<br/>
	 * winning {@link Coalition}
	 * @param winningCoalition current {@link Coalition} winning competition for consciousness
	 */
	@Override
	public void learn(Coalition winningCoalition) {
		AttentionCodelet coalitionCodelet = winningCoalition.getCreatingAttentionCodelet();
		if(coalitionCodelet instanceof DefaultAttentionCodelet){
			AttentionCodelet newCodelet = getDefaultCodelet();
			NodeStructure content = (NodeStructure) winningCoalition.getContent();
			newCodelet.setSoughtContent(content.copy());
			addCodelet(newCodelet);
			logger.log(Level.FINER, "Created new codelet: {1}", 
					new Object[]{TaskManager.getCurrentTick(),newCodelet});
		}else if (coalitionCodelet != null){
			//TODO Reinforcement amount might be a function of the broadcast's activation
			coalitionCodelet.reinforceBaseLevelActivation(codeletReinforcement);
			logger.log(Level.FINER, "Reinforcing codelet: {1}", 
					new Object[]{TaskManager.getCurrentTick(),coalitionCodelet});
		}
	}

	@Override
	public Object getModuleContent(Object... params) {
		if(params != null && params.length > 0 && params[0] instanceof String){
			if("GlobalWorkspace".equalsIgnoreCase((String) params[0])){
				return modulesMap.get(ModuleName.GlobalWorkspace);
			}
		}
		return null;
	}

	@Override
	public void decayModule(long ticks) {
		//TODO not yet implemented
	}
	
}