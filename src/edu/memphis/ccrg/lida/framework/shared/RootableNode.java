package edu.memphis.ccrg.lida.framework.shared;

import edu.memphis.ccrg.lida.framework.shared.activation.Desirable;

/**
 * A {@link Node} which unifies multiple nodes attached to it.
 * This feature is not fully implemented in this framework version.
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 */
public interface RootableNode extends Node, Desirable {

	/**
	 * Sets Node type.
	 * 
	 * @param t
	 *            {@link NodeType}
	 */
	public void setNodeType(NodeType t);

	/**
	 * Gets Node Type.
	 * 
	 * @return {@link NodeType}
	 */
	public NodeType getNodeType();
}
