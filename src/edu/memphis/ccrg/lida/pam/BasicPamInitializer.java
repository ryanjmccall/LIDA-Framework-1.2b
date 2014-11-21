/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.pam;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.Agent;
import edu.memphis.ccrg.lida.framework.initialization.FullyInitializable;
import edu.memphis.ccrg.lida.framework.initialization.GlobalInitializer;
import edu.memphis.ccrg.lida.framework.initialization.Initializer;
import edu.memphis.ccrg.lida.framework.shared.Node;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * An {@link Initializer} for {@link PerceptualAssociativeMemory} which receives a parameter named
 * 'nodes' containing a list of node labels.  Nodes are created in {@link PerceptualAssociativeMemory} and 
 * nodes are added to the {@link GlobalInitializer}.
 * A parameter name 'links' contains a list of link definitions.
 * 
 * The definition for 'nodes' is: <br/>
 * <b>nodeLabel1,nodeLabel2,...</b>
 * <br/>
 * The definition for 'links' is: <br/>
 * <b>sourceNodeLabel1:sinkNodeLabel2,...</b>
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 */
public class BasicPamInitializer implements Initializer {

    private static final Logger logger = Logger.getLogger(BasicPamInitializer.class.getCanonicalName());

    @Override
    public void initModule(FullyInitializable module, Agent agent,
            Map<String, ?> params) {
        PerceptualAssociativeMemory pam = (PerceptualAssociativeMemory) module;
//        ElementFactory factory = ElementFactory.getInstance();

        String nodes = (String) params.get("nodes");
        if (nodes != null) {
            GlobalInitializer globalInitializer = GlobalInitializer.getInstance();
            String[] defs = nodes.split(",");
            for (String nodeDef : defs) {
                nodeDef = nodeDef.trim();
                String[] nodeParams = nodeDef.split(":");
                String label = nodeParams[0];
                if("".equals(label)){
                	logger.log(Level.WARNING, 
        			"empty string found in nodes specification, node labels must be non-empty");
                }else{
                	logger.log(Level.INFO, "loading PamNode: {0}", label);
                	PamNode node = pam.addDefaultNode(label);
//	                PamNode node = (PamNode) factory.getNode("PamNodeImpl", label);
	                if(node == null){
	                	logger.log(Level.WARNING, "failed to get node '{0}' from pam", label);
	                }else{
//	                	node = pam.addDefaultNode(node);
	                	globalInitializer.setAttribute(label, node);
	                	 if (nodeParams.length > 1) {
	 	                	double blActivation = 0;
	                     	try{
	                     		blActivation = Double.parseDouble(nodeParams[2]);
	                     	}catch (NumberFormatException e) {
	                     		logger.log(Level.WARNING, "Bad base-level activation for link {1}.", 
	                     				new Object[]{TaskManager.getCurrentTick(),node});
	 						}
	                     	node.setBaseLevelActivation(blActivation);
	 	                }
	                }
	               
                }
            }
        }

        String links = (String) params.get("links");
        if (links != null) {
            String[] linkDefs = links.split(",");
            for (String linkDef : linkDefs) {
                linkDef = linkDef.trim();
                if("".equals(linkDef)){
                	logger.log(Level.WARNING, 
        			"empty string found in links specification, link defs must be non-empty");
                	continue;
                }
                logger.log(Level.INFO, "loading PamLink: {0}", linkDef);
                String[] linkParams = linkDef.split(":");
                if (linkParams.length < 2) {
                    logger.log(Level.WARNING, "bad link specification " + linkDef, TaskManager.getCurrentTick());
                    continue;
                }
                Node source = pam.getNode(linkParams[0].trim());
                Node sink = pam.getNode(linkParams[1].trim());
                if (source != null && sink != null) {
//                    Link link = factory.getLink("PamLinkImpl", source, sink, PerceptualAssociativeMemoryImpl.PARENT);
//                    pam.addDefaultLink(link);
                    PamLink pl = pam.addDefaultLink(source, sink, PerceptualAssociativeMemoryImpl.PARENT);
                    if(pl == null){
                    	logger.log(Level.WARNING, "bad link specification " + linkDef, TaskManager.getCurrentTick());
                    }else if(linkParams.length > 2){
                    	double blActivation = 0;
                    	try{
                    		blActivation = Double.parseDouble(linkParams[2]);
                    	}catch (NumberFormatException e) {
                    		logger.log(Level.WARNING, "Bad base-level activation for link {1}.", 
                    				new Object[]{TaskManager.getCurrentTick(),pl});
						}
                    	pl.setBaseLevelActivation(blActivation);
                    }
                } else {
                    logger.log(Level.WARNING, "could not find source or sink " + linkDef, TaskManager.getCurrentTick());
                }
                
                
            }
        }
    }
}
