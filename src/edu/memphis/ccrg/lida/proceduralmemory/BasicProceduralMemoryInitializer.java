/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.proceduralmemory;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.actionselection.Action;
import edu.memphis.ccrg.lida.actionselection.ActionImpl;
import edu.memphis.ccrg.lida.framework.Agent;
import edu.memphis.ccrg.lida.framework.initialization.FullyInitializable;
import edu.memphis.ccrg.lida.framework.initialization.GlobalInitializer;
import edu.memphis.ccrg.lida.framework.initialization.Initializer;
import edu.memphis.ccrg.lida.framework.shared.Node;
import edu.memphis.ccrg.lida.framework.shared.NodeStructure;
import edu.memphis.ccrg.lida.framework.shared.NodeStructureImpl;
import edu.memphis.ccrg.lida.framework.shared.activation.Learnable;
import edu.memphis.ccrg.lida.pam.PerceptualAssociativeMemoryImpl;
import edu.memphis.ccrg.lida.proceduralmemory.ProceduralMemoryImpl.ConditionType;

/**
 * Basic {@link ProceduralMemory} {@link Initializer} which reads 
 * String parameters beginning with 'scheme.' and creates a scheme based
 * on the parameter.  The definition is: <br/>
 * <b>schemeLabel|(contextNode1, contextNode2,...)(contextLink1, contextLink2,...)|actionName|(resultNode1, resultNode2,...)(resultLink1, resultLink2,...)|baseLevelActivation</b>
 * @author Ryan J. McCall
 * @author Javier Snaider
 *
 */
public class BasicProceduralMemoryInitializer implements Initializer {

	private static final Logger logger = Logger.getLogger(BasicProceduralMemoryInitializer.class.getCanonicalName());
	
	@Override
	public void initModule(FullyInitializable module, Agent agent,
			Map<String, ?> params) {
		ProceduralMemory pm = (ProceduralMemory)module;
	    GlobalInitializer initializer = GlobalInitializer.getInstance();
	    	    
	    for(String key: params.keySet()){
	    	if(key.startsWith("scheme")){
	    		String schemeSpec = (String) params.get(key);
	    		String[] elements = schemeSpec.split("\\|");    
	    		logger.log(Level.INFO,"Loading scheme: {0}",schemeSpec);
	    		if(elements.length == 5){
	    			String label = elements[0].trim();
	    			
	    			String context = elements[1].trim();
	    			NodeStructure contextNS = loadNodeStructure(initializer, context);
	    				
	    			String actionName = elements[2].trim();
	    			Action action = (Action) initializer.getAttribute(actionName);
	    			if(action == null){
	    				action = new ActionImpl();
	    				action.setLabel(actionName);
	    				initializer.setAttribute(actionName, action);
	    			}
	    			
	    			String result = elements[3].trim();
	    			NodeStructure resultNS = loadNodeStructure(initializer, result);
	    			
	    			String blActivation = elements[4].trim();
	    			double bla = Learnable.DEFAULT_BASE_LEVEL_ACTIVATION;
	    			try{
	    				bla = Double.parseDouble(blActivation);
	    			}catch(NumberFormatException e){
	    				logger.log(Level.WARNING,"could not parse base-level activation: {0}",blActivation);
	    			}
	    			
	    			Scheme s = pm.getNewScheme(action);
	    			s.setLabel(label);
	    			s.setBaseLevelActivation(bla);
	    			for(Node n : contextNS.getNodes()){
	    				s.addCondition(n,ConditionType.CONTEXT);
	    			}
	    			for(Node n : resultNS.getNodes()){
	    				s.addCondition(n, ConditionType.ADDINGLIST);
	    			}	    	        
	    		}else{
	    			logger.log(Level.WARNING, 
	    					"scheme specification must have 5 parts separated by | " +
	    					"e.g. schemeLabel|(contextNode1, contextNode2)(contextLink1, contextLink2)" +
	    					"|actionName|(resultNode1, resultNode2)(resultLink1, resultLink2)|baseLevelActivation");
	    		}
	    	}
	    }
	}

	/*
	 * 
	 * @param initializer
	 * @param nsSpecification
	 * @return NodeStructure
	 */
	private static NodeStructure loadNodeStructure(GlobalInitializer initializer, String nsSpecification) {		
		String[] contextNodes = nsSpecification.substring(nsSpecification.indexOf('(') + 1, nsSpecification.indexOf(')')).trim().split(",");
		int startLinks = nsSpecification.indexOf(')')+ 1;
		String contexLinksSpec = nsSpecification.substring(startLinks);
		String[] contextLinks = contexLinksSpec.substring(contexLinksSpec.indexOf('(') + 1, contexLinksSpec.indexOf(')')).trim().split(",");
		NodeStructure loadedNodeStructure = new NodeStructureImpl();
		for(String nodeName: contextNodes){
			if("".equals(nodeName)){
				continue;
			}
			nodeName = nodeName.trim();
			Node n = (Node) initializer.getAttribute(nodeName);
			if(n != null){
				loadedNodeStructure.addDefaultNode(n);
			}else{
				logger.log(Level.WARNING, 
    					"could not find node {0} in global initializer",nodeName);
			}
		}
		for(String linkName: contextLinks){
			linkName = linkName.trim();
			if("".equals(linkName)){
				continue;
			}
			String[] nodes = linkName.split(":");
			if(nodes.length == 2){
				Node source = (Node) initializer.getAttribute(nodes[0].trim());
				Node sink = (Node) initializer.getAttribute(nodes[1].trim());
				//TODO third parameter for category
				if(source != null && sink != null){
					loadedNodeStructure.addDefaultLink(source, sink, PerceptualAssociativeMemoryImpl.PARENT, 1.0, -1.0);
				}else{
					logger.log(Level.WARNING, 
	    					"could not find source or sink of link {0} in global initializer",linkName);
				}
			}else{
				logger.log(Level.WARNING, 
    					"bad link specification {0} ",linkName);
			}
		}
		return loadedNodeStructure;
	}

}
