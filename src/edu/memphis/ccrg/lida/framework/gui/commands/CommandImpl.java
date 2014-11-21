/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.gui.commands;

import java.util.HashMap;
import java.util.Map;

import edu.memphis.ccrg.lida.framework.Agent;

/**
 * Abstract implementation of {@link Command}.
 * Extend from this base class to create new commands overriding {@link #execute(Agent)}.
 * 
 * @author Javier Snaider
 *
 */
public abstract class CommandImpl implements Command {
	
	private Map<String,Object> parameters=new HashMap<String,Object>();
	/**
	 * Result of the command's execution. May be set during the execution of the {@link #execute(Agent)} method.
	 */
	protected Object result;

	@Override
	public abstract void execute(Agent agent);

	@Override
	public Object getParameter(String name) {
		Object res=null;
		if (parameters!=null){
			res=parameters.get(name);
		}
		return res;
	}

	@Override
	public Object getResult() {
		return result;
	}

	@Override
	public void setParameter(String name, Object value) {
		parameters.put(name, value);
	}

	@Override
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	
	@Override 
	public String toString(){
		return getClass().getSimpleName();
	}

}
