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
 * Implementation of Kanerva's sparse distributed memory. This implementation is
 * based on the model described in P. Kanerva, "Sparse Distributed Memory and
 * Related Models" in <i>Associative Neural Memories: Theory and Implementation
 * </i>, pp. 50-76, Oxford University Press, 1993.
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 */
public class SparseDistributedMemoryImpl implements SparseDistributedMemory {

	private static final Logger logger = Logger
			.getLogger(SparseDistributedMemoryImpl.class.getCanonicalName());
	
	/*
	 * Maximum iterations allowed in determining whether a retrieval converges 
	 */
	private static final int MAX_ITERATIONS = 20;
	
	/*
	 * The hard locations that store data in this sdm
	 */
	private HardLocation[] hardLocations;
	
	/*
	 * Number of hard locations in this sdm 
	 */
	private int memorySize;
	
	/*
	 * Size of vectors stored in the hard locations
	 */
	private int wordLength;
	
	/*
	 * Size of the address vectors 
	 */
	private int addressLength;
	
	/*
	 * Size of the radius of the hypersphere used to find the "nearby" hard locations of an address 
	 */
	private int activationRadius;

	/**
	 * Constructs a new {@link SparseDistributedMemory} with equal address and word sizes 
	 * 
	 * @param memorySize
	 *            number of hard locations
	 * @param radius
	 *            the activation radius used to find the nearest hard locations
	 * @param wordLength
	 *            the word size and the address size
	 */
	public SparseDistributedMemoryImpl(int memorySize, int radius,
			int wordLength) {
		this.memorySize = memorySize;
		hardLocations = new HardLocation[memorySize];
		for (int i = 0; i < memorySize; i++) {
			hardLocations[i] = new HardLocationImpl(
					BitVectorUtils.getRandomVector(wordLength));
		}
		
		this.activationRadius = radius;
		this.wordLength = wordLength;
		this.addressLength = wordLength;
	}

	/**
	 * Constructs a new {@link SparseDistributedMemory} with specified parameters
	 * 
	 * @param memorySize
	 *            the number of hard location
	 * @param radius
	 *            the activation radius used to find the nearest hard locations
	 * @param wordLength
	 *            size of vectors stored at hard locations
	 * @param addrLength
	 *            the address size
	 */
	public SparseDistributedMemoryImpl(int memorySize, int radius,
			int wordLength, int addrLength) {
		this.memorySize = memorySize;
		hardLocations = new HardLocation[memorySize];
		for (int i = 0; i < memorySize; i++) {
			hardLocations[i] = new HardLocationImpl(
					BitVectorUtils.getRandomVector(addrLength), wordLength);
		}
		
		this.activationRadius = radius;
		this.wordLength = wordLength;
		this.addressLength = addrLength;
	}

	@Override
	public void store(BitVector wrd, BitVector addr) {
		for (int i = 0; i < memorySize; i++) {
			if (hardLocations[i].hammingDistance(addr) <= activationRadius) {
				hardLocations[i].write(wrd);
			}
		}
	}

	@Override
	public void store(BitVector wrd) {
		store(wrd, wrd);
	}

	@Override
	public void mappedStore(BitVector wrd, BitVector mapping) {
		if (wrd.size() == addressLength) {
			BitVector mapped = wrd.copy();
			mapped.xor(mapping);
			store(mapped);
		} else {
			BitVector mapped = wrd.partFromTo(0, addressLength - 1);
			mapped.xor(mapping);
			BitVector aux = wrd.copy();
			aux.replaceFromToWith(0, addressLength - 1, mapped, 0);
			store(aux, mapped);
		}
	}

	@Override
	public BitVector retrieve(BitVector addr) {
		int[] locationsSum = new int[wordLength];
		for (int i = 0; i < memorySize; i++) {
			if (hardLocations[i].hammingDistance(addr) <= activationRadius) {
				hardLocations[i].read(locationsSum);
			}
		}
		BitVector res = new BitVector(wordLength);
		for (int i = 0; i < wordLength; i++) {
			boolean aux;
			if (locationsSum[i]==0) {
				//not clear if sum is positive or negative, so assign randomly
				aux = (Math.random() > 0.5);
			} else {
				aux = (locationsSum[i] > 0);
			}
			res.putQuick(i, aux);
		}
		return res;
	}

	@Override
	public BitVector retrieve(BitVector addr, BitVector mapping) {
		BitVector mapped = addr.copy();
		mapped.xor(mapping);
		BitVector res = retrieve(mapped);
		if (res != null) {
			if (res.size() == addressLength) {
				res.xor(mapping);
			} else {
				BitVector aux = res.partFromTo(0, addressLength - 1);
				aux.xor(mapping);
				res.replaceFromToWith(0, addressLength - 1, aux, 0);
			}
		}
		return res;
	}

	@Override
	public BitVector retrieveIterating(BitVector addr) {
		BitVector res = null;
		for (int i = 1; i < MAX_ITERATIONS; i++) {
			res = retrieve(addr);
			BitVector aux = res.partFromTo(0, addr.size() - 1);
			// TODO hamming distance tolerance instead of strict equality
			if (aux.equals(addr)) {
				logger.log(Level.FINER, "number of iterations: {1}", 
						new Object[]{TaskManager.getCurrentTick(), i});
				return res;
			}
			addr = aux;
		}
		return null;
	}

	@Override
	public BitVector retrieveIterating(BitVector addr, BitVector mapping) {
		BitVector mapped = addr.copy();
		mapped.xor(mapping);

		BitVector res = retrieveIterating(mapped);
		if (res != null) {
			if (res.size() == addressLength) {
				res.xor(mapping);
			} else {
				BitVector aux = res.partFromTo(0, addressLength - 1);
				aux.xor(mapping);
				res.replaceFromToWith(0, addressLength - 1, aux, 0);
			}
		}
		return res;
	}

}