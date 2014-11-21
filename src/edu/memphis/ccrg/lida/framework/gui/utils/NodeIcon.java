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
package edu.memphis.ccrg.lida.framework.gui.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

import edu.memphis.ccrg.lida.framework.gui.panels.NodeStructurePanel;
import edu.memphis.ccrg.lida.framework.shared.Link;
import edu.memphis.ccrg.lida.framework.shared.Node;

/**
 * Utility which {@link NodeStructurePanel} uses to represent {@link Node} and {@link Link}.
 * 
 * @author Javier Snaider
 *
 */
public class NodeIcon implements Icon {
	
	/**
	 * Default node icon
	 */
	public static final Icon NODE_ICON=new NodeIcon(20,Color.red);
	/**
	 * Default link icon
	 */
	public static final Icon LINK_ICON=new NodeIcon(5,Color.black);

	private int size;
	private Color color;
	
	
	/**
	 * @param size NodeIcon size
	 * @param color NodeIcon color
	 */
	public NodeIcon(int size,Color color) {
		this.color = color;
		this.size = size;
	}

	@Override
	public int getIconHeight() {
		return size;
	}

	@Override
	public int getIconWidth() {
		return size;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setColor(color);
		g.fillOval(x, y, size, size);
	}

}
