(ns poli-users.controller
  (:require [poli-users.db.datomic :as db]
            [clj-http.client :as http])
  (:import (java.util UUID)))

(def poli-auth-host "http://localhost:4000")

(defn model->external [user]
  (->> (map (fn [[k v]] [(name k) v]) user)
       (into {})))

(defn register-user [user-type user]
  (let [response (http/post (str poli-auth-host "/register") {:form-params user :content-type :json :as :json})]
    (when (http/success? response)
      (db/create-user user-type user))))

(defn login-user [user-type {:keys [email password]}]
  (let [{:keys [body] :as response} (http/post (str poli-auth-host "/login") {:form-params {:email email :password password} :content-type :json :as :json})]
    (when (http/success? response)
      (db/user-by-id (keyword (:user-type body)) (UUID/fromString (:id body))))))