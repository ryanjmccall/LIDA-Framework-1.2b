/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.shared;

import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * Implementors of this interface can have a refractory period.
 * The unit of the time period is in ticks, the unit of time in the framework.
 * 
 * @see TaskManager
 * @author Ryan J. McCall
 */
public interface RefractoryPeriod {
	
	/**
	 * Sets refractoryPeriod
	 * @param ticks length of refractory period in ticks
	 * @see TaskManager
	 */
	public void setRefractoryPeriod(int ticks);
	
	/**
	 * Gets refractoryPeriod
	 * @return length of refractory period in ticks
	 * @see TaskManager
	 */
	public int getRefractoryPeriod();

}
