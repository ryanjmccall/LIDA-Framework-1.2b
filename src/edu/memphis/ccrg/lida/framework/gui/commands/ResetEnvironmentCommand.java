/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.gui.commands;

import edu.memphis.ccrg.lida.environment.Environment;
import edu.memphis.ccrg.lida.framework.Agent;
import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.ModuleName;

/**
 * Resets the {@link Environment} of the current application.
 * @author Ryan Mccall
 *
 */
public class ResetEnvironmentCommand extends CommandImpl {

	@Override
	public void execute(Agent agent) {
		FrameworkModule environ = agent.getSubmodule(ModuleName.Environment);
		if(environ != null && environ instanceof Environment){
			((Environment)environ).resetState();
		}
	}

}
