(ns onyx-java.instance.lifecycles
  (:gen-class)
  (:import [org.onyxplatform.api.java.instance BindUtils Loader OnyxFn]))

(def instances (atom {}))
(def classloaders (atom {}))

(defn keyname [id]
  (keyword (str id)))

(defn before-java-task-ctr [event lifecycle]
    (let [classloader (Loader.)
          task-inst (BindUtils/loadFn classloader
                    (str (:java-instance/class (:onyx.core/task-map event)))
                    (str (:java-instance/ctr (:onyx.core/task-map event)))
                    (:java-instance/args (:onyx.core/task-map event)))]
            (swap! classloaders assoc (keyname (:java-instance/id (:onyx.core/task-map event))) classloader)
            (swap! instances assoc (keyname (:java-instance/id (:onyx.core/task-map event))) task-inst))
            {})

(defn before-java-task-basic [event lifecycle]
    (let [classloader (Loader.)
          task-inst (BindUtils/loadFn classloader
                    (str (:java-instance/class (:onyx.core/task-map event)))
                    (:java-instance/args (:onyx.core/task-map event)))]
            (swap! classloaders assoc (keyname (:java-instance/id (:onyx.core/task-map event))) classloader)
            (swap! instances assoc (keyname (:java-instance/id (:onyx.core/task-map event))) task-inst))
            {})

(defn after-java-task [event lifecycle]
    (if (contains? @instances (keyname (:java-instance/id (:onyx.core/task-map event))))
      (let [k (keyname (:java-instance/id (:onyx.core/task-map event)))]
        (swap! classloaders dissoc k)
        (swap! instances dissoc k)))
        {})

(def ctr-instance-calls
    {:lifecycle/before-task-start before-java-task-ctr
    :lifecycle/after-task-stop after-java-task})

(def basic-instance-calls
    {:lifecycle/before-task-start before-java-task-basic
    :lifecycle/after-task-stop after-java-task})

(defn make-instance-lifecycle [task-name task-type]
    (if (= task-type "basic")
    {:lifecycle/task (keyword task-name)
     :lifecycle/calls :onyx-java.instance.lifecycles/basic-instance-calls}
    {:lifecycle/task (keyword task-name)
     :lifecycle/calls :onyx-java.instance.lifecycles/ctr-instance-calls}))
