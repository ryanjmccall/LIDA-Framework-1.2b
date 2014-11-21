/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/

package edu.memphis.ccrg.lida.episodicmemory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cern.colt.bitvector.BitVector;
import edu.memphis.ccrg.lida.episodicmemory.sdm.SparseDistributedMemory;
import edu.memphis.ccrg.lida.episodicmemory.sdm.SparseDistributedMemoryImpl;
import edu.memphis.ccrg.lida.episodicmemory.sdm.Translator;
import edu.memphis.ccrg.lida.framework.FrameworkModuleImpl;
import edu.memphis.ccrg.lida.framework.ModuleListener;
import edu.memphis.ccrg.lida.framework.shared.NodeStructure;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.globalworkspace.BroadcastListener;
import edu.memphis.ccrg.lida.globalworkspace.Coalition;

/**
 * This is the canonical implementation of {@link EpisodicMemory}. It uses a
 * {@link SparseDistributedMemory} to store the information.
 * 
 * @author Javier Snaider
 */
public class EpisodicMemoryImpl extends FrameworkModuleImpl implements
		EpisodicMemory, BroadcastListener, CueListener {

	private static final Logger logger = Logger
			.getLogger(EpisodicMemoryImpl.class.getCanonicalName());

	/**
	 * Default number of hard locations
	 */
	public static final int DEF_HARD_LOCATIONS = 10000;

	/**
	 * Default address length for {@link SparseDistributedMemory}
	 */
	public static final int DEF_ADDRESS_LENGTH = 1000;
	/**
	 * Default word length for {@link SparseDistributedMemory}
	 */
	public static final int DEF_WORD_LENGTH = 1000;
	/**
	 * Default access radius for {@link SparseDistributedMemory}
	 */
	public static final int DEF_ACCESS_RADIUS = 451;

	private SparseDistributedMemory sdm;
	private Translator translator;
	private List<LocalAssociationListener> localAssocListeners = new ArrayList<LocalAssociationListener>();
	private int numOfHardLoc = DEF_HARD_LOCATIONS;
	private int addressLength = DEF_ADDRESS_LENGTH;
	private int wordLength = DEF_WORD_LENGTH;

	/**
	 * Constructs a new object with default values
	 */
	public EpisodicMemoryImpl() {
	}

	/**
	 * Will set parameters with the following names:<br/>
	 * <br/>
	 * 
	 * <b>em.numOfHardLoc</b> number of hard locations created in SDM to store
	 * data<br/>
	 * <b>em.addressLength</b> size of the hard locations' address<br/>
	 * <b>em.wordLength</b> size of the hard locations' words (storage)<br/>
	 * <b>em.activationRadius</b> size of the radius of the hypersphere used to
	 * find the "nearby" hard locations of an address</b>
	 */
	@Override
	public void init() {
		numOfHardLoc = (Integer) getParam("em.numOfHardLoc", DEF_HARD_LOCATIONS);
		addressLength = (Integer) getParam("em.addressLength",
				DEF_ADDRESS_LENGTH);
		wordLength = (Integer) getParam("em.wordLength", DEF_WORD_LENGTH);
		int radius = (Integer) getParam("em.activationRadius",
				DEF_ACCESS_RADIUS);
		sdm = new SparseDistributedMemoryImpl(numOfHardLoc, radius, wordLength,
				addressLength);
	}

	@Override
	public void addListener(ModuleListener listener) {
		if (listener instanceof LocalAssociationListener) {
			localAssocListeners.add((LocalAssociationListener) listener);
		} else {
			logger
					.log(
							Level.WARNING,
							"tried to add listener {0} but LocalAssociationListener is required",
							listener);
		}
	}

	@Override
	public void receiveBroadcast(Coalition coalition) {
		NodeStructure ns = (NodeStructure) coalition.getContent();
		BitVector address = null;
		if (translator != null) {
			try {
				address = translator.translate(ns);
			} catch (Exception e) {
				logger.log(Level.WARNING, "Translation failed {1}",
						new Object[] { TaskManager.getCurrentTick(),
								e.getMessage() });
			}
			sdm.store(address);
		} else {
			logger.log(Level.SEVERE,
					"Translator is null, wasn't set up properly.", TaskManager
							.getCurrentTick());
		}
	}

	/**
	 * Receive a cue as a {@link NodeStructure} In this implementation, first
	 * the cue is translated to a {@link BitVector}.
	 * 
	 * @param ns
	 *            NodeStructure to retrieve
	 * @see Translator
	 */
	@Override
	public void receiveCue(NodeStructure ns) {
		BitVector address = null;
		if (translator != null) {
			try {
				address = translator.translate(ns);
			} catch (Exception e) {
				logger.log(Level.WARNING, "Translation failed {1}",
						new Object[] { TaskManager.getCurrentTick(),
								e.getMessage() });
				return;
			}
			BitVector out = sdm.retrieveIterating(address);
			if (out != null) {// no local association
				NodeStructure result = null;
				try {
					result = translator.translate(out);
				} catch (Exception e) {
					logger.log(Level.WARNING, "Translation failed {1}",
							new Object[] { TaskManager.getCurrentTick(),
									e.getMessage() });
					return;
				}
				if (result.getNodeCount() > 0) {
					sendLocalAssociation(result);
				}
			}
		} else {
			logger.log(Level.SEVERE, "Translator is not defined.", TaskManager
					.getCurrentTick());
		}

	}

	/*
	 * Sends local association to all listeners
	 */
	private void sendLocalAssociation(NodeStructure localAssociation) {
		logger.log(Level.FINER, "Sending Local Association", TaskManager
				.getCurrentTick());
		for (LocalAssociationListener l : localAssocListeners) {
			l.receiveLocalAssociation(localAssociation);
		}
	}

	@Override
	public void learn(Coalition coalition) {
		// TODO implement learning
	}

	/**
	 * Sets the {@link Translator} of this {@link EpisodicMemoryImpl}
	 * 
	 * @param translator
	 *            the {@link Translator} to set
	 */
	public void setTranslator(Translator translator) {
		this.translator = translator;
	}

	/**
	 * Returns the {@link Translator} associated with this
	 * {@link EpisodicMemoryImpl}
	 * 
	 * @return the {@link Translator}
	 */
	public Translator getTranslator() {
		return translator;
	}

	/**
	 * Returns the SDM used in this {@link EpisodicMemory}
	 * 
	 * @return the {@link SparseDistributedMemory}
	 */
	public SparseDistributedMemory getSdm() {
		return sdm;
	}

	@Override
	public void decayModule(long ticks) {
	}
}