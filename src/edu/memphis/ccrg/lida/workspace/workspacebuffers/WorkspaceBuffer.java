/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.workspace.workspacebuffers;

import java.util.Map;

import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.workspace.WorkspaceContent;
import edu.memphis.ccrg.lida.workspace.WorkspaceImpl;
import edu.memphis.ccrg.lida.workspace.structurebuildingcodelets.StructureBuildingCodelet;

/**
 * A submodule of the Workspace.  Managed by {@link WorkspaceImpl}.  
 * {@link StructureBuildingCodelet} read and write from them.
 * 
 * @author Ryan J. McCall
 * @author Javier Snaider
 */
public interface WorkspaceBuffer extends FrameworkModule {
	
	/**
	 * Gets buffer content based on specified parameters.
	 * @param params optional parameters to specify what content is returned
	 * @return {@link WorkspaceContent}
	 */
	public WorkspaceContent getBufferContent(Map<String, Object> params);
	
	/**
	 * Adds specified content to this workspace buffer.
	 * @param content {@link WorkspaceContent} to add
	 */
	public void addBufferContent(WorkspaceContent content);
	
}
