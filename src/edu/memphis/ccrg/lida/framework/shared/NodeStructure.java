/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.shared;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.collection.UnmodifiableCollection;
import org.apache.commons.collections15.map.UnmodifiableMap;
import org.apache.commons.collections15.set.UnmodifiableSet;

/**
 * A NodeStructure holds a collection of {@link Node}s an {@link Link}s. It is used
 * as a main conceptual representation among LIDA modules.
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 * @see NodeStructureImpl
 */
public interface NodeStructure {
	
	/**
	 * Creates, adds and returns a new {@link Node} of default type with specified attributes.
	 * @param label label of new {@link Node}
	 * @param a initial activation of new {@link Node}
	 * @param rt initial removal threshold of new {@link Node}
	 * @return the {@link Node} added to the NodeStructure or null 
	 */
	public Node addDefaultNode(String label, double a, double rt);
	
	/**
	 * Adds a copy of specified {@link Node} to this NodeStructure. The copy will be of the default
	 * type of this NodeStructure, NOT of the type of the specified node. If Node with the same
	 * id already exists the the old node's activation is updated ONLY IF it is higher than the existing activation.
	 * @param n Node to add.
	 * @return The copied Node that is stored in this NodeStructure or the existing, updated, Node already there.
	 */
	public Node addDefaultNode(Node n);
	
	/**
	 * @return copied and/or updated nodes that are now present in this NodeStructure
	 * @see #addDefaultNode(Node)
	 * @param nodes Node to be added.
	 */
	public Collection<Node> addDefaultNodes(Collection<Node> nodes);

	/**
	 * Creates, adds and returns a new {@link Node} of specified type with specified attributes.
	 * @param type Factory type of new {@link Node}
	 * @param label label of new {@link Node}
	 * @param a initial activation of new {@link Node}
	 * @param rt initial removal threshold of new {@link Node}
	 * @return the {@link Node} added to the NodeStructure or null 
	 */
	public Node addNode(String type, String label, double a, double rt);
	
	/**
	 * Add a Node of a specified type to this NodeStructure.<br/>
	 * If a Node with the same id already exists in the NodeStructure the existing Node will
	 * have its activation updated. In this case the Node's type doesn't change.
	 * @param n Node
	 * @param type name of node's type in {@link ElementFactory}
	 * @return copy of node actually added.
	 */
	public Node addNode(Node n, String type);
	
	/**
	 * Creates and adds a new Link of default type with specified attributes.  Source and sink must
	 * already be in this NodeStructure.
	 * @param source Link's source {@link Node}
	 * @param sink Link's sink, a {@link Node} or a {@link Link}	
	 * @param category Link's {@link LinkCategory}
	 * @param activation initial link activation
	 * @param removalThreshold amount of activation Link must maintain to remain in this NodeStructure after decaying.
	 * @return created Link or null if either source or sink are not already present. 
	 */
	public Link addDefaultLink(Node source, Linkable sink, LinkCategory category, double activation, double removalThreshold);

	/**
	 * Creates and adds a new Link of default type with specified attributes.  Source and sink must
	 * already be in this NodeStructure.
	 * @param idSource id of link's source
	 * @param idSink {@link ExtendedId} of link's sink
	 * @param type Link's {@link LinkCategory}
	 * @param activation initial link activation
	 * @param removalThreshold amount of activation Link must maintain to remain in this NodeStructure after decaying.
	 * @return created Link or null if either source or sink are not already present. 
	 */
	public Link addDefaultLink(int idSource, ExtendedId idSink,
			LinkCategory type, double activation, double removalThreshold);

	/**
	 * Creates and adds a new Link of default type with specified attributes.  Source and sink must
	 * already be in this NodeStructure.  Allows multiple links from the same source and sink as long
	 * as their LinkCategory differs.
	 * @param idSource id of link's source
	 * @param idSink id of link's sink
	 * @param type Link's {@link LinkCategory}
	 * @param activation initial link activation
	 * @param removalThreshold amount of activation Link must maintain to remain in this NodeStructure after decaying.
	 * @return created Link or null if either source or sink are not already present. 
	 */
	public Link addDefaultLink(int idSource, int idSink,
			LinkCategory type, double activation, double removalThreshold);

