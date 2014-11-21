/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.actionselection;

import java.util.Collection;

import edu.memphis.ccrg.lida.framework.FrameworkModule;

/**
 * Specification for the action selection module of LIDA.
 * 
 * @author Ryan J. McCall
 * 
 */
public interface ActionSelection extends FrameworkModule {

	/**
	 * Adds specified {@link ActionSelectionListener}.
	 * 
	 * @param l a module that receives selected actions from {@link ActionSelection}
	 */
	public void addActionSelectionListener(ActionSelectionListener l);

	/**
	 * Adds specified {@link PreafferenceListener}
	 * @param l a module that receives preafference from {@link ActionSelection}
	 */
	public void addPreafferenceListener(PreafferenceListener l);
	
	/**
	 * Selects a behavior (containing an action) for execution.
	 * @param behaviors {@link Collection} of behaviors currently available in the module
	 * @param candidateThreshold threshold for a behavior to be a candidate
	 * @return winning Behavior or null if none was chosen
	 */
	public Behavior selectBehavior(Collection<Behavior> behaviors, double candidateThreshold);
	
	/**
	 * Returns a view of the behaviors currently in {@link ActionSelection}
	 * @return a {@link Collection} of {@link Behavior} objects
	 */
	public Collection<Behavior> getBehaviors();
}
