/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.pam;

import edu.memphis.ccrg.lida.framework.shared.Link;
import edu.memphis.ccrg.lida.framework.shared.LinkCategory;
import edu.memphis.ccrg.lida.framework.shared.Node;

/**
 * A PamNode is a {@link Node} which resides in {@link PerceptualAssociativeMemory} and 
 * represents a feature or a concept.  
 * PamNodes are involved in activation passing where Nodes are not. 
 * They can represent the {@link LinkCategory} of a {@link Link}.
 * 
 * @author Ryan J. McCall
 * @author Javier Snaider
 * @see PerceptualAssociativeMemory
 */
public interface PamNode extends Node, PamLinkable, LinkCategory {
	
}