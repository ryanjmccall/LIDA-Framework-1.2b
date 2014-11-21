/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.memphis.ccrg.lida.framework.gui.events;

/**
 * An object that listens for FrameworkGuiEvents, that is, data being sent from the model (framework)
 * to the GUI.
 * @author Javier Snaider
 */
public interface FrameworkGuiEventListener {
	
	/**
	 * Must be able to receive FrameworkGuiEvents  
	 * @param event GuiEvent
	 */
	public void receiveFrameworkGuiEvent(FrameworkGuiEvent event);
	
}
