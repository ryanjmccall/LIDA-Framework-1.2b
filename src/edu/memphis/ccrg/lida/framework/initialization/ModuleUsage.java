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
 * Specifies different ways a {@link FullyInitializable} will use
 * an associated module.
 * 
 * @see FullyInitializable#setAssociatedModule(FrameworkModule, String)
 * @author Javier Snaider
 *
 */
@SuppressWarnings(value = { "all" })
public class ModuleUsage {
	//TODO dynamic enum like ModuleName

	public static final String NOT_SPECIFIED = "NOT_SPECIFIED";
	public static final String TO_READ_FROM = "TO_READ_FROM";
	public static final String TO_WRITE_TO = "TO_WRITE_TO";
	public static final String TO_DELETE_FROM = "TO_DELETE_FROM";
	public static final String TO_CHECK_FROM = "TO_CHECK_FROM";
	public static final String TO_LISTEN_FROM = "TO_LISTEN_FROM";	
	
}
