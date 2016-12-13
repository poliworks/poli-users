(ns poli-users.controller
  (:require [poli-users.db.datomic :as db]
            [clj-http.client :as http]
            [schema.core :as s]
            [poli-users.adapters :as adapters]
            [poli-users.models.user :as m-u])
  (:import (java.util UUID)))

(def poli-auth-host (or (System/getenv "poli_auth") "http://localhost:3000"))
(def poli-uploader-host (or (System/getenv "poli_uploader") "http://localhost:5000"))

(s/defn register-user :- m-u/User
  [user-type :- s/Keyword user img]
  (let [new-user-id  (db/gen-uuid)
        request-user (assoc user :type user-type :id new-user-id)]
    (when-let [response (http/post (str poli-auth-host "/register") {:form-params request-user
                                                                     :content-type :json
                                                                     :as :json
                                                                     :throw-exceptions false})]
      (when (http/success? response)
        (assoc (->> (dissoc request-user :password :type)
                    (adapters/external->model user-type)
                    (db/create-user user-type)) (adapters/user-type->attribute user-type :token) (:token (:body response)))))))

(s/defn login-user :- m-u/User
  [user-type {:keys [email password]}]
  (let [{:keys [body] :as response} (http/post (str poli-auth-host "/login") {:form-params {:email email :password password} :content-type :json :as :json})]
    (when (http/success? response)
      (-> (db/user-by-id user-type (UUID/fromString (:id body)))
          (assoc (adapters/user-type->attribute user-type :token) (:token body))))))

(s/defn get-user :- m-u/User
  [user-type :- s/Keyword id :- s/Uuid]
  (db/user-by-id user-type id))

(defn set-image [user-type user-id image]
  (let [image-url (-> (http/post (str poli-uploader-host "/upload/poli-room/users") {:multipart [{:part-name "file"
                                                                                                  :name (:filename image)
                                                                                                  :content (:tempfile image)
                                                                                                  :mime-type (:content-type image)}]
                                                                                     :as :json}) :body :uri)]
    (db/update-user user-type (UUID/fromString user-id) {(adapters/user-type->attribute user-type :picture-url) image-url})))
