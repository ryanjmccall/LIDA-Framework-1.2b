/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.pam;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.shared.ElementFactory;
import edu.memphis.ccrg.lida.framework.shared.Link;
import edu.memphis.ccrg.lida.framework.shared.Linkable;
import edu.memphis.ccrg.lida.framework.shared.LinkCategory;
import edu.memphis.ccrg.lida.framework.shared.LinkImpl;
import edu.memphis.ccrg.lida.framework.shared.Node;
import edu.memphis.ccrg.lida.framework.shared.activation.Learnable;
import edu.memphis.ccrg.lida.framework.shared.activation.LearnableImpl;
import edu.memphis.ccrg.lida.framework.strategies.DecayStrategy;
import edu.memphis.ccrg.lida.framework.strategies.ExciteStrategy;
import edu.memphis.ccrg.lida.framework.strategies.TotalActivationStrategy;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * Default implementation of {@link PamLink}.
 * 
 * @author Ryan J. McCall
 */
public class PamLinkImpl extends LinkImpl implements PamLink {
	
	private static final Logger logger = Logger.getLogger(PamLinkImpl.class.getCanonicalName());
	
	/*
	 * Private Learnable object used for all learnable methods
	 */
	private LearnableImpl learnable;
	
	/**
	 * Default constructor
	 * Only {@link ElementFactory} should be creating this Object.
	 */
	public PamLinkImpl() {
		super();
		groundingPamLink = this;
		learnable = new LearnableImpl();
	}
	/**
	 * Copy constructor
	 * @deprecated Use {@link ElementFactory#getLink(String, Node, Linkable, LinkCategory, String, String, double, double)} instead.
	 * @param pl source {@link PamLinkImpl}
	 */
	@Deprecated
	public PamLinkImpl(PamLinkImpl pl) {
		super(pl);
		groundingPamLink = this;
		learnable=new LearnableImpl((LearnableImpl)pl.learnable);
	}

	/** 
	 * Must call the {@link #init()} of the internal {@link Learnable}.
	 * @see LearnableImpl#init()
	 * @see ElementFactory#getLink(String, edu.memphis.ccrg.lida.framework.shared.Node, edu.memphis.ccrg.lida.framework.shared.Linkable, edu.memphis.ccrg.lida.framework.shared.LinkCategory, String, String, double, double)
	 */
	@Override
	public void init(){
		learnable.init(getParameters());
	}

	//LINK
	@Override
	public void updateLinkValues(Link link) {
		if(link instanceof PamLinkImpl){
			PamLinkImpl pl = (PamLinkImpl) link;
			learnable.setBaseLevelActivation(pl.getBaseLevelActivation());           
		}else if(link != null){
			logger.log(Level.FINEST, "Cannot set PamLinkImpl-specific values. Required: {1} \n Received: {2}",
					new Object[]{TaskManager.getCurrentTick(),PamLinkImpl.class.getCanonicalName(),link.getClass()});
		}
	}

	//OBJECT
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof PamLinkImpl){
			PamLinkImpl l = (PamLinkImpl) obj;
			return getExtendedId().equals(l.getExtendedId());
		}
		return false;	
	}
	
	@Override
	public int hashCode() { 
	    return getExtendedId().hashCode();
	}

	//LEARNABLE METHODS
	
	@Override
	public double getActivation() {
		return learnable.getActivation();
	}

	@Override
	public void setActivation(double activation) {
		learnable.setActivation(activation);
	}

	@Override
	public double getTotalActivation() {
		return learnable.getTotalActivation();
	}

	@Override
	public void excite(double amount) {
		learnable.excite(amount);
	}

	@Override
	public synchronized void setExciteStrategy(ExciteStrategy strategy) {
		learnable.setExciteStrategy(strategy);
	}

	@Override
	public ExciteStrategy getExciteStrategy() {
		return learnable.getExciteStrategy();
	}

	@Override
	public void decay(long ticks) {
		learnable.decay(ticks);

	}

	@Override
	public synchronized void setDecayStrategy(DecayStrategy strategy) {
		learnable.setDecayStrategy(strategy);
	}

	@Override
	public DecayStrategy getDecayStrategy() {
		return learnable.getDecayStrategy();
	}

	@Override
	public void setActivatibleRemovalThreshold(double threshold) {
		learnable.setActivatibleRemovalThreshold(threshold);
	}

	@Override
	public double getActivatibleRemovalThreshold() {
		return learnable.getActivatibleRemovalThreshold();
	}

	@Override
	public boolean isRemovable() {
		return learnable.isRemovable();
	}

	@Override
	public double getBaseLevelActivation() {
		return learnable.getBaseLevelActivation();
	}

	@Override
	public void setBaseLevelActivation(double amount) {
		learnable.setBaseLevelActivation(amount);
	}

	@Override
	public void reinforceBaseLevelActivation(double amount) {
		learnable.reinforceBaseLevelActivation(amount);
	}

	@Override
	public void setBaseLevelExciteStrategy(ExciteStrategy strategy) {
		learnable.setBaseLevelExciteStrategy(strategy);
	}

	@Override
	public ExciteStrategy getBaseLevelExciteStrategy() {
		return learnable.getBaseLevelExciteStrategy();
	}

	@Override
	public void decayBaseLevelActivation(long ticks) {
		learnable.decayBaseLevelActivation(ticks);
	}

	@Override
	public void setBaseLevelDecayStrategy(DecayStrategy strategy) {
		learnable.setBaseLevelDecayStrategy(strategy);
	}

	@Override
	public DecayStrategy getBaseLevelDecayStrategy() {
		return learnable.getBaseLevelDecayStrategy();
	}

	@Override
	public void setBaseLevelRemovalThreshold(double threshold) {
		learnable.setBaseLevelRemovalThreshold(threshold);

	}

	@Override
	public double getLearnableRemovalThreshold() {
		return learnable.getLearnableRemovalThreshold();
	}

	@Override
	public TotalActivationStrategy getTotalActivationStrategy() {
		return learnable.getTotalActivationStrategy();
	}

	@Override
	public void setTotalActivationStrategy(TotalActivationStrategy strategy) {
		learnable.setTotalActivationStrategy(strategy);
	}	
}