	/**
	 * Adds a copy, of default link type, based on the specified {@link Link}, to this NodeStructure. If Link with the same 
	 * id already exists then the old Link's activation is updated.
	 * Copied link will have the default link type of this {@link NodeStructure} when
	 * it is added.
	 * @param l Link to copy and add.
	 * @return the copied Link that is actually stored in this NodeStructure, or the existing link that is updated.
	 * If Link cannot be added then null is returned.
	 */
	public Link addDefaultLink(Link l);
	
	/**
	 * Copies specified Links and then adds the copies to this NodeStructure.
	 * If any Link with the same 
	 * id already exists then the old Link's activation is updated.
	 * Copied links will have the default link type of this {@link NodeStructure} when
	 * they are added.
	 * Note if Links in supplied Collection link to each other then this method does NOT guarantee
	 * that all Links will be added properly.  Links should be added one at a time in this case after
	 * the dependent links are already present. 
	 * 
	 * @param links Links to copy and add.
	 * @return the copied Links that are actually stored in this NodeStructure, or any existing links.
	 */
	public Collection<Link> addDefaultLinks(Collection<Link> links);
	
	/**
	 * Creates and adds a new {@link Link} of specified type with specified attributes.  Source and sink must
	 * already be in this NodeStructure.
	 * 
	 * @param type Factory type of the link to be created
	 * @param srcId id of link's source
	 * @param snkId {@link ExtendedId} of link's sink
	 * @param cat Link's {@link LinkCategory}
	 * @param a initial link activation
	 * @param rt removal threshold, amount of activation Link must maintain to remain in this NodeStructure after decaying.
	 * @return created Link or null if either source or sink are not already present. 
	 */
	public Link addLink(String type, int srcId, ExtendedId snkId,
			LinkCategory cat, double a, double rt);

	/**
	 * Creates and adds a new {@link Link} of specified type with specified attributes.  Source and sink must
	 * already be in this NodeStructure.
	 * @param type Factory type of the link to be created
	 * @param src the link's source {@link Node}
	 * @param sink the link's sink {@link Linkable}
	 * @param cat the link's {@link LinkCategory}
	 * @param a initial link activation
	 * @param rt removal threshold, amount of activation Link must maintain to remain in this NodeStructure after decaying.
	 * @return created Link or null if either source or sink are not already present. 
	 */
	public Link addLink(String type, Node src, Linkable sink, LinkCategory cat,
			double a, double rt);
	
	/**
	 * Adds copy of specified Link. Copy is of specified type.
	 * @param l original {@link Link}
	 * @param linkType type of copied {@link Link}
	 * @return new {@link Link} or null if such a link cannot be created.
	 */
	public Link addLink(Link l, String linkType);

	/**
	 * Removes specified {@link Node} if present.
	 * @param n Node to remove.
	 */
	public void removeNode(Node n);
	
	/**
	 * Removes specified {@link Link} if present.
	 * @param l Link to remove.
	 */
	public void removeLink(Link l);

	/**
	 * Removes specified {@link Linkable} if present.
	 * @param l Linkable to remove.
	 */
	public void removeLinkable(Linkable l);

	/**
	 * Removes {@link Linkable} with specified {@link ExtendedId} if present.
	 * @param id ExtendedId of Linkable to remove.
	 */
	public void removeLinkable(ExtendedId id);

	/**
	 * Removes all links from this {@link NodeStructure}.
	 */
	public void clearLinks();

	/**
	 * Removes all nodes and links from this NodeStructure.
	 *
	 */
	public void clearNodeStructure();

	/**
	 * Returns whether this NodeStructure contains specified Node.
	 * @param n Node checked for.
	 * @return true if contains a Node with the same id.
	 */
	public boolean containsNode(Node n);

	/**
	 * Returns whether this NodeStructure contains Node with specified id.
	 * @param id id of Node checked for.
	 * @return true if contains a Node with the same id.
	 */
	public boolean containsNode(int id);

