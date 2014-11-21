/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.strategies;

import java.util.Map;

import edu.memphis.ccrg.lida.framework.initialization.Initializable;

/**
 * Default implementation of sigmoid decay.  Uses two parameters in activation calculation.
 * Can pass these parameters when the strategy is initialized. (see factoriesData.xml).
 * Alternatively, parameters can be passed in the decay method call.  
 * Formula used: 1 / (1 + exp(-a* x + c))
 * @author Javier Snaider
 * @author Ryan J. McCall
 *
 */
public class SigmoidDecayStrategy extends StrategyImpl implements DecayStrategy {

	private static final double DEFAULT_A = 1.0;
	private double a = DEFAULT_A;
	
	private static final double DEFAULT_C = 0.0;
	private double c = DEFAULT_C;
	
	private static final double epsilon = 1e-10;
	
	/**
	 * If this method is overridden, this init() must be called first! i.e. super.init();
	 * Will set parameters with the following names:<br/><br/>
     * 
     * <b>a</b> slope component of the decay function's linear scaling, 1 / (1 + exp(-a* x + c))<br/>
     * <b>c</b> intercept component of the decay function's linear scaling, 1 / (1 + exp(-a* x + c))<br/>
     * If any parameter is not specified its default value will be used.
     * 
     * @see Initializable
	 */
	@Override
	public void init() {
		a = (Double) getParam("a", DEFAULT_A);
		c = (Double) getParam("c", DEFAULT_C);
	}
	
	
    /**
     * Decays the current activation according to some internal decay function.
     * @param currentActivation activation of the entity before decay.
     * @param ticks The number of ticks to decay.
     * @param params optionally accepts 2 double parameters of sigmoid activation calculation.
     * @return new activation 
     */
	@Override
	public double decay(double currentActivation, long ticks, Object... params) {
		double aa = a;
		double cc = c;
		if(params.length == 2){
			aa = (Double) params[0];
			cc = (Double) params[1];
		}
		return calcActivation(currentActivation, ticks, aa, cc);
	}

	
	/**
	 * Decays the current activation according to some internal decay function.
	 * @param currentActivation activation of the entity before decay.
	 * @param ticks how much time has passed since last decay
	 * @param params optionally accepts 2 parameters of sigmoid activation calculation.
	 * @return new activation amount
	 */
	@Override
	public double decay(double currentActivation, long ticks,
			Map<String, ? extends Object> params) {
		double aa = a;
		double cc = c;
		if(params != null && params.containsKey("a") && params.containsKey("c")){
			aa = (Double) params.get("a");
			cc = (Double) params.get("c");
		}
		return calcActivation(currentActivation, ticks, aa, cc);
	}
	
	/* To calculate activation value of decay operation by sigmoid strategy
	 * @param curActiv current activation
	 * @param ticks parameter of ticks
	 * @param aa parameter of M (default value is 1.0)
	 * @param cc parameter of M (default value is 0.0)
	 * @return Calculated activation value
	 */
	private double calcActivation(double curActiv, long ticks,
			double aa, double cc) {
		double curExcitation = -(Math.log((1.0 + epsilon - curActiv)/(curActiv + epsilon)) + cc) / aa - ticks;
		return 1/(1 + Math.exp(-(aa * curExcitation + cc)));
	}

}
