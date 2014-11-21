/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.tasks;

import java.util.Collection;

import org.apache.commons.collections15.collection.UnmodifiableCollection;

import edu.memphis.ccrg.lida.framework.initialization.Initializable;

/**
 * TaskSpawners manage {@link FrameworkTask} objects. Maintains a {@link Collection} of all added tasks.
 * Provides method to process the result of a {@link FrameworkTask}.
 * 
 * @author Ryan J. McCall
 */
public interface TaskSpawner extends Initializable {
	
	/**
	 * Set the TaskManager this TaskSpawner will use to actually run the
	 * tasks.
	 * 
	 * @param tm the {@link TaskManager} of the system.
	 */
	public void setTaskManager(TaskManager tm);
	
	/**
	 * Adds and runs supplied FrameworkTask.
	 * @param task the task to add.
	 */
	public void addTask(FrameworkTask task);

	/**
	 * Adds and runs supplied FrameworkTasks.
	 * 
	 * @param tasks
	 *            a collection of tasks to be run.
	 */
	public void addTasks(Collection<? extends FrameworkTask> tasks);
		
	/**
	 * This method receives a task  that has finished. TaskSpawners can choose what to do 
	 * with the FrameworkTask each time it finishes running. Generally the FrameworkTask's {@link TaskStatus}
	 * determines this action.
	 * 
	 * @param task finished {@link FrameworkTask}
	 */
	public void receiveFinishedTask(FrameworkTask task);
	
	/**
	 * Cancels specified task if it exists in this {@link TaskSpawner}
	 * Task is removed from {@link TaskSpawner} and canceled in the {@link TaskManager}. 
	 * This is only possible if the tick for which the task 
	 * is scheduled has not been reached.
	 * 
	 * @param task The task to cancel.
	 * @see TaskManager#cancelTask(FrameworkTask)
	 * @return true if the task was canceled, false otherwise
	 */
	public boolean cancelTask(FrameworkTask task);

	/**
	 * Returns whether this TaskSpawner manages this task.
	 * @param t a FrameworkTask
	 * @return true if this {@link TaskSpawner} contains a task with task's id
	 */
	public boolean containsTask(FrameworkTask t);
	
	/**
	 * Returns a {@link UnmodifiableCollection} that contains the FrameworkTasks in this
	 * TaskSpawner. Tasks' TaskStatus may or may not be running.
	 * Use {@link #getTasks()} instead.
	 * 
	 * @deprecated The returned tasks may not have {@link TaskStatus#RUNNING}. Replaced by {@link #getTasks()}.
	 * @return collection of running tasks.
	 */
	@Deprecated
	public Collection<FrameworkTask> getRunningTasks();
	
	/**
	 * Returns the {@link FrameworkTask} objects controlled by this TaskSpawner.
	 * 
	 * @return a {@link Collection} FrameworkTasks.
	 */
	public Collection<FrameworkTask> getTasks();
	
}
