/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
/**
 *
 */
package edu.memphis.ccrg.lida.framework.gui.panels;

import javax.swing.JPanel;

import edu.memphis.ccrg.lida.framework.Agent;
import edu.memphis.ccrg.lida.framework.gui.FrameworkGui;
import edu.memphis.ccrg.lida.framework.gui.FrameworkGuiController;

/**
 * A GUI Panel which can be displayed in the {@link FrameworkGui}
 * 
 * @author Javier Snaider
 */
public interface GuiPanel {
    /**
     * Initializes panel
     * @param param Parameters to initialize with.
     */
    public void initPanel(String[] param);
	
	/**
	 * Registers the {@link FrameworkGuiController} as the controller.
	 * @param lgc GuiController for this panel
	 */
	public void registerGuiController(FrameworkGuiController lgc);
	
	/**
	 * Sets {@link Agent} object as the model for this panel.
	 * @param agent {@link Agent} object
	 */
	public void registerAgent(Agent agent);
	
	/**
	 * Update Panel to display supplied object
	 * @param o Object to display
	 */
	public void display (Object o);
	
	/**
	 * Refreshes the content this panel displays.
	 */
	public void refresh();
	
	/**
	 * Returns associated JPanel
	 * @return a JPanel
	 */
	public JPanel getPanel();
    
    /**
     * Sets name of panel
     * @param name label for panel
     */
    public void setName(String name);
    
    /**
     * Gets name of panels
     * @return name of panel
     */
    public String getName();
    
}

