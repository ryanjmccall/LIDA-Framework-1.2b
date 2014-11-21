/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.sensorymemory;

import java.util.Map;

import edu.memphis.ccrg.lida.framework.FrameworkModule;

/**
 * This is the interface to be implemented by sensory memory modules.
 * Implementing modules sense the environment, store the sensed data ,and
 * process it.
 * 
 * @author Ryan J. McCall
 */
public interface SensoryMemory extends FrameworkModule{

	/**
	 * Adds a listener to this memory. This listener constantly checks for
	 * information being sent from this memory to other modules (Perceptual
	 * Associative Memory and Sensory Motor Memory).
	 * 
	 * @param l
	 *            the listener added to this memory
	 */
	public void addSensoryMemoryListener(SensoryMemoryListener l);

	/**
	 * Runs all the sensors associated with this memory. The sensors get the
	 * information from the environment and store in this memory for later
	 * processing and passing to the perceptual memory module.
	 */
	public void runSensors();
	
	/**
	 * Returns content from this SensoryMemory.
	 * Intended to be used by feature detectors to get specific parts of the sensory memory.
	 * @param modality user may optionally use this parameter to specify modality.
	 * @param params optional parameters
	 * @return content
	 */
	public Object getSensoryContent(String modality, Map<String, Object> params);

}
