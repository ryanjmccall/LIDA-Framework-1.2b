/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.globalworkspace.triggers;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.globalworkspace.Coalition;
import edu.memphis.ccrg.lida.globalworkspace.GlobalWorkspace;
import edu.memphis.ccrg.lida.globalworkspace.GlobalWorkspaceImpl;

/**
 * Implements a trigger that is activated when the sum of all {@link Coalition} objects
 * in {@link GlobalWorkspace} is greater than a threshold.
 * 
 * @author Javier Snaider
 * 
 */
public class AggregateCoalitionActivationTrigger implements BroadcastTrigger {

	private static final double DEFAULT_THRESHOLD = 0.5;
	private Logger logger = Logger
			.getLogger(AggregateCoalitionActivationTrigger.class
					.getCanonicalName());
	/**
	 * Reference to the {@link GlobalWorkspace}
	 */
	protected GlobalWorkspace gw;
	/**
	 * The activation threshold
	 */
	protected double threshold;

	/**
	 * Calculates the aggregate activation of all coalitions in the {@link GlobalWorkspace} and 
	 * if it is over threshold a broadcast is triggered.
	 * This method is called each time a new {@link Coalition} enters the {@link GlobalWorkspace}
	 * 
	 * @param coalitions
	 *            a Collection of all the {@link Coalition} objects in the {@link GlobalWorkspace}.
	 * @see GlobalWorkspaceImpl#addCoalition(Coalition)
	 */
	@Override
	public void checkForTriggerCondition(Collection<Coalition> coalitions) {
		double aggregateActivation = 0.0;
		for (Coalition c : coalitions) {
			aggregateActivation += c.getActivation();
		}
		if (aggregateActivation > threshold) {
			logger.log(Level.FINE, "Aggregate Activation trigger fires",
					TaskManager.getCurrentTick());
			gw.triggerBroadcast(this);
		}
	}

	@Override
	public void reset() {
		// not applicable
	}

	/**
	 * This method expects a parameter with name "threshold" of type double representing the coalition activation threshold 
	 * at which the trigger will fire.
	 * 
	 * @see BroadcastTrigger#init(Map, GlobalWorkspace)
	 */
	@Override
	public void init(Map<String, Object> parameters, GlobalWorkspace gw) {
		this.gw = gw;
		Object o = parameters.get("threshold");
		if ((o != null) && (o instanceof Double)) {
			threshold = (Double) o;
			if(threshold < 0.0){
				logger.log(Level.WARNING, "Invalid threshold parameter, using default.", TaskManager.getCurrentTick());
				threshold = DEFAULT_THRESHOLD;
			}
		}else{
			threshold = DEFAULT_THRESHOLD;
			logger.log(Level.WARNING, "Failed to set threshold parameter, using default.", TaskManager.getCurrentTick());
		}
	}

	@Override
	public void start() {
		// not applicable
	}
	
	/**
	 * Gets threshold
	 * @return threshold to activate the trigger 
	 */
	public double getThreshold(){
		return threshold;
	}

}