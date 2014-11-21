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
import edu.memphis.ccrg.lida.pam.PamLink;
import edu.memphis.ccrg.lida.pam.PamLinkImpl;
import edu.memphis.ccrg.lida.pam.PerceptualAssociativeMemory;

/**
 * A {@link Link} that connects a {@link Node} to a {@link Linkable} (Node or Link).
 * 
 * @author Ryan J. McCall
 * @author Javier Snaider
 * @see ElementFactory
 */
public class LinkImpl extends ActivatibleImpl implements Link {

	private static final Logger logger = Logger.getLogger(LinkImpl.class.getCanonicalName());

	/*
	 * Source of this link, always a node.
	 */
	private Node source;
	
	/*
	 * Sink of this link, a Linkable.
	 */
	private Linkable sink;
	
	/*
	 * A custom id dependent on the source's and the sink's ids.
	 */
	private ExtendedId extendedId;
	
	/*
	 * Category of this Link.
	 */
	private LinkCategory category;
	/*
	 * Type of this link in the ElementFactory 
	 */
	private String factoryType;
	
	/**
	 * {@link PamLink} in a {@link PerceptualAssociativeMemory} that grounds this Link.
	 */
	protected PamLink groundingPamLink;
	
	/**
	 * Default constructor
	 */
	public LinkImpl() {
		super();
	}
	
	/**
	 * Constructs a new {@link Link} with specified parameters.
	 * @deprecated Use {@link ElementFactory#getLink(Node, Linkable, LinkCategory)} instead.
	 * @param src source {@link Node}
	 * @param snk sink {@link Linkable}
	 * @param cat link's {@link LinkCategory}
	 */
	@Deprecated
	public LinkImpl(Node src, Linkable snk, LinkCategory cat) {
		if(src == null){
			throw new IllegalArgumentException("Cannot create a link with null source.");
		}else if(snk == null){
			throw new IllegalArgumentException("Cannot create a link with null sink.");
		}else if(cat == null){
			throw new IllegalArgumentException("Cannot create a link with null category.");
		}else if(src.equals(snk)){
			throw new IllegalArgumentException("Cannot create a link with the same source and sink.");
		}else if(snk.getExtendedId().isComplexLink()){
			throw new IllegalArgumentException("Sink cannot be a complex link. Must be a node or simple link.");
		}else{
			this.source = src;
			this.sink = snk;
			this.category = cat;
			updateExtendedId();
		}
	}

	/**
	 * Copy constructor
	 * @deprecated Use {@link ElementFactory#getLink(String, Node, Linkable, LinkCategory, String, String, double, double)} instead.
	 * @param l source {@link LinkImpl}
	 */
	@Deprecated
	public LinkImpl(LinkImpl l) {
		if(l == null){
			logger.log(Level.WARNING, "Cannot construct a Link from null.", TaskManager.getCurrentTick());
		}else{
			sink = l.getSink();
			source = l.getSource();
			category = l.getCategory();
			groundingPamLink = l.getGroundingPamLink();
			updateExtendedId();
		}
	}
	
	/*
	 * Refreshes this Link's ExtendedId based on its category, source, and sink.
	 */
	private void updateExtendedId() {
		if(category != null && source != null && sink != null){
			if(logger.isLoggable(Level.FINEST)){
				logger.log(Level.FINEST, "ExtendedID updated", TaskManager.getCurrentTick());
			}
			extendedId = new ExtendedId(source.getId(), sink.getExtendedId(), category.getId());
		}
	}

	@Override
	public ExtendedId getExtendedId() {
		return extendedId;
	}

	@Override
	public Linkable getSink() {
		return sink;
	}

	@Override
	public Node getSource() {
		return source;
	}

	@Override
	public LinkCategory getCategory() {
		return category;
	}

	@Override
	public synchronized void setSink(Linkable snk) {
		if(snk == null){
			logger.log(Level.WARNING, "Cannot set sink to null", TaskManager.getCurrentTick());
		}else if(snk.equals(source)){
			logger.log(Level.WARNING, "Cannot set sink to same Linkable as source", TaskManager.getCurrentTick());
		}else if(snk.getExtendedId().isComplexLink()){
			logger.log(Level.WARNING, "Cannot set sink to be a complex link.", TaskManager.getCurrentTick());
		}else {
			this.sink = snk;
			updateExtendedId();
		}
	}

	@Override
	public synchronized void setSource(Node src) {
		if(src == null){
			logger.log(Level.WARNING, "Cannot set source to null", TaskManager.getCurrentTick());
		}else if(src.equals(sink)){
			logger.log(Level.WARNING, "Cannot set link's source to the same Linkable as its sink", TaskManager.getCurrentTick());
		}else{
			source = src;
			updateExtendedId();
		}
	}

	@Override
	public synchronized void setCategory(LinkCategory c) {
		if(c == null){
			logger.log(Level.WARNING, "Cannot set a Link's category to null", TaskManager.getCurrentTick());
		}else{
			category = c;
			updateExtendedId();
		}
	}

	@Override
	public PamLink getGroundingPamLink() {
		return groundingPamLink;
	}

	@Override
	public synchronized void setGroundingPamLink(PamLink l) {
		groundingPamLink = l;
	}
	
	/**
	 * This method compares this LinkImpl with any kind of Link.
	 * Two Links are equal if and only if they have the same id.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Link) {
			Link other = (Link) obj;
			return extendedId.equals(other.getExtendedId());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return extendedId.hashCode();
	}
	
	
	@Override
	public String getLabel() {
		return category.getLabel();
	}
	
	@Override
	public String toString() {
		return getLabel() + extendedId;
	}
	
	@Override
	public boolean isSimpleLink(){
		return extendedId.isSimpleLink();
	}

	/**
	 * This default implementation of {@link Link} has all of its attributes updated by {@link NodeStructureImpl} when links are updated.
	 * Therefore this class does not have to implement this method.
	 * Any subclass with specific class members (e.g. PamLinkImpl) should however override this method.
	 * @see PamLinkImpl#updateLinkValues(Link)
	 * @see NodeStructureImpl#addLink(Link, String)
	 * @see NodeStructureImpl#getNewLink(Link, String, Node, Linkable, LinkCategory)
	 */
	@Override
	public void updateLinkValues(Link l) {
	}

	@Override
	public String getFactoryType() {
		return factoryType;
	}

	@Override
	public synchronized void setFactoryType(String t) {
		factoryType = t;
	}
}