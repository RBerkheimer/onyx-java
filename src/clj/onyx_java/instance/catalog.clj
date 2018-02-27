(ns onyx-java.instance.catalog
  (:gen-class))

(defn make-instance-task
    ([task-name batch-size batch-timeout class args]
        {:onyx/name (keyword task-name)
            :onyx/fn :onyx-java.instance.bind/method
            :onyx/type :function
            :onyx/batch-size batch-size
            :onyx/batch-timeout batch-timeout
            :java-instance/id (java.util.UUID/randomUUID)
            :java-instance/class class
            :java-instance/args args
            :onyx/params [:java-instance/id] })
    ([task-name batch-size batch-timeout class ctr args]
        {:onyx/name (keyword task-name)
            :onyx/fn :onyx-java.instance.bind/method
            :onyx/type :function
            :onyx/batch-size batch-size
            :onyx/batch-timeout batch-timeout
            :java-instance/id (java.util.UUID/randomUUID)
            :java-instance/class class
            :java-instance/ctr ctr
            :java-instance/args args
            :onyx/params [:java-instance/id] }))
