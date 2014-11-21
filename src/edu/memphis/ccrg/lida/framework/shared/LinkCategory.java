/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.shared;

import edu.memphis.ccrg.lida.pam.PamNode;

/**
 * Specifies the category of a Link. 
 * @see PamNode
 * @see Link
 * @author Javier Snaider
 * @author Ryan J. McCall
 */
public interface LinkCategory {
	
	/**
	 * @return readable label
	 */
	public String getLabel();
	
	/**
	 * @return category's id
	 */
	public int getId();
	
}
