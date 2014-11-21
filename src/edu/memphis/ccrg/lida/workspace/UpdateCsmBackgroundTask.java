/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.workspace;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.ModuleName;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTaskImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.workspace.workspacebuffers.WorkspaceBuffer;

/**
 * A background task in the {@link Workspace} which transfers percepts from the
 * Perceptual buffer to the Current Situational Model
 * 
 * @author Ryan J. McCall
 * 
 */
public class UpdateCsmBackgroundTask extends FrameworkTaskImpl {

	private static final Logger logger = Logger
			.getLogger(UpdateCsmBackgroundTask.class.getCanonicalName());

	private WorkspaceBuffer perceptualBuffer;
	private WorkspaceBuffer csm;

	@Override
	public void setAssociatedModule(FrameworkModule module, String moduleUsage) {
		if (module instanceof Workspace) {
			perceptualBuffer = (WorkspaceBuffer) module.getSubmodule(ModuleName.PerceptualBuffer);
			csm = (WorkspaceBuffer) module.getSubmodule(ModuleName.CurrentSituationalModel);
		}
	}

	/**
	 * Retrieves nodes from PAM and provides them to attentional codelets. This
	 * function gets PAM's nodes and provides them to CurrentSituationalModel,
	 * which will be accessed by attentional codelets.
	 */
	@Override
	protected void runThisFrameworkTask() {
		if (logger.isLoggable(Level.FINEST)) {
			logger.log(Level.FINEST, "Updating CSM with perceptual buffer content.",
					TaskManager.getCurrentTick());
		}
		csm.addBufferContent(perceptualBuffer.getBufferContent(null));
	}
}
