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

import edu.memphis.ccrg.lida.globalworkspace.BroadcastContent;
import edu.memphis.ccrg.lida.workspace.WorkspaceContent;

/**
 * An immutable NodeStructureImpl.  Throws {@link UnsupportedOperationException} if any methods
 * which modify {@link NodeStructureImpl} are called.  
 * @author Ryan J. McCall
 */
public class UnmodifiableNodeStructureImpl implements NodeStructure, BroadcastContent, WorkspaceContent{
	
	private NodeStructure ns;
	
	/**
	 * Default Constructor.
	 * @param src supplied NodeStructure
	 */
	public UnmodifiableNodeStructureImpl(NodeStructure src){
		if(src != null){
			ns = src;
		}else{
			ns = new NodeStructureImpl();
		}
	}

	/**
	 * Copy Constructor.
	 * @param src supplied NodeStructure
	 * @param shouldCopy If true, the supplied NodeStructure will be copied.  Otherwise supplied NodeStructure
	 * will be used directly.
	 */
	public UnmodifiableNodeStructureImpl(NodeStructure src, boolean shouldCopy){
		if(src != null){
			if (shouldCopy){
				ns = src.copy();
			}else{
				ns = src;			
			}
		}else{
			ns = new NodeStructureImpl();
		}
	}

	/**
	 * Returns true if both NodeStructures have the same nodes and links.
	 * @param o Object
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if(o instanceof NodeStructure){
			NodeStructure otherNs = (NodeStructure) o;
			if(getNodeCount() != otherNs.getNodeCount() || 
					getLinkCount() != otherNs.getLinkCount()){
				return false;
			}
			
			for (Node n2 : otherNs.getNodes()) {
				if(!containsNode(n2.getId())){
					return false;
				}
			}
			for (Link l2 : otherNs.getLinks()) {
				if(!containsLink(l2.getExtendedId())){
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		//Generate a value for nodes based on individual node id and
		//the number of nodes total.
		Long aggregateNodeHash = 0L;
		for(Node n: ns.getNodes()){
			aggregateNodeHash += n.hashCode();			
		}	
		aggregateNodeHash = aggregateNodeHash * 31 + ns.getNodeCount() * 37;
		
		//Generate a value for links based on individual link id and
		//the number of links total.
		Long aggregateLinkHash = 0L;
		for(Link l: ns.getLinks()){
			aggregateLinkHash += l.hashCode();
		}
		aggregateLinkHash = aggregateLinkHash * 41 + ns.getLinkCount() * 43;
			
		int hash = 47 + aggregateNodeHash.hashCode();
		return hash * 53 + aggregateLinkHash.hashCode();
	}

	@Override
	public NodeStructure copy(){
		return new UnmodifiableNodeStructureImpl(ns, true);
	}
	
	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public Link addDefaultLink(Link l) {
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
	}
	
	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public Link addDefaultLink(int idSource, ExtendedId idSink,
			LinkCategory type, double activation, double removalThreshold){
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
	}

	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public Link addDefaultLink(int idSource, int idSink,
			LinkCategory type, double activation, double removalThreshold){
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
	}

	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public Collection<Link> addDefaultLinks(Collection<Link> links) {
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
	}

	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public Node addDefaultNode(Node n) {
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
	}

	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public Link addLink(Link l, String linkType) {
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
	}

	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public Node addNode(Node n, String factoryType) {
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
	}

	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public Collection<Node> addDefaultNodes(Collection<Node> nodes) {
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
	}

	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public void removeLink(Link l) {
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
	}

	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public void removeLinkable(Linkable l) {
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
	}

	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public void removeNode(Node n) {
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
	}

	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public void removeLinkable(ExtendedId id){
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
		
	}
	
	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public void clearLinks(){
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
	}

	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public void clearNodeStructure(){
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
	}

	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public void mergeWith(NodeStructure ns) {
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
	}

	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public Link addDefaultLink(Node source, Linkable sink,
			LinkCategory category, double activation, double removalThreshold) {
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
	}

	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public Node addDefaultNode(String label, double a, double rt) {
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
	}

	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public Link addLink(String type, int srcId, ExtendedId snkId,
			LinkCategory cat, double a, double rt) {
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
	}

	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public Node addNode(String type, String label, double a, double rt) {
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
	}
	
	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public Link addLink(String type, Node src, Linkable sink, LinkCategory cat,
			double a, double rt) {
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
	}

	/**
	 * @throws UnsupportedOperationException Cannot modify this object once created.
	 */
	@Override
	public void decayNodeStructure(long ticks) {
		throw new UnsupportedOperationException("UnmodifiableNodeStructure cannot be modified");
	}

	@Override
	public boolean containsLink(Link l) {
		return ns.containsLink(l);
	}

	@Override
	public boolean containsLink(ExtendedId id) {
		return ns.containsLink(id);
	}

	@Override
	public boolean containsLinkable(Linkable l) {
		return ns.containsLinkable(l);
	}

	@Override
	public boolean containsLinkable(ExtendedId id) {
		return ns.containsLinkable(id);
	}

	@Override
	public boolean containsNode(Node n) {
		return ns.containsNode(n);
	}

	@Override
	public boolean containsNode(int id) {
		return ns.containsNode(id);
	}

	@Override
	public boolean containsNode(ExtendedId id) {
		return ns.containsNode(id);
	}

	@Override
	public Set<Link> getAttachedLinks(Linkable l) {
		return ns.getAttachedLinks(l);
	}

	@Override
	public Set<Link> getAttachedLinks(Linkable linkable, LinkCategory cat) {
		return ns.getAttachedLinks(linkable, cat);
	}

	@Override
	public Map<Linkable, Link> getConnectedSinks(Node n) {
		return ns.getConnectedSinks(n);
	}

	@Override
	public Map<Node, Link> getConnectedSources(Linkable linkable) {
		return ns.getConnectedSources(linkable);
	}

	@Override
	public String getDefaultLinkType() {
		return ns.getDefaultLinkType();
	}

	@Override
	public String getDefaultNodeType() {
		return ns.getDefaultNodeType();
	}

	@Override
	public Link getLink(ExtendedId ids) {
		return ns.getLink(ids);
	}

	@Override
	public int getLinkCount() {
		return ns.getLinkCount();
	}

	@Override
	public Linkable getLinkable(ExtendedId eid) {
		return ns.getLinkable(eid);
	}

	@Override
	public int getLinkableCount() {
		return ns.getLinkableCount();
	}

	@Override
	public Map<Linkable, Set<Link>> getLinkableMap() {
		return ns.getLinkableMap();
	}

	@Override
	public Collection<Linkable> getLinkables() {
		return ns.getLinkables();
	}

	@Override
	public Collection<Link> getLinks() {
		return ns.getLinks();
	}

	@Override
	public Set<Link> getLinks(LinkCategory cat) {
		return ns.getLinks(cat);
	}

	@Override
	public Node getNode(int id) {
		return ns.getNode(id);
	}

	@Override
	public Node getNode(ExtendedId eid) {
		return ns.getNode(eid);
	}

	@Override
	public int getNodeCount() {
		return ns.getNodeCount();
	}

	@Override
	public Collection<Node> getNodes() {
		return ns.getNodes();
	}
	
	@Override
	public String toString(){
		return ns.toString();
	}

	@Override
	public NodeStructure getSubgraph(Collection<Node> nodes, int d) {
		return ns.getSubgraph(nodes, d);
	}

	@Override
	public NodeStructure getSubgraph(Collection<Node> nodes, int d,
			double threshold) {
		return ns.getSubgraph(nodes, d, threshold);
	}
}
