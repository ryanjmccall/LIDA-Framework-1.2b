/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.initialization;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Utilities for loading {@link Properties}
 * 
 * @author Javier Snaider
 * 
 */
public class ConfigUtils {

	private static final Logger logger = Logger.getLogger(ConfigUtils.class
			.getCanonicalName());

	/**
	 * Loads specified {@link Properties} file.
	 * 
	 * @param fileName
	 *            file name
	 * @return {@link Properties} or null if the file is invalid.
	 */
	public static Properties loadProperties(String fileName) {
		Properties properties = new Properties();
		if (fileName != null) {
			try {
				properties.load(new BufferedReader(new FileReader(fileName)));
			} catch (FileNotFoundException e) {
				logger.log(Level.WARNING, e.toString());
				properties = null;
			} catch (IOException e) {
				logger.log(Level.WARNING, e.toString());
				properties = null;
			}
		} else {
			logger.log(Level.WARNING, "Properties file not specified");
			properties = null;
		}
		return properties;
	}

	/**
	 * Configures the Logger manager with specified configFile properties file.
	 * @param path path of properties file
	 * @see LogManager
	 */
	public static void configLoggers(String path) {
		FileInputStream fis;
		try {
			fis = new FileInputStream(path);
			LogManager.getLogManager().readConfiguration(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			logger.log(Level.WARNING, 
					"Exception: {0}\n occurred loading Logging Properties File from path: {1}",
					new Object[]{e, path});
		} catch (SecurityException e) {
			logger.log(Level.WARNING, 
					"Exception: {0}\n occurred loading Logging Properties File from path: {1}",
					new Object[]{e, path});
		} catch (IOException e) {
			logger.log(Level.WARNING, 
					"Exception: {0}\n occurred loading Logging Properties File from path: {1}",
					new Object[]{e, path});
		}
	}
}