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

/**
 * Listen to response from Episodic memory to a previous cue.
 * This cue need not originate from this same Listener. Any class can
 * originate the cue even if it does not implement this interface.
 *
 * @author Ryan J. McCall
  */
public interface LocalAssociationListener extends ModuleListener{
	
	/**
	 * @param association The response generated from the Episodic Memory to a previous cue.
	 */
	public void receiveLocalAssociation(NodeStructure association);

}
