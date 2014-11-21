/*******************************************************************************
 * Copyright (c) 2009, 2010 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.actionselection;

import edu.memphis.ccrg.lida.proceduralmemory.ProceduralUnit;
import edu.memphis.ccrg.lida.proceduralmemory.Scheme;

/**
 * An instantiated {@link Scheme} with a context, adding list, and deleting list.
 * @author Javier Snaider
 * @author Ryan J. McCall
 */
public interface Behavior extends ProceduralUnit {
	
	/**
	 * Gets the scheme underlying the behavior.
	 * 
	 * @return the generating scheme
	 */
	public Scheme getScheme();
	
	/**
	 * Gets the scheme underlying the behavior.
	 * 
	 * @param s the new generating scheme
	 */
	public void setScheme(Scheme s);
}