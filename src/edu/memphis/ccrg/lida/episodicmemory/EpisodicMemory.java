/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/

package edu.memphis.ccrg.lida.episodicmemory;

import edu.memphis.ccrg.lida.framework.FrameworkModule;


/**
 * The interface for LIDA's episodic memory. Episodic memory in LIDA
 * communicates with the workspace, receiving memory cues, and returning local
 * associations. 
 * <p>
 * Specific implementations of episodic memories must implement
 * this interface. Every implementation of this interface must also implement
 * {@link edu.memphis.ccrg.lida.globalworkspace.BroadcastListener}.
 * 
 * @author Javier Snaider
 */
public interface EpisodicMemory extends FrameworkModule{
    
}