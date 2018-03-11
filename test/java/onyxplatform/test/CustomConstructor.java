package onyxplatform.test;

import clojure.lang.IPersistentMap;
import java.util.ArrayList;
import org.onyxplatform.api.java.instance.IOnyxFnConstructor;
import org.onyxplatform.api.java.utils.MapFns;

/**
TaskMap is the standard data structure for User Job Tasks. The TaskMap must
be the only argument of the user class constructor in TaskAPI,
must be the only input parameter in the 'run' method of a task, and
is a good choice for passing to native methods, as it has several available
Convenience functions in C. TaskMaps are a map and hold arbitrary nestings
of key/value pairs. Keys must be strings, and values are allowed to be any java object.
**/
public class CustomConstructor implements IOnyxFnConstructor {

    public IPersistentMap value;

    public CustomConstructor() {
        this.value = MapFns.emptyMap();
    }

    public CustomConstructor(IPersistentMap o) {
        this.value =  o;
    }

    public Object get(String k) {
        return MapFns.get(this.value, k);
    }

    public void add(String k, Object v) {
        this.value = MapFns.assoc(this.value, k, v);
    }

    public void remove(String k) {
        this.value = MapFns.dissoc(this.value, k);
    }

    public IPersistentMap getCast() {
        return this.value;
    }

    public Object getValue() {
        return this.value;
    }

    public IPersistentMap persistentMap() {
        return this.value;
    }

    public String getType() {
        return "map";
    }

    @Override
    public String toString() {
        return String.format(this.value.toString());
   }

}
