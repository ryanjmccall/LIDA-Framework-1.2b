package edu.memphis.ccrg.lida.framework.shared;

/**
 * A marker of Node type. Different types are treated differently in {@link NodeStructure}.
 * This feature is not fully implemented in this framework version.
 * @author Ryan J. McCall
 */
public enum NodeType {
	
	/**
	 * Catch-all type
	 */
	defaultType,
	/**
	 * {@link Node}s that represent objects
	 */
	object,
	/**
	 * {@link Node}s that represent events
	 */
	event,
	/**
	 * {@link Node}s that represent built-in need feelings e.g. thirst, hunger
	 */
	needFeeling,
	/**
	 * {@link Node}s that represent interpretive (appraisal) feelings, e.g. sweet, sour
	 */
	interprativeFeeling

}
