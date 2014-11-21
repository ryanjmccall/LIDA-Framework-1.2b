/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.sensorymotormemory;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.actionselection.Action;
import edu.memphis.ccrg.lida.framework.Agent;
import edu.memphis.ccrg.lida.framework.initialization.FullyInitializable;
import edu.memphis.ccrg.lida.framework.initialization.GlobalInitializer;
import edu.memphis.ccrg.lida.framework.initialization.Initializer;

/**
 * Basic SensoryMotorMemory {@link Initializer} which reads 
 * String parameters beginning with 'smm.' and creates a action-algorithm mapping based
 * on the parameter.  The definition is: <br/>
 * <b>actionName,algorithm</b>
 * @author Ryan J. McCall
 * @author Javier Snaider
 */
public class BasicSensoryMotorMemoryInitializer implements Initializer {

	private static final Logger logger = Logger.getLogger(BasicSensoryMotorMemoryInitializer.class.getCanonicalName());
	private static final GlobalInitializer initializer = GlobalInitializer.getInstance();
	private BasicSensoryMotorMemory smm; 
	
    @Override
    public void initModule(FullyInitializable module, Agent agent, Map<String, ?> params) {
        smm = (BasicSensoryMotorMemory) module;
        
        for(String key: params.keySet()){
	    	if(key.startsWith("smm.mapping.")){
	    		Object value = params.get(key);
	    		String smmDef = "";
	    		if(value instanceof String){
	    			smmDef = (String) params.get(key);
	    		}else{
	    			logger.log(Level.WARNING, "Parameter name: " + key + " started with smm.mapping. but did not contain a valid def");
	    			continue;
	    		}
	    		logger.log(Level.INFO, "loading smm action-algorithm mapping: {0}", smmDef);
	    		String[] elements = smmDef.split(",");
	    		if(elements.length == 2){
	    			String actionName = elements[0].trim();
	    			//TODO Typed Parameters
	    			String algorithmName = elements[1].trim();
	    			if("".equals(algorithmName)){
	    				logger.log(Level.WARNING, "missing algorithm name for smm: {0}",smmDef);
	    				continue;
	    			}
	    			Action action = (Action) initializer.getAttribute(actionName);
	    	        if(action != null){
	    	        	smm.addActionAlgorithm(action.getId(), algorithmName);
	    	        }else{
	    	        	logger.log(Level.WARNING, "could not find agent action: {0}",actionName);
	    	        }
	    		}else{
	    			logger.log(Level.WARNING, 
	    					"incorrect smm def: {0} must have form 'actionName,algorithm'",smmDef);
	    		}
	    		
	    	}
        }
    }
}