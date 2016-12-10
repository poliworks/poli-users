(ns poli-users.models.common
  (:require [schema.core :as s]))

(defn- convert-to-schema [schema-map key]
  (if (:required schema-map)
    (s/required-key key)
    (s/optional-key key)))

(defn gen-schema [skeleton]
  (->> (map (fn [[k v]] [(convert-to-schema v k) (:schema v)]) skeleton)
       (into {})))
