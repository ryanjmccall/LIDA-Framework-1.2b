/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.actionselection;

import edu.memphis.ccrg.lida.framework.initialization.InitializableImpl;

/**
 * Default implementation of {@link Action}.
 * 
 * @author Ryan J. McCall
 * @author Javier Snaider
 * 
 */
public class ActionImpl extends InitializableImpl implements Action {

	private static int idGenerator = 0;
	private int id;
	private String label;

	/**
	 * Default constructor
	 */
	public ActionImpl() {
		id = idGenerator++;
	}

	/**
	 * Convenience constructor that set the Action's label 
	 * @param label the label to set
	 */
	public ActionImpl(String label) {
		this();
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void setLabel(String name) {
		this.label = name;
	}

	@Override
	public int getId() {
		return id;
	}
	
	@Override
	public String toString(){
		return label;
	}
}
