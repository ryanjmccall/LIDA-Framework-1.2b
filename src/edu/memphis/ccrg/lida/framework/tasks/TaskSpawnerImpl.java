/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.tasks;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.initialization.AgentXmlFactory;
import edu.memphis.ccrg.lida.framework.initialization.InitializableImpl;
import edu.memphis.ccrg.lida.framework.shared.ConcurrentHashSet;

/**
 * Maintains a queue of running tasks and their task status. Methods to add and
 * cancel tasks. This implementation actually uses {@link TaskManager} to
 * execute the tasks.
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 */
public class TaskSpawnerImpl extends InitializableImpl implements TaskSpawner {

	private static final Logger logger = Logger.getLogger(TaskSpawnerImpl.class
			.getCanonicalName());
	/*
	 * The tasks currently controlled by this TaskSpawner.
	 */
	private Set<FrameworkTask> controlledTasks = new ConcurrentHashSet<FrameworkTask>();

	private TaskManager taskManager;

	/**
	 * This default constructor is used by the {@link AgentXmlFactory}.
	 * {@link TaskManager} must be set using {@link TaskSpawner#setTaskManager(TaskManager)}
	 */
	public TaskSpawnerImpl() {
	}

	/**
	 * Convenience constructor that sets the {@link TaskManager}
	 * @param tm the {@link TaskManager} to set
	 */
	public TaskSpawnerImpl(TaskManager tm) {
		taskManager = tm;
	}

	@Override
	public void setTaskManager(TaskManager tm) {
		taskManager = tm;
	}

	@Override
	public void addTasks(Collection<? extends FrameworkTask> tasks) {
		if(tasks != null){
			for (FrameworkTask r : tasks) {
				addTask(r);
			}
		}
	}

	@Override
	public void addTask(FrameworkTask task) {
		if(task == null){
			logger.log(Level.WARNING, "Cannot add a null task",TaskManager.getCurrentTick());
		}else if(task.getTaskStatus() == TaskStatus.CANCELED){
			logger.log(Level.WARNING, "Cannot add task {1} because its TaskStatus is CANCELED.", new Object[] {
					TaskManager.getCurrentTick(), task });
		}else{
			task.setControllingTaskSpawner(this);
			controlledTasks.add(task);
			runTask(task);
			logger.log(Level.FINEST, "Task {1} added", new Object[] {
					TaskManager.getCurrentTick(), task });
		}
	}

	/*
	 * Schedule the FrameworkTask to be executed. Sets task status to RUNNING.
	 */
	private void runTask(FrameworkTask task) {
		logger.log(Level.FINEST, "Running task {1}", new Object[] {
				TaskManager.getCurrentTick(), task });
		task.setTaskStatus(TaskStatus.RUNNING);
		taskManager.scheduleTask(task, task.getNextTicksPerRun());
	}

	@Override
	public void receiveFinishedTask(FrameworkTask task) {
		switch (task.getTaskStatus()) {
			case FINISHED_WITH_RESULTS:
				processResults(task);
				removeTask(task);
				logger.log(Level.FINEST, "FINISHED_WITH_RESULTS {1}", new Object[] {
						TaskManager.getCurrentTick(), task });
				break;
			case FINISHED:
				removeTask(task);
				logger.log(Level.FINEST, "FINISHED {1}", new Object[] {
						TaskManager.getCurrentTick(), task });
				break;
			case CANCELED:
				removeTask(task);
				logger.log(Level.FINEST, "CANCELLED {1}", new Object[] {
						TaskManager.getCurrentTick(), task });
				break;
			case RUNNING:
				logger.log(Level.FINEST, "RUNNING",
						new Object[] { TaskManager.getCurrentTick(), task });
				runTask(task);
				break;
		}
	}

	/*
	 * When a finished task is received and its status is FINISHED_WITH_RESULTS
	 * or FINISHED or CANCELLED This method is called to remove the task from
	 * this TaskSpawner.
	 */
	private void removeTask(FrameworkTask task) {
		logger.log(Level.FINEST, "Removing task {1}", new Object[] {
				TaskManager.getCurrentTick(), task });
		controlledTasks.remove(task);
	}

	/**
	 * When a {@link FrameworkTask} has completed one execution and its status is {@link TaskStatus#FINISHED_WITH_RESULTS}
	 * this method is called to handle the results.
	 * 
	 * @param task the task to be processed
	 */
	protected void processResults(FrameworkTask task) {
	}

	@Override
	public boolean cancelTask(FrameworkTask task) {
		if(containsTask(task)){
			removeTask(task);
			return taskManager.cancelTask(task);
		}
		return false;
	}

	@Override
	public boolean containsTask(FrameworkTask task) {
		return (task == null)? false:controlledTasks.contains(task);
	}

	@Deprecated
	@Override
	public Collection<FrameworkTask> getRunningTasks() {
		return getTasks();
	}
	
	@Override
	public Collection<FrameworkTask> getTasks() {
		return Collections.unmodifiableCollection(controlledTasks);
	}
}