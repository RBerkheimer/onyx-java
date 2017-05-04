package org.onyxplatform.api.java.instance;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.IPersistentMap;
import clojure.lang.PersistentVector;

import org.onyxplatform.api.java.OnyxNames;
import org.onyxplatform.api.java.OnyxMap;
import org.onyxplatform.api.java.Catalog;
import org.onyxplatform.api.java.Task;
import org.onyxplatform.api.java.Job;

import org.onyxplatform.api.java.utils.MapFns;

public class BindUtils implements OnyxNames {

	protected static IFn instcatFn;
	protected static IFn releaseFn;
	protected static IFn releaseAllFn;

	/**
 	* Loads the clojure namespaces.
 	*/
	static {
    		IFn requireFn = Clojure.var(CORE, Require);
		requireFn.invoke(Clojure.read(INSTANCE_CATALOG));
		instcatFn = Clojure.var(INSTANCE_CATALOG, CreateMethod);
		releaseFn = Clojure.var(INSTANCE_CATALOG, ReleaseInst);
		releaseAllFn = Clojure.var(INSTANCE_CATALOG, ReleaseAllInst);
	}

	public static Catalog addFn(Catalog catalog, String taskName, 
				    int batchSize, int batchTimeout,
				    String fqClassName, IPersistentMap ctrArgs) 
	{
		IPersistentMap methodCat = (IPersistentMap) instcatFn.invoke(taskName, 
								             batchSize, batchTimeout, 
							  	             fqClassName, ctrArgs);
		OnyxMap e = MapFns.toOnyxMap(methodCat);
		Task methodTask = new Task(e);
		return catalog.addTask(methodTask);
	}

	public static void releaseInstance(Task task) {
		IPersistentMap e = task.toMap();
		releaseFn.invoke(e);
		System.gc();
	}

	public static void releaseInstances(Job j) {
		PersistentVector at = j.getCatalog().tasks();
		releaseAllFn.invoke(at);
		System.gc();
	}
}
