/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.pam;

import java.util.Collection;
import java.util.Set;

import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.shared.ExtendedId;
import edu.memphis.ccrg.lida.framework.shared.Link;
import edu.memphis.ccrg.lida.framework.shared.LinkCategory;
import edu.memphis.ccrg.lida.framework.shared.Linkable;
import edu.memphis.ccrg.lida.framework.shared.Node;
import edu.memphis.ccrg.lida.framework.shared.NodeStructure;
import edu.memphis.ccrg.lida.pam.tasks.BasicDetectionAlgorithm;
import edu.memphis.ccrg.lida.pam.tasks.DetectionAlgorithm;
import edu.memphis.ccrg.lida.pam.tasks.ExcitationTask;
import edu.memphis.ccrg.lida.pam.tasks.PropagationTask;

/**
 * A main module which contains feature detectors, nodes, and links.
 * @author Ryan J. McCall
 */
public interface PerceptualAssociativeMemory extends FrameworkModule{


	/**
	 * Adds a new {@link PamNode} of default type to PAM with specified label.
	 * Label must be unique. If not, existing node with specified label is returned.
	 * @param label the label of the new {@link PamNode}
	 * @return the new {@link PamNode} added to PAM, the existing Node with specified label or null
	 */
	public PamNode addDefaultNode(String label);

	/**
	 * Adds a new {@link PamNode} of specified type to PAM with specified label.  Type should refer to a subclass of {@link PamNodeImpl}.
	 * Label must be unique. If not, existing node with specified label is returned.
	 * @param type the type of the new {@link PamNode}
	 * @param label the label of the new {@link PamNode}
	 * @return the new {@link PamNode} added to PAM, the existing Node with specified label or null
	 */
	public PamNode addNode(String type, String label);

	/**
	 * Adds a new {@link PamLink} of default type to PAM. If a link with the same attributes already exists
	 * the existing link will be returned instead.
	 * @param src the link's source
	 * @param snk the link's sink
	 * @param cat the link's category
	 * @return the new PamLink, the existing PamLink, or null
	 */
	public PamLink addDefaultLink(Node src, Linkable snk, LinkCategory cat);

	/**
	 * 
	 * Adds a new {@link PamLink} of specified type to PAM. Type should refer to a subclass of {@link PamLinkImpl}.
	 * If a link with the same attributes already exists the existing link will be returned instead. 
	 * @param type link's type
	 * @param src the link's source
	 * @param snk the link's sink
	 * @param cat the link's category
	 * @return the new PamLink, the existing PamLink, or null
	 */
	public PamLink addLink(String type, Node src, Linkable snk, LinkCategory cat);
	
	/**
	 * Adds a COPY of specified node to this {@link PerceptualAssociativeMemory}.
	 * Node will be of Pam's default type.
	 * 
	 * @deprecated Use either {@link #addNode(String, String)} or {@link #addDefaultNode(String)} instead. 
	 * @param node PamNode
	 * @return Copied PamNode actually stored in this PAM.
	 */
	@Deprecated
	public PamNode addDefaultNode(Node node);
	
	/**
	 * Adds a COPY of a collection of Nodes to this PAM.
	 * Nodes will be of Pam's default type.
	 * 
	 * @deprecated Use either {@link #addNode(String, String)} or 
	 * 	{@link #addDefaultNode(String)} instead.
	 * @param nodes nodes to add
	 * @return Copied PamNodes actually stored in this PAM
	 */
	@Deprecated
	public Set<PamNode> addDefaultNodes(Set<? extends Node> nodes);
	
	/**
	 * Adds a COPY of specified link to this PAM.
	 * Link will be of Pam's default type.
	 *
	 * @deprecated Use either {@link #addLink(String, Node, Linkable, LinkCategory)} or
	 * 	 {@link #addDefaultLink(Node, Linkable, LinkCategory)} instead.
	 * @param link  PamLink to add
	 * @return Copied PamLink actually stored in this PAM
	 */
	@Deprecated
	public PamLink addDefaultLink(Link link);
	
	/**
	 * Adds a COPY of specified collection of PamLinks to this PAM.
	 * Links will be of Pam's default type.
	 *
	 * @deprecated Use either {@link #addLink(String, Node, Linkable, LinkCategory)} or
	 * 	 {@link #addDefaultLink(Node, Linkable, LinkCategory)} instead.
	 * @param links  PamLinks to add
	 * @return Copied PamLinks actually stored in this PAM
	 */
	@Deprecated
	public Set<PamLink> addDefaultLinks(Set<? extends Link> links);
	
	/**
	 * Adds specified {@link DetectionAlgorithm} to be run.
	 * @param fd {@link DetectionAlgorithm}
	 */
	public void addDetectionAlgorithm(DetectionAlgorithm fd);
		
	/**
	 * Adds {@link PamListener}.
	 *
	 * @param pl listener
	 */
	public void addPamListener(PamListener pl);	
	
	/**
	 * Sets {@link PropagationStrategy} governing how activation is propagated in this PAM.
	 *
	 * @param strategy {@link PropagationStrategy}
	 */
	public void setPropagationStrategy(PropagationStrategy strategy);
	
