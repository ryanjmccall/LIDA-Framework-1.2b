/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.initialization;

import java.text.MessageFormat;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

/**
 * Logger formatter for the framework that can be used with any {@link Handler}.
 * @author Javier Snaider
 * @see LogManager
 */
public class FrameworkFormatter extends Formatter {

	@Override
	public String format(LogRecord logRecord) {
		String logMessages = new String("");
		// String dateString="";
		long actualTick = 0L;
		// String name;

		String message = logRecord.getMessage();
		if (message != null) {
			MessageFormat mf = new MessageFormat(message);

			Object[] param = logRecord.getParameters();
			if (param != null && param[0] instanceof Long) {
				actualTick = (Long) param[0];
			}
			logMessages = String.format("%010d :%010d :%-10s :%-60s \t-> %s %n",
					logRecord.getSequenceNumber(), actualTick, logRecord
							.getLevel(), logRecord.getLoggerName(), mf
							.format(logRecord.getParameters()));
			return logMessages;
		}
		return "";
	}
}
