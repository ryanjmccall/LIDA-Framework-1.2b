package edu.memphis.ccrg.lida.workspace.workspacebuffers;

import edu.memphis.ccrg.lida.globalworkspace.BroadcastListener;
import edu.memphis.ccrg.lida.workspace.Workspace;
import edu.memphis.ccrg.lida.workspace.WorkspaceContent;

/**
 * A {@link WorkspaceBuffer} storing the recent contents of
 * consciousness. It is a submodule of the {@link Workspace}. 
 * @author Ryan J. McCall
 * @author Javier Snaider
 *
 */
public interface BroadcastQueue extends WorkspaceBuffer, BroadcastListener {

	/**
	 * Returns content of specified position
	 * @param index position in the queue
	 * @return {@link WorkspaceContent} at index position or null
	 */
	public WorkspaceContent getPositionContent(int index);
	
}
