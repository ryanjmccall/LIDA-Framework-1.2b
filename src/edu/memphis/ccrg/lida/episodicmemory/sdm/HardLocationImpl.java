/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.episodicmemory.sdm;

import java.util.logging.Level;
import java.util.logging.Logger;

import cern.colt.bitvector.BitVector;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * Default implementation of {@link HardLocation}.
 * 
 * @author Javier Snaider
 */
public class HardLocationImpl implements HardLocation {
	
	private static final Logger logger = Logger.getLogger(HardLocationImpl.class.getCanonicalName());
	private static final byte DEFAULT_COUNTER_MAX = 40;
	private final byte counterMax = DEFAULT_COUNTER_MAX;
	
	private BitVector address;
	private int wordLength;
	private byte[] counters;
	private int writeCount;

	/**
	 * Constructs a new hard location with specified address and length.
	 * @param address {@link BitVector}
	 * @param wordLength length of the words
	 */
	public HardLocationImpl(BitVector address, int wordLength) {
		this.address = address;
		this.wordLength = wordLength;
		counters = new byte[wordLength];
	}

	/**
	 * Constructs a new hard location with specified address
	 * @param address {@link BitVector} address of this HardLocation
	 */
	public HardLocationImpl(BitVector address) {
		this(address, address.size());
	}

	@Override
	public BitVector getAddress() {
		return address;
	}

	@Override
	public void setAddress(BitVector address) {
		this.address = address;
	}

	@Override
	public byte[] getCounters() {
		return counters;
	}

	@Override
	public int getWriteCount() {
		return writeCount;
	}

	@Override
	public void write(BitVector word) {
		writeCount++;
		int size = word.size();

		// if (size>wordLength){
		// throw new IllegalArgumentException();
		// }
		for (int j = 0; j < size; j++) {
			if (word.getQuick(j)) {
				if (counters[j] < counterMax) {
					counters[j] += 1;
				}
			} else {
				if (counters[j] > -counterMax) {
					counters[j] += -1;
				}
			}
		}
	}

	@Override
	public void setCounters(byte[] newCounters) {
		for (int i = 0; i < this.wordLength; i++) {
			counters[i] = newCounters[i];
		}
	}

	@Override
	public int[] read(int[] buff) {

		// if (buff.length<wordLength){
		// throw new IllegalArgumentException();
		// }

		for (int i = 0; i < wordLength; i++) {
//			int inc=0;
			buff[i] += Integer.signum(counters[i]);
		}
		return buff;
	}

	@Override
	public int hammingDistance(BitVector vector) {
		if(vector == null){
			logger.log(Level.WARNING,"The vector can not be null.",TaskManager.getCurrentTick());			return Integer.MAX_VALUE;
		}
		
		BitVector aux = vector.copy();
		aux.xor(address);

		return aux.cardinality();
	}

}
