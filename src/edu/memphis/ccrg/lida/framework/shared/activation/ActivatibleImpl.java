/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.shared.activation;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.initialization.InitializableImpl;
import edu.memphis.ccrg.lida.framework.shared.ElementFactory;
import edu.memphis.ccrg.lida.framework.shared.Node;
import edu.memphis.ccrg.lida.framework.strategies.DecayStrategy;
import edu.memphis.ccrg.lida.framework.strategies.ExciteStrategy;
import edu.memphis.ccrg.lida.framework.tasks.Codelet;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * Generic {@link Activatible} Implementation. Useful for classes to extend from it, 
 * e.g. {@link Node} and {@link Codelet}.
 *  
 * @author Javier Snaider
 * 
 */
public class ActivatibleImpl extends InitializableImpl implements Activatible {
	
	private static final Logger logger = Logger.getLogger(ActivatibleImpl.class.getCanonicalName());
	private static final ElementFactory factory = ElementFactory.getInstance();

	private ExciteStrategy exciteStrategy;
	private DecayStrategy decayStrategy;
	private double activation;
	private double removalThreshold;

	/**
	 * Default constructor
	 */
	public ActivatibleImpl() {
		activation = DEFAULT_ACTIVATION;
		removalThreshold = DEFAULT_ACTIVATIBLE_REMOVAL_THRESHOLD;
		decayStrategy = factory.getDefaultDecayStrategy();
		exciteStrategy = factory.getDefaultExciteStrategy();
	}
	
	/**
	 * @deprecated To be removed in the future. 
	 * @param activation initial activation
	 * @param removalThreshold initial removableThreshold
	 * @param excite {@link ExciteStrategy}
	 * @param decay {@link DecayStrategy}
	 */
	@Deprecated
	public ActivatibleImpl(double activation, double removalThreshold, ExciteStrategy excite, DecayStrategy decay) {
		this.activation = activation;
		this.removalThreshold = removalThreshold;
		this.exciteStrategy = excite;
		this.decayStrategy = decay;
	}

	@Override
	public void decay(long ticks) {	
		if (decayStrategy != null) {
			if(logger.isLoggable(Level.FINEST)){
				logger.log(Level.FINEST, "Before decaying {1} has current activation: {2}",
							new Object[]{TaskManager.getCurrentTick(),this,getActivation()});
			}
			synchronized(this){
				activation = decayStrategy.decay(getActivation(),ticks);
			}
			if(logger.isLoggable(Level.FINEST)){
				logger.log(Level.FINEST, "After decaying {1} has current activation: {2}",
							new Object[]{TaskManager.getCurrentTick(),this,getActivation()});
			}
		}
	}

	@Override
	public void excite(double excitation) {	
		if (exciteStrategy != null) {
			if(logger.isLoggable(Level.FINEST)){
				logger.log(Level.FINEST, "Before excitation {1} has current activation: {2}",
							new Object[]{TaskManager.getCurrentTick(),this,getActivation()});
			}
			synchronized(this){
				activation = exciteStrategy.excite(getActivation(), excitation);
			}
			if(logger.isLoggable(Level.FINEST)){
				logger.log(Level.FINEST, "After excitation {1} has current activation: {2}",
							new Object[]{TaskManager.getCurrentTick(),this,getActivation()});
			}
		}
	}

	@Override
	public double getActivation() {
		return activation;
	}
	
	@Override
	public double getActivatibleRemovalThreshold() {
		return removalThreshold;
	}

	@Override
	public DecayStrategy getDecayStrategy() {
		return decayStrategy;
	}

	@Override
	public ExciteStrategy getExciteStrategy() {
		return exciteStrategy;
	}

	@Override
	public void setActivation(double a) {
		if(a > 1.0){
			synchronized(this){
				activation = 1.0;
			}
		}else if(a < 0.0){
			synchronized(this){
				activation = 0.0;
			}
		}else{
			synchronized(this){
				activation = a;
			}
		}
	}
	
	@Override
	public void setActivatibleRemovalThreshold(double threshold) {
		if(threshold > 1.0){
			synchronized(this){
				removalThreshold = 1.0;
			}
		}else{
			synchronized(this){
				removalThreshold = threshold;
			}
		}
	}

	@Override
	public void setDecayStrategy(DecayStrategy s) {
		decayStrategy = s;
	}

	@Override
	public void setExciteStrategy(ExciteStrategy s) {
		exciteStrategy = s;
	}
	
	@Override
	public double getTotalActivation() {
		return getActivation();
	}

	@Override
	public boolean isRemovable() {
		return getActivation() <= removalThreshold;
	}

}
