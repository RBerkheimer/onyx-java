package org.onyxplatform.api.java.instance;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import clojure.lang.AFn;
import clojure.lang.IPersistentMap;
import clojure.lang.PersistentHashMap;

import org.onyxplatform.api.java.OnyxNames;
import org.onyxplatform.api.java.OnyxMap;
import org.onyxplatform.api.java.utils.MapFns;

import org.onyxplatform.api.java.instance.IOnyxFnConstructor;

/**
 * OnyxFn is the base class for all User type classes that a User wishes
 * to use as an object instance as a task within an Onyx workflow. User functions
 * must use OnyxFn, which uses the Loader in this package to automatically perform
 * classloading. User classes must extend this OnyxFn, and implement the consumeSegment
 * method.
 */
public abstract class OnyxFn extends AFn implements OnyxNames {

	protected IPersistentMap cntrArgs;

	/**
	 * The constructor MUST be overridden by the concrete subclass.
	 * It should be overridden, taking an IPersistentMap as the only argument,
	 * and call the superconstructor using the argument
	 * (E.g., public ExampleExtender(IPersistentMap m) { super(m);})
	 * @param m An IPersistentMap of constructor arguments used for the class
	 */
	public OnyxFn(IPersistentMap m) {
		cntrArgs = m;
	}

    public OnyxFn(IOnyxFnConstructor m) {
        cntrArgs = m.getCast();
    }

	/**
	 * Turns the constructor arguments from an IPersistentMap into an OnyxMap object.
	 * @return an OnyxMap object representation of the constructor args
	 */
	public OnyxMap cntrArgs() {
		return MapFns.toOnyxMap(cntrArgs);
	}

	/**
	 * This abstract method must be overridden by the extending class - it
	 * wraps the main body of work that should be performed on the segment
	 * passed to it. The segment must be passed within an IPersistentMap,
	 * and once within the method, anything can be done to the segment.
	 * Once all processing is complete, the method output should be passed
	 * back as either an IPersistentMap or a PersistentVector containing
	 * IPersistentMaps.
	 * @param m An IPersistentMap containing the segment to be worked on.
	 * @return an IPersistentMap or PersistentVector of IPersistentMaps containing the method processing output
	 */
	public abstract Object consumeSegment(IPersistentMap m);

	public Object invoke(Object arg1) {
		IPersistentMap segment = (IPersistentMap) arg1;
		return consumeSegment(segment);
	}
}
