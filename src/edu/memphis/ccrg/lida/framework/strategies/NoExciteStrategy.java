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
 * An {@link ExciteStrategy} that never modifies the activation passed to it.
 * @author Ryan J. McCall
 * @author Javier Snaider
 */
public class NoExciteStrategy extends StrategyImpl implements ExciteStrategy {

	/**
	 * Default constructor
	 */
	public NoExciteStrategy() {		
	}
	
	
	/**
     * Excites the current activation according to some internal excite function.
     * @param currentActivation activation of the entity before excite.
     * @param excitation amount of activation to adds
     * @param params parameters: N/A
     * @return new activation amount
     */
	@Override
	public double excite(double currentActivation, double excitation, Object... params) {
		return currentActivation;
	}

	
	/**
	 * Excites the current activation according to some internal excite function.
	 * @param currentActivation activation of the entity before excite.
	 * @param excitation amount of activation to adds
	 * @param params parameters: N/A
	 * @return new activation amount
	 */
	@Override
	public double excite(double currentActivation, double excitation, Map<String, ?> params) {
		return currentActivation;
	} 

}
