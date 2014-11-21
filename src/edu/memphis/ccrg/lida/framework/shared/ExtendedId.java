/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.shared;

/**
 * Generalized Id for Both {@link Node}s and {@link Link}s.
 * Link's source must be a {@link Node}. Link's sink can be a Node or a Link.
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 */
public class ExtendedId {

	private int linkCategory;
	private int sourceNodeId;
	private int sinkLinkCategory;
	private int sinkNode1Id;
	private int sinkNode2Id;
	
	private static final int UNDEFINED = Integer.MIN_VALUE;
	
	/*
	 * There are 3 categories of possible ExtendedId instances.
	 * 
	 * Case 1: Id is for Node: 
	 * variable sourceNodeId is set to the specified nodeId
	 * the rest of the variables are set to UNDEFINED
	 *
	 * 
	 * Case 2: Id is for a Simple link, where the source and sink are nodes.
	 * linkCategory is set using the first argument
	 * and sourceNodeId is set using the second.
	 * Then sinkCategory and sinkNode2Id are UNDEFINED.
	 * while sinkNode1Id is the id of the sink node.
	 *
	 * Case 3: Id is for a Complex link, where the sink is a SIMPLE Link, call it L1.
	 * sourceNodeId and linkCategory are set normally.
	 * Then sinkLinkCategory is the LinkCategory of L1. 
	 * Then sinkNode1Id is L1's sourceNodeId.
	 * Then sinkNode2Id is L1's sinkNode1Id
	 * 
	 */

	/**
	 * Constructs an ExtendedId for a {@link Link}.
	 * @param sourceNodeId Node's id
	 * @param sinkId Sink's id
	 * @param categoryId Link's category
	 */
	public ExtendedId(int sourceNodeId, ExtendedId sinkId, int categoryId) {
		super();
		this.linkCategory = categoryId;
		this.sourceNodeId = sourceNodeId;
		this.sinkLinkCategory = sinkId.linkCategory;
		this.sinkNode1Id = sinkId.sourceNodeId;
		this.sinkNode2Id = sinkId.sinkNode1Id;
	}

	/**
	 * Constructs an ExtendedId for a {@link Node}
	 * 
	 * @param nodeId Node's id
	 */
	public ExtendedId(int nodeId) {
		super();
		this.linkCategory = UNDEFINED;
		this.sourceNodeId = nodeId;
		this.sinkLinkCategory = UNDEFINED;
		this.sinkNode1Id = UNDEFINED;
		this.sinkNode2Id = UNDEFINED;
	}

	/**
	 * Returns source Node id
	 * 
	 * @return id of the source Node
	 */
	public int getSourceNodeId() {
		return sourceNodeId;
	}

	/**
	 * Returns whether id is for a node.
	 * 
	 * @return true if this ExtendedId is for a Node.
	 */
	public boolean isNodeId() {
		return linkCategory == UNDEFINED;
	}

	/**
	 * Returns whether this ExtendedId is for a {@link Link} between 2 nodes.
	 * @return true if id is for a simple link.  false if id is for a node or a complex link.
	 */
	public boolean isSimpleLink(){
		return linkCategory != UNDEFINED && sinkLinkCategory == UNDEFINED;
	}
	
	/**
	 * Returns whether this ExtendedId is for a {@link Link} between a node and a Link.
	 * @return true if id is for a complex link.  false if id is for a node or a simple link.
	 */
	public boolean isComplexLink(){
		return linkCategory != UNDEFINED && sinkLinkCategory != UNDEFINED;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ExtendedId) {
			ExtendedId otherId = (ExtendedId) o;
			return (linkCategory == otherId.linkCategory && 
					sourceNodeId == otherId.sourceNodeId && 
					sinkLinkCategory == otherId.sinkLinkCategory && 
					sinkNode1Id == otherId.sinkNode1Id   && 
					sinkNode2Id == otherId.sinkNode2Id);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (linkCategory ^ sourceNodeId ^ sinkLinkCategory ^ sinkNode1Id ^ sinkNode2Id);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		sb.append(linkCategory).append(","); 
		sb.append(sourceNodeId).append(",");
		sb.append(sinkLinkCategory).append(","); 
		sb.append(sinkNode1Id).append(","); 
		sb.append(sinkNode2Id).append("]");
		return sb.toString();
	}
}
