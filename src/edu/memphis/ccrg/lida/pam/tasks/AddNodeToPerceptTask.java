/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.pam.tasks;

import edu.memphis.ccrg.lida.framework.shared.Node;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTaskImpl;
import edu.memphis.ccrg.lida.pam.PamNode;
import edu.memphis.ccrg.lida.pam.PerceptualAssociativeMemory;

/**
 * A task which adds a {@link PamNode} to the percept.
 * @author Ryan J. McCall
 */
public class AddNodeToPerceptTask extends FrameworkTaskImpl {
	
	private Node node;
	private PerceptualAssociativeMemory pam;

	/**
	 * Default constructor
	 * @param n the {@link Node} to add
	 * @param pam {@link PerceptualAssociativeMemory}
	 */
	public AddNodeToPerceptTask(Node n, PerceptualAssociativeMemory pam) {
		node = n;
		this.pam = pam;
	}

	/**
	 * Adds {@link Node} to the percept then finishes.
	 */
	@Override
	protected void runThisFrameworkTask() {		
		pam.addToPercept(node);
		cancel();
	}
}
