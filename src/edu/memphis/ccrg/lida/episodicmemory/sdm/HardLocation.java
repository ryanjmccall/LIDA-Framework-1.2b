/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.episodicmemory.sdm;

import cern.colt.bitvector.BitVector;

/**
 * Hard locations are basic components of {@link SparseDistributedMemory}. Each has 
 * a unique address that consists of a {@link BitVector} of size equal to that of the {@link SparseDistributedMemory}.
 * It is composed of multiple counters (one for each dimension of the space). A HardLocation can 
 * be written to or read from. 
 * 
 * @author Javier Snaider
 */
public interface HardLocation {

	/**
	 * Gets address
	 * @return the address of this HardLocation
	 */
	public BitVector getAddress();

	/**
	 * Sets the address of this HardLocation in the vector space
	 * @param address the {@link BitVector} address to set
	 */
	public void setAddress(BitVector address);

	/**
	 * Returns the counters of this HardLocation 
	 * Each HardLocation has wordSize counters. Each counter is byte size.
	 * Counters are incremented or decremented when the memory is written.
	 * @return the counters of this HardLocation 
	 */
	public byte[] getCounters();

	/**
	 * Sets the counters of this HardLocation 
	 * Each HardLocation has wordSize counters. Each counter is byte size.
	 * Counters are incremented or decremented when the memory is written.
	 * @param newCounters new counters
	 */
	public void setCounters(byte[] newCounters);

	/**
	 * Gets write count
	 * @return the number of times that this HardLocation was written to.
	 */
	public int getWriteCount();

	/**
	 * Writes specified {@link BitVector} to this HardLocation.
	 * For each bit in word, the corresponding counter is incremented if the bit is 1 or decremented if
	 * the bit is 0.
	 *  
	 * @param word word to be written
	 */
	public void write(BitVector word);

	/**
	 * Reads this HardLocation. The binary vector read from this hard location is 
	 * added to the int[] buffer.
	 * @param buffer an int[] buffer where the result of the read is added
	 * @return the updated int[] buffer
	 */
	public int[] read(int[] buffer);
	
	/**
	 * Returns the Hamming distance between a {@link BitVector} and the address of this HardLocation
	 * @param vector {@link BitVector} to be compared with this {@link HardLocation}
	 * @return the Hamming distance.
	 */
	public int hammingDistance(BitVector vector);
}