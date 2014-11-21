/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.pam;

import edu.memphis.ccrg.lida.framework.ModuleListener;
import edu.memphis.ccrg.lida.framework.shared.Link;
import edu.memphis.ccrg.lida.framework.shared.Node;
import edu.memphis.ccrg.lida.framework.shared.NodeStructure;

/**
 * A PamListener receives percepts from {@link PerceptualAssociativeMemory}
 * asynchronously.
 * 
 * @author Ryan J. McCall
 */
public interface PamListener extends ModuleListener{
	
	/**
	 * Receive a {@link NodeStructure} percept.
	 * @param ns a NodeStructure
	 */
	public void receivePercept(NodeStructure ns);
	
	/**
	 * Receive a {@link Node} percept.
	 * @param n a {@link Node}
	 */
	public void receivePercept(Node n);
	
	/**
	 * Receive a {@link Link} percept.
	 * @param l a {@link Link}
	 */
	public void receivePercept(Link l);
	
}