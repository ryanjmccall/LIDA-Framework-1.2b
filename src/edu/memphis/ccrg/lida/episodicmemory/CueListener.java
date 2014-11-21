/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.episodicmemory;

import edu.memphis.ccrg.lida.framework.ModuleListener;
import edu.memphis.ccrg.lida.framework.shared.NodeStructure;
import edu.memphis.ccrg.lida.workspace.Workspace;

/**
 * Listens to cues from the {@link Workspace}. 
 * This interface is typically implemented by {@link EpisodicMemory} modules.
 * 
 * @author Ryan J. McCall
 */
public interface CueListener extends ModuleListener {
	
	/**
	 * Receive a cue
	 * @param cue a {@link NodeStructure} to cue {@link EpisodicMemory} with
	 */
	public void receiveCue(NodeStructure cue);

}

