/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/

package edu.memphis.ccrg.lida.episodicmemory.sdm;

import java.util.Collection;

import cern.colt.bitvector.BitVector;
import edu.memphis.ccrg.lida.framework.shared.ElementFactory;
import edu.memphis.ccrg.lida.framework.shared.Node;
import edu.memphis.ccrg.lida.framework.shared.NodeStructure;
import edu.memphis.ccrg.lida.framework.shared.NodeStructureImpl;
import edu.memphis.ccrg.lida.pam.PerceptualAssociativeMemory;

/**
 * This is the class that translates from nodes to boolean vectors and vice-
 * versa. The translation works by assigning a unique index to every node.
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 */
public class BasicTranslator implements Translator {

	private static final ElementFactory factory = ElementFactory.getInstance();
	private int size;
	private PerceptualAssociativeMemory pam;

	/**
	 * Default constructor. The {@link PerceptualAssociativeMemory} and vectors' size must be set before to use this {@link Translator}.
	 */
	public BasicTranslator(){		
	}
	
	/**
	 * Constructs a new translator with specified attributes.
	 * 
	 * @param size
	 *            the number of positions of the bit vector
	 * @param pam
	 *            the {@link PerceptualAssociativeMemory} associated with this translator
	 */
	public BasicTranslator(int size, PerceptualAssociativeMemory pam) {
		this.size = size;
		this.pam = pam;
	}

	/**
	 * Translates a {@link BitVector} into a {@link NodeStructure}. Since the method {@link BitVector#getQuick(int)} 
	 * is used, no preconditions are checked.
	 * 
	 * @param data
	 *            the {@link BitVector} to be translated
	 * @return a {@link NodeStructure} representing the positions in the bit vector,
	 *         each node has a unique ID
	 * @see BitVector
	 */
	@Override
	public NodeStructure translate(BitVector data) {
		NodeStructure ns = new NodeStructureImpl();
		for (int i = 0; i < size; i++) {
			if (data.getQuick(i)) {
				Node n = pam.getNode(i);
				if(n == null){
					continue;
				}
				ns.addDefaultNode(factory.getNode(n));
			}
		}
		return ns;
	}

	/**
	 * Translates a {@link NodeStructure} into a {@link BitVector}. Currently only nodes
	 * are translated, but links, and maybe activations must be also
	 * handled.
	 * 
	 * @param ns the {@link NodeStructure} to be translated
	 * @return a {@link BitVector} representing the {@link Node} objects in the {@link NodeStructure}
	 */
	@Override
	public BitVector translate(NodeStructure ns) {
		BitVector v = new BitVector(size);
		Collection<Node> nodes = ns.getNodes();
		if(nodes != null){
			for (Node n : nodes) {
				v.put(n.getId(), true);
			}
		}
		return v;
	}

	/**
	 * Gets size
	 * @return the vector's size 
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets size
	 * @param s the vector's size in the {@link SparseDistributedMemory}
	 */
	public void setSize(int s) {
		size = s;
	}

	/**
	 * Gets Pam
	 * @return the {@link PerceptualAssociativeMemory}
	 */
	public PerceptualAssociativeMemory getPam() {
		return pam;
	}

	/**
	 * Sets Pam
	 * @param pam the {@link PerceptualAssociativeMemory} to set
	 */
	public void setPam(PerceptualAssociativeMemory pam) {
		this.pam = pam;
	}
	
}