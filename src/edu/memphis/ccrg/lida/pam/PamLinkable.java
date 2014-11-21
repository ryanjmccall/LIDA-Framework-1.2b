/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.pam;

import edu.memphis.ccrg.lida.framework.shared.Linkable;
import edu.memphis.ccrg.lida.framework.shared.activation.Learnable;

/**
 * A {@link Learnable} {@link Linkable}, a {@link PamNode} or 
 * {@link PamLink}
 * @author Ryan J. McCall
 * @see PamNode
 * @see PamLink
 * @see PerceptualAssociativeMemory
 */
public interface PamLinkable extends Linkable, Learnable{

}
