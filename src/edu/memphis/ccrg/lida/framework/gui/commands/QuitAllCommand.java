/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.gui.commands;

import edu.memphis.ccrg.lida.framework.Agent;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * Stops all thread execution and quits the system.
 * 
 * @author Javier Snaider
 *
 */
public class QuitAllCommand extends CommandImpl {

	@Override
	public void execute(Agent agent) {
		TaskManager tm = agent.getTaskManager();
		tm.pauseTasks();
		try {
			Thread.sleep(100);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		tm.stopRunning();
	}

}
