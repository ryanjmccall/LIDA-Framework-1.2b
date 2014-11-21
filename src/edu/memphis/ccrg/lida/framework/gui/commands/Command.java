/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.memphis.ccrg.lida.framework.gui.commands;

import java.util.Map;

import edu.memphis.ccrg.lida.framework.Agent;

/**
 * A command is an encapsulation of an event from the Gui such as a button press or a 
 * slider state change.  
 * 
 * Implementations should add themselves to guiCommands.properties.
 * @author Javier Snaider
 */
public interface Command {

    /**
	 * Executes this command performing the necessary actions in the model (the {@link Agent}).
	 * 
	 * @param agent
	 *            {@link Agent} Object
	 */
    public void execute(Agent agent);
    
    /**
	 * Returns result of the command.
	 * 
	 * @return the result of the command's execution
	 */
    public Object getResult();    

    /**
	 * Sets optional parameters for command.
	 * 
	 * @param parameters
	 *            parameters
	 */
    public void setParameters(Map<String, Object> parameters);
    
    /**
	 * Set single parameter for command.
	 * 
	 * @param name
	 *            name of parameter
	 * @param value
	 *            default value to use if parameter cannot be found
	 */
    public void setParameter(String name, Object value);
    
    /**
	 * Gets a parameter by name.
	 * 
	 * @param name
	 *            retrieved parameter
	 * @return the parameter
	 */
    public Object getParameter(String name);
    
    
}
