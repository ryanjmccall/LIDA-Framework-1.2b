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
package edu.memphis.ccrg.lida.framework.tasks;
 
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.shared.activation.LearnableImpl;

/**
 * This class implements the FrameworkTask Interface. This class should be used as the base class for all FrameworkTasks.
 * @author Javier Snaider
 */
public abstract class FrameworkTaskImpl extends LearnableImpl implements FrameworkTask {

	private static final Logger logger= Logger.getLogger(FrameworkTaskImpl.class.getCanonicalName());

	private final static int defaultTicksPerRun = 1;
	private static long nextTaskID;
	
	/*
	 * frequency in ticks  
	 */
	private int ticksPerRun = defaultTicksPerRun;
	private long taskID;
	private long nextExcecutionTicksPerRun = defaultTicksPerRun;
	/**
	 * {@link TaskStatus} of this task. Initial value is {@link TaskStatus#RUNNING}.
	 */
	protected TaskStatus status = TaskStatus.RUNNING;
	private TaskSpawner controllingTS;
	private long scheduledTick;
    private final String taskName;
	
	/**
	 * Constructs a {@link FrameworkTaskImpl} with default ticksPerRun
	 */
	public FrameworkTaskImpl() {
		this(defaultTicksPerRun,null);
	}
	
	/**
	 * Constructs a {@link FrameworkTaskImpl} with specified ticksPerRun
	 * @param ticksPerRun task's run frequency
	 */
	public FrameworkTaskImpl(int ticksPerRun) {
		this(ticksPerRun,null);
	}
	
	/**
	 * Constructs a {@link FrameworkTaskImpl} with default ticksPerRun and specified
	 * controlling {@link TaskSpawner}
	 * @param ticksPerRun task's run frequency
	 * @param ts controlling {@link TaskSpawner}
	 */
	public FrameworkTaskImpl(int ticksPerRun, TaskSpawner ts) {
		taskID = nextTaskID++;
		controllingTS = ts;
		setTicksPerRun(ticksPerRun);
        taskName = getClass().getSimpleName() + "["+taskID+"]";
	}
	
	@Override
	public long getScheduledTick() {
		return scheduledTick;
	}

	@Override
	public void setScheduledTick(long t) {
		scheduledTick = t;
	}

	/** 
	 * This method should not be called directly nor should it be overridden.
	 * Override {@link #runThisFrameworkTask()} instead.
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public FrameworkTask call() {
		nextExcecutionTicksPerRun = ticksPerRun;		
		try{
			runThisFrameworkTask();
		}catch(Exception e){
			logger.log(Level.WARNING, "Exception encountered during the execution of task {1}. \n {e}", 
					new Object[] {TaskManager.getCurrentTick(),this,e});
			e.printStackTrace();
		}
		if (controllingTS != null){ 
			try{
				controllingTS.receiveFinishedTask(this);
			}catch(Exception e){
				logger.log(Level.WARNING, 
						"Exception encountered during the execution of method 'receiveFinishedTask' in TaskSpawner: {1} \n {e}", 
						new Object[] {TaskManager.getCurrentTick(),this,e});
				e.printStackTrace();
			}
		}else {
			logger.log(Level.WARNING, "Task {1} does not have an assigned TaskSpawner",
					new Object[] {TaskManager.getCurrentTick(), this });
		}
		return this;
	}

	/**
	 * To be overridden by extending classes. Overriding method should execute a
	 * handful of statements considered to constitute a single iteration of the
	 * task. For example, a codelet might look in a buffer for some
	 * content and make a change to it in a single iteration. 
	 * The overriding method may also change the {@link TaskStatus} of a task. 
	 * For example, if the task should only run once and stop, then the method {@link #cancel()}
	 * may be used to stop the task from further execution (calls of this {@link #runThisFrameworkTask()} beyond the current one.
	 */
	protected abstract void runThisFrameworkTask();

	@Override
	public synchronized void setTaskStatus(TaskStatus s) {
		if (status == TaskStatus.CANCELED){
			logger.log(Level.WARNING, "Cannot set TaskStatus to {1}. TaskStatus is already CANCELED so it cannot be modified again.", 
					new Object[]{TaskManager.getCurrentTick(),s});
		}else {
			status = s;
		}
	}

	@Override
	public TaskStatus getTaskStatus() {
		return status;
	}

	@Override
	public long getTaskId() {
		return taskID;
	}

	@Override
	public synchronized int getTicksPerRun() {
		return ticksPerRun;
	}

	@Override
	public synchronized void setTicksPerRun(int ticks) {
		if (ticks > 0){
			ticksPerRun = ticks;
			setNextTicksPerRun(ticks);
		}
	}

	@Deprecated
	@Override
	public void stopRunning() {
		cancel();
	}
	
	@Override
	public void cancel(){
		setTaskStatus(TaskStatus.CANCELED);
	}
	
	@Override
	public TaskSpawner getControllingTaskSpawner() {		
		return controllingTS;
	}
	
	@Override
	public void setControllingTaskSpawner(TaskSpawner ts) {
		controllingTS=ts;		
	}
	
	@Override
	public long getNextTicksPerRun() {		
		return nextExcecutionTicksPerRun;
	}
	
	@Override
	public void setNextTicksPerRun(long tick) {
		nextExcecutionTicksPerRun = tick;	
	}
	
	/**
	 * Subclasses may override this method.
	 */
	@Override
	public void setAssociatedModule(FrameworkModule module, String moduleUsage) {
	}	
	
	@Override
	public boolean equals(Object o){
		if(o instanceof FrameworkTaskImpl){
			return taskID == ((FrameworkTaskImpl) o).getTaskId();
		}
		return false;
	}
	@Override
	public int hashCode(){
		return (int) taskID;
	}
	
	@Override
	public String toString(){
        return taskName;
    }
}