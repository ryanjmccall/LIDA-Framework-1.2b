/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.tasks;

import edu.memphis.ccrg.lida.framework.shared.NodeStructure;
import edu.memphis.ccrg.lida.framework.shared.NodeStructureImpl;

/**
 * Abstract implementation of {@link Codelet}.
 * 
 * @author Ryan J. McCall
 */
public abstract class CodeletImpl extends FrameworkTaskImpl implements Codelet {

	/**
	 * Content which this codelet responds to.
	 */
	protected NodeStructure soughtContent = new NodeStructureImpl();
	
	@Override
	public NodeStructure getSoughtContent() {
		return soughtContent;
	}

	@Override
	public void setSoughtContent(NodeStructure content) {
		soughtContent = content;
	}
}
