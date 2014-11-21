/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.attentioncodelets;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.FrameworkModule;
import edu.memphis.ccrg.lida.framework.shared.NodeStructure;
import edu.memphis.ccrg.lida.framework.shared.activation.LearnableImpl;
import edu.memphis.ccrg.lida.framework.tasks.CodeletImpl;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;
import edu.memphis.ccrg.lida.globalworkspace.Coalition;
import edu.memphis.ccrg.lida.globalworkspace.CoalitionImpl;
import edu.memphis.ccrg.lida.globalworkspace.GlobalWorkspace;
import edu.memphis.ccrg.lida.workspace.workspacebuffers.WorkspaceBuffer;

/**
 * Abstract implementation of {@link AttentionCodelet} that checks the CSM for desired
 * content.  If this is found it creates a
 * {@link Coalition} and adds it to the {@link GlobalWorkspace}.
 * 
 * @author Ryan J. McCall
 * 
 */
public abstract class AttentionCodeletImpl extends CodeletImpl implements
        AttentionCodelet {

    private static final Logger logger = Logger.getLogger(AttentionCodeletImpl.class.getCanonicalName());
    private static final int DEFAULT_CODELET_REFRACTORY_PERIOD = 50;
    private int codeletRefractoryPeriod;

    /**
     * Where codelet will look for and retrieve sought content from
     */
    protected WorkspaceBuffer currentSituationalModel;
    /**
     * where {@link Coalition}s will be added
     */
    protected GlobalWorkspace globalWorkspace;

    /**
     * If this method is overridden, this init must be called first! i.e. super.init();
	 * Will set parameters with the following names:<br/><br/>
     * 
     * <b>refractoryPeriod</b> period in ticks that will pass after this codelet creates a coaltion before it can create another<br/> 
     * <br/> 
     * 
     * If any parameter is not specified its default value will be used.
     * @see LearnableImpl#init()
	 */
    @Override
    public void init() {
    	super.init();
        Integer refractoryPeriod = (Integer) getParam("refractoryPeriod", DEFAULT_CODELET_REFRACTORY_PERIOD);
        setRefractoryPeriod(refractoryPeriod);
    }

    @Override
    public void setAssociatedModule(FrameworkModule module, String usage) {
        if (module instanceof WorkspaceBuffer) {
            currentSituationalModel = (WorkspaceBuffer) module;
        } else if (module instanceof GlobalWorkspace) {
            globalWorkspace = (GlobalWorkspace) module;
        } else {
            logger.log(Level.WARNING, "module {1} cannot be associated",
                    new Object[]{TaskManager.getCurrentTick(), module});
        }
    }

    /**
     * If sought content is found it the CSM, then retrieve it
     * and create a coalition from it finally adding it to the
     * {@link GlobalWorkspace}.
     */
    @Override
    protected void runThisFrameworkTask() {
        if (bufferContainsSoughtContent(currentSituationalModel)) {
            NodeStructure csmContent = retrieveWorkspaceContent(currentSituationalModel);
            if(csmContent == null){
            	logger.log(Level.WARNING, "Null WorkspaceContent returned in {1}. Coalition cannot be formed.",
            			new Object[]{TaskManager.getCurrentTick(), this});
            }else if (csmContent.getLinkableCount() > 0) {
                Coalition coalition = new CoalitionImpl(csmContent, this);
                globalWorkspace.addCoalition(coalition);
                logger.log(Level.FINER, "{1} adds new coalition with activation {2}",
                        new Object[]{TaskManager.getCurrentTick(), this, coalition.getActivation()});
                setNextTicksPerRun(codeletRefractoryPeriod);
            }
        }
    }

    @Override
    public void setRefractoryPeriod(int ticks) {
        if (ticks > 0) {
            codeletRefractoryPeriod = ticks;
        } else {
            codeletRefractoryPeriod = DEFAULT_CODELET_REFRACTORY_PERIOD;
            logger.log(Level.WARNING,
                    "refractory period must be positive, using default value",
                    TaskManager.getCurrentTick());
        }

    }

    @Override
    public int getRefractoryPeriod() {
        return codeletRefractoryPeriod;
    }
}