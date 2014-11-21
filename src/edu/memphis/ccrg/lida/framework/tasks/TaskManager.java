/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.Agent;
import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.ModuleName;
import edu.memphis.ccrg.lida.framework.gui.events.FrameworkGuiEvent;
import edu.memphis.ccrg.lida.framework.gui.events.FrameworkGuiEventListener;
import edu.memphis.ccrg.lida.framework.gui.events.GuiEventProvider;
import edu.memphis.ccrg.lida.framework.shared.ConcurrentHashSet;

/**
 * All tasks in the {@link Agent} system are executed by this class. Controls
 * the decay of all the {@link FrameworkModule}s in {@link Agent}. Keeps track
 * of the current tick, the unit of time in the application. Maintains a task
 * queue where each position represents the time (in ticks) when a task will be
 * executed. Multiple tasks can be scheduled for the same tick. Uses an
 * {@link ExecutorService} to obtain the threads to run all the tasks scheduled
 * in one tick concurrently.
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 */
public class TaskManager implements GuiEventProvider {

	private static final Logger logger = Logger.getLogger(TaskManager.class
			.getCanonicalName());

	/**
	 * Default minimum duration of a tick in real time (ms)
	 */
	public static final int DEFAULT_TICK_DURATION = 1;
	/**
	 * Default number of threads in the {@link ExecutorService}
	 */
	public static final int DEFAULT_NUMBER_OF_THREADS = 50;
	/*
	 * Determines whether or not spawned tasks should run
	 */
	private volatile boolean tasksPaused = true;

	/*
	 * Whether or not this task manager is shutting down its tasks
	 */
	private volatile boolean shuttingDown = false;

	private volatile long endOfNextInterval = 0L;
	private volatile static long currentTick = 0L;
	private volatile Long maxTick = 0L;
	private volatile boolean inIntervalMode = false;
	private final Object lock = new Object();

	private ConcurrentMap<Long, Set<FrameworkTask>> taskQueue;
	/*
	 * Length of time of 1 tick in milliseconds. The actual time thats the tick
	 * unit represents. In practice tickDuration affects the speed of tasks in
	 * the simulation.
	 */
	private int tickDuration = DEFAULT_TICK_DURATION;

	/*
	 * Service used to execute the tasks
	 */
	private ExecutorService executorService;

	/*
	 * Main thread of the system.
	 */
	private Thread taskManagerThread;

	/*
	 * List of the FrameworkModules managed by this class
	 */
	private List<DecayableWrapper> decayables = new ArrayList<DecayableWrapper>();

	private volatile long lastGuiEventTick;
	private volatile int guiEventsInterval = 0;
	private List<FrameworkGuiEventListener> guiListeners = new ArrayList<FrameworkGuiEventListener>();
	private FrameworkGuiEvent defaultGuiEvent = new FrameworkGuiEvent(
			ModuleName.Agent, "TicksEvent", null);

	/**
	 * Constructs a new TaskManager.
	 * @param tickDuration
	 *            - length of time of 1 tick in milliseconds
	 * @param maxPoolSize
	 *            - max number of threads used by the ExecutorService
	 */
	public TaskManager(int tickDuration, int maxPoolSize) {
		int corePoolSize = DEFAULT_NUMBER_OF_THREADS;
		long keepAliveTime = 10;
		if (tickDuration >= 0) {
			this.tickDuration = tickDuration;
		} else {
			logger.log(Level.WARNING, "Tick duration must be 0 or greater",
					currentTick);
		}
		if (corePoolSize > maxPoolSize) {
			corePoolSize = maxPoolSize;
		}
		taskQueue = new ConcurrentHashMap<Long, Set<FrameworkTask>>();
		executorService = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
				keepAliveTime, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());

