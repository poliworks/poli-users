(ns poli-users.controller
  (:require [poli-users.db.datomic :as db]
            [clj-http.client :as http]
            [schema.core :as s]
            [poli-users.models.user :as m-u])
  (:import (java.util UUID)))

(def poli-auth-host "http://localhost:4000")

(defn model->external [user]
  (->> (map (fn [[k v]] [(name k) v]) user)
       (into {})))

(s/defn register-user :- m-u/User
  [user-type :- s/Keyword user]
  (let [response (http/post (str poli-auth-host "/register") {:form-params user :content-type :json :as :json})]
    (when (http/success? response)
      (db/create-user user-type user))))

(s/defn login-user :- m-u/User
  [user-type {:keys [email password]}]
  (let [{:keys [body] :as response} (http/post (str poli-auth-host "/login") {:form-params {:email email :password password} :content-type :json :as :json})]
    (when (http/success? response)
      (db/user-by-id user-type (UUID/fromString (:id body))))))

(s/defn get-user :- m-u/User
  [user-type :- s/Keyword id :- s/Uuid]
  (db/user-by-id user-type id))
