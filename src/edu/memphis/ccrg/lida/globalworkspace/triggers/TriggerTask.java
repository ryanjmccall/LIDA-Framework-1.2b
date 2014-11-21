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

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.tasks.FrameworkTaskImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.globalworkspace.GlobalWorkspace;

/**
 * TriggerTask is executed when certain number of ticks has passed. In this case the
 * {@link GlobalWorkspace} is told to trigger the broadcast
 * 
 * @author Javier Snaider
 */
public class TriggerTask extends FrameworkTaskImpl {

	private Logger logger = Logger.getLogger(TriggerTask.class
			.getCanonicalName());

	private String name;

	private TriggerListener gw;
	
	private BroadcastTrigger trigger;

	/**
	 * Constructors a new TriggerTask with specified attributes
	 * @param tpr ticksPerRun of this task
	 * @param gw {@link TriggerListener} which is the {@link GlobalWorkspace}
	 * @param name Name of the trigger
	 * @param trigger {@link BroadcastTrigger} that created this task
	 */
	public TriggerTask(int tpr, TriggerListener gw, String name, BroadcastTrigger trigger) {
		super(tpr);
		this.gw = gw;
		this.name = name;
		this.trigger=trigger;
	}

	@Override
	protected void runThisFrameworkTask() {
		logger.log(Level.FINE, name, TaskManager.getCurrentTick());
		gw.triggerBroadcast(trigger);
		cancel();
	}

	//TODO remove name
	@Override
	public String toString() {
		return name + "-" + super.toString();
	}

}