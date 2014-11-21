/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.gui.utils;

import edu.memphis.ccrg.lida.framework.gui.panels.NodeStructurePanel;
import edu.memphis.ccrg.lida.framework.shared.Link;

/**
 * Utility which {@link NodeStructurePanel} uses to represent {@link Link}s.
 * This represents a Link in the Panel.  For every {@link Link} we create two {@link GuiLink}s:
 * The first {@link GuiLink}  has type 'S' for "source".  
 * It connects the source of the {@link Link} to the {@link Link} itself.
 * 
 * The second {@link GuiLink}  has type 'D' for "destination".  
 * It connects the {@link Link} itself to the sink of the {@link Link}   
 * 
 * @author Javier Snaider
 *
 */
public class GuiLink {
	
	private Link link;
	private char type;

	/**
	 * @param link {@link Link}
	 * @param type type
	 */
	public GuiLink(Link link, char type) {
		this.link = link;
		this.type = type;
	}
	
	/**
	 * @return the link
	 */
	public Link getLink() {
		return link;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(Link link) {
		this.link = link;
	}

	/**
	 * @return the type
	 */
	public char getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(char type) {
		this.type = type;
	}

	@Override
	public boolean equals (Object o){
		if (o instanceof GuiLink){
			GuiLink gl= (GuiLink)o;
			return (link.equals(gl.link) && type == gl.type);
		}
		return false;
		
	}
	
	@Override
	public int hashCode(){
		return link.hashCode() + type;
	}
	@Override
	public String toString(){
		return type+"";
	}
}
