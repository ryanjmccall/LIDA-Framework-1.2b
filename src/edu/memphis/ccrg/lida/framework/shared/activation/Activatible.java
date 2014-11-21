/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.shared.activation;

import edu.memphis.ccrg.lida.framework.initialization.Initializable;
import edu.memphis.ccrg.lida.framework.strategies.DecayStrategy;
import edu.memphis.ccrg.lida.framework.strategies.ExciteStrategy;

/**
 * An object with activation, has strategies 
 * to both excite and decay this activation
 * 
 * @author Ryan J. McCall
 *
 */
public interface Activatible extends Initializable {
	
	/**
	 * Default removal threshold for {@link Activatible} 
	 */
	public static final double DEFAULT_ACTIVATIBLE_REMOVAL_THRESHOLD = 0.0;
	
	/**
	 * Default activation for {@link Activatible} 
	 */
	public static final double DEFAULT_ACTIVATION = 0.0;
		
	/**
	 * Returns the current activation of this activatible
	 * @return the current activation.
	 */
	public double getActivation();
	
	/**
	 * Set the current activation. Used for initialization, not during regular execution, use excite instead.
	 * @param activation new activation
	 */
    public void setActivation(double activation);
    /**
     * Returns the total activation of this activatible
     * @return The total activation. 
     * It should return the current activation if no base activation is used.
     */
	public double getTotalActivation();
    
	/**
	 * The current activation of this node is increased 
	 * using the excitation value as a parameter for the ExciteStrategy 
	 * 
	 * @param   amount the value to be used to increase the current activation of
	 *          this node
	 */
    public void excite(double amount); 
    /**
     * Sets the excite strategy
     * @param strategy the Excite strategy for the current activation.
     */
	public void setExciteStrategy(ExciteStrategy strategy);
	/**
	 * Gets the excite strategy
	 * @return the excite strategy
	 */
	public ExciteStrategy getExciteStrategy();
	
	/**
	 * decay the current activation using the decay strategy. The decay depends on 
	 * the time since the last decaying. It is indicated by the parameter ticks.
	 * 
	 * @param ticks the number of ticks to decay
	 */
	public void decay(long ticks);	

    /**
     * Sets the decay strategy
     * @param strategy the decay strategy for the current activation.
     */
	public void setDecayStrategy(DecayStrategy strategy);
	
	/**
	 * Gets the decay strategy
	 * @return the decay strategy.
	 */
	public DecayStrategy getDecayStrategy();
	
	/**
	 * Sets activatibleRemovalThreshold
	 * @param threshold threshold for removal of this activatible
	 */
	public void setActivatibleRemovalThreshold(double threshold);
	
	/**
	 * Gets activatibleRemovalThreshold
	 * @return threshold for removal of this activatible
	 */
	public double getActivatibleRemovalThreshold();
	
	/**
	 * Returns true if this Activatible is removable
	 * @return true if activation is less than activatibleRemovalThreshold
	 */
	public boolean isRemovable();

}