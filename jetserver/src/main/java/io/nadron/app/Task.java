package io.nadron.app;

import io.nadron.service.TaskManagerService;

/**
 * Represents a task that can be executed in the game system. Any class that
 * implements this interface and submits instances to the
 * {@link TaskManagerService} instance will be managed by the container. It
 * will automatically store the task such that restarts of the server do not
 * stop recurring tasks from stopping. In future, this may also be used for
 * sending tasks from one server node to another during node shutdown etc.
 * 
 * @author Abraham Menacherry
 * 
 */
public interface Task extends Runnable
{
	/**
	 * @return returns the unique task id of the task. For future
	 *         implementations, this value has to be unique across multiple
	 *         server nodes.
	 */
	Object getId();

	/**
	 * @param id
	 *            Set the unique task id.
	 */
	void setId(Object id);
}
