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
package edu.memphis.ccrg.lida.framework.shared.activation;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.initialization.Initializable;
import edu.memphis.ccrg.lida.framework.shared.ElementFactory;
import edu.memphis.ccrg.lida.framework.strategies.DecayStrategy;
import edu.memphis.ccrg.lida.framework.strategies.DefaultTotalActivationStrategy;
import edu.memphis.ccrg.lida.framework.strategies.ExciteStrategy;
import edu.memphis.ccrg.lida.framework.strategies.TotalActivationStrategy;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * Default implementation of {@link Learnable}.
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 *
 */
public class LearnableImpl extends ActivatibleImpl implements Learnable {

	private static final Logger logger = Logger.getLogger(LearnableImpl.class.getCanonicalName());
	private static final ElementFactory factory = ElementFactory.getInstance();
	
	private double baseLevelActivation;
	private double learnableRemovalThreshold;
	private ExciteStrategy baseLevelExciteStrategy;
	private DecayStrategy baseLevelDecayStrategy;
	private TotalActivationStrategy totalActivationStrategy;
	private static final String DEFAULT_TOTAL_ACTIVATION_TYPE = DefaultTotalActivationStrategy.class.getSimpleName();

	/**
	 * Constructs a new instance with default values.
	 */
	public LearnableImpl() {
		super();
		baseLevelActivation = DEFAULT_BASE_LEVEL_ACTIVATION;
		learnableRemovalThreshold = DEFAULT_LEARNABLE_REMOVAL_THRESHOLD;
		baseLevelDecayStrategy = factory.getDefaultDecayStrategy();
		baseLevelExciteStrategy = factory.getDefaultExciteStrategy();
		totalActivationStrategy = (TotalActivationStrategy) factory.getStrategy(DEFAULT_TOTAL_ACTIVATION_TYPE);
	}
	
	/**
	 * Copy constructor.
	 * @deprecated This functionality is subsumed by {@link ElementFactory}.
	 * @param l {@link LearnableImpl}
	 */
	@Deprecated
	public LearnableImpl(LearnableImpl l) {
		this(l.getActivation(), l.getActivatibleRemovalThreshold(),  l.getBaseLevelActivation(), l.getLearnableRemovalThreshold(),
			l.getExciteStrategy(), l.getDecayStrategy(),l.getBaseLevelExciteStrategy(), l.getBaseLevelDecayStrategy(), l.getTotalActivationStrategy());
	}
	
	/**
	 * Constructs a new instance with specified attributes.
	 * @deprecated This functionality is subsumed by {@link ElementFactory}.
	 * @param activation current activation
	 * @param activatibleRemovalThreshold activation threshold needed for this instance to remain active
	 * @param baseLevelActivation base-level activation for learning
	 * @param learnableRemovalThreshold base-level activation needed for this instance to remain active
	 * @param exciteStrategy {@link ExciteStrategy} for exciting {@link ActivatibleImpl} activation.
	 * @param decayStrategy {@link DecayStrategy} for decaying {@link ActivatibleImpl} activation.
	 * @param baseLevelExciteStrategy {@link ExciteStrategy} for reinforcing {@link LearnableImpl} base-level activation.
	 * @param baseLevelDecayStrategy {@link DecayStrategy} for decaying {@link LearnableImpl} base-level activation.
	 * @param taStrategy {@link TotalActivationStrategy} how this instance will calculate its total activation.
	 */
	@Deprecated
	public LearnableImpl(double activation, double activatibleRemovalThreshold, double baseLevelActivation, double learnableRemovalThreshold,
			ExciteStrategy exciteStrategy, DecayStrategy decayStrategy, ExciteStrategy baseLevelExciteStrategy, DecayStrategy baseLevelDecayStrategy, TotalActivationStrategy taStrategy) {
		super(activation, activatibleRemovalThreshold, exciteStrategy, decayStrategy);
		
		this.baseLevelActivation = baseLevelActivation;
		this.learnableRemovalThreshold=learnableRemovalThreshold;
		this.baseLevelExciteStrategy = baseLevelExciteStrategy;
		this.baseLevelDecayStrategy = baseLevelDecayStrategy;
		this.totalActivationStrategy = taStrategy;
	}
	
