package edu.memphis.ccrg.lida.framework.initialization;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * Default implementation of {@link Initializable}
 * @author Ryan J. McCall
 * @author Javier Snaider
 */
public class InitializableImpl implements Initializable {

	private static final Logger logger = Logger.getLogger(InitializableImpl.class.getCanonicalName());
	private Map<String, ?> parameters;
	
	@Override
	public void init(Map<String, ?> params) {
		parameters = params;
		init();
	}

	/**
	 * This is a convenience method for custom initialization. It is called from {@link #init(Map)}. 
	 * Subclasses can overwrite this method and call {@link #getParam(String, Object)} to access parameters by name.
	 * If this method is overridden, the init of the superclass must be called first.
	 */
	@Override
	public void init() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getParam(String name, T defaultValue) {
		if(defaultValue == null){
			logger.log(Level.SEVERE, "Illegal argument: Default value cannot be null",
						TaskManager.getCurrentTick());
			throw new IllegalArgumentException("Illegal argument: Default value cannot be null");
		}
		
		T value = null;
		if (parameters != null) {
			if(parameters.containsKey(name)){
				Object paramValue = parameters.get(name);
				if(paramValue == null){
					logger.log(Level.WARNING, "Parameter with name {1} has value null\nUsing default parameter value", 
							new Object[]{TaskManager.getCurrentTick(), name});
				}else{
					Class<?> classType = defaultValue.getClass();
					if(classType.isInstance(paramValue)){
						value=(T)paramValue;
					}else{
						logger.log(Level.WARNING, 
								"Parameter with name {1} has type {2} but type {3} was expected. Returning default value",
								new Object[]{TaskManager.getCurrentTick(),name, 
								paramValue.getClass().getCanonicalName(),
								classType});					
					}
				}
			}else{
				logger.log(Level.WARNING, "Cannot find parameter with name: \"{1}\" for Initializable: \"{2}\". " +
						"\nUsing default parameter value. If this is an error check the parameter name in the configuration files",
						new Object[]{TaskManager.getCurrentTick(),name, toString()});
			}
		}else{
			logger.log(Level.WARNING, "Parameters for {1} have not been initialized", 
					new Object[]{TaskManager.getCurrentTick(), toString()});
		}
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}
	
	@Override
	public boolean containsParameter(String key){
		return parameters.containsKey(key);
	}

	@Override
	public Map<String, ?> getParameters() {
		return (parameters!=null)?Collections.unmodifiableMap(parameters):null;
	}

}
