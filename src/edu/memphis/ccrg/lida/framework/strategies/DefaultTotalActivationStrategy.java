/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.strategies;


/**
 * Default method to calculate total activation.  Sums activations returning sum or 1.0, whichever is lowest.
 * @author Ryan J. McCall
 *
 */
public class DefaultTotalActivationStrategy extends StrategyImpl implements TotalActivationStrategy {

	@Override
	public double calculateTotalActivation(double baseLevelActivation,
			double currentActivation) {
		double sum = baseLevelActivation + currentActivation;
		return (sum > 1.0) ? 1.0 : sum;
	}

}
