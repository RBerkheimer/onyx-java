package org.onyxplatform.api.java.instance;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.IPersistentMap;
import clojure.lang.PersistentVector;
import java.lang.Thread;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.onyxplatform.api.java.Catalog;
import org.onyxplatform.api.java.Job;
import org.onyxplatform.api.java.Lifecycle;
import org.onyxplatform.api.java.OnyxMap;
import org.onyxplatform.api.java.OnyxNames;
import org.onyxplatform.api.java.Task;
import org.onyxplatform.api.java.utils.MapFns;
import java.util.UUID;

/**
 * BindUtils is a static utility class designed to work with User Classes
 * which extend the OnyxFn abstract class.
 * This utility provides a method which can add an object instance that is
 * derived from a user class to a job catalog.
 * It also provides methods related to memory management of these catalog objects,
 * allowing users to manually unload the instances when they are no longer used
 * by the job.
 */
public class BindUtils implements OnyxNames {

	protected static IFn makeInstanceTask;
    protected static IFn makeInstanceLifecycle;

	/**
 	* Loads the clojure namespaces.
 	*/
	static {
    	IFn requireFn = Clojure.var(CORE, Require);

		requireFn.invoke(Clojure.read(INSTANCE_CATALOG));
		makeInstanceTask = Clojure.var(INSTANCE_CATALOG, MakeInstanceTask);

        requireFn.invoke(Clojure.read(INSTANCE_LIFECYCLES));
        makeInstanceLifecycle = Clojure.var(INSTANCE_LIFECYCLES, MakeInstanceLifecycle);

	}

	/**
	 * Creates and adds an object instance to an existing Catalog object.
	 * The object instance is derived from a user class which extends
	 * the OnyxFn abstract class. To use this method, a name for the object
	 * must be provided, along with the fully qualified user base class,
	 * a map of arguments to use as constructor args for the class,
	 * and the environment parameters batchSize and batchTimeout.
	 * @param  job       The job to which the instance task will be added
	 * @param  taskName      a string to use as a name for the object instance task in the Catalog
	 * @param  batchSize     an integer describing the number of segments that will be read at a time
	 * @param  batchTimeout  an integer describing the longest amount of time (ms) that a task will wait before reading segments
	 * @param  fqClassName   a string naming the fully qualified user class to use in object instance creation
	 * @param  ctrArgs       an IPersistentMap containing arguments to use in the user class constructor
	 * @return                returns the updated catalog which includes the added task
	 */
	public static void addFn(Job job, String taskName, int batchSize, int batchTimeout,
				                String fqClassName, IPersistentMap ctrArgs) {
	IPersistentMap rawTaskMap = (IPersistentMap) makeInstanceTask.invoke(taskName, batchSize, batchTimeout, fqClassName, ctrArgs);
		OnyxMap taskMap = MapFns.toOnyxMap(rawTaskMap);
		Task task = new Task(taskMap);
		job.getCatalog().addTask(task);
        IPersistentMap rawLifecycleMap = (IPersistentMap) makeInstanceLifecycle.invoke(taskName, "basic");
        OnyxMap lifecycleMap = MapFns.toOnyxMap(rawLifecycleMap);
        Lifecycle lifecycle = new Lifecycle(lifecycleMap);
        job.getLifecycles().addLifecycle(lifecycle);
	}

