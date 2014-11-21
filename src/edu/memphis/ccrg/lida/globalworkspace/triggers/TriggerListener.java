/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.globalworkspace.triggers;

import edu.memphis.ccrg.lida.globalworkspace.GlobalWorkspace;

/**
 * This interface should be implemented by the class that wants to receive {@link BroadcastTrigger}
 * notifications. In general, it is the same class that implements {@link GlobalWorkspace} interface.
 *  
 * @author Javier Snaider
 */
public interface TriggerListener {
	
	/**
	 * Listener must trigger a competition for consciousness and a conscious broadcast of the winner
	 * @param t trigger that is initiating the broadcast
	 */
	public void triggerBroadcast(BroadcastTrigger t);
}
