/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.attentioncodelets;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.initialization.GlobalInitializer;
import edu.memphis.ccrg.lida.framework.shared.Linkable;
import edu.memphis.ccrg.lida.framework.shared.Node;
import edu.memphis.ccrg.lida.framework.shared.NodeStructure;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.globalworkspace.Coalition;
import edu.memphis.ccrg.lida.workspace.workspacebuffers.WorkspaceBuffer;

/**
 * An {@link AttentionCodelet} that seeks to create {@link Coalition}s from its sought content.
 * The resulting {@link Coalition} includes these nodes and possibly neighbors nodes.
 * 
 * @author Ryan J. McCall
 */
public class NeighborhoodAttentionCodelet extends DefaultAttentionCodelet {

    private static final Logger logger = Logger.getLogger(NeighborhoodAttentionCodelet.class.getCanonicalName());

    /**
     * If this method is overridden, this init() must be called first! i.e. super.init();
	 * Will set parameters with the following names:<br/><br/>
     * 
     * <b>nodes</b> Labels of nodes that comprise this codelet's sought content<br/><br/>
     * If any parameter is not specified its default value will be used.
     * 
     * @see DefaultAttentionCodelet#init()
	 */
    @Override
	public void init() {
		super.init();
		super.attentionThreshold = 0.0;//want nodes regardless of their activation
		String nodeLabels = getParam("nodes", "");
		if (nodeLabels != null && !nodeLabels.equals("")) {
            GlobalInitializer globalInitializer = GlobalInitializer.getInstance();
            String[] labels = nodeLabels.split(",");
            for (String label : labels) {
                label = label.trim();
                Node node = (Node) globalInitializer.getAttribute(label);
                if (node != null) {
                    soughtContent.addDefaultNode(node);
                }else{
                	logger.log(Level.WARNING, "could not find node with label: {0} in global initializer", label);
                }
            }
        }
    }
    
    /**
     * Returns true if specified WorkspaceBuffer contains this codelet's sought
     * content.
     * 
     * @param buffer
     *            the WorkspaceBuffer to be checked for content
     * @return true, if successful
     */
    @Override
    public boolean bufferContainsSoughtContent(WorkspaceBuffer buffer) {
        NodeStructure model = (NodeStructure) buffer.getBufferContent(null);

        for (Linkable ln : soughtContent.getLinkables()) {
            if (!model.containsLinkable(ln)) {
                return false;
            }
        }

        logger.log(Level.FINEST, "Attn codelet {1} found sought content",
                new Object[]{TaskManager.getCurrentTick(), this});
        return true;
    }
   
}