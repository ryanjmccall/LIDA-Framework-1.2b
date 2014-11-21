/*******************************************************************************
 * Copyright (c) 2009, 2010 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
/**
 * Behavior.java
 *
 * @author Ryan J. McCall 
 */
package edu.memphis.ccrg.lida.actionselection;

import java.util.Collection;

import edu.memphis.ccrg.lida.framework.shared.activation.ActivatibleImpl;
import edu.memphis.ccrg.lida.proceduralmemory.Condition;
import edu.memphis.ccrg.lida.proceduralmemory.Scheme;

/**
 * Default implementation of {@link Behavior}
 * @author Ryan J. McCall
 * @author Javier Snaider
 */
public class BehaviorImpl extends ActivatibleImpl implements Behavior{

	/*
	 * Unique identifier
	 */
	private int behaviorId;

	/*
	 * The scheme from which this behavior was instantiated. 
	 */
	private Scheme scheme;
	
	/**
	 * Construct a new behavior with default parameters
	 */
	public BehaviorImpl(){
		super();
	}

	//Behavior methods
	@Override
	public void setId(int id) {
		behaviorId = id;
	}

	@Override
	public int getId() {
		return behaviorId;
	}

	@Override
	public Scheme getScheme() {
		return scheme;
	}

	@Override
	public void setScheme(Scheme s) {
		scheme  = s;
	}
	
	//Object method
	@Override
	public String toString(){
		return scheme.getLabel() + "-" + getId();
	}
	
	@Override
	public Collection<Condition> getContextConditions() {
		return scheme.getContextConditions();
	}

	@Override
	public Collection<Condition> getAddingList() {
		return scheme.getAddingList();
	}

	@Override
	public Collection<Condition> getDeletingList() {
		return scheme.getDeletingList();
	}

	@Override
	public Action getAction() {
		return scheme.getAction();
	}

	@Override
	public String getLabel() {
		return scheme.getLabel();
	}

	@Override
	public void setLabel(String l) {		
	}
}