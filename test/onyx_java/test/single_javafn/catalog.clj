(ns onyx-java.test.single-javafn.catalog
  (:import [org.onyxplatform.api.java Catalog Task Job OnyxNames TaskScheduler]
          [org.onyxplatform.api.java.utils AsyncCatalog MapFns]
          [org.onyxplatform.api.java.instance BindUtils])
  (:require [clojure.test :refer [deftest is]]))


(defn build-job []
  (let [scheduler (TaskScheduler. OnyxNames/BalancedTaskSchedule)
        job (Job. scheduler)
        cat (.getCatalog job)
        fqns "onyxplatform.test.PassFn"
        ctr-args {}]
    (BindUtils/addFn job "pass" 5 50 fqns ctr-args)
    (AsyncCatalog/addInput cat "in" 5 50)
    (AsyncCatalog/addOutput cat "out" 5 50)
    job))

(def expected [{:onyx/name :pass,
                :onyx/fn :onyx-java.instance.bind/method,
                :onyx/type :function,
                :onyx/batch-size 5
                :onyx/batch-timeout 50,
                :java-instance/class "onyxplatform.test.PassFn",
                :java-instance/args  {},
                ; NOTE: java-instance/id is stripped before comparison, as it is generated
                :onyx/params  [:java-instance/id] }

               {:onyx/name :in,
                :onyx/plugin :onyx.plugin.core-async/input,
                :onyx/medium :core.async,
                :onyx/type :input,
                :onyx/max-peers 1,
                :onyx/batch-size 5
                :onyx/batch-timeout 50 }

               {:onyx/name :out,
                :onyx/plugin :onyx.plugin.core-async/output,
                :onyx/medium :core.async,
                :onyx/type :output,
                :onyx/max-peers 1,
                :onyx/batch-size 5
                :onyx/batch-timeout 50 }])

(deftest valid-catalog?
  (let [cat (.getCatalog (build-job))
        ; Strip generated IDs before comparing
        tasks (map
                #(dissoc % :java-instance/id)
                (.tasks cat)) ]
    (is (= expected tasks))))
