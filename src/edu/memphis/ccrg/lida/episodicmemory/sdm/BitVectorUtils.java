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
 * Utilities for {@link BitVector}
 * 
 * @author Javier Snaider
 */
public class BitVectorUtils {
	
	/**
	 * Returns a random {@link BitVector} of specified size
	 * @param s the size of the new vector
	 * @return a new random vector of size s.
	 */
	public static BitVector getRandomVector(int s) {
		BitVector v = new BitVector(s);
		for (int i = 0; i < s; i++) {
			v.putQuick(i, Math.random() > .5);
		}
		return v;
	}

	/**
	 * Calculates the Hamming distances between two {@link BitVector} addresses.
	 * 
	 * @param vector1
	 *            the first {@link BitVector}
	 * @param vector2
	 *            the second {@link BitVector}
	 * @return the Hamming distances
	 */
	public static int hamming(BitVector vector1, BitVector vector2) {
		BitVector aux = vector2.copy();
		aux.xor(vector1);
		return aux.cardinality();
	}

	/**
	 * Returns a noisy {@link BitVector}
	 * @param original original {@link BitVector}
	 * @param noise number of noisy bits to introduce
	 * @return a new vector which differs from the original by at most 'noise' bits
	 */
	public static BitVector getNoisyVector(BitVector original, int noise) {
		BitVector v = original.copy();
		int size = v.size();
		for (int i = 0; i < noise; i++) {
			int pos = (int) (Math.random() * size);
			v.putQuick(pos, !v.getQuick(pos));
		}
		return v;
	}

	/**
	 * Adds a {@link BitVector} to an accumulation array. The bits of the {@link BitVector} are first converted to either 0 to -1.  
	 * The BitVector is not modified.
	 * @param accum the accumulation array.
	 * @param v the vector to be added
	 * @return the new accumulation array
	 */
	public static int[] getVectorSum(int[] accum, BitVector v) {
		for (int i = 0; i < v.size(); i++) {
			accum[i] += (v.getQuick(i)) ? 1 : -1;
		}
		return accum;
	}

 
	/**
	 * Sums specified vectors.
	 * 
	 * @param accum
	 *            the accum
	 * @param vector
	 *            the vector
	 * @return the int[]
	 */
	public static int[] getVectorSum(int[] accum, int[] vector) {
		for (int i = 0; i < vector.length; i++) {
			accum[i] += vector[i];
		}
		return accum;
	}

	/**
	 * Vector to bipolar.
	 * 
	 * @param accum
	 *            the accum
	 * @param v
	 *            the v
	 * @return the int[]
	 */
	public static int[] vectorToBipolar(int[] accum, BitVector v) {
		for (int i = 0; i < v.size(); i++) {
			accum[i] += (v.getQuick(i)) ? 1 : -1;
		}
		return accum;
	}

	/**
	 * Vector to bipolar.
	 * 
	 * @param v
	 *            the v
	 * @return the int[]
	 */
	public static int[] vectorToBipolar(BitVector v) {
		int[] accum = new int[v.size()];
		for (int i = 0; i < v.size(); i++) {
			accum[i] += (v.getQuick(i)) ? 1 : -1;
		}
		return accum;
	}

	/**
	 * Gets a normalized vector.
	 * 
	 * @param buff a int[] vector
	 * @return new normalized {@link BitVector}
	 */
	public static BitVector getNormalizedVector(int[] buff) {
		BitVector res = new BitVector(buff.length);
		for (int i = 0; i < buff.length; i++) {
			res.putQuick(i, buff[i] > 0);
			if (buff[i] == 0) {
				res.putQuick(i, (Math.random() > .5));
			}
		}
		return res;
	}

	/**
	 * Multiply vectors.
	 * 
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @return the bit vector
	 */
	public static BitVector multiplyVectors(BitVector a, BitVector b) {
		BitVector res = a.copy();
		res.xor(b);
		return res;
	}
        
        /**
         * Gets the complement of a bit vector.
         * @param a     the input bit vector 
         * @return      the complement of the input bit vector
         */
        public static BitVector getComplement(BitVector a) {
            BitVector res = a.copy();
            res.not();
            return res;
        }
}