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

import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.initialization.GlobalInitializer;
import edu.memphis.ccrg.lida.framework.initialization.Initializable;
import edu.memphis.ccrg.lida.framework.shared.Linkable;
import edu.memphis.ccrg.lida.framework.tasks.FrameworkTaskImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.pam.PamLinkable;
import edu.memphis.ccrg.lida.pam.PamNode;
import edu.memphis.ccrg.lida.pam.PerceptualAssociativeMemory;
import edu.memphis.ccrg.lida.sensorymemory.SensoryMemory;

/**
 * This class implements the FeatureDetector interface and provides default
 * methods. Users should extend this class and overwrite the detect() and
 * excitePam() methods. A convenience init() method is added to initialize the
 * class. This method can be overwritten as well. This implementation is
 * oriented to detect features from sensoryMemory, but the implementation can be
 * used to detect and burstActivation from other modules, like Workspace,
 * emotions or internal states.
 * 
 * @author Ryan J. McCall
 * @author Javier Snaider
 * 
 */
public abstract class BasicDetectionAlgorithm extends FrameworkTaskImpl
		implements DetectionAlgorithm {

	private static final Logger logger = Logger
			.getLogger(BasicDetectionAlgorithm.class.getCanonicalName());

	/**
	 * the {@link SensoryMemory}
	 */
	protected SensoryMemory sensoryMemory;
	/**
	 * the {@link PerceptualAssociativeMemory}
	 */
	protected PerceptualAssociativeMemory pam;
	/**
	 * {@link PamLinkable} this algorithm detects
	 */
	protected PamLinkable linkable;

	/**
	 * Default constructor. Associated {@link Linkable},
	 * {@link PerceptualAssociativeMemory} and {@link SensoryMemory} must be set
	 * using setters.
	 */
	public BasicDetectionAlgorithm() {
	}

	@Override
	public void setAssociatedModule(FrameworkModule module, String moduleUsage) {
		if (module instanceof PerceptualAssociativeMemory) {
			pam = (PerceptualAssociativeMemory) module;
		} else if (module instanceof SensoryMemory) {
			sensoryMemory = (SensoryMemory) module;
		} else {
			logger.log(Level.WARNING, "Cannot set associated module {1}",
					new Object[] { TaskManager.getCurrentTick(), module });
		}
	}

	@Override
	public void setPamLinkable(PamLinkable linkable) {
		this.linkable = linkable;
	}

	@Override
	public PamLinkable getPamLinkable() {
		return linkable;
	}

	/**
	 * This task can be initialized with the following parameters:<br><br/>
	 * 
	 * <b>node type=string</b>label of the Node in {@link PerceptualAssociativeMemory} this algorithm detects<br/>
	 * 
	 * @see Initializable
	 */
	@Override
	public void init() {
		super.init();
		String nodeLabel = (String) getParam("node", "");
		if (nodeLabel != null) {
			nodeLabel = nodeLabel.trim();
			PamNode node = (PamNode) GlobalInitializer.getInstance()
					.getAttribute(nodeLabel);
			if (node != null) {
				setPamLinkable(node);
			} else {
				logger.log(Level.WARNING,
								"could not get node {1} from global initializer",
								new Object[] { TaskManager.getCurrentTick(),
										nodeLabel });
			}
		}
	}

	@Override
	protected void runThisFrameworkTask() {
		double amount = detect();
		if (logger.isLoggable(Level.FINEST)) {
			logger
					.log(Level.FINEST, "detection performed {1}: {2}",
							new Object[] { TaskManager.getCurrentTick(), this,
									amount });
		}
		if (amount > 0.0) {
			if (logger.isLoggable(Level.FINEST)) {
				logger.log(Level.FINEST, "Pam excited: {1} by {2}",
						new Object[] { TaskManager.getCurrentTick(), amount,
								this });
			}
			pam.receiveExcitation(linkable, amount);
		}
	}

	/**
	 * Override this method implementing feature detection algorithm.
	 * 
	 * @return degree [0,1] to which the feature was detected
	 */
	@Override
	public abstract double detect();

}