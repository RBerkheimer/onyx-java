(ns onyx-java.instance.lifecycles
  (:gen-class))

(def instances (atom {}))

(defn class-factory [classname & types]
    ;; Uses a classname and an arbitrary number of (optional) arguments
    ;; to define a factory which is capable of creating classes.
  (let [args (map #(with-meta (symbol (str "x" %2)) {:tag %1}) types (range))]
    (eval `(fn [~@args] (new ~(symbol classname) ~@args)))))

(defn make-basic-factory [fqclass]
    (class-factory fqclass "clojure.lang.IPersistentMap"))

(defn make-user-factory [fqclass fqctrclass]
    (class-factory fqclass fqctrclass))

(defn keyname [id]
  (keyword (str id)))

(defn before-java-task-ctr [event lifecycle]
    (let [user-class (str (:java-instance/class (:onyx.core/task-map event)))
          user-ctr (str (:java-instance/ctr (:onyx.core/task-map event)))
          ctr-args (:java-instance/args (:onyx.core/task-map event))
          ctr-factory (make-user-factory user-ctr "clojure.lang.IPersistentMap")
          ctr-instance (ctr-factory ctr-args)
          class-factory (make-user-factory user-class user-ctr)
          instance (class-factory ctr-instance)
          instance-key (keyname (:java-instance/id (:onyx.core/task-map event)))]
            (swap! instances assoc instance-key instance))
            {})

(defn before-java-task-basic [event lifecycle]
    (let [user-class (str (:java-instance/class (:onyx.core/task-map event)))
          ctr-args (:java-instance/args (:onyx.core/task-map event))
          class-factory (make-basic-factory user-class)
          instance (class-factory ctr-args)
          instance-key (keyname (:java-instance/id (:onyx.core/task-map event)))]
            (swap! instances assoc instance-key instance))
            {})

(defn after-java-task [event lifecycle]
    (if (contains? @instances (keyname (:java-instance/id (:onyx.core/task-map event))))
      (let [k (keyname (:java-instance/id (:onyx.core/task-map event)))]
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
