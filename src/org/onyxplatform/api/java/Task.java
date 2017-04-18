package org.onyxplatform.api.java;

import clojure.lang.IPersistentMap;
import java.util.Map;

/**
 * A task specifies a map that represents a single onyx unit of work. This
 * corresponds to an actual system function and piece of code to run in the
 * context of an onyx job.
 * Tasks derive from OnyxEntity.
 */
public class Task extends OnyxEntity
{
	protected static String coerceKw = OnyxCatalogEntry;

	/**
	 * Creates a new Task object using OnyxEntity superconstructor.
	 * @return new Task object
	 */
	public Task() {
	}

	/**
	 * Creates a new Task object using an existing content map.
	 * Uses OnyxEntity superconstructor.
	 * @param  PersistentHashMap ent           existing map to use for new Task
	 * @return                   new Task object
	 */
	public Task(Task task) {
    		super(task.entry);
	}


	public Task(OnyxEntity e) {
		super(e);
	}

	/**
	 * Coerces Task object content map into proper onyx Task.
	 * Returns the onyx representation without altering the existing content map.
	 * @param  Map<String, Object>       jMap content map to coerce
	 * @return             onyx representation of content map
	 */
	protected IPersistentMap coerce(Map<String, Object> jMap) {
		return (IPersistentMap) castTypesFn.invoke(coerceKw,jMap);
	}

}
