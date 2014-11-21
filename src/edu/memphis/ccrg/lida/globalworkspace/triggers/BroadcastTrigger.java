/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
/**
 * 
 */
package edu.memphis.ccrg.lida.globalworkspace.triggers;

import java.util.Collection;
import java.util.Map;

import edu.memphis.ccrg.lida.globalworkspace.Coalition;
import edu.memphis.ccrg.lida.globalworkspace.GlobalWorkspace;

/**
 * A BroadcastTrigger determines when a new broadcast must be triggered.
 * Its {@link #start()} method should be invoked once and only once (this is likely to be when the GlobalWorkspace starts)
 * Its {@link #checkForTriggerCondition(Collection)} method is called every time a new {@link Coalition} enters the {@link GlobalWorkspace}.
 *  
 * @author Javier Snaider
 * @author Ryan J. McCall
 * @see IndividualCoaltionActivationTrigger
 * @see NoBroadcastOccurringTrigger
 */
public interface BroadcastTrigger {
	
	/**
	 * Provides a generic way to setup a BroadcastTrigger. It should be called when 
	 * the trigger is created.
	 * @param params a map for generic parameters
	 * @param gw A {@link TriggerListener} and likely a class that implements the {@link GlobalWorkspace} interface.
	 */
	public void init (Map<String,Object> params, GlobalWorkspace gw);
	
	/**
	 * When called the trigger checks if its firing condition. If it has it initiates a competition for consciousness. 
	 * This method is called for all registered triggers each time a new {@link Coalition} is put in the {@link GlobalWorkspace}
	 * @param coalitions All the {@link Coalition} objects currently in the {@link GlobalWorkspace}.
	 */
	public void checkForTriggerCondition (Collection<Coalition> coalitions);
	
	/**
	 * Resets the trigger. Called each time a new broadcast is triggered.
	 */
	public void reset();
	
	/**
	 * Starts the trigger.
	 */
	public void start();
}