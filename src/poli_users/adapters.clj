(ns poli-users.adapters
  (:require [schema.core :as s]))

(defn model->external [user]
  (->> (map (fn [[k v]] [(keyword (name k)) v]) user)
       (into {})))

(defn external->model [user-type user]
  (->> (map (fn [[k v]] [(keyword (name user-type) (name k)) v]) user)
       (into {})))

(s/defn user-type->primary-key :- s/Keyword
        [user-type :- s/Keyword]
        (keyword (name user-type) "id"))
