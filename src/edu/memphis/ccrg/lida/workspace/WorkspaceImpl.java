/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.workspace;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.episodicmemory.CueListener;
import edu.memphis.ccrg.lida.episodicmemory.LocalAssociationListener;
import edu.memphis.ccrg.lida.framework.FrameworkModuleImpl;
import edu.memphis.ccrg.lida.framework.ModuleListener;
import edu.memphis.ccrg.lida.framework.ModuleName;
import edu.memphis.ccrg.lida.framework.shared.Link;
import edu.memphis.ccrg.lida.framework.shared.Node;
import edu.memphis.ccrg.lida.framework.shared.NodeStructure;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.globalworkspace.BroadcastListener;
import edu.memphis.ccrg.lida.globalworkspace.Coalition;
import edu.memphis.ccrg.lida.pam.PamListener;
import edu.memphis.ccrg.lida.workspace.workspacebuffers.WorkspaceBuffer;

/**
 * 
 * The Workspace contains the Perceptual and 
 * Episodic Buffers as well as the Broadcast Queue and Current Situational Model. 
 * This class implements the Facade pattern.  Any outside module that wishes to access and/or 
 * modify these Workspace components must do so through this class. 
 * Thus this class defines the methods to access the data of these submodules.
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 *
 */
public class WorkspaceImpl extends FrameworkModuleImpl implements Workspace, PamListener, 
									  	LocalAssociationListener, BroadcastListener{
	
	private static final Logger logger = Logger.getLogger(WorkspaceImpl.class.getCanonicalName());

	private List<CueListener> cueListeners = new ArrayList<CueListener>();
	private List<WorkspaceListener> workspaceListeners = new ArrayList<WorkspaceListener>();
	
	/**
	 * Default constructor
	 */
	public WorkspaceImpl(){
	}	
	
	@Override
	public void addListener(ModuleListener listener) {
		if (listener instanceof WorkspaceListener){
			addWorkspaceListener((WorkspaceListener)listener);
		}else if (listener instanceof CueListener){
			addCueListener((CueListener)listener);
		}else{
			logger.log(Level.WARNING, "Listener {1} was not added, wrong type.", 
					new Object[]{TaskManager.getCurrentTick(),listener});
		}
	}

	@Override
	public void addCueListener(CueListener l){
		cueListeners.add(l);
	}

	@Override
	public void addWorkspaceListener(WorkspaceListener listener){
		workspaceListeners.add(listener);
	}
	
	@Override
	public void cueEpisodicMemories(NodeStructure content){
		for(CueListener c: cueListeners){
			c.receiveCue(content);
		}
		logger.log(Level.FINER, "Cue performed.", TaskManager.getCurrentTick());
	}
	
	/*
	 * Received broadcasts are sent to the BroadcastQueue
	 */
	@Override
	public void receiveBroadcast(Coalition c) {
		if(containsSubmodule(ModuleName.BroadcastQueue)){
			BroadcastListener listener = (BroadcastListener) getSubmodule(ModuleName.BroadcastQueue);
			listener.receiveBroadcast(c);
		}else{
			logger.log(Level.WARNING, "Received broadcast content but Workspace does not have a broadcast queue.", TaskManager.getCurrentTick());
		}
	}
	
	/*
	 * Received local associations are merged into the episodic buffer.
	 * Then they are sent to PAM.
	 */
	@Override
	public void receiveLocalAssociation(NodeStructure association) {
		if(containsSubmodule(ModuleName.EpisodicBuffer)){
			WorkspaceBuffer buffer = (WorkspaceBuffer) getSubmodule(ModuleName.EpisodicBuffer);
			buffer.addBufferContent((WorkspaceContent) association);
			for(WorkspaceListener listener: workspaceListeners){
				listener.receiveWorkspaceContent(ModuleName.EpisodicBuffer, buffer.getBufferContent(null));
			}
		}else{
			logger.log(Level.WARNING, "Received a Local assocation but Workspace does not have an episodic buffer.", TaskManager.getCurrentTick());
		}
		
	}
	
	/*
	 * Implementation of the PamListener interface.  Adds newPercept to the
	 * the perceptualBuffer.
	 */
	@Override
	public void receivePercept(NodeStructure newPercept) {
		if(containsSubmodule(ModuleName.PerceptualBuffer)){
			WorkspaceBuffer buffer = (WorkspaceBuffer) getSubmodule(ModuleName.PerceptualBuffer);
			buffer.addBufferContent((WorkspaceContent) newPercept);
		}else{
			logger.log(Level.WARNING, "Received a percept but Workspace does not have a perceptual buffer.", TaskManager.getCurrentTick());
		}
	}
	
	@Override
	public void receivePercept(Node n) {
		if(containsSubmodule(ModuleName.PerceptualBuffer)){
			WorkspaceBuffer buffer = (WorkspaceBuffer) getSubmodule(ModuleName.PerceptualBuffer);
			NodeStructure ns = buffer.getBufferContent(null);
			ns.addDefaultNode(n);
		}else{
			logger.log(Level.WARNING, "Received a percept but Workspace does not have a perceptual buffer.", TaskManager.getCurrentTick());
		}
	}

	@Override
	public void receivePercept(Link l) {
		if(containsSubmodule(ModuleName.PerceptualBuffer)){
			WorkspaceBuffer buffer = (WorkspaceBuffer) getSubmodule(ModuleName.PerceptualBuffer);
			NodeStructure ns = buffer.getBufferContent(null);
			ns.addDefaultLink(l);
		}else{
			logger.log(Level.WARNING, "Received a percept but Workspace does not have a perceptual buffer.", TaskManager.getCurrentTick());
		}
	}

	@Override
	public Object getModuleContent(Object... params) {
		return null;
	}

	@Override
	public void learn(Coalition coalition) {
		// Not applicable
	}

	/** 
	 * Should do nothing, submodules' decayModule method will be called 
     * in FrameworkModuleImpl#taskManagerDecayModule.
	 * @see edu.memphis.ccrg.lida.framework.FrameworkModule#decayModule(long)
	 */
	@Override
	public void decayModule(long ticks) {
	}
	
}
