/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.gui;

import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.Agent;
import edu.memphis.ccrg.lida.framework.gui.commands.Command;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * Default Implementation of {@link FrameworkGuiController}
 * 
 * @author Javier Snaider
 */
public class FrameworkGuiControllerImpl implements FrameworkGuiController {

	private static final Logger logger = Logger.getLogger(FrameworkGuiControllerImpl.class.getCanonicalName());
	
	private Agent agent;
	private Properties commands;

	/**
	 * 
	 * 
	 * @param agent
	 *            {@link Agent} object
	 * @param commands
	 *            the commands
	 */
	public FrameworkGuiControllerImpl(Agent agent, Properties commands) {
		super();
		this.agent = agent;
 		this.commands = commands;
	}
	
	@Override
	public Object executeCommand (String commandName, Map<String,Object> parameters){
		if(commandName == null){
			logger.log(Level.WARNING,"Received null command name");
			return null;
		}
		String commandClass = commands.getProperty(commandName);
		Command command=null;
		if(commandClass != null){
			try {
				command=(Command)(Class.forName(commandClass)).newInstance();
			} catch (Exception e) {
				logger.log(Level.WARNING,e.getMessage(),e);
			}
		}
		if (command == null){
			return null;
		}
		if(parameters != null){
			command.setParameters(parameters);
		}
		return executeCommand(command);
	}

	@Override
	public Object executeCommand (Command command){
		if(command != null){
			command.execute(agent);
			logger.log(Level.FINE, "Command {1} executed",
					new Object[]{TaskManager.getCurrentTick(),command});
			return command.getResult();
		}
		logger.log(Level.WARNING, "Cannot execute null command", TaskManager.getCurrentTick());
		return null;
	}

	@Override
	public void registerAgent(Agent agent) {
		this.agent = agent;
	}

}
