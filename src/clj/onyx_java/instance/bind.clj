(ns onyx-java.instance.bind
  (:gen-class)
  (:require [onyx-java.instance.lifecycles :as lcs]))

(defn get-instance [id]
    (let [k (lcs/keyname id)]
        (if (contains? @lcs/instances k)
            (get @lcs/instances k))))

(defn method [id segment]
    (let [inst-ifn (get-instance id)]
        (inst-ifn segment)))
