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
package edu.memphis.ccrg.lida.globalworkspace;

import edu.memphis.ccrg.lida.framework.ModuleListener;

/**
 * Modules that receive the conscious broadcast must implement this interface. Implementers
 * will receive each winning {@link Coalition} from the {@link GlobalWorkspace}.
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 */
public interface BroadcastListener extends ModuleListener{
	
	/**
	 * Listener must receive a broadcast of a the winning {@link Coalition}
	 * This method should return as possible in order to not delay the rest of the broadcasting.
	 * @param c the {@link Coalition} that won the most recent competition for consciousness
	 */
	public void receiveBroadcast(Coalition c);
	
	/**
	 * A place-holder method to remind implementing classes that 
	 * they should implement learning. LIDA theory says receivers of the broadcast 
	 * should learn from it.
	 * This method will not be called directly by the {@link GlobalWorkspace} 
	 * and thus it should be managed by the receiving module.
	 * @param c the {@link Coalition} that won the most recent competition for consciousness
	 */
	public void learn(Coalition c);
	
}