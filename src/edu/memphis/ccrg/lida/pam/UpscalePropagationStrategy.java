/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.pam;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.strategies.StrategyImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * Calculates a new activation using an upscale parameter.
 * 
 * @author Ryan J. McCall
 */
public class UpscalePropagationStrategy extends StrategyImpl implements PropagationStrategy{
	
	private Logger logger = Logger.getLogger(UpscalePropagationStrategy.class.getCanonicalName());

	/*
	 * Calculate and return an activation to propagate.
	 * 
	 * @param params
	 *            the params
	 * @return the activation to propagate
	 */
	@Override
	public double getActivationToPropagate(Map<String, Object> params) {
		if(params.containsKey("totalActivation") && params.containsKey("upscale")){
			return (Double)params.get("totalActivation") * (Double)params.get("upscale");
		}else{
			logger.log(Level.WARNING,"Unable to obtain parameters",TaskManager.getCurrentTick());
			return 0.0;
		}
	}
}
