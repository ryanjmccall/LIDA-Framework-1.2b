/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.workspace.structurebuildingcodelets;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.shared.Linkable;
import edu.memphis.ccrg.lida.framework.shared.NodeStructure;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.workspace.WorkspaceContent;
import edu.memphis.ccrg.lida.workspace.workspacebuffers.WorkspaceBuffer;

/**
 * Default implementation of {@link StructureBuildingCodelet}.  Checks for sought content
 * in all accessible {@link WorkspaceBuffer}s and adds all buffer content to the Current Situational Model. 
 * @author Ryan J. McCall
 *
 */
public class BasicStructureBuildingCodelet extends StructureBuildingCodeletImpl {
	
	private static Logger logger = Logger.getLogger(BasicStructureBuildingCodelet.class.getCanonicalName());
	
	/**
	 * Default constructor
	 */
	public BasicStructureBuildingCodelet(){
	}
	
	@Override
	protected void runThisFrameworkTask(){	
		logger.log(Level.FINEST, "SB codelet {1} being run.", 
				new Object[]{TaskManager.getCurrentTick(),this});
		for(WorkspaceBuffer readableBuffer: readableBuffers.values()){
			if(bufferContainsSoughtContent(readableBuffer)){
				writableBuffer.addBufferContent((WorkspaceContent) retrieveWorkspaceContent(readableBuffer));
			}
		}
		logger.log(Level.FINEST, "SB codelet {1} finishes one run.",
				new Object[]{TaskManager.getCurrentTick(),this});
	}

	@Override
	public NodeStructure retrieveWorkspaceContent(WorkspaceBuffer buffer) {
		return buffer.getBufferContent(null);
	}

	@Override
	public boolean bufferContainsSoughtContent(WorkspaceBuffer buffer) {
		NodeStructure ns = (NodeStructure) buffer.getBufferContent(null);
		for(Linkable ln: soughtContent.getLinkables()){
			if(!ns.containsLinkable(ln)){
				return false;
			}
		}
		logger.log(Level.FINEST, "SBcodelet {1} found sought content",
				new Object[]{TaskManager.getCurrentTick(),this});
		return true;
	}

}
