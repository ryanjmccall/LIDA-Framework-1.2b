/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
/**
 * 
 */
package edu.memphis.ccrg.lida.globalworkspace.triggers;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.globalworkspace.Coalition;
import edu.memphis.ccrg.lida.globalworkspace.GlobalWorkspace;


/**
 * A triggers that fires when a certain number of ticks have passed without having
 * a broadcast occur.
 * 
 * @author Javier Snaider
 */
public class NoBroadcastOccurringTrigger implements BroadcastTrigger {

	private static final Logger logger = Logger.getLogger(NoBroadcastOccurringTrigger.class.getCanonicalName());
	private static final String DEFAULT_NAME = "NoBroadcastOccurringTrigger";
	private static final int DEFAULT_DELAY = 10;
	/*
	 * How long since last broadcast before this trigger is activated
	 */
	private int delay;
	private TriggerTask task;
	private GlobalWorkspace gw;
	private String name="";
	private TaskManager tm;

	/**
	 * Gets the Task Manager
	 * @return the {@link TaskManager}
	 */
	public TaskManager getTaskManager() {
		return tm;
	}
	
	/**
	 * Gets delay
	 * @return number of ticks that must pass without a broadcast before this trigger fires
	 */
	public int getDelay(){
		return delay;
	}

	/**
	 * Sets the Task Manager
	 * @param taskManager the {@link TaskManager}
	 */
	public void setTaskManager(TaskManager taskManager) {
		this.tm = taskManager;
	}

	/**
	 * This method expects an Integer with name "delay" standing for trigger delay.
	 * Also a String, "name" of the trigger for logging purposes.
	 * 
	 * @see BroadcastTrigger#init(Map, GlobalWorkspace)
	 */
	@Override
	public void init(Map<String, Object> parameters, GlobalWorkspace gw) {
		this.gw=gw;
		Object o = parameters.get("delay");
		if ((o != null)&& (o instanceof Integer)) {
			delay= (Integer)o;
			if(delay <= 0){
				logger.log(Level.WARNING, "Invalid delay parameter, using default.", TaskManager.getCurrentTick());
				delay = DEFAULT_DELAY;
			}
		}else{
			delay = DEFAULT_DELAY;
			logger.log(Level.WARNING, "Failed to set delay parameter, using default.", TaskManager.getCurrentTick());
		}
		
		o = parameters.get("name");
		if ((o != null)&& (o instanceof String)) {
			name= (String)o;
		}else{
			name = DEFAULT_NAME;
			logger.log(Level.WARNING, "Failed to set name parameter, using default.", TaskManager.getCurrentTick());
		}	
	}

	@Override
	public void start() {
		task=new TriggerTask(delay,gw,name,this);
		gw.getAssistingTaskSpawner().addTask(task);
	}

	@Override
	public void checkForTriggerCondition(Collection<Coalition> coalitions) {
	}

	@Override
	public void reset() {
		if (task != null){
			gw.getAssistingTaskSpawner().cancelTask(task);
		}
		start();
	}

}