/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.globalworkspace.triggers;

import java.util.Collection;
import java.util.TimerTask;

import edu.memphis.ccrg.lida.globalworkspace.Coalition;
import edu.memphis.ccrg.lida.globalworkspace.GlobalWorkspace;


/**
 * A trigger that fires when a certain number of ticks have passed without 
 * a new {@link Coalition} being added to the {@link GlobalWorkspace}.
 * Inherits most of its fields and methods from its parent class {@link NoBroadcastOccurringTrigger}.
 * 
 * @author Javier Snaider
 * @see NoBroadcastOccurringTrigger
 */
public class NoCoalitionArrivingTrigger extends NoBroadcastOccurringTrigger {
	
	/**
	 * Called each time a new coalition is added to the {@link GlobalWorkspace}.
	 * Specifically for this trigger {@link NoBroadcastOccurringTrigger#reset()} is called which resets the {@link TimerTask} object.  
	 * Thus this trigger fires when a certain number of ticks have passed without a new {@link Coalition} entering the {@link GlobalWorkspace}. 
	 * 
	 * @param coalitions {@link Coalition} objects the trigger can check
	 */
	@Override
	public void checkForTriggerCondition(Collection<Coalition> coalitions) {
		reset();
	}
}