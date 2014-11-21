/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.strategies;

import java.util.Map;

import edu.memphis.ccrg.lida.framework.shared.ElementFactory;

/**
 * A strategy pattern for decaying Activatibles or Learnables 
 * 
 * Implementations should add themselves to {@link ElementFactory} via the
 * factoriesData.xml configuration file.
 *  
 * @author Javier Snaider
 * @author Ryan J. McCall
 */
public interface DecayStrategy extends Strategy{

    /**
     * Decays the current activation according to some internal decay function.
     * @param currentActivation activation of the entity before decay.
     * @param ticks The number of ticks to decay.
     * @param params optional parameters
     * @return new activation 
     */
	public double decay(double currentActivation, long ticks, Object... params);
	
	/**
	 * Decays the current activation according to some internal decay function.
	 * @param currentActivation activation of the entity before decay.
	 * @param ticks how much time has passed since last decay
	 * @param params parameters
	 * @return new activation amount
	 */
	public double decay(double currentActivation, long ticks, Map<String, ? extends Object>params);
        
}