	/**
	 * Gets {@link PropagationStrategy} governing how activation is propagated in this PAM.
	 *
	 * @return this Pam's {@link PropagationStrategy}
	 */
	public PropagationStrategy getPropagationStrategy();
	
	/**
	 * Excites specified {@link PamLinkable} an amount of activation.
	 * @param linkable Id of the PamLinkable receiving the activation
	 * @param amount amount of activation to excite
	 * @see ExcitationTask {@link BasicDetectionAlgorithm}
	 */
	public void receiveExcitation(PamLinkable linkable, double amount);
	
	/**
	 * Excites {@link PamLinkable} with an amount of activation.
	 * @param linkables Ids of PamLinkable to be excited
	 * @param amount amount of activation
	 */
	public void receiveExcitation(Set<PamLinkable> linkables, double amount);
	
	/**
	 * Propagates activation from a {@link PamNode} to its parents.
	 *
	 * @param pamNode The {@link PamNode} to propagate activation from.
	 * @see ExcitationTask
	 * @see PropagationTask
	 */
	public void propagateActivationToParents(PamNode pamNode);

	/**
	 * Adds a NodeStructure to the percept.
	 *
	 * @param ns NodeStructure
	 */
	public void addToPercept(NodeStructure ns);
	/**
	 * Adds {@link Node} to the percept.
	 * @param n Node to add
	 */
	public void addToPercept(Node n);
	/**
	 * Adds {@link Link} to the percept.
	 * @param l Link to add
	 */
	public void addToPercept(Link l);
	
	/**
	 * Returns LinkCategory with specified id.
	 * @param id id of LinkCategory sought
	 * @return LinkCategory or null if category does not exist in PAM.
	 */
	public LinkCategory getLinkCategory(int id);
	
	/**
	 * Returns all categories in this Pam
	 * @return Collection of all {@link LinkCategory}
	 */
	public Collection<LinkCategory> getLinkCategories();
	
	/**
	 * Adds a COPY of specified LinkCategory to this {@link PerceptualAssociativeMemory}.
	 * Category must also be a node in order to be added. Node will be of Pam's default type. 
	 * @param cat {@link LinkCategory}
	 * @return Copied LinkCategory actually stored in this PAM.
	 */
	public LinkCategory addLinkCategory(LinkCategory cat);
	
	/**
	 * Returns true if this PAM contains specified PamNode.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
	public boolean containsNode(Node node);
	
	/**
	 * Contains node.
	 *
	 * @param id ExtendedId of sought node
	 * @return true if PAM contains the node with this id.
	 */
	public boolean containsNode(ExtendedId id);
	
	/**
	 * Returns true if this PAM contains specified PamLink.
	 *
	 * @param link the link
	 * @return true, if successful
	 */
	public boolean containsLink(Link link);
	
	/**
	 * Contains link.
	 *
	 * @param id ExtendedId of sought link
	 * @return true if PAM contains the link with this id.
	 */
	public boolean containsLink(ExtendedId id);
	
	/**
	 * Sets perceptThreshold
	 * @param t threshold for a {@link Linkable} to become part of the percept
	 */
	public void setPerceptThreshold(double t);
	
	/**
	 * Sets upscaleFactor
	 * @param f scale factor for feed-forward activation propagation
	 */
	public void setUpscaleFactor(double f);
	
	/**
	 * Gets upscaleFactor
	 * @return scale factor for feed-forward activation propagation
	 */
	public double getUpscaleFactor();
	
	/**
	 * Sets downscaleFactor 
	 * @param f scale factor for top-down activation propagation
	 */
	public void setDownscaleFactor(double f);

	/**
	 * Gets downscaleFactor
	 * @return scale factor for top-down activation propagation
	 */
	public double getDownscaleFactor();
	
	/**
	 * Returns whether PamLinkable is above percept threshold.
	 * @param l a PamLinkable
	 * @return true if PamLinkable's total activation is above percept threshold 
	 */
	public boolean isOverPerceptThreshold(PamLinkable l);
	
	/**
	 * Returns the {@link PamNode} with specified id from this PAM or null.
	 *
	 * @param id the id
	 * @return the pam node
	 */
	public Node getNode(int id);
	
	/**
	 * Returns the {@link PamNode} with specified {@link ExtendedId} or null
	 * @param id sought {@link ExtendedId}
	 * @return PamNode  the actual Node
	 */
	public Node getNode(ExtendedId id);
	
	/**
	 * Returns the {@link PamNode} with specified label or null.
	 * This method is intended to be used only during initialization.
	 * @param label sought
	 * @return PamNode  the actual Node
	 */
	public Node getNode(String label);
	
	/**
	 * 
	 * @param id link's eid
	 * @return the {@link PamLink} with specified id from this PAM or null.
	 */
	public Link getLink(ExtendedId id);
	
	/**
	 * Returns an unmodifiable collection of the {@link PamNode}s in this PAM as {@link Node}s.
	 *
	 * @return the PamNodes of this PAM
	 */
	public Collection<Node> getNodes();
	
	/**
	 * Returns an unmodifiable collection of the {@link PamLink}s in this PAM as {@link Link}s.
	 *
	 * @return the PamLink of this PAM
	 */
	public Collection<Link> getLinks();
		
} 