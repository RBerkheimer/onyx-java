package org.onyxplatform.api.java.instance;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.lang.Thread;

import clojure.lang.IPersistentMap;
import clojure.lang.IFn;

import org.onyxplatform.api.java.OnyxMap;
import org.onyxplatform.api.java.utils.MapFns;
import org.onyxplatform.api.java.utils.ResourceUtils;

/**
* Loader is a basic custom class loader used by BindUtils during
* instance creation ensuring that the OnyxFn class (and its derived classes)
* will be garbage collected when the instance is released.
*
* This is to ensure that the class will be unloaded from memory along
* with any native library loaded into it. If this is not done then the
* default system class loader will always have a reachable reference to
* any class loaded preventing memory reclimation of underlying native
* libraries.
*/
public class Loader extends ClassLoader {

	protected OnyxMap cache = new OnyxMap();

	public Loader() {
		super(Thread.currentThread().getContextClassLoader());
	}

	@Override
	public Class<?> loadClass(String n)
		throws ClassNotFoundException, java.lang.SecurityException {
		return super.loadClass(n);
	}

	private byte[] loadClassData(String name) throws IOException {
		InputStream stream = null;
		try {
			stream = getClass().getClassLoader().getResourceAsStream(name);
			int size = stream.available();
			byte buff[] = new byte[size];
			DataInputStream in = new DataInputStream(stream);
			in.readFully(buff);
			in.close();
			return buff;
		} finally {
			if (stream != null) {
				ResourceUtils.safeCloseInputStream(stream);
			}
		}
	}
}
