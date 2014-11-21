/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.globalworkspace.triggers;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.globalworkspace.Coalition;
import edu.memphis.ccrg.lida.globalworkspace.GlobalWorkspace;

/**
 * A trigger that fires if any coalition is above a threshold.
 * 
 * @author Javier Snaider
 *
 */
public class IndividualCoaltionActivationTrigger extends AggregateCoalitionActivationTrigger {
	
	private static final Logger logger = Logger.getLogger(IndividualCoaltionActivationTrigger.class.getCanonicalName());
	
	/**
	 * Triggers a broadcast if any {@link Coalition} object's activation is over threshold.
	 * @param coalitions {@link Coalition} objects current in the {@link GlobalWorkspace}
	 */
	@Override
	public void checkForTriggerCondition(Collection<Coalition> coalitions) {
		for(Coalition c : coalitions){
			if(c.getActivation() > threshold){
				logger.log(Level.FINE,"Individual Activation trigger fires",TaskManager.getCurrentTick());
				gw.triggerBroadcast(this);
				break;
			}
		}
	}
}