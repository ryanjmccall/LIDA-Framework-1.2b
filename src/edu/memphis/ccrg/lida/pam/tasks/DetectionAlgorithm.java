/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.pam.tasks;

import edu.memphis.ccrg.lida.framework.tasks.FrameworkTask;
import edu.memphis.ccrg.lida.pam.PamLinkable;
import edu.memphis.ccrg.lida.pam.PamNode;
import edu.memphis.ccrg.lida.sensorymemory.SensoryMemory;

/**
 * A process which detects a pattern (feature) in {@link SensoryMemory} content and excites {@link PamNode}s 
 * representing that pattern.
 * @author Javier Snaider
 * @author Ryan J. McCall
 * @see BasicDetectionAlgorithm
 */
public interface DetectionAlgorithm extends FrameworkTask {
	
	/**
	 * Detects a feature.
	 * @return value from 0.0 to 1.0 representing the degree to which the feature occurs.
	 */
	public double detect(); 
	
	/**
	 * Returns {@link PamLinkable} this algorithm can detect.
	 * 
	 * @return the pam nodes
	 */
	public PamLinkable getPamLinkable();
	
	/**
	 * Adds {@link PamLinkable} that will be detected by this algorithm.
	 * @param linkable s {@link PamLinkable}
	 */
	public void setPamLinkable(PamLinkable linkable);
	
}
