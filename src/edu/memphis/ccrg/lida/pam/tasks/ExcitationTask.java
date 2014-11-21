/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.pam.tasks;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.tasks.FrameworkTask;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTaskImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.pam.PamLinkable;
import edu.memphis.ccrg.lida.pam.PamNode;
import edu.memphis.ccrg.lida.pam.PerceptualAssociativeMemory;

/**
 * A task which performs the excitation of a single {@link PamNode}.
 * 
 * @see PerceptualAssociativeMemory#receiveExcitation(PamLinkable, double)
 * 
 * @author Ryan J. McCall
 * 
 */
public class ExcitationTask extends FrameworkTaskImpl {

	private static final Logger logger = Logger.getLogger(ExcitationTask.class
			.getCanonicalName());

	/*
	 * PamNode to be excited
	 */
	private PamNode node;

	/*
	 * Amount to excite
	 */
	private double excitationAmount;

	/*
	 * Used to make another excitation call
	 */
	private PerceptualAssociativeMemory pam;

	/**
	 * Instantiates a new excitation task to excite supplied {@link PamNode}
	 * specified amount.
	 * 
	 * @param ticksPerRun
	 *            the ticks per run
	 * @param n
	 *            to be excited
	 * @param excitation
	 *            amount to excite
	 * @param pam
	 *            PerceptualAssociativeMemory module
	 */
	public ExcitationTask(int ticksPerRun, PamNode n, double excitation,PerceptualAssociativeMemory pam) {
		super(ticksPerRun);
		node = n;
		excitationAmount = excitation;
		this.pam = pam;
	}

	/**
	 * This method first excites the {@link PamNode}, if this puts the {@link PamNode}
	 * over the percept threshold it creates an {@link AddNodeToPerceptTask} to
	 * add it to the percept. In either case it calls
	 * {@link PerceptualAssociativeMemory#propagateActivationToParents(PamNode)}
	 * to pass the node's activation, then the tasks finishes.
	 */
	@Override
	protected void runThisFrameworkTask() {
		node.excite(excitationAmount);
		if (pam.isOverPerceptThreshold(node)) {
			if (logger.isLoggable(Level.FINEST)) {
				logger.log(Level.FINEST, "PamNode {1} over threshold",
						new Object[] { TaskManager.getCurrentTick(), node });
			}
			FrameworkTask task = new AddNodeToPerceptTask(node, pam);
			pam.getAssistingTaskSpawner().addTask(task);
		}
		pam.propagateActivationToParents(node);
		cancel();
	}
}