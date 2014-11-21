package edu.memphis.ccrg.lida.framework.shared.activation;


/**
 * An object with desirability.
 * @author Ryan J. McCall
 */
public interface Desirable {

	/**
	 * Default desirability for {@link Desirable}.
	 */
	public static final double DEFAULT_DESIRABILITY = 0.0;
	
	/**
	 * Returns the current desirability of this node.
	 * 
	 * @return a double signifying the degree to which this node is desired by
	 *         the agent
	 */
	public double getDesirability();

	/**
	 * Sets node desirability.
	 * 
	 * @param d
	 *            degree to which this node is desired by the agent
	 */
	public void setDesirability(double d);
    
    /**
     * Returns the total desirability of this {@link Desirable}.
     * @return The total desirability. 
     * It should return the current desirability if no base-level desirability is used.
     */
	public double getTotalDesirability();
	
	/**
	 * Gets net desirability
	 * 
	 * @return the difference between desirability and activation
	 */
	public double getNetDesirability();
	
}
