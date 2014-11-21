/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.sensorymotormemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.actionselection.Action;
import edu.memphis.ccrg.lida.actionselection.ActionSelectionListener;
import edu.memphis.ccrg.lida.environment.Environment;
import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.FrameworkModuleImpl;
import edu.memphis.ccrg.lida.framework.ModuleListener;
import edu.memphis.ccrg.lida.framework.initialization.Initializable;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTaskImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.sensorymemory.SensoryMemoryListener;

/**
 * Default implementation of a Map-based SensoryMotorMemory
 * 
 * @author Ryan J. McCall
 * 
 */
public class BasicSensoryMotorMemory extends FrameworkModuleImpl implements
		SensoryMotorMemory, SensoryMemoryListener, ActionSelectionListener {

	private static final Logger logger = Logger
			.getLogger(BasicSensoryMotorMemory.class.getCanonicalName());

	private static final int DEFAULT_BACKGROUND_TASK_TICKS = 1;
	private int processActionTaskTicks;
	private List<SensoryMotorMemoryListener> listeners = new ArrayList<SensoryMotorMemoryListener>();
	private Map<Number, Object> actionAlgorithmMap = new HashMap<Number, Object>();
	private Environment environment;

	/**
	 * Default constructor
	 */
	public BasicSensoryMotorMemory() {
	}

	/**
     * Will set parameters with the following names:<br/><br/>
     * 
     * <b>smm.processActionTaskTicks</b> delay (in ticks) on the processing of an Action receive from ActionSelection<br/>
     * 
     * @see Initializable
     */
	@Override
	public void init() {
		processActionTaskTicks = (Integer)getParam("smm.processActionTaskTicks", DEFAULT_BACKGROUND_TASK_TICKS);
	}
	
	@Override
	public void addListener(ModuleListener listener) {
		if (listener instanceof SensoryMotorMemoryListener) {
			addSensoryMotorMemoryListener((SensoryMotorMemoryListener) listener);
		} else {
			logger.log(Level.WARNING, "Cannot add listener {1}",
					new Object[]{TaskManager.getCurrentTick(),listener});
		}
	}

	@Override
	public void addSensoryMotorMemoryListener(SensoryMotorMemoryListener l) {
		listeners.add(l);
	}

	@Override
	public void setAssociatedModule(FrameworkModule module, String moduleUsage) {
		if (module instanceof Environment) {
			environment = (Environment) module;
		} else {
			logger.log(Level.WARNING, "Cannot add module {1}",
					new Object[]{TaskManager.getCurrentTick(),module});
		}
	}

	/**
	 * Adds an Algorithm to this {@link SensoryMotorMemory}
	 * @param actionId Id of {@link Action} which is implemented by the algorithm
	 * @param action an algorithm
	 */
	public void addActionAlgorithm(Number actionId, Object action) {
		actionAlgorithmMap.put(actionId, action);
	}

	@Override
	public synchronized void receiveAction(Action action) {
		if(action != null){
			ProcessActionTask t = new ProcessActionTask(action);
			taskSpawner.addTask(t);
		}else{
			logger.log(Level.WARNING, "Received null action", TaskManager.getCurrentTick());
		}
	}
	private class ProcessActionTask extends FrameworkTaskImpl {
		private Action action;
		public ProcessActionTask(Action a) {
			super(processActionTaskTicks);
			action = a;
		}
		@Override
		protected void runThisFrameworkTask() {
			Object alg = actionAlgorithmMap.get((Number) action.getId());
			if(alg != null){
				sendActuatorCommand(alg);
			}else{
				logger.log(Level.WARNING, "Could not find algorithm for action {1}",
						new Object[]{TaskManager.getCurrentTick(),action});
			}
			cancel();
		}
	}

	@Override
	public void sendActuatorCommand(Object command) {
		environment.processAction(command);
		for (SensoryMotorMemoryListener l : listeners) {
			l.receiveActuatorCommand(command);
		}
	}

	@Override
	public void decayModule(long ticks) {
		// module-specific decay code
	}

	@Override
	public Object getModuleContent(Object... params) {
		return null;
	}

	@Override
	public void receiveSensoryMemoryContent(Object content) {
		// Research problem
	}

}