		taskManagerThread = new Thread(new TaskManagerMainLoop());
		taskManagerThread.start();
	}

	/**
	 * Current tick in the system. Tasks scheduled for this tick have been
	 * executed or they are being executed.
	 * 
	 * @return current tick
	 */
	public static long getCurrentTick() {
		return currentTick;
	}

	/**
	 * Returns max tick.
	 * 
	 * @return the farthest tick in the future that has a scheduled task. in
	 *         other words, the highest tick position in the task queue that has
	 *         scheduled task(s).
	 */
	public long getMaxTick() {
		return maxTick;
	}

	/**
	 * This attribute is used for interval execution mode. Returns
	 * endOfNextInterval
	 * 
	 * @return the absolute tick when the current execution interval ends
	 */
	public long getEndOfNextInterval() {
		return endOfNextInterval;
	}

	/**
	 * Sets tickDuration
	 * 
	 * @param d
	 *            the new tick duration, the length of time of 1 tick in
	 *            milliseconds. The actual time that the tick unit represents.
	 *            In practice tickDuration affects the speed of tasks in the
	 *            simulation.
	 */
	public synchronized void setTickDuration(int d) {
		if (d >= 0) {
			tickDuration = d;
		} else {
			logger.log(Level.WARNING, "Tick duration must be 0 or greater",
					currentTick);
		}
	}

	/**
	 * @return tickDuration
	 */
	public int getTickDuration() {
		return tickDuration;
	}

	/**
	 * Sets guiEventsInterval
	 * 
	 * @param i
	 *            set a new guiEventsInterval, the number of ticks between gui
	 *            events generated by the TaskManager. Used to refresh the GUI
	 *            panels. with 0, no events are generated.
	 */
	public synchronized void setGuiEventsInterval(int i) {
		if (i >= 0) {
			guiEventsInterval = i;
		} else {
			logger.log(Level.WARNING, "guiEventsInterval must be 0 or greater",
					currentTick);
		}
	}

	/**
	 * @return guiEventsInterval
	 */
	public int getGuiEventsInterval() {
		return guiEventsInterval;
	}

	/**
	 * @return true if system is in interval execution mode
	 */
	public boolean isInIntervalMode() {
		return inIntervalMode;
	}

	/**
	 * Sets inIntervalMode. 
	 * @param intervalMode true to set the system to interval execution mode, false to
	 *            exit.
	 */
	public void setInIntervalMode(boolean intervalMode) {
		inIntervalMode = intervalMode;
		if (!intervalMode) {
			synchronized (lock) {
				lock.notify();
			}
		}
	}

	/**
	 * @return UnmodifiableMap of the task queue
	 */
	public Map<Long, Set<FrameworkTask>> getTaskQueue() {
		return Collections.unmodifiableMap(taskQueue);
	}

	/**
	 * @return true if tasks are paused
	 */
	public boolean isTasksPaused() {
		return tasksPaused;
	}

	/**
	 * Finish the executions of all tasks scheduled for the currentTick and
	 * pauses all further tasks executions.
	 */
	public void pauseTasks() {
		logger.log(Level.INFO, "All tasks paused.", currentTick);
		tasksPaused = true;
	}

	/**
	 * Resumes the execution of tasks in the queue.
	 */
	public void resumeTasks() {
		if (!shuttingDown) {
			logger
					.log(
							Level.INFO,
							"Resuming execution of all tasks. Current tick is {0}. Last scheduled task at tick {1}.",
							new Object[] { currentTick, maxTick });

			tasksPaused = false;

			synchronized (lock) {
				lock.notify();
			}
		}
	}

	/**
	 * Cancels the task from the Task Queue. This is only possible if the tick
	 * for which the task is scheduled has not been reached.
	 * 
	 * @param task
	 *            The task to cancel.
	 * @return true if it was , false otherwise.
	 */
	public boolean cancelTask(FrameworkTask task) {
		if (task != null) {
			long time = task.getScheduledTick();
			if (time > currentTick) {
				Set<FrameworkTask> set = taskQueue.get(time);
				if (set != null) {
					return set.remove(task);
				}
			}
		} else {
			logger.log(Level.WARNING, "Cannot cancel a null task", currentTick);
		}
		return false;
	}

	/**
	 * Sets a number of ticks to execute when the system is in interval
	 * execution mode. The system will execute all tasks scheduled in the queue
	 * until currentTick + ticks.
	 * 
	 * @param ticks
	 *            the number of ticks to use as an interval.
	 */
	public void addTicksToExecute(long ticks) {
		if (ticks > 0) {
			if (inIntervalMode) {
				if (endOfNextInterval < currentTick) {
					endOfNextInterval = currentTick;
				}
				endOfNextInterval += ticks;
				synchronized (lock) {
					lock.notify();
				}
			}
		} else {
			logger.log(Level.WARNING,
					"Number of ticks added must be greater than zero",
					currentTick);
		}
	}

	/**
	 * Schedules the task for execution in currentTick + inXTicks If inXTicks is
	 * negative or 0, the task is not scheduled.
	 * 
	 * @param task
	 *            the task to schedule
	 * @param inXTicks
	 *            the number of ticks in the future that the task will be
	 *            scheduled for execution.
	 * @return true if the task was scheduled.
	 */
	public boolean scheduleTask(FrameworkTask task, long inXTicks) {
		if (task == null) {
			logger.log(Level.WARNING, "Cannot schedule a null task",
					currentTick);
			return false;
		}
		if (inXTicks < 1) {
			logger
					.log(
							Level.WARNING,
							"task {1} was scheduled with inXTicks of {2} but this must be 1 or greater",
							new Object[] { currentTick, task, inXTicks });
			return false;
		}
		Long time = currentTick + inXTicks;
		Set<FrameworkTask> set = taskQueue.get(time);
		if (set == null) {
			Set<FrameworkTask> set2 = new ConcurrentHashSet<FrameworkTask>();
			set = taskQueue.putIfAbsent(time, set2);
			if (set == null) {// there wasn't a set already at key 'time'
				set = set2;
				synchronized (maxTick) {
					if (time > maxTick) {
						maxTick = time;
						synchronized (lock) {
							lock.notify();
						}
					}
				}
			}
		}
		task.setScheduledTick(time);
		set.add(task);
		return true;
	}

	private long goNextTick() {
		// TODO optimize this method to skip ticks until the next tick with
		// scheduled tasks is found
		Set<FrameworkTask> set = taskQueue.get(++currentTick);
		taskQueue.remove(currentTick);
		logger.log(Level.FINEST, "Tick {0} executed", currentTick);
		if (set != null) {
			try {
				decayModules();
				executorService.invokeAll(set); // Execute all tasks scheduled
				// for this tick
			} catch (InterruptedException e) {
				if (!shuttingDown) {
					logger.log(Level.WARNING,
							"Current tick {0} was interrupted because of {1}",
							new Object[] { currentTick, e.getMessage() });
				} else {
					logger
							.log(
									Level.INFO,
									"Current tick {0} interrupted for application shutdown.",
									currentTick);
				}
			}
		}
		return currentTick;
	}

	private void decayModules() {
		DecayableWrapper.setDecayInterval(currentTick);
		try {
			executorService.invokeAll(decayables);
		} catch (InterruptedException e) {
			logger.log(Level.WARNING, "Decaying interrupted. Message: {1}",
					new Object[] { currentTick, e.getMessage() });
		}
		DecayableWrapper.setLastDecayTick(currentTick);
		logger.log(Level.FINEST, "Modules decayed", currentTick);
	}

	/**
	 * Set the Collection of modules for decaying.
	 * 
	 * @param modules
	 *            a Collection with the FrameworkModules
	 */
	public void setDecayingModules(Collection<FrameworkModule> modules) {
		if (modules != null) {
			for (FrameworkModule lm : modules) {
				if (lm != null) {
					decayables.add(new DecayableWrapper(lm));
				}
			}
		}
	}

	/**
	 * This inner class implements the main loop of the system. The main loop
	 * waits on the lock if the tasks are paused, if in interval mode and have
	 * reached endOfNextInterval, or if no tasks are scheduled beyond current
	 * tick.
	 */
	private class TaskManagerMainLoop implements Runnable {

		@Override
		public void run() {
			while (!shuttingDown) {
				synchronized (lock) {
					if ((currentTick >= maxTick)
							|| (inIntervalMode && (currentTick >= endOfNextInterval))
							|| tasksPaused) {
						try {
							lock.wait();
							continue;
						} catch (InterruptedException e) {
							logger.log(Level.INFO, "Main loop interrupted.",
									currentTick);
							return;
						}
					}
				}

				long initTime = System.currentTimeMillis(); // For real time

				goNextTick(); // Execute one tick of the simulation

				long duration = System.currentTimeMillis() - initTime;
				if (duration < tickDuration) {// TODO change this if multiple
												// ticks are executed in
												// goNextTick()
					try {
						Thread.sleep(tickDuration - duration);
					} catch (InterruptedException e) {
						return;
					}
				}
				// To update Gui
				if (guiEventsInterval > 0 && !guiListeners.isEmpty()) {
					if (currentTick - lastGuiEventTick >= guiEventsInterval) {
						sendEventToGui(defaultGuiEvent);
						lastGuiEventTick = currentTick;
					}
				}
			}// while
		}
	}// class

	/**
	 * This is an auxiliary class to perform the decaying of the modules in
	 * parallel.
	 * 
	 * @author Javier Snaider
	 * @author Ryan J. McCall
	 * 
	 */
	private static class DecayableWrapper implements Callable<Void> {

		private static long ticksToDecay;
		private static long lastDecayTick = 0L;
		private FrameworkModule module;

		/**
		 * Updates the interval that all decayables should decay. Must be setup
		 * before executing the run() method.
		 * 
		 * @param currentTick
		 *            current tick of the task manager
		 */
		public static void setDecayInterval(long currentTick) {
			ticksToDecay = currentTick - lastDecayTick;
		}

		/**
		 * Sets the last time that the decayables were decayed.
		 * 
		 * @param tick
		 *            last tick modules were decayed
		 */
		public static void setLastDecayTick(long tick) {
			DecayableWrapper.lastDecayTick = tick;
		}

		public DecayableWrapper(FrameworkModule m) {
			module = m;
		}

		@Override
		public Void call() {
			if (module != null) {
				try {
					module.taskManagerDecayModule(ticksToDecay);
				} catch (Exception e) {
					logger
							.log(
									Level.SEVERE,
									"Exception occurred during the execution of the 'taskManagerDecayModule(long ticks)' method in module: {1}. \n{2}",
									new Object[] { currentTick, module, e });
					e.printStackTrace();
				}
			} else {
				logger.log(Level.WARNING, "Cannot decay null", currentTick);
			}
			return null;
		}
	}

	/**
	 * This method stops all tasks executing and prevents further tasks from
	 * being executed. It is used to shutdown the entire system. Method shuts
	 * down all tasks, the executor service, waits, and exits.
	 */
	public void stopRunning() {
		shuttingDown = true;
		taskManagerThread.interrupt();
		// Now that we can be sure that active tasks will no longer be executed
		// the executor service can be shutdown.
		executorService.shutdown();
		logger.log(Level.INFO, "All threads and tasks told to stop",
				currentTick);
		try {
			executorService.awaitTermination(800, TimeUnit.MILLISECONDS);
			executorService.shutdownNow();
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.log(Level.INFO, "TaskManager shutting down. System exiting.",
				currentTick);
		System.exit(0);
	}

	@Override
	public void addFrameworkGuiEventListener(FrameworkGuiEventListener listener) {
		if (listener != null) {
			guiListeners.add(listener);
		} else {
			logger.log(Level.WARNING, "Can not add null as a GuiListener",
					TaskManager.getCurrentTick());
		}
	}

	@Override
	public void sendEventToGui(FrameworkGuiEvent event) {
		for (FrameworkGuiEventListener listener : guiListeners) {
			listener.receiveFrameworkGuiEvent(event);
		}
	}

	/**
	 * This method clean up the Task Queue and reset to 0 the currentTick and
	 * the maxTick. All {@link TaskSpawner} must be reset also. This method is
	 * intended to be used only when the {@link Agent} is reset. Currently used only for testing. To be
	 * implemented in the framework in the future.
	 */
	void reset() {
		taskQueue = new ConcurrentHashMap<Long, Set<FrameworkTask>>();
		endOfNextInterval = 0L;
		currentTick = 0L;
		maxTick = 0L;
		tasksPaused = true;
		inIntervalMode = false;
		DecayableWrapper.lastDecayTick = 0;
		DecayableWrapper.ticksToDecay = 0;
		lastGuiEventTick = 0;
	}

	@Override
	public String toString() {
		return "TaskManager";
	}
}