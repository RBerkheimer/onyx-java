(ns onyx-java.test.single-cljfn.catalog
  (import [org.onyxplatform.api.java Catalog Task Job OnyxNames TaskScheduler]
          [org.onyxplatform.api.java.utils AsyncCatalog MapFns])
  (:require [clojure.test :refer [deftest is]]))


(defn build-job []
  (let [scheduler (TaskScheduler. OnyxNames/BalancedTaskSchedule)
        job (Job. scheduler)
        cat (.getCatalog job)
        om (MapFns/fromResources "catalog-single-clj.edn")
        t (Task. om) ]
    (-> cat
        (.addTask t))
    (AsyncCatalog/addInput cat "in" 5 50)
    (AsyncCatalog/addOutput cat "out" 5 50)
    job))

(def expected [{:onyx/name :pass
                :onyx/fn :onyx-java.test.single-cljfn.functions/pass-through
                :onyx/type :function
                :onyx/batch-size 5
                :onyx/batch-timeout 50}

               {:onyx/name :in
                :onyx/plugin :onyx.plugin.core-async/input
                :onyx/medium :core.async
                :onyx/type :input
                :onyx/max-peers 1
                :onyx/batch-size 5
                :onyx/batch-timeout 50 }

               {:onyx/name :out
                :onyx/plugin :onyx.plugin.core-async/output
                :onyx/medium :core.async
                :onyx/type :output
                :onyx/max-peers 1
                :onyx/batch-size 5
                :onyx/batch-timeout 50 }])

(deftest valid-catalog?
  (let [cat (.getCatalog (build-job))]
    (is (= expected (.tasks cat)))))
