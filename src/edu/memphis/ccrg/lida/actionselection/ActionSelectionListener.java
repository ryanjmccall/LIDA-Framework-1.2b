/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.actionselection;

import edu.memphis.ccrg.lida.framework.ModuleListener;

/**
 * A listeners of the {@link ActionSelection} Module
 * 
 * @author Ryan J. McCall
 *
 */
public interface ActionSelectionListener extends ModuleListener{
	
	/**
	 * Listener must receive actions.  Will be called for each action selected by the behavior network.
	 * @param action - Id of the action stored in sensory motor memory
	 */
	public void receiveAction(Action action);

}
