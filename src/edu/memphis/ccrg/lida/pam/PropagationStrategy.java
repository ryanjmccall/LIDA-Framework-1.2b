/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.pam;

import java.util.Map;

import edu.memphis.ccrg.lida.framework.strategies.Strategy;

/**
 * A {@link Strategy} that calculates an activation to be propagated.
 * 
 * @author Ryan J. McCall
 */
public interface PropagationStrategy extends Strategy{
	
	/**
	 * Various parameters can be passed to this method for the
	 * calculation of activation to propagate.
	 *
	 * @param params Map of parameters
	 * @return the calculated activation to propagate
	 */
	public double getActivationToPropagate(Map<String, Object> params);
}
