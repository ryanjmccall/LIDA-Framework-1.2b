/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.strategies;

/**
 * A strategy that calculates total activation.
 * @author Ryan J. McCall
 */
public interface TotalActivationStrategy extends Strategy{

	/**
	 * Calculates and returns total activation.
	 * @param bla Base-level activation
	 * @param ca current activation
	 * @return calculated total activation
	 */
	public double calculateTotalActivation(double bla, double ca);
	
}
