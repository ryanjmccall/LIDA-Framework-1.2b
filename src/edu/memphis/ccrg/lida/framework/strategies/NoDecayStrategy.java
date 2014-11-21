/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.strategies;

import java.util.Map;

/**
 * A {@link DecayStrategy} that never modifies the activation passed to it.
 * @author Ryan J. McCall
 * @author Javier Snaider
 */
public class NoDecayStrategy extends StrategyImpl implements DecayStrategy {

	/**
	 * Default constructor
	 */
	public NoDecayStrategy() {
	}

	
    /**
     * Decays the current activation according to some internal decay function.
     * @param currentActivation activation of the entity before decay.
     * @param ticks The number of ticks to decay.
     * @param params optional parameters: N/A
     * @return new activation 
     */
	@Override
	public double decay(double currentActivation, long ticks, Object... params) {
		return currentActivation;
	}

	
	/**
	 * Decays the current activation according to some internal decay function.
	 * @param currentActivation activation of the entity before decay.
	 * @param ticks how much time has passed since last decay
	 * @param params parameters: N/A
	 * @return new activation amount
	 */
	@Override
	public double decay(double currentActivation, long ticks,
			Map<String, ? extends Object> params) {
		return currentActivation;
	}
}