	/**
	 * Returns whether this NodeStructure contains Node with specified ExtendedId.
	 * @param id ExtendedId of Node checked for.
	 * @return true if contains a Node with the same ExtendedId.
	 */
	public boolean containsNode(ExtendedId id);

	/**
	 * Returns whether this NodeStructure contains specified Link.
	 * @param l Link checked for.
	 * @return true if contains a Link with the same id.
	 */
	public boolean containsLink(Link l);
	
	/**
	 * Returns whether this NodeStructure contains Link with specified {@link ExtendedId}.
	 * @param id Link checked for.
	 * @return true if contains a {@link Link} with the same {@link ExtendedId}.
	 */
	public boolean containsLink(ExtendedId id);

	/**
	 * Returns whether this NodeStructure contains specified {@link Linkable}.
	 * @param l {@link Linkable} checked for.
	 * @return true if contains a {@link Linkable} with the same {@link ExtendedId}.
	 */
	public boolean containsLinkable(Linkable l);
	
	/**
	 * Returns whether this NodeStructure contains {@link Linkable} with specified {@link ExtendedId}.
	 * @param id {@link Linkable} checked for.
	 * @return true if contains a {@link Linkable} with the same {@link ExtendedId}.
	 */
	public boolean containsLinkable(ExtendedId id);
	
	/**
	 * Merges specified NodeStructure into this one.  Adds all nodes and adds all Links.
	 * Activations are updated if Linkable is already present.
	 * @param ns NodeStructure
	 */
	public void mergeWith(NodeStructure ns);

	/**
	 * Returns a deep copy of this {@link NodeStructure}.
	 * @return {@link NodeStructure}
	 */
	public NodeStructure copy();
	
	/**
	 * Decays the {@link Linkable}s of this {@link NodeStructure}.
	 * 
	 * @param ticks
	 *            the number of ticks to decay for. 
	 */
	public void decayNodeStructure(long ticks);

	/**
	 * Returns a copy of the node in this NodeStructure with specified id.
	 * @param id id of node
	 * @return Node with specified id or null if not present
	 */
	public Node getNode(int id);

	/**
	 * Returns a copy of the node in this NodeStructure with specified {@link ExtendedId}.
	 * @param eid ExtendedId of node
	 * @return Node with specified ExtendedId or null if not present
	 */
	public Node getNode(ExtendedId eid);

	/**
	 * Returns all {@link Node}s.
	 * @return all {@link Node}s in this NodeStructure
	 */
	public Collection<Node> getNodes();

	/**
	 * Gets {@link Link} with specified {@link ExtendedId} if present.
	 * @param id {@link ExtendedId} of sought Link
	 * @return Link or null if no Link exists
	 */
	public Link getLink(ExtendedId id);

	/**
	 * Returns the Links of this NodeStructure.
	 * @return an {@link UnmodifiableCollection} of all Links
	 */
	public Collection<Link> getLinks();
	
	/**
	 * Returns all Links of this NodeStructure with specified {@link LinkCategory}.
	 * @param cat the {@link LinkCategory} to search for
	 * @return Links having specified {@link LinkCategory}
	 */
	public Set<Link> getLinks(LinkCategory cat);

	/**
	 * Gets all {@link Link} objects directly connected to specified {@link Linkable}.
	 * @param l the Linkable whose attached links will be returned
	 * @return an {@link UnmodifiableSet} of all Links connected to specified {@link Linkable}
	 */
	public Set<Link> getAttachedLinks(Linkable l);

	/**
	 * Gets all {@link Link}s directly connected to specified {@link Linkable} with specified {@link LinkCategory}
	 * @param lnk a {@link Linkable}
	 * @param cat LinkCategory
	 * @return Links
	 */
	public Set<Link> getAttachedLinks(Linkable lnk, LinkCategory cat);

	/**
	 * Gets {@link Linkable} with specified {@link ExtendedId}.
	 * @param id {@link ExtendedId}
	 * @return a Linkable
	 */
	public Linkable getLinkable(ExtendedId id);

