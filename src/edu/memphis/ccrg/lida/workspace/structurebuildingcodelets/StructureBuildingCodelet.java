/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.workspace.structurebuildingcodelets;

import edu.memphis.ccrg.lida.framework.tasks.Codelet;

/**
 * Demon-like process operating on the workspace searching for particular content
 * which, when found, triggers its action producing its result.  
 * Has workspace buffers it can access.
 * 
 * @author Ryan J. McCall
 *
 */
public interface StructureBuildingCodelet extends Codelet{
	 
	 /** 
	  * Returns result of codelet's run
	 * @return Current information about the codelet's progress
	 */
	public Object getCodeletRunResult();
	
	/**
	 * Clears this codelet's fields in preparation for reuse. 
	 * Idea is that the same codelet object is reconfigured at runtime
	 * after it finishes to be run as a different altogether codelet. 
	 */
	public void reset();

}