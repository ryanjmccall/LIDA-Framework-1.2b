/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.globalworkspace;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.Agent;
import edu.memphis.ccrg.lida.framework.initialization.FullyInitializable;
import edu.memphis.ccrg.lida.framework.initialization.Initializer;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.globalworkspace.triggers.AggregateCoalitionActivationTrigger;
import edu.memphis.ccrg.lida.globalworkspace.triggers.BroadcastTrigger;
import edu.memphis.ccrg.lida.globalworkspace.triggers.IndividualCoaltionActivationTrigger;
import edu.memphis.ccrg.lida.globalworkspace.triggers.NoBroadcastOccurringTrigger;
import edu.memphis.ccrg.lida.globalworkspace.triggers.NoCoalitionArrivingTrigger;

/**
 * Default {@link Initializer} implementation for the {@link GlobalWorkspace}.
 * Other implementations should always add at least one {@link BroadcastTrigger} to their
 * {@link GlobalWorkspace} module.
 * 
 * @author Javier Snaider
 */
public class GlobalWorkspaceInitializer implements Initializer {

	private static final Logger logger = Logger
			.getLogger(GlobalWorkspaceInitializer.class.getCanonicalName());

	private static final Integer DEFAULT_DELAY_NO_BROADCAST = 100;
	private static final Integer DEFAULT_DELAY_NO_NEW_COALITION = 50;
	private static final Double DEFAULT_AGGREGATE_ACT_THRESHOLD = 0.8;
	private static final Double DEFAULT_INDIVIDUAL_ACT_THRESHOLD = 0.5;

	@Override
	public void initModule(FullyInitializable module, Agent lida,
			Map<String, ?> params) {
		GlobalWorkspace globalWksp = (GlobalWorkspace) module;

        Integer delayNoBroadcast = (Integer) params.get("globalWorkspace.delayNoBroadcast");
		if (delayNoBroadcast == null || delayNoBroadcast <= 0) {
			delayNoBroadcast = DEFAULT_DELAY_NO_BROADCAST;
			logger.log(Level.WARNING, "Invalid delay no broadcast parameter, using default",
					TaskManager.getCurrentTick());
		}

		Integer delayNoNewCoalition = (Integer) params
				.get("globalWorkspace.delayNoNewCoalition");
		if (delayNoNewCoalition == null || delayNoNewCoalition <= 0) {
			delayNoNewCoalition = DEFAULT_DELAY_NO_NEW_COALITION;
			logger.log(Level.WARNING,
					"Invalid delay no new coalition parameter, using default",
					TaskManager.getCurrentTick());
		}

		Double aggregateActivationThreshold = (Double) params
				.get("globalWorkspace.aggregateActivationThreshold");
		if (aggregateActivationThreshold == null || aggregateActivationThreshold < 0.0) {
			aggregateActivationThreshold = DEFAULT_AGGREGATE_ACT_THRESHOLD;
			logger
					.log(
							Level.WARNING,
							"Invalid aggregate activation threshold parameter, using default",
							TaskManager.getCurrentTick());
		}

		Double individualActivationThreshold = (Double) params
				.get("globalWorkspace.individualActivationThreshold");
		if (individualActivationThreshold == null || individualActivationThreshold < 0.0) {
			individualActivationThreshold = DEFAULT_INDIVIDUAL_ACT_THRESHOLD;
			logger.log(Level.WARNING,
							"Invalid individual activation threshold parameter, using default",
							TaskManager.getCurrentTick());
		}

		BroadcastTrigger tr = new NoBroadcastOccurringTrigger();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("name", "NoBroadcastOccurringTrigger");
		parameters.put("delay", delayNoBroadcast);
		tr.init(parameters, globalWksp);
		globalWksp.addBroadcastTrigger(tr);

		tr = new AggregateCoalitionActivationTrigger();
		parameters.clear();
		parameters.put("threshold", aggregateActivationThreshold);
		tr.init(parameters, globalWksp);
		globalWksp.addBroadcastTrigger(tr);

		tr = new NoCoalitionArrivingTrigger();
		parameters.clear();
		parameters.put("name", "NoCoalitionArrivingTrigger");
		parameters.put("delay", delayNoNewCoalition);
		tr.init(parameters, globalWksp);
		globalWksp.addBroadcastTrigger(tr);

		tr = new IndividualCoaltionActivationTrigger();
		parameters.clear();
		parameters.put("threshold", individualActivationThreshold);
		tr.init(parameters, globalWksp);
		globalWksp.addBroadcastTrigger(tr);
	}

}
