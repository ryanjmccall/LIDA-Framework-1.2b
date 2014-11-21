/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.gui.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.Agent;
import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.ModuleName;

/**
 * A collection of static Gui utility methods.
 * @author Ryan J. McCall
 * @author Javier Snaider
 *
 */
public class GuiUtils {
	
	private static final Logger logger = Logger.getLogger(GuiUtils.class
			.getCanonicalName());

	/**
	 * Utility method to parse a String to obtain a {@link FrameworkModule}
	 * @param param String to parse
	 * @param agent {@link Agent}
	 * @return FrameworkModule with specified name or null
 	 */
	public static FrameworkModule parseFrameworkModule(String param, Agent agent) {
		if(param == null){
			logger.log(Level.WARNING, "null string argument.", 0L);
			return null;
		}
		
		String[] modules = param.trim().split("\\.");
		ModuleName moduleType = ModuleName.getModuleName(modules[0]);
		if (moduleType == null) {
			logger.log(Level.WARNING, "Error getting module type {1}", new Object[]{0L, modules[0]});
			return null;
		}
		FrameworkModule module = agent.getSubmodule(moduleType);
		if (module == null) {
			logger.log(Level.WARNING, "Error getting submodule {1}", new Object[]{0L,moduleType});
			return null;
		}
		for (int i = 1; i < modules.length; i++) {
			moduleType = ModuleName.getModuleName(modules[i]);
			if (moduleType == null) {
				logger.log(Level.WARNING, "Error getting submodule {1}", 
						new Object[]{0L, moduleType});
				return null;
			}

			module = module.getSubmodule(moduleType);
			if (module == null) {
				logger.log(Level.WARNING, "Error getting submodule {1}", 
						new Object[]{0L, moduleType});
				return null;
			}
		}
		return module;
	}
}
