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
 * Basic {@link ExciteStrategy} governed by a linear curve.
 * @author Ryan J. McCall
 * @author Javier Snaider
 */
public class LinearExciteStrategy extends StrategyImpl implements ExciteStrategy {
	
	/*
	 * The default slope
	 * 
	 */
	private static final double DEFAULT_M = 1.0;

	/*
	 * The slope of this linear curve.
	 */
	private double m;

	/**
	 * Creates a new instance of LinearCurve. Values for slope and intercept are
	 * set to the default ones.
	 */
	public LinearExciteStrategy() {
		m = DEFAULT_M;
	}
	
	/**
	 * If this method is overridden, this init() must be called first! i.e. super.init();
	 * Will set parameters with the following names:<br/><br/>
     * 
     * <b>m</b> slope of the excite function<br/>
     * If any parameter is not specified its default value will be used.
     * 
     * @see Initializable
	 */
	@Override
	public void init() {
		m = (Double) getParam("m", DEFAULT_M);
	}
	
	
	/**
     * Excites the current activation according to some internal excite function.
     * @param currentActivation activation of the entity before excite.
     * @param excitation amount of activation to adds
     * @param params optionally accepts 1 double parameter specifying the slope of
     * excitation and activations.
     * @return new activation amount
     */
	@Override
	public double excite(double currentActivation, double excitation, Object... params) {
		double mm = m;
		if (params!= null && params.length != 0) {
			mm = (Double) params[0];
		}
		
		return calcActivation(currentActivation, excitation, mm);
	}
	
	
	/**
	 * Excites the current activation according to some internal excite function.
	 * @param currentActivation activation of the entity before excite.
	 * @param excitation amount of activation to adds
	 * @param params optionally accepts 1 parameter specifying the slope of
     * excitation and activations.
	 * @return new activation amount
	 */
	@Override
	public double excite(double currentActivation, double excitation, Map<String, ?> params) {
		double mm = m;
		if(params != null && params.containsKey("m")){
			mm = (Double) params.get("m");
		}
		
		return calcActivation(currentActivation, excitation, mm);
	} 

	/* To calculate activation value of excite operation by linear strategy
	 * @param currentActivation currentActivation current activation
	 * @param excitation parameter of excitation
	 * @param mm parameter of slope (default value is 1.0)
	 * @return Calculated activation value
	 */
	private double calcActivation(double currentActivation, double excitation,
			double mm) {
		currentActivation += mm * excitation;
		if(currentActivation > 1.0){
			return 1.0;
		}else if(currentActivation < 0.0){
			return 0.0;
		}
		return currentActivation;
	}

}
