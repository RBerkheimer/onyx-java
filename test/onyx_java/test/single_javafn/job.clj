(ns onyx-java.test.single-javafn.job
  (:require [clojure.test :refer [deftest is]]
            [onyx-java.test.job :as j]
            [onyx-java.test.single-javafn.catalog :as cat]))

(deftest java-pass-through
  (let [job (cat/build-job)
        inputs  [{:pass-through "PASSTHROUGH"}]
        outputs (j/run-job job inputs) ]
    (is (= (first inputs) (first (:out outputs))))))
