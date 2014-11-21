/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.initialization;


import edu.memphis.ccrg.lida.framework.FrameworkModule;

/**
 * An {@link Initializable} object e.g. an {@link FrameworkModule} that is initialized by the AgentXmlFactory.
 * 
 * @author Ryan J. McCall
 * @author Javier Snaider
 *
 * @see AgentXmlFactory
 * @see FrameworkModule
 */
public interface FullyInitializable extends Initializable {
	
	/**
	 * Sets an associated FrameworkModule.
	 * @param m the module to be associated.
     * @param usg how module will be used 
     * @see ModuleUsage
	 */
	public void setAssociatedModule(FrameworkModule m, String usg);
	
}
