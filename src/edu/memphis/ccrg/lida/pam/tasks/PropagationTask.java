/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.pam.tasks;

import edu.memphis.ccrg.lida.framework.tasks.FrameworkTaskImpl;
import edu.memphis.ccrg.lida.pam.PamLink;
import edu.memphis.ccrg.lida.pam.PamLinkable;
import edu.memphis.ccrg.lida.pam.PamNode;
import edu.memphis.ccrg.lida.pam.PerceptualAssociativeMemory;

/**
 * A task which propagates an amount of activation
 * along a {@link PamLink} to its sink.  
 *
 * @author Ryan J. McCall
 */
public class PropagationTask extends FrameworkTaskImpl {
		
	private PamLinkable sink;
	private PamLink link;
	private double excitationAmount;
	private PerceptualAssociativeMemory pam;

	/**
	 * Default constructor.
	 * @param ticksPerRun task's ticks per run
	 * 
	 * @param link
	 *            the link from the source to the parent
	 * @param amount
	 *            the amount to excite
	 * @param pam
	 *            the pam
	 */
	public PropagationTask(int ticksPerRun, PamLink link, double amount,
						   PerceptualAssociativeMemory pam) {
		super(ticksPerRun);
		this.link = link;
		this.sink = (PamLinkable) link.getSink();
		this.excitationAmount = amount;
		this.pam = pam;	
	}

	/**
	 * Excites the {@link PamLink} specified amount. Excites link's sink based
	 * on link's new activation. If this puts sink over its percept threshold
	 * then both Link and sink will be send as a percept. Calls
	 * {@link PerceptualAssociativeMemory#propagateActivationToParents(PamNode)}
	 * with sink and finishes. 
	 */
	@Override
	protected void runThisFrameworkTask() {
		link.setActivation(excitationAmount);
		sink.excite(excitationAmount * link.getBaseLevelActivation());
		if(pam.isOverPerceptThreshold(sink)){
			AddLinkToPerceptTask task = new AddLinkToPerceptTask(link, pam);
			pam.getAssistingTaskSpawner().addTask(task);
		}
		if(sink instanceof PamNode){
			pam.propagateActivationToParents((PamNode) sink);
		}
		cancel();
	}
}
