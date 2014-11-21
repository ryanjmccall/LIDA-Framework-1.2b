/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/

package edu.memphis.ccrg.lida.attentioncodelets;

import edu.memphis.ccrg.lida.framework.shared.RefractoryPeriod;
import edu.memphis.ccrg.lida.framework.tasks.Codelet;
import edu.memphis.ccrg.lida.globalworkspace.Coalition;
import edu.memphis.ccrg.lida.globalworkspace.GlobalWorkspace;
import edu.memphis.ccrg.lida.workspace.workspacebuffers.WorkspaceBuffer;

/**
 * A kind of {@link Codelet} that checks {@link WorkspaceBuffer}
 * for its desired content and possibly adds {@link Coalition} to the
 * {@link GlobalWorkspace}
 * @author Ryan J. McCall
 * 
 */
public interface AttentionCodelet extends Codelet, RefractoryPeriod {


}
