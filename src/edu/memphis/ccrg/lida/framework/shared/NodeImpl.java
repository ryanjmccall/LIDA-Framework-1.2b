/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.shared;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.shared.activation.ActivatibleImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.pam.PamNode;
import edu.memphis.ccrg.lida.pam.PamNodeImpl;
import edu.memphis.ccrg.lida.pam.PerceptualAssociativeMemory;

/**
 * Default {@link Node} implementation.
 *
 * @author Javier Snaider
 * @author Ryan J. McCall
 * @see ElementFactory
 */
public class NodeImpl extends ActivatibleImpl implements Node {

	private static final Logger logger = Logger.getLogger(NodeImpl.class.getCanonicalName());
	
	private int id;
	private ExtendedId extendedId;
	private String label ="Node";
	private String factoryName;
	private String toStringName;
	
	/**
	 * {@link PamNode} in {@link PerceptualAssociativeMemory} which grounds this {@link Node}
	 */
	protected PamNode groundingPamNode;

	@Override
	public synchronized void setFactoryType(String n) {
		factoryName = n;
	}

	@Override
	public String getFactoryType() {
		return factoryName;
	}

	/**
	 * Default constructor
	 */
	public NodeImpl(){
		super();
	}

	/**
	 * Copy constructor.
	 * @deprecated Use {@link ElementFactory#getNode(Node)} instead.
	 * @param n source {@link NodeImpl}
	 */
	@Deprecated
	public NodeImpl(NodeImpl n) {
		if(n == null){
			logger.log(Level.WARNING, "Cannot construct a Node from null.", TaskManager.getCurrentTick());
		}else{
			this.id = n.id;
			this.extendedId = n.extendedId;
			this.groundingPamNode = n.groundingPamNode;
			this.label = n.label;
			updateName();
		}	
	}
	
	@Override
	public synchronized void setId(int id) {
		this.id = id;
		extendedId = new ExtendedId(id);
		updateName();
	}
	
	/**
	 * Convenience method to set Node's {@link ExtendedId}.  Also sets node's id.
	 * @param eid {@link ExtendedId}
	 */
	public synchronized void setExtendedId(ExtendedId eid) {
		if(eid == null){
			logger.log(Level.WARNING, "Supplied ExtendedId was null. ExtendedId not set.", TaskManager.getCurrentTick());
		}else if(eid.isNodeId()){
			this.extendedId = eid;
			this.id = eid.getSourceNodeId();
			updateName();
		}else{
			logger.log(Level.WARNING, "Cannot give a Node a Link's ExtendedId", TaskManager.getCurrentTick());
		}
	}

	/*
	 * update node's name
	 */
	private void updateName(){
		toStringName = label + "["+id+"]";
	}
	
	@Override
	public ExtendedId getExtendedId() {
		return extendedId;
	}
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return label;
	}
	@Override
	public void setLabel(String l) {
		this.label=l;
		updateName();
	}

	@Override
	public PamNode getGroundingPamNode() {
		return groundingPamNode;
	}
	@Override
	public synchronized void setGroundingPamNode(PamNode n) {
		groundingPamNode = n;
	}

	/**
	 * This method compares this object with any kind of Node. returns true if
	 * the id of both are the same.
	 * @param o Object
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Node) {
			return ((Node) o).getId() == id;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	@Override
	public String toString(){
		return toStringName;
	}
	

	/**
	 * This default implementation of {@link Node} has all of its attributes updated by {@link NodeStructureImpl} or
	 * {@link ElementFactory} when nodes are updated.
	 * Therefore this class does not have to implement this method.
	 * Any subclass with specific class members (e.g. PamNodeImpl) should however override this method.
	 * @see PamNodeImpl#updateNodeValues(Node)
	 * @see NodeStructureImpl#addNode(Node, String)
	 */
	@Override
	public void updateNodeValues(Node n) {
	}

	@Override
	public ExtendedId getConditionId() {
		return extendedId;
	}

}