    /**
     * Creates and adds an object instance to an existing Catalog object.
     * The object instance is derived from a user class which extends
     * the OnyxFn abstract class. To use this method, a name for the object
     * must be provided, along with the fully qualified user base class,
     * a map of arguments to use as constructor args for the class,
     * and the environment parameters batchSize and batchTimeout.
     * @param  catalog       the catalog object to which the new object instance will be added as a task
     * @param  taskName      a string to use as a name for the object instance task in the Catalog
     * @param  batchSize     an integer describing the number of segments that will be read at a time
     * @param  batchTimeout  an integer describing the longest amount of time (ms) that a task will wait before reading segments
     * @param  fqClassName   a string naming the fully qualified user class to use in object instance creation
     * @param  ctrArgs       an IPersistentMap containing arguments to use in the user class constructor
     * @return                returns the updated catalog which includes the added task
     */
    public static void addFn(Job job, String taskName, int batchSize, int batchTimeout,
                                String fqClassName, String ctrClassName, IPersistentMap ctrArgs) {
        IPersistentMap rawTaskMap = (IPersistentMap) makeInstanceTask.invoke(taskName, batchSize, batchTimeout, fqClassName, ctrClassName, ctrArgs);
        OnyxMap taskMap = MapFns.toOnyxMap(rawTaskMap);
		Task task = new Task(taskMap);
		job.getCatalog().addTask(task);
        IPersistentMap rawLifecycleMap = (IPersistentMap) makeInstanceLifecycle.invoke(taskName, "user");
        OnyxMap lifecycleMap = MapFns.toOnyxMap(rawLifecycleMap);
        Lifecycle lifecycle = new Lifecycle(lifecycleMap);
        job.getLifecycles().addLifecycle(lifecycle);
    }

	/**
	 * Returns an IFn representation of a dynamically loaded object instance derived
	 * from a user class extending OnyxFn.
	 * @param  fqClassName   The fully qualified classname of the class from which to derive an object instance
	 * @param  args          An IPersistentMap of constructor args to use in object instance creation
	 * @return                                             IFn representation of the object instance
	 * @throws ClassNotFoundException                      Class cannot be found
	 * @throws NoSuchMethodException                       Class doesnt have a proper constructor
	 * @throws InstantiationException                      Object cannot be instantiated do to any instantiation error
	 * @throws IllegalAccessException                      method or class definition was unavailable
	 * @throws java.lang.reflect.InvocationTargetException an abstracted error in the method call, unpack to see actual cause
	 */
     @SuppressWarnings("rawtypes")
	public static IFn loadFn(Loader loader, String fqClassName, IPersistentMap args)
		throws ClassNotFoundException,
		NoSuchMethodException,
		InstantiationException,
		IllegalAccessException,
		java.lang.reflect.InvocationTargetException
	{
		Class<?> ifnClazz = loader.loadClass(fqClassName);
        Class<?> ipmClazz = loader.loadClass("clojure.lang.IPersistentMap");
		Constructor ctr = ifnClazz.getConstructor(new Class[] { ipmClazz });
		return (IFn) ctr.newInstance(new Object[] { args });
	}

    /**
     * Returns an IFn representation of a dynamically loaded object instance derived
     * from a user class extending OnyxFn.
     * @param  fqClassName   The fully qualified classname of the class from which to derive an object instance
     * @param  args          An IPersistentMap of constructor args to use in object instance creation
     * @return                                             IFn representation of the object instance
     * @throws ClassNotFoundException                      Class cannot be found
     * @throws NoSuchMethodException                       Class doesnt have a proper constructor
     * @throws InstantiationException                      Object cannot be instantiated do to any instantiation error
     * @throws IllegalAccessException                      method or class definition was unavailable
     * @throws java.lang.reflect.InvocationTargetException an abstracted error in the method call, unpack to see actual cause
     */
     @SuppressWarnings("rawtypes")
    public static IFn loadFn(Loader loader, String fqClassName, String ctrClassName, IPersistentMap args)
        throws ClassNotFoundException,
        NoSuchMethodException,
        InstantiationException,
        IllegalAccessException,
        java.lang.reflect.InvocationTargetException
    {
        Class<?> ifnClazz = loader.loadClass(fqClassName);
        Class<?> ctrClazz = loader.loadClass(ctrClassName);
        Class<?> ipmClazz = loader.loadClass("clojure.lang.IPersistentMap");
        Constructor ctrCtr = ctrClazz.getConstructor(new Class[] { ipmClazz });
        Constructor ctr = ifnClazz.getConstructor(new Class[] { ctrClazz });
        return (IFn) ctr.newInstance(ctrCtr.newInstance(new Object[] { args }));
    }

}