	/**
	 * Returns all Linkables, all Nodes and Links, currently in this {@link NodeStructure}.
	 * @return all Linkables
	 */
	public Collection<Linkable> getLinkables();

	/**
	 * Returns a map of all the {@link Linkable} objects currently in the NodeStructure and their attached links.
	 * @return an {@link UnmodifiableMap} of the {@link Linkable} objects in this NodeStructure and their attached links.
	 */
	public Map<Linkable, Set<Link>> getLinkableMap();

	/**
 	 * Returns a {@link Map} of all sink {@link Linkable} objects connected to specified {@link Node}.
 	 * The keys of the map are the connected sinks and the values are the Links connecting the sinks to the specified {@link Node}.
	 * @param n supplied node
	 * @return map of sinks and links connecting node to them
	 */
	public Map<Linkable, Link> getConnectedSinks(Node n);
	
	/**
	 * Returns a {@link Map} of all Nodes connected to specified {@link Linkable} as a source.  
 	 * The keys of the map are the connected sources and the values are the Links connecting the sources to the specified {@link Linkable}
	 * @param lnk the {@link Linkable} whose connected sources will be returned
	 * @return {@link Map} of all sources connected to lnk and the links connecting them to lnk
	 */
	public Map<Node, Link> getConnectedSources(Linkable lnk);
	
	/**
	 * Gets the number of nodes.
	 * @return number of nodes currently in the NodeStructure
	 */
	public int getNodeCount();

	/**
	 * Gets the number of links.
	 * @return number of links currently in the NodeStructure
	 */
	public int getLinkCount();

	/**
	 * Gets the number of linkables (nodes and links).
	 * @return number of {@link Linkable} objects currently in the NodeStructure
	 */
	public int getLinkableCount();

	/**
	 * Gets default {@link Node} type of the NodeStructure.
	 * @return default type of {@link Node} objects in the NodeStructure
	 */
	public String getDefaultNodeType();

	/**
	 * Gets default {@link Link} type of the NodeStructure.
	 * @return link type of {@link Link} objects in the NodeStructure
	 */
	public String getDefaultLinkType();
	
	/**
	 * Returns a copy of a subgraph of this {@link NodeStructure}.
	 * The subgraph will contain all specified nodes with non-zero activation if 
	 * they are currently present in the NodeStructure.
	 * Additionally all other nodes having distance, the number of links, less or equal to d from the specified nodes and 
	 * that have non-zero activation are part of the subgraph.
	 * If a Node has non-zero activation then the depth-first search will not continue further from that Node.
	 * Finally all links that connect two {@link Linkable} elements that are both d or less from the specified nodes are included in the subgraph.
	 * @param nodes the nodes which will be the roots from which the subgraph will be formed
	 * @param d the greatest distance a node can be from a specified nodes to be included in the subgraph
	 * @return A copy of a subgraph of this NodeStructure which involves specified and all 
	 * satisfied neighbor nodes
	 */
	public  NodeStructure getSubgraph(Collection<Node> nodes,int d);
	
	/**
	 * Returns a copy of a subgraph of this {@link NodeStructure}.
	 * The subgraph will contain all specified nodes with activation at or above specified threshold if 
	 * they are currently present in the NodeStructure.
	 * Additionally all other nodes having distance, the number of links, less or equal to d from the specified nodes and 
	 * that have sufficient activation are part of the subgraph.
	 * If a Node has insufficient activation then the depth-first search will not continue further from that Node.
	 * Finally all links that connect two {@link Linkable} elements that are both d or less from the specified nodes are included in the subgraph.
	 * @param nodes the nodes which will be the roots from which the subgraph will be formed
	 * @param d the greatest distance a node can be from a specified nodes to be included in the subgraph
	 * @param threshold activation requirement for a node to be part of the subgraph. 
	 * @return A copy of a subgraph of this NodeStructure which involves specified and all 
	 * satisfied neighbor nodes
	 */
	public NodeStructure getSubgraph(Collection<Node> nodes, int d, double threshold);
}