	/**
	 * If this method is overridden, this init() must be called first! i.e. super.init();
	 * Will set parameters with the following names:<br/><br/>
     * 
     * <b>learnable.baseLevelActivation</b> initial base-level activation<br/>
     * <b>learnable.baseLevelRemovalThreshold</b> initial removal threshold<br/>
     * <b>learnable.baseLevelDecayStrategy</b> name of base-level decay strategy<br/>
     * <b>learnable.baseLevelExciteStrategy</b> name of base-level excite strategy<br/>
     * <b>learnable.totalActivationStrategy</b> name of total activation strategy<br/><br/>
     * If any parameter is not specified its default value will be used.
     * 
     * @see Initializable
	 */
	@Override
	public void init(){
		baseLevelActivation = (Double) getParam("learnable.baseLevelActivation",DEFAULT_BASE_LEVEL_ACTIVATION);
		learnableRemovalThreshold = (Double) getParam("learnable.baseLevelRemovalThreshold", DEFAULT_LEARNABLE_REMOVAL_THRESHOLD);
		String decayName = (String) getParam("learnable.baseLevelDecayStrategy", factory.getDefaultDecayType());
		baseLevelDecayStrategy = factory.getDecayStrategy(decayName);
		
		String exciteName = (String) getParam("learnable.baseLevelExciteStrategy", factory.getDefaultExciteType());
		baseLevelExciteStrategy = factory.getExciteStrategy(exciteName);
		
		String totalActivationName = (String) getParam("learnable.totalActivationStrategy", DEFAULT_TOTAL_ACTIVATION_TYPE);
		totalActivationStrategy = (TotalActivationStrategy) factory.getStrategy(totalActivationName);
		if(totalActivationStrategy == null){
			totalActivationStrategy = (TotalActivationStrategy) factory.getStrategy(DEFAULT_TOTAL_ACTIVATION_TYPE);
		}
	}

	@Override
	public void decay(long ticks){
		decayBaseLevelActivation(ticks);
		super.decay(ticks);
	}
	
	@Override
	public boolean isRemovable() {
		return getBaseLevelActivation() <= learnableRemovalThreshold;
	}

	@Override
	public double getTotalActivation() { 
	    return totalActivationStrategy.calculateTotalActivation(getBaseLevelActivation(), getActivation());
	}

	@Override
	public void decayBaseLevelActivation(long ticks) {
		if (baseLevelDecayStrategy != null) {
			if(logger.isLoggable(Level.FINEST)){
				logger.log(Level.FINEST, "Before decaying {1} has base-level activation: {2}",
							new Object[]{TaskManager.getCurrentTick(),this,getBaseLevelActivation()});
			}
			synchronized(this){
				baseLevelActivation = baseLevelDecayStrategy.decay(getBaseLevelActivation(),ticks);
			}
			if(logger.isLoggable(Level.FINEST)){
				logger.log(Level.FINEST, "After decaying {1} has base-level activation: {2}",
							new Object[]{TaskManager.getCurrentTick(),this,getBaseLevelActivation()});
			}
		}		
	}
	
	@Override
	public void reinforceBaseLevelActivation(double amount) {
		if (baseLevelExciteStrategy != null) {
			if(logger.isLoggable(Level.FINEST)){
				logger.log(Level.FINEST, "Before reinforcement {1} has base-level activation: {2}",
							new Object[]{TaskManager.getCurrentTick(),this,getBaseLevelActivation()});
			}
			synchronized(this){
				baseLevelActivation = baseLevelExciteStrategy.excite(getBaseLevelActivation(), amount);
			}
			if(logger.isLoggable(Level.FINEST)){
				logger.log(Level.FINEST, "After reinforcement {1} has base-level activation: {2}",
							new Object[]{TaskManager.getCurrentTick(),this,getBaseLevelActivation()});
			}
		}		
	}

	@Override
	public ExciteStrategy getBaseLevelExciteStrategy() {
		return baseLevelExciteStrategy;
	}

	@Override
	public void setBaseLevelExciteStrategy(ExciteStrategy s) {
		baseLevelExciteStrategy = s;
	}

	@Override
	public DecayStrategy getBaseLevelDecayStrategy() {
		return baseLevelDecayStrategy;
	}

	@Override
	public void setBaseLevelDecayStrategy(DecayStrategy s) {
		baseLevelDecayStrategy = s;
	}

	@Override
	public void setBaseLevelActivation(double a) {
		if(a < 0.0){
			synchronized (this) {
				baseLevelActivation = 0.0;
			}
		}else if(a > 1.0){
			synchronized (this) {
				baseLevelActivation = 1.0;
			}
		}else{
			synchronized (this) {
				baseLevelActivation = a;
			}
		}		
	}
	
	@Override
	public double getBaseLevelActivation() {
		return baseLevelActivation;
	}

	@Override
	public double getLearnableRemovalThreshold() {
		return learnableRemovalThreshold;
	}

	@Override
	public void setBaseLevelRemovalThreshold(double t) {
		learnableRemovalThreshold = t;
	}
	
	@Override
	public TotalActivationStrategy getTotalActivationStrategy() {
		return totalActivationStrategy;
	}

	@Override
	public void setTotalActivationStrategy(TotalActivationStrategy s) {
		totalActivationStrategy = s;
	}
}