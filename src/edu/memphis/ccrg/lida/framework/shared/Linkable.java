/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.shared;

import edu.memphis.ccrg.lida.framework.initialization.Initializable;
import edu.memphis.ccrg.lida.framework.shared.activation.Activatible;

/**
 * A object that can have links attached to it.
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 */
public interface Linkable extends Activatible, Initializable {

	/**
	 * Gets label.
	 * 
	 * @return readable label
	 */
	public String getLabel();
	
	/**
	 * Gets extendedId.
	 * 
	 * @return a general id for Linkables.
	 */
	public ExtendedId getExtendedId();
	
    /**
     * Gets factory type
     * @return the factory type of the Linkable 
     * @see ElementFactory
     */
    public String getFactoryType();
    
    /**
     * Sets factory type
     * @param t the factory type of the Linkable
     * @see ElementFactory
     */
    public void setFactoryType(String t);

}
