/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.sensorymemory;

import edu.memphis.ccrg.lida.framework.ModuleListener;

/**
 * This interface should be implemented for receiving and using information coming from a {@link SensoryMemory} module.
 * @author Ryan J. McCall
 *
 */
public interface SensoryMemoryListener extends ModuleListener{

	/**
	 * This method is used to receive information from sensory memory.
	 * Sensory-Motor Memory calls this method and receives the information of sensory memory. 
	 * @param content an Object containing {@link SensoryMemory} content
	 */
	public void receiveSensoryMemoryContent(Object content);
	
}
