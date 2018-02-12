(ns onyx-java.instance.catalog
  (:gen-class))

(defn create-method
    ([task-name batch-size batch-timeout fqclassname ctr-args]
  {:onyx/name (keyword task-name)
   :onyx/fn :onyx-java.instance.bind/method
   :onyx/type :function
   :onyx/batch-size batch-size
   :onyx/batch-timeout batch-timeout

   ; Instance binding bootstrapping
   :java-instance/id (java.util.UUID/randomUUID)
   :java-instance/class fqclassname
   :java-instance/ctr-args ctr-args
   :onyx/params [:java-instance/id
                 :java-instance/class
                 :java-instance/ctr-args] })
     ([task-name batch-size batch-timeout fqclassname ctr-class ctr-args]
   {:onyx/name (keyword task-name)
    :onyx/fn :onyx-java.instance.bind/method
    :onyx/type :function
    :onyx/batch-size batch-size
    :onyx/batch-timeout batch-timeout

    ; Instance binding bootstrapping
    :java-instance/id (java.util.UUID/randomUUID)
    :java-instance/class fqclassname
    :java-instance/ctr-class ctr-class
    :java-instance/ctr-args ctr-args
    :onyx/params [:java-instance/id
                  :java-instance/class
                  :java-instance/ctr-class
                  :java-instance/ctr-args] }))


(defn onyx-instance? [task]
  (contains? task :java-instance/id))

(defn id [task]
  (get task :java-instance/id "MISSING"))
