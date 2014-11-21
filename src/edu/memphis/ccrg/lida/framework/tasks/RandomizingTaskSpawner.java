package edu.memphis.ccrg.lida.framework.tasks;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A {@link TaskSpawner} which randomizes the execution of tasks. For each execution, a task's
 * nextTicksPerRun is randomized using a uniform distribution.<br/>
 * To modify this distribution override the method {@link #randomizeTicksPerRun(long)}.
 *  
 * @author Javier Snaider
 * @author Ryan J. McCall
 */
public class RandomizingTaskSpawner extends TaskSpawnerImpl {
	
	private static final Logger logger = Logger.getLogger(RandomizingTaskSpawner.class.getCanonicalName());
	private static final double DEFAULT_VARIATION = 0.1;
	private double variation = DEFAULT_VARIATION;
	
	/**
	 * Sets the following parameter:
	 * <b>variation type=double</b> the amount of possible variation in a randomized value of ticksPerRun from its initial value<br/>
	 */
	@Override
	public void init(){
		super.init();
		variation = getParam("variation", DEFAULT_VARIATION);
	}

	/**
	 * Gets variation
	 * @return the variation
	 */
	public double getVariation() {
		return variation;
	}

	/**
	 * Sets variation
	 * @param v the variation to set
	 */
	public void setVariation(double v) {
		if(v >= 0.0 && v <= 1.0){
			variation = v;
		}else{
			logger.log(Level.WARNING, "Variation must be in [0,1]", TaskManager.getCurrentTick());
		}
	}

	/**
	 * Randomizes ticksPerRun by variation percent. Uses uniform distribution for random value.
	 * @param ticksPerRun parameter to randomize
	 * @return new non-zero ticksPerRun value 
	 */
	public long randomizeTicksPerRun(long ticksPerRun){
		long delta = Math.round((Math.random()-0.5)*2.0 * variation * ticksPerRun);
		long newTicks = ticksPerRun + delta;
		return (newTicks > 0)? newTicks : 1;
	}

	/**
	 * First randomizes task's ticksPerRun and then adds and runs it.
	 * @param task the task to add.
	 */
	@Override
	public void addTask(FrameworkTask task) {
		task.setNextTicksPerRun(randomizeTicksPerRun(task.getNextTicksPerRun()));
		super.addTask(task);
	}
	
	/**
	 * First randomizes task's ticksPerRun and then adds and runs it then calls {@link TaskSpawnerImpl#receiveFinishedTask(FrameworkTask)}.
	 */
	@Override
	public void receiveFinishedTask(FrameworkTask task) {
		task.setNextTicksPerRun(randomizeTicksPerRun(task.getNextTicksPerRun()));
		super.receiveFinishedTask(task);
	}
}
