/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.tasks;

import java.util.concurrent.Callable;

import edu.memphis.ccrg.lida.framework.initialization.FullyInitializable;
import edu.memphis.ccrg.lida.framework.shared.activation.Learnable;

/**
 * This is the base interface for all task process in the LIDA framework. 
 * All parts of processes in the LIDA Framework have to implement this interface.
 * A FrameworkTask is intended as a small fraction of a process. For example a {@link Codelet} or a Feature 
 * detector are examples of FrameworkTask. However, if the process includes a loop, one run of 
 * the FrameworkTask represents only one iteration of the loop.
 * 
 * A {@link TaskSpawner} can send FrameworkTasks to the {@link TaskManager} for execution. A {@link TaskSpawner} receives
 * the task each time it finishes running, so the TaskSpawner can decide if this particular task
 * must run again or not. This is based on the status of the FrameworkTask.
 * The FrameworkTask should set its status during it execution.  Implementations of this interface should 
 * call the task's TaskSpawner method, {@link TaskSpawner#receiveFinishedTask(FrameworkTask)}, to handle the finished task at the end of the 'call' method.
 * 
 * @see FrameworkTaskImpl Most tasks can extend from this instead of implementing this interface from scratch.
 * 
 * @author Ryan J. McCall
 * @author Javier Snaider
 *
 */ 
public interface FrameworkTask extends Callable<FrameworkTask>, Learnable, FullyInitializable{
	
	/**
	 * Returns status
	 * @return current FrameworkTask status
	 */
	public TaskStatus getTaskStatus();
	
	/**
	 * Sets the task's {@link TaskStatus}. If {@link TaskStatus} is {@link TaskStatus#CANCELED} {@link TaskStatus} will not be modified.  
	 * @param s the new {@link TaskStatus} 
	 */
	public void setTaskStatus(TaskStatus s);

	/**
	 * Tells this FrameworkTask to shutdown.
	 * @deprecated To be replaced by {@link #cancel()} which should be used instead. 
	 * The method's name makes it ambiguous as to whether it sets {@link TaskStatus} to {@link TaskStatus#FINISHED} or {@link TaskStatus#CANCELED}.
	 */
	@Deprecated
	public void stopRunning();
	
	/**
	 * Sets this FrameworkTask's {@link TaskStatus} to {@link TaskStatus#CANCELED}.<br/>
	 * FrameworkTask cannot be restarted and its {@link TaskStatus} can no longer change.
	 */
	public void cancel();
		
	/**
	 * A unique id that is set at the time of creation.
	 * 
	 * @return  id unique task identifier
	 */
	public long getTaskId();
	
	/**
	 * Sets ticksPerRun
	 * @see #setNextTicksPerRun(long) change ticksPerRun for the next run only
	 * 
	 * @param t number of ticks that will occur between executions of this task
	 */
	public void setTicksPerRun(int t);

	/**
	 * Gets ticksPerRun
	 * @return number of ticks that will occur between executions of this task
	 */
	public int getTicksPerRun();
	
	/**
	 * Sets TaskSpawner that controls this FrameworkTask.
	 * @param ts the TaskSpawner
	 */
	public void setControllingTaskSpawner(TaskSpawner ts);
	
	/**
	 * Gets TaskSpawner that controls this FrameworkTask.
	 * @return the TaskSpawner.
	 */
	public TaskSpawner getControllingTaskSpawner();
	
	/**
	 * Sets nextTicksPerRun
	 * 
	 * @see #setTicksPerRun(int) to set the permanent (default) number
	 * of ticksPerRun
	 * 
	 * @param t number of ticks that must pass before for the next, and only the next, execution of this
	 * FrameworkTask.
	 */
	public void setNextTicksPerRun(long t);
	
	/**
	 * Gets nextTicksPerRun
	 * @return number of ticks that will occur before the next execution of this
	 * {@link FrameworkTask}.
	 */
	public long getNextTicksPerRun();
	
	/**
	 * Sets tick when this task will be run next. This method is used by TaskManager when
	 * a new task is added.
	 * 
	 * @see TaskManager
	 * @param t tick to schedule this task
	 */
	public void setScheduledTick(long t);

	/**
	 * Returns the tick when this task is scheduled to run next.
	 * Could be in the future if this task is already scheduled for execution.
	 * @return scheduledTick tick when this task will run next
	 */
	public long getScheduledTick();
}