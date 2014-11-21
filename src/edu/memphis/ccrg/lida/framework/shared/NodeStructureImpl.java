/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.shared;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.shared.activation.Activatible;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.globalworkspace.BroadcastContent;
import edu.memphis.ccrg.lida.pam.PamLink;
import edu.memphis.ccrg.lida.workspace.WorkspaceContent;

/**
 * Default implementation of {@link NodeStructure}. The source and sink of a
 * link must be present before it can be added. Links can connect two nodes
 * (simple link) or can connect a node and another SIMPLE link. Nodes and links
 * are copied when added. This prevents having the same node (object) in two
 * different NodeStructures.
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 * @author Daqi Dong
 * @author Pulin Agrawal
 * @see ExtendedId
 */
public class NodeStructureImpl implements NodeStructure, BroadcastContent,
		WorkspaceContent{

	private static final Logger logger = Logger
			.getLogger(NodeStructureImpl.class.getCanonicalName());

	/*
	 * Standard factory for new objects. Used to create copies when adding
	 * linkables to this NodeStructure
	 */
	private static ElementFactory factory = ElementFactory.getInstance();	
	
	/*
	 * Nodes contained in this NodeStructure indexed by their id
	 */
	private ConcurrentMap<Integer, Node> nodes = new ConcurrentHashMap<Integer, Node>();

	/*
	 * Links contained in this NodeStructure indexed by their id String.
	 */
	private ConcurrentMap<ExtendedId, Link> links = new ConcurrentHashMap<ExtendedId, Link>();

	/*
	 * Links that each Linkable (Node or Link) has.
	 */
	private ConcurrentMap<Linkable, Set<Link>> linkableMap = new ConcurrentHashMap<Linkable, Set<Link>>();

	/*
	 * Default Node type used.
	 */
	private String defaultNodeType;

	/*
	 * Default Link type used.
	 */
	private String defaultLinkType;

	/**
	 * Default constructor. Uses the default node and link types of the factory
	 */
	public NodeStructureImpl() {
		defaultNodeType = factory.getDefaultNodeType();
		defaultLinkType = factory.getDefaultLinkType();
	}

	/**
	 * Creates a new NodeStructureImpl with specified default Node type and link
	 * Type. If either is not in the factory the factory's defaults are used.
	 * 
	 * @param nodeType kind of node used in this NodeStructure
	 * 
	 * @param linkType kind of link used in this NodeStructure
	 * 
	 * @see ElementFactory
	 */
	public NodeStructureImpl(String nodeType, String linkType) {
		this();
		if (factory.containsNodeType(nodeType)) {
			defaultNodeType = nodeType;
		} else {
			logger.log(Level.SEVERE, "Unsupported Node type: {1}",
					new Object[]{TaskManager.getCurrentTick(),nodeType});
			throw new IllegalArgumentException();
		}

		if (factory.containsLinkType(linkType)) {
			defaultLinkType = linkType;
		} else {
			logger.log(Level.SEVERE, "Unsupported Link type: {1}",
					new Object[]{TaskManager.getCurrentTick(),linkType});
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Copy constructor. Specifies Node and Link types used to copy Node and
	 * Links. Specified types are the default types for the copy.
	 * 
	 * @param ns
	 *            original NodeStructure
	 * @see #mergeWith(NodeStructure)
	 */
	public NodeStructureImpl(NodeStructure ns) {
		this(ns.getDefaultNodeType(), ns.getDefaultLinkType());
		internalMerge(ns);
	}
	
	@Override
	public Node addDefaultNode(Node n) {
		return addNode(n, defaultNodeType);
	}

	@Override
	public Collection<Node> addDefaultNodes(Collection<Node> nodes) {
		if (nodes == null) {
			logger.log(Level.WARNING,
					"Cannot add nodes. Node collection is null", TaskManager
							.getCurrentTick());
			return null;
		}
		Collection<Node> storedNodes = new ArrayList<Node>();
		for (Node n : nodes) {
			if(n == null){
				continue;
			}
			Node stored = addNode(n, defaultNodeType);
			storedNodes.add(stored);
		}
		return storedNodes;
	}

	@Override
	public synchronized Node addNode(Node n, String type) {
		if (n == null) {
			logger.log(Level.WARNING, "Cannot add null Node.", TaskManager
					.getCurrentTick());
			return null;
		}
		if (!factory.containsNodeType(type)) {
			logger.log(Level.WARNING,
							"Factory does not contain node type {1}. Check that type is defined in factoryData.xml. Node {2} not added",
							new Object[]{TaskManager.getCurrentTick(),type,n});
			return null;
		}
		Node node = nodes.get(n.getId());
		if (node == null) {
			node = getNewNode(n, type);
			if (node != null) {
				nodes.put(node.getId(), node);
				linkableMap.put(node, new HashSet<Link>());
			} else {
				logger.log(Level.WARNING, "Could not create new node of type: {1} ",
						new Object[]{TaskManager.getCurrentTick(),type});
			}
		} else if(type.equals(node.getFactoryType())){
			if (node.getActivation() < n.getActivation()) {
				node.setActivation(n.getActivation());
			}
			node.updateNodeValues(n);
		} else {
			logger.log(Level.WARNING, "Cannot add Node {1} of type {2} because another Node {3} having a different type {4} and the same id is already present. Existing Node returned.", 
						new Object[]{TaskManager.getCurrentTick(),n,type,node,node.getFactoryType()});
			node = null;
		}
		return node;
	}
	
	@Override
	public synchronized Node addDefaultNode(String label, double a, double rt){
		return addNode(defaultNodeType, label, a, rt);
	}
	
	@Override
	public synchronized Node addNode(String type, String label, double a, double rt){
		Node n = factory.getNode(defaultNodeType, null, type);
		if(n != null){
			n.setLabel(label);
			n.setActivation(a);
			n.setActivatibleRemovalThreshold(rt);
			nodes.put(n.getId(), n);
			linkableMap.put(n, new HashSet<Link>());
		}
		return n;	
	}

	/**
	 * If copy is false, this method adds a already generated {@link Node}
	 *  to this NodeStructure without copying it.
	 * If copy is true, {@link NodeStructure#addDefaultNode(Node)} is used.
	 * If a Node with the same id is already in this NodeStructure, the new
	 * Node is not added.
	 * 
	 * This method is intended for internal use only. 
	 * @param n the Node to add
	 * @param shouldCopy determines if the node is copied or not.
	 * @return The Node stored in this NodeStructure
	 */
	protected Node addNode(Node n, boolean shouldCopy){
		if(shouldCopy){
			return addDefaultNode(n);
		}else if (n == null){
			logger.log(Level.WARNING, "Cannot add null Node.", TaskManager
					.getCurrentTick());
			return null;
		}else{
			Node node = nodes.get(n.getId());
			if (node == null) {
					node=n;
					nodes.put(node.getId(), node);
					linkableMap.put(node, new HashSet<Link>());
			} else {
				logger.log(Level.FINE,
						"Cannot add node, it is already in this NodeStructure.", TaskManager
								.getCurrentTick());				
			}
			return node;
		}
	}

	/**
	 * This method can be overwritten to customize the Node Creation.
	 * This implementation returns a new {@link Node} of specified type that is 
	 * compatible in the NodeStrucutreImpl. If an original node is specified the 
	 * new Node will copy the relevant attributes of the original. 
	 * 
	 * @param oNode The original Node or null
	 * @param desiredType the {@link ElementFactory} name of the desired node type
	 * @return a new Node if is no original was specified, or a copy of the specified original node
	 * @see ElementFactory#getNode(String, Node, String)
	 */
	protected Node getNewNode(Node oNode, String desiredType) {
		return factory.getNode(defaultNodeType, oNode, desiredType);
	}
	
	@Override
	public synchronized Link addDefaultLink(Link l) {
		return addLink(l, defaultLinkType);
	}

	@Override
	public Collection<Link> addDefaultLinks(Collection<Link> links) {
		if (links == null) {
			logger.log(Level.WARNING,
					"Cannot add links. Link collection is null", TaskManager
							.getCurrentTick());
			return null;
		}
		Collection<Link> storedLinks = new ArrayList<Link>();
		// Add simple links
		for (Link l : links) {
			if(l == null){
				continue;
			}
			if (l.isSimpleLink()) {
				Link addedLink = addDefaultLink(l);
				if(addedLink != null){
					storedLinks.add(addedLink);
				}
			}
		}
		// Add complex links
		for (Link l : links) {
			if(l == null){
				continue;
			}
			if (l.isSimpleLink() == false) {
				Link addedLink = addDefaultLink(l);
				if(addedLink != null){
					storedLinks.add(addedLink);
				}
			}
		}
		return storedLinks;
	}
	
	@Override
	public synchronized Link addLink(Link l, String type) {
		if (!factory.containsLinkType(type)) {
			logger.log(Level.WARNING,
							"Cannot add link {1} of type {2} because factory does not contain that Link type. Check that type is defined in factoryData xml file.",
							new Object[]{TaskManager.getCurrentTick(),l,type});
			return null;
		}
		if (!isLinkValid(l)) {
			return null;
		}
		double newActivation = l.getActivation();
		Link link = links.get(l.getExtendedId());
		if (link == null) {
			Node source = l.getSource();
			Node newSource = nodes.get(source.getId());
			Linkable sink = l.getSink();
			Linkable newSink = null;
			if (sink instanceof Node) {
				Node snode = (Node) sink;
				newSink = nodes.get(snode.getId());
			} else {
				newSink = links.get(sink.getExtendedId());
			}
			link = generateNewLink(l,type,newSource,newSink,l.getCategory(),
					newActivation, l.getActivatibleRemovalThreshold(), l.getGroundingPamLink());
		} else if(type.equals(link.getFactoryType())){
			if(newActivation > link.getActivation()){
				link.setActivation(newActivation);
			}
			link.updateLinkValues(l);
		}else {
			logger.log(Level.WARNING, "Cannot add Link {1} of type {2} because another Link {3} having a different type {4} and the same id is already present. Existing Link returned.", 
					new Object[]{TaskManager.getCurrentTick(),l,type,link,link.getFactoryType()});
			link = null;
		}
		return link;
	}

	@Override
	public synchronized Link addDefaultLink(Node source, Linkable sink,
			LinkCategory category, double activation, double removalThreshold) {
		if(source == null) {
			logger.log(Level.WARNING,
					"Cannot add link because source is null", TaskManager
							.getCurrentTick());
			return null;
		}
		if(sink == null){
			logger.log(Level.WARNING,
					"Cannot add link because sink is null", TaskManager
							.getCurrentTick());
			return null;
		}
		return addDefaultLink(source.getId(), sink.getExtendedId(), category,
				activation, removalThreshold);
	}

	@Override
	public synchronized Link addDefaultLink(int sourceId, ExtendedId sinkId,
			LinkCategory category, double activation, double removalThreshold) {
		return addLink(defaultLinkType, sourceId, sinkId, category, activation, removalThreshold);
	}

	@Override
	public synchronized Link addDefaultLink(int sourceId, int sinkId,
			LinkCategory cat, double activation, double removalThreshold) {
		if(cat == null){
			logger.log(Level.WARNING,
					"Cannot add new link because category is null.",TaskManager.getCurrentTick());
			return null;
		}
		if(!isConnectionValid(sourceId, new ExtendedId(sinkId))){
			return null;
		}
		Node source = getNode(sourceId);
		Linkable sink = getNode(sinkId);
		ExtendedId newLinkId = new ExtendedId(sourceId, sink.getExtendedId(),
				cat.getId());
		Link link = getLink(newLinkId);
		if (link == null) {
			link = generateNewLink(null, defaultLinkType, source, sink, cat,
					activation, removalThreshold, null);
		}else if(activation > link.getActivation()){
			link.setActivation(activation);
		}
		return link;
	}
	
	@Override
	public synchronized Link addLink(String type, Node src, Linkable sink, LinkCategory cat, double a, double rt){
		if(src == null){
			logger.log(Level.WARNING,
					"Cannot add link because source is null", TaskManager
							.getCurrentTick());
			return null;
		}
		if(sink == null){
			logger.log(Level.WARNING,
					"Cannot add link because sink is null", TaskManager
							.getCurrentTick());
			return null;	
		}
		return addLink(type, src.getId(), sink.getExtendedId(), cat, a, rt);
	}
	
	@Override
	public synchronized Link addLink(String type, int srcId, ExtendedId snkId,
			LinkCategory cat, double a, double rt){
		if (!factory.containsLinkType(type)) {
			logger.log(Level.WARNING,
							"Cannot add new link of type {2} because factory does not contain that Link type. Check that type is defined in factoryData xml file.",
							new Object[]{TaskManager.getCurrentTick(),type});
			return null;
		}
		if(cat == null){
			logger.log(Level.WARNING,
					"Cannot add new link because category is null.",TaskManager.getCurrentTick());
			return null;
		}
		if(!isConnectionValid(srcId, snkId)){
			return null;
		}
		ExtendedId newLinkId = new ExtendedId(srcId, snkId, cat
				.getId());
		Link link = getLink(newLinkId);
		if (link == null) {
			Node source = getNode(srcId);
			Linkable sink = getLinkable(snkId);
			link = generateNewLink(null, type, source, sink, cat,
					a, rt, null);
		}else if(type.equals(link.getFactoryType())){ 
			if(a > link.getActivation()){
				link.setActivation(a);
			}
		}else{
			logger.log(Level.WARNING, "Cannot add new Link of type {2} because another Link {3} having a different type {4} and the same id is already present. Existing Link returned.", 
					new Object[]{TaskManager.getCurrentTick(),type,link,link.getFactoryType()});
			link = null;
		}
		return link;
	}
	
	/*
	 * Returns true if Link l can currently be added to the NodeStructure.
	 * Calls isConnectionValid().
	 * @see #isConnectionValid
	 */
	private boolean isLinkValid(Link l){
		if (l == null) {
			logger.log(Level.WARNING, "Cannot add null", TaskManager
					.getCurrentTick());
			return false;
		}
		Node src = l.getSource();
		Linkable sink = l.getSink();
		if(src == null){
			logger.log(Level.WARNING,
					"Cannot add Link, its source is null.",
					TaskManager.getCurrentTick());
			return false;
		}
		if(sink == null){
			logger.log(Level.WARNING,
					"Cannot add Link, its sink is null.",
					TaskManager.getCurrentTick());
			return false;
		}
		return isConnectionValid(src.getId(), sink.getExtendedId());
	}
	
	/*
	 * Returns true if a Link from specified source to specified sink can be added
	 */
	private boolean isConnectionValid(int srcId, ExtendedId sinkId){
		if (!containsNode(srcId)) {
			logger.log(Level.WARNING,
					"Cannot add Link because its source is not present in this NodeStructure",
					TaskManager.getCurrentTick());
			return false;
		}
		if (!containsLinkable(sinkId)) {
			logger.log(Level.WARNING,
					"Cannot add Link because its sink is not present in this NodeStructure",
					TaskManager.getCurrentTick());
			return false;
		}
		if(sinkId.isComplexLink()){
			logger.log(Level.WARNING,
					"Cannot add Link because its sink is a complex Link, sinks must be a Node or simple Link.",
					TaskManager.getCurrentTick());
			return false;
		}
		if (sinkId.isNodeId() && sinkId.getSourceNodeId() == srcId) {
			logger.log(Level.WARNING,
					"Cannot add Link with the same source and sink.",
					TaskManager.getCurrentTick());
			return false;
		}
		return true;
	}

	/*
	 * Generates a new Link with specified type and values.
	 */
	private Link generateNewLink(Link link, String linkType, Node newSource,
			Linkable newSink, LinkCategory category, double activation,
			double removalThreshold, PamLink groundingPamLink) {
		Link newLink = getNewLink(link, linkType, newSource, newSink, category);
		if(newLink!=null){
			// set values of passed in parameters not handled by 'getNewLink'
			newLink.setActivation(activation);
			newLink.setActivatibleRemovalThreshold(removalThreshold);
			if(groundingPamLink != null){
				newLink.setGroundingPamLink(groundingPamLink);
			}
	
			links.put(newLink.getExtendedId(), newLink);
			if (!linkableMap.containsKey(newLink)) {
				linkableMap.put(newLink, new HashSet<Link>());
			}
	
			Set<Link> tempLinks = linkableMap.get(newSource);
			if (tempLinks == null) {
				tempLinks = new HashSet<Link>();
				linkableMap.put(newSource, tempLinks);
			}
			tempLinks.add(newLink);
	
			tempLinks = linkableMap.get(newSink);
			if (tempLinks == null) {
				tempLinks = new HashSet<Link>();
				linkableMap.put(newSink, tempLinks);
			}
			tempLinks.add(newLink);
		}else{
			logger.log(Level.WARNING, "Could not create new link of type: {1} ",
					new Object[]{TaskManager.getCurrentTick(),linkType});
		}
		return newLink;
	}

	/**
	 * This method can be overridden to customize the Link Creation. some of
	 * the parameter could be redundant in some cases.
	 * 
	 * @param oLink
	 *            original {@link Link}
	 * @param newType the {@link ElementFactory} name of the new {@link Link} type
	 * @param src
	 *            The new source
	 * @param snk
	 *            The new sink
	 * @param cat
	 *            the type of the link
	 * 
	 * @return The link to be used in this NodeStructure
	 */
	protected Link getNewLink(Link oLink, String newType, Node src,
			Linkable snk, LinkCategory cat) {
		Link newLink = factory.getLink(defaultLinkType, newType, src, snk,
				cat);
		if(newLink != null){
			newLink.updateLinkValues(oLink);
		}
		return newLink;
	}

	@Override
	public NodeStructure copy() {
		return new NodeStructureImpl(this);
	}

	@Override
	public void mergeWith(NodeStructure ns) {
		internalMerge(ns);
	}

	/*
	 * This allows subclasses of NodeStructure to override merge but still gives
	 * this class a merge to be called from the constructor.
	 */
	private void internalMerge(NodeStructure ns) {
		if (ns == null) {
			logger.log(Level.WARNING, "Cannot merge with null", TaskManager
					.getCurrentTick());
			return;
		}
		// Add nodes
		for(Node n: ns.getNodes()){
			addNode(n, n.getFactoryType());
		}

		Collection<Link> cl = ns.getLinks();
		// Add simple links
		for (Link l : cl) {
			if (l.isSimpleLink()) {
				addLink(l,l.getFactoryType());
			}
		}

		// Add complex links
		for (Link l : cl) {
			if (l.isSimpleLink() == false) {
				addLink(l,l.getFactoryType());
			}
		}
	}
	
	@Override
	public void removeNode(Node n) {
		removeLinkable(n);
	}

	@Override
	public void removeLink(Link l) {
		removeLinkable(l);
	}
	
	@Override
	public synchronized void removeLinkable(Linkable linkable) {
		// First check if the NS actually contains specified linkable to prevent
		// null pointers.
		if (!containsLinkable(linkable)) {
			return;
		}

		// Need to remove all links connected to the linkable specified to be
		// removed.
		Set<Link> tempLinks = linkableMap.get(linkable);
		if(tempLinks != null){ 
			//must put these links in another collection to prevent concurrent modification exception in a recursive call
			Set<Link> connectedLinks = new HashSet<Link>(tempLinks);
			for (Link connectedLink : connectedLinks) {
				// for all of the links connected to linkable
				removeLinkable(connectedLink);
			}
		}

		// finally remove the linkable and its links
		linkableMap.remove(linkable);
		if (linkable instanceof Node) {
			nodes.remove(((Node) linkable).getId());
		} else if (linkable instanceof Link) {
			//if removing a link then must also remove the 2 references to the link
			//get actual link object 
			Link aux = links.get(linkable.getExtendedId());
			//get and remove source's reference to link
			Set<Link> sourceLinks = linkableMap.get(aux.getSource());
			if (sourceLinks != null) {
				sourceLinks.remove(aux);
			}
			//get and remove sink's reference to link
			Set<Link> sinkLinks = linkableMap.get(aux.getSink());
			if (sinkLinks != null) {
				sinkLinks.remove(aux);
			}
			//finally remove the link from links map
			links.remove(linkable.getExtendedId());
		}
	}

	@Override
	public void removeLinkable(ExtendedId id) {
		if (!containsLinkable(id)) {
			return;
		}

		if (id.isNodeId()) {
			removeLinkable(nodes.get(id.getSourceNodeId()));
		} else {
			removeLinkable(links.get(id));
		}
	}

	@Override
	public synchronized void clearLinks() {
		for (Link l : links.values()) {
			removeLink(l);
		}
		links.clear();
	}

	@Override
	public synchronized void clearNodeStructure() {
		linkableMap.clear();
		nodes.clear();
		links.clear();
	}

	@Override
	public void decayNodeStructure(long ticks) {
		for (Linkable linkable : linkableMap.keySet()) {
			Activatible a = (Activatible) linkable;
			a.decay(ticks);
			if (a.isRemovable()) {
				removeLinkable(linkable);
			}
		}
	}

	@Override
	public Node getNode(int id) {
		return nodes.get(id);
	}

	@Override
	public Node getNode(ExtendedId id) {
		if (id == null) {
			return null;
		}
		if (id.isNodeId()) {
			return nodes.get(id.getSourceNodeId());
		} else {
			return null;
		}
	}

	@Override
	public Collection<Node> getNodes() {
		Collection<Node> aux = nodes.values();
		return (aux == null) ? null : Collections.unmodifiableCollection(aux);
	}
	
	@Override
	public int getNodeCount() {
		return nodes.size();
	}

	@Override
	public Link getLink(ExtendedId id) {
		return (id == null)? null:links.get(id);
	}

	@Override
	public Set<Link> getLinks(LinkCategory cat) {
		if(cat == null){
			return null;
		}
		Set<Link> results = new HashSet<Link>();
		for (Link l : links.values()) {
			if (cat.equals(l.getCategory())) {
				results.add(l);
			}
		}
			
		return Collections.unmodifiableSet(results);
	}

	@Override
	public Collection<Link> getLinks() {
		Collection<Link> aux = links.values();
		return (aux == null)? null: Collections.unmodifiableCollection(aux);
	}
	
	@Override
	public int getLinkCount() {
		return links.size();
	}

	
	@Override
	public Linkable getLinkable(ExtendedId ids) {
		if(ids == null){
			return null;
		}
		if (ids.isNodeId()) {
			return getNode(ids.getSourceNodeId());
		} else {
			return getLink(ids);
		}
	}

	@Override
	public Collection<Linkable> getLinkables() {
		return Collections.unmodifiableCollection(linkableMap.keySet());
	}

	@Override
	public Map<Linkable, Set<Link>> getLinkableMap() {
		return Collections.unmodifiableMap(linkableMap);
	}
	

	@Override
	public int getLinkableCount() {
		return linkableMap.size();
	}
	
	@Override
	public Set<Link> getAttachedLinks(Linkable lnk) {
		if (lnk == null) {
			return null;
		}
		Set<Link> aux = linkableMap.get(lnk);
		return (aux == null)? null: Collections.unmodifiableSet(aux);
	}

	@Override
	public Set<Link> getAttachedLinks(Linkable lnk, LinkCategory cat) {
		if (lnk == null || cat == null) {
			return null;
		}
		Set<Link> attachedLinks = linkableMap.get(lnk);
		if (attachedLinks == null) {
			return null;
		}
		Set<Link> results = new HashSet<Link>();
		for (Link l : attachedLinks) {
			if (cat.equals(l.getCategory())) {
				results.add(l);
			}
		}
		return Collections.unmodifiableSet(results);
	}

	@Override
	public Map<Node, Link> getConnectedSources(Linkable lnk) {
		if(lnk == null){
			return null;
		}		
		Set<Link> candidateLinks = linkableMap.get(lnk);
		Map<Node, Link> sourceLinkMap = new HashMap<Node, Link>();
		if (candidateLinks != null) {
			for (Link link : candidateLinks) {
				Node source = link.getSource();
				if (!source.equals(lnk)) {
					sourceLinkMap.put(source, link);
				}
			}
		}
		return Collections.unmodifiableMap(sourceLinkMap);
	}
	
	@Override
	public Map<Linkable, Link> getConnectedSinks(Node n) {
		if(n == null){
			return null;
		}
		Set<Link> candidateLinks = linkableMap.get(n);
		Map<Linkable, Link> sinkLinkMap = new HashMap<Linkable, Link>();
		if (candidateLinks != null) {
			for (Link link : candidateLinks) {
				Linkable sink = link.getSink();
				if (!sink.equals(n)) {
					sinkLinkMap.put(sink, link);
				}
			}
		}
		return Collections.unmodifiableMap(sinkLinkMap);
	}
	
	@Override
	public String getDefaultLinkType() {
		return defaultLinkType;
	}

	@Override
	public String getDefaultNodeType() {
		return defaultNodeType;
	}
	
	@Override
	public boolean containsNode(Node n) {
		return (n == null)? false : nodes.containsKey(n.getId());
	}

	@Override
	public boolean containsNode(int id) {
		return nodes.containsKey(id);
	}

	@Override
	public boolean containsNode(ExtendedId id) {
		if(id == null){
			return false;
		}
		return id.isNodeId() && nodes.containsKey(id.getSourceNodeId());
	}

	@Override
	public boolean containsLink(Link l) {
		return (l == null)? false : links.containsKey(l.getExtendedId());
	}

	@Override
	public boolean containsLink(ExtendedId id) {
		return (id == null)? false : links.containsKey(id);
	}

	@Override
	public boolean containsLinkable(Linkable l) {
		return (l == null)? false : linkableMap.containsKey(l);
	}

	@Override
	public boolean containsLinkable(ExtendedId id) {
		return (containsNode(id) || containsLink(id));
	}
	
	@Override
	public  NodeStructure getSubgraph(Collection<Node> nodes,int d) {
		return getSubgraph(nodes,d,0.0);
	}

	@Override
	public NodeStructure getSubgraph(Collection<Node> nodes,
			int d, double threshold) {
		if (nodes == null ){
			logger.log(Level.WARNING, "Collection of specified nodes are not available.",
					TaskManager.getCurrentTick());
			return null;
		}
		if (nodes.isEmpty()){
			logger.log(Level.WARNING, "Collection of specified nodes should not be empty.",
					TaskManager.getCurrentTick());
			return null;
		}
		if (d < 0){
			logger.log(Level.WARNING, "Desired distance should not be negative.",
					TaskManager.getCurrentTick());
			return null;
		}
		
		if (threshold < 0){
			logger.log(Level.WARNING, "Desired threshold should not be negative.",
					TaskManager.getCurrentTick());
			return null;
		}
		//	Distance should be not bigger than number of all links.
		if (d > getLinkCount()){
			d = getLinkCount();
		}
		
		//Preserve default Node and Link type of the originating NodeStructure
		NodeStructure subNodeStructure = new NodeStructureImpl(getDefaultNodeType(), getDefaultLinkType());		
		for (Node n : nodes) {
			//Add nodes to the sub node structure and scan from each node
			if(n != null){
				depthFirstSearch(n, d, subNodeStructure, threshold);
			}
		}
		//	Add all simple links to the sub node structure
		for (Node subNode : subNodeStructure.getNodes()) {
			//Get the simple links for each Node already in the subgraph
			Map<Node, Link> sources = getConnectedSources(subNode);
			for (Node n : sources.keySet()) {
				//Add the simple link only if its source is present in the subgraph
				if (subNodeStructure.containsNode(n)) {
					subNodeStructure.addLink(sources.get(n), sources.get(n).getFactoryType());
				}
			}
		}
		//Add all complex links.
		for (Node subNode : subNodeStructure.getNodes()) {
			// Get the potential complex links for every node present in the subgraph
			Map<Linkable, Link> sinks = getConnectedSinks(subNode);
			for (Linkable l : sinks.keySet()) {
				//If Linkable is a link and the sub graph contains it then there is a complex link to add. 
				if ((l instanceof Link) && subNodeStructure.containsLinkable(l)){
					subNodeStructure.addLink(sinks.get(l), sinks.get(l).getFactoryType());
				}
			}
		}
		return subNodeStructure;
	}
		
	/*
	 * @param currentNode One specified node that be considered as neighbor nodes
	 * or specified nodes in sub NodeStructure 
	 * @param step The distance between specified nodes and this current Node
	 * @param distanceLeftToGo The farthest distance between specified nodes and
	 * its neighbor nodes
	 * @param subNodeStructure Nodes contained in subNodeStructure. 
	 * @param threshold Lower bound of Node's activation
	 * It involves specified nodes and all neighbor nodes whose distance
	 * from one of specified nodes is not bigger than farthest distance 
	 * coming from arguments, and those nodes' activation is not lower than threshold.
	 * Also it involves all links between these above nodes.
	 */
	private void depthFirstSearch(Node currentNode, int distanceLeftToGo, 
			NodeStructure subNodeStructure, double threshold) {
		Node actual = getNode(currentNode.getId());
		if (actual != null && (actual.getActivation() >= threshold)){
			subNodeStructure.addNode(actual, actual.getFactoryType());
			//Get all connected Sinks
			Map<Linkable, Link> subSinks = getConnectedSinks(actual);
			Set<Linkable> subLinkables = subSinks.keySet();
			for (Linkable l : subLinkables) {
				if (l instanceof Node && 0 < distanceLeftToGo){
					depthFirstSearch((Node)l, distanceLeftToGo - 1, subNodeStructure, threshold);
				}
			}
			//Get all connected Sources
			Map<Node, Link> subSources = getConnectedSources(actual);
			Set<Node> parentNodes = subSources.keySet();
			for (Node n : parentNodes) {
				if (0 < distanceLeftToGo){
					depthFirstSearch(n, distanceLeftToGo - 1, subNodeStructure, threshold);
				}
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("Nodes (");
		for (Node n : nodes.values()) {
			result.append(n.toString()).append(",");
		}
		if(nodes.size() != 0){
			result.deleteCharAt(result.length()-1);
		}
		result.append(") Links (");
		for (Link l : links.values()) {
			result.append(l.toString()).append(",");
		}
		if(links.size() != 0){
			result.deleteCharAt(result.length()-1);
		}
		result.append(")");
		return result.toString();
	}

	/**
	 * Returns true if two NodeStructures are meaningfully equal, else false.
	 * Two NodeStructures are equal if they have the same exact nodes and links
	 * and the nodes and links are of the same type.
	 * 
	 * @param ns1
	 *            first {@link NodeStructure}
	 * @param ns2
	 *            second {@link NodeStructure}
	 * @return boolean if the {@link NodeStructure}s are equal
	 */
	public static boolean compareNodeStructures(NodeStructure ns1,
			NodeStructure ns2) {
		if(ns1 == null || ns2==null){
			return false;
		}
		if (ns1.getNodeCount() != ns2.getNodeCount()) {
			return false;
		}
		if (ns1.getLinkCount() != ns2.getLinkCount()) {
			return false;
		}
		for (Node n1 : ns1.getNodes()) {
			if (!ns1.containsNode(n1)) {
				return false;
			}
		}
		for (Link l1 : ns1.getLinks()) {
			if (!ns1.containsLink(l1)) {
				return false;
			}
		}
		return true;
	}
}