/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.framework.initialization;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is intended for use by Initializers during initialization only.
 * It allows Initializers to access the same attributes. Attributes can be
 * added by an Initializer and retrieved later by any other Initializer.
 * 
 * @author Javier Snaider
 * @author Ryan J. McCall
 *
 */
public class GlobalInitializer {

    /*
     * Sole instance of this class that will be used.
     */
    private static final GlobalInitializer instance = new GlobalInitializer();
    private final Map<String, Object> globalAttributes = new HashMap<String, Object>();

    /**
     * Returns the sole instance of this class. Implements the Singleton
     * pattern.
     *
     * @return instance sole instance of this class
     */
    public static GlobalInitializer getInstance() {
        return instance;
    }

    /*
     * Creates the initializer
     */
    private GlobalInitializer() {
    }

    /**
     * Returns the value associated with a key
     * @param key the key
     * @return the value associated with key
     */
    public Object getAttribute(String key) {
        return globalAttributes.get(key);
    }

    /**
     * Sets an attribute
     * @param key the key of the attribute
     * @param value the value to associate with key
     */
    public void setAttribute(String key, Object value) {
        globalAttributes.put(key, value);
    }

    /**
     * Removes the attribute associated with 
     * @param key the key
     * @return The last value of the attribute
     */
    public Object removeAttribute(String key) {
        return globalAttributes.remove(key);
    }

    /**
     * Deletes all attributes
     */
    public void clearAttributes() {
        globalAttributes.clear();
    }
}
