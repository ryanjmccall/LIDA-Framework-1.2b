/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.shared;

import edu.memphis.ccrg.lida.pam.PamLink;

/**
 * A link connects two Linkable objects. 
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 */
public interface Link extends Linkable{
	
	/**
	 * Returns whether Link is Simple (connects two nodes)
	 * @return true if simple, false if complex (between a node and a simple link).
	 */
	public boolean isSimpleLink();
	
	/**
	 * One end of the link which provides activation to the sink.  
	 * @return source linkable
	 */
	public Node getSource();

	/**
	 * One end of the link which receives activation from the source.
	 * @return sink linkable
	 */
	public Linkable getSink();
	
	/**
	 * Set source linkable.
	 * 
	 * @param source
	 *            the new source
	 */
	public void setSource(Node source);

	/**
	 * Set sink linkable.
	 * 
	 * @param sink
	 *            the new sink
	 */
	public void setSink(Linkable sink);
	
	/**
	 * Get LinkCategory of this link.
	 * 
	 * @return the category
	 */
	public LinkCategory getCategory();

	/**
	 * Set LinkCategory.
	 * 
	 * @param type
	 *            the new category
	 */
	public void setCategory(LinkCategory type);

	/**
	 * Set the grounding PamLink for this link.
	 * 
	 * @param l
	 *            the new grounding pam link
	 */
	public void setGroundingPamLink(PamLink l);

	/**
	 * Get the grounding PamLink for this link.
	 * 
	 * @return the grounding pam link
	 */
	public PamLink getGroundingPamLink();
	
	/** 
	 * Subclasses of Link should override this method to set all of their type-specific member data
	 * using the values of the specified Link.  
	 * Thus specified Link must be of the same subclass type.
	 * @param link Link whose values are used to update with.
	 */
	public void updateLinkValues(Link link);

}