/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.pam.tasks;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.initialization.GlobalInitializer;
import edu.memphis.ccrg.lida.framework.initialization.Initializable;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTaskImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.pam.PamLinkable;
import edu.memphis.ccrg.lida.pam.PamNode;
import edu.memphis.ccrg.lida.pam.PerceptualAssociativeMemory;
import edu.memphis.ccrg.lida.sensorymemory.SensoryMemory;

/**
 * This class implements the FeatureDetector interface and provides default methods. 
 * Users should extend this class and overwrite the detect() and excitePam() methods.
 * A convenience init() method is added to initialize the class. This method can be 
 * overwritten as well.
 * This implementation is oriented to detect features from sensoryMemory, but the implementation 
 * can be used to detect and send excitation from other modules, like Workspace, emotions or internal states.
 * 
 * @author Ryan J. McCall
 * 
 */
public abstract class MultipleDetectionAlgorithm extends FrameworkTaskImpl implements DetectionAlgorithm {

	private static final Logger logger = Logger.getLogger(MultipleDetectionAlgorithm.class.getCanonicalName());
	/**
	 * Map of {@link PamLinkable}
	 */
	protected Map<String, PamLinkable> pamNodeMap = new HashMap<String, PamLinkable>();
	/**
	 * the {@link PerceptualAssociativeMemory}
	 */
	protected PerceptualAssociativeMemory pam;
	/**
	 * {@link PamLinkable} this algorithm detects
	 */
	protected SensoryMemory sensoryMemory;
	
	/**
	 * 
	 */
	public MultipleDetectionAlgorithm(){
	}
	
	@Override
	public void setAssociatedModule(FrameworkModule module, String moduleUsage){
		if(module instanceof PerceptualAssociativeMemory){
			pam = (PerceptualAssociativeMemory) module;
		}else if(module instanceof SensoryMemory){
			sensoryMemory = (SensoryMemory) module;
		}else{
			logger.log(Level.WARNING, "Cannot set associated module {1}",
					new Object[]{TaskManager.getCurrentTick(),module});
		}
	}
	
	/**
	 * This task can be initialized with the following parameters:<br><br/>
	 * 
	 * <b>nodes type=string</b>labels of the Nodes in {@link PerceptualAssociativeMemory} this algorithm detects<br/>
	 * 
	 * @see Initializable
	 */
	@Override
	public void init (){
		super.init();
		String nodeLabels = (String) getParam("nodes", "");
		if (nodeLabels != null) {
			GlobalInitializer globalInitializer = GlobalInitializer
					.getInstance();
			String[] labels = nodeLabels.split(",");
			for (String label : labels) {
				label = label.trim();
				PamNode node = (PamNode) globalInitializer.getAttribute(label);
				if (node != null) {
					addPamLinkable(node);
				}else{
					logger.log(Level.WARNING, "could not get node with label {1} from global initializer",
							new Object[]{TaskManager.getCurrentTick(),label});
				}
			}
		}
	}
	
	/**
	 * Adds {@link PamLinkable}.
	 * @param linkable {@link PamLinkable} that will be detected by this algorithm
	 */
	public void addPamLinkable(PamLinkable linkable) {
		pamNodeMap.put(linkable.getLabel(), linkable);
	}
	
	@Override
	protected void runThisFrameworkTask(){
		detectLinkables();
		if(logger.isLoggable(Level.FINEST)){
			logger.log(Level.FINEST,"detection performed {1}"
					,new Object[]{TaskManager.getCurrentTick(),this});
		}
	}

	/**
	 * Override this method for domain-specific feature detection
	 */
	public abstract void detectLinkables();

	//Methods below are not applicable
	@Override
	public double detect() {
		return 0;
	}
	@Override
	public PamLinkable getPamLinkable() {
		Collection<PamLinkable> nodes = pamNodeMap.values();
		if(nodes.size() != 0){
			return nodes.iterator().next();
		}
		return null;
	}
	@Override
	public void setPamLinkable(PamLinkable linkable) {
	}
}
