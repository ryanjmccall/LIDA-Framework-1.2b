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
import edu.memphis.ccrg.lida.framework.shared.Node;
import edu.memphis.ccrg.lida.framework.shared.NodeImpl;
import edu.memphis.ccrg.lida.framework.shared.activation.Learnable;
import edu.memphis.ccrg.lida.framework.shared.activation.LearnableImpl;
import edu.memphis.ccrg.lida.framework.strategies.DecayStrategy;
import edu.memphis.ccrg.lida.framework.strategies.ExciteStrategy;
import edu.memphis.ccrg.lida.framework.strategies.TotalActivationStrategy;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * Default implementation of {@link PamNode}.  A {@link Learnable} {@link Node} that
 * overrides hashCode and equals.  Has a private {@link LearnableImpl} to help 
 * implement all {@link Learnable} methods.
 * @author Ryan J. McCall
 */
public class PamNodeImpl extends NodeImpl implements PamNode {

    private static final Logger logger = Logger.getLogger(PamNodeImpl.class.getCanonicalName());
    /*
     * Private Learnable object used for all learnable methods
     */
    private LearnableImpl learnable;

    /**
     * Default constructor
     */
    public PamNodeImpl() {
        super();
        groundingPamNode = this;
        learnable = new LearnableImpl();
    }

    /**
     * Copy constructor
     * @deprecated Use {@link ElementFactory#getNode(Node, String)} or similar method in {@link ElementFactory} instead.
     * @param pn source {@link PamNodeImpl}
     */
    @Deprecated
    public PamNodeImpl(PamNodeImpl pn) {
        super(pn);
        groundingPamNode = this;
        learnable = new LearnableImpl(pn.learnable);
    }

    /** 
     * Must call the {@link #init()} of the internal {@link Learnable}.
     * @see LearnableImpl#init()
     * @see ElementFactory#getNode(String, String, String, String, double, double)
     */
    @Override
    public void init() {
        learnable.init(getParameters());
    }

    @Override
    public void updateNodeValues(Node n) {
        if (n instanceof PamNodeImpl) {
            PamNodeImpl pn = (PamNodeImpl) n;
            learnable.setBaseLevelActivation(pn.getBaseLevelActivation());   
        } else {
            logger.log(Level.FINEST, "Cannot set PamNodeImpl-specific values. Required: {1} \n Received: {2}",
                    new Object[]{TaskManager.getCurrentTick(), PamNodeImpl.class.getCanonicalName(), n});
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof PamNodeImpl) {
            return getId() == ((PamNodeImpl) o).getId();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getId();
    }

    //LEARNABLE METHODS
    @Override
    public double getActivation() {
        return learnable.getActivation();
    }

    @Override
    public void setActivation(double a) {
        learnable.setActivation(a);
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