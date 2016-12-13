(ns poli-users.server
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [poli-users.db.datomic :as db]
            [poli-users.controller :as controller]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :refer :all]
            [ring.middleware.cors :as cors]
            [ring.middleware.keyword-params :as kp]
            [ring.middleware.multipart-params :as mp]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [poli-users.adapters :as adapters])
  (:import (java.util UUID)))


(defn response-or-error [user error-code error-message]
  (if (nil? user)
    {:status error-code :body {:error error-message}}
    {:status 200 :body (adapters/model->external user)}))

(defn register [user-type {:keys [body params] :as request}]
  (-> (controller/register-user user-type body (-> (get params "image") :tempfile))
      (response-or-error 400 "Could not register user")))

(defn login [user-type {:keys [body] :as request}]
  (-> (controller/login-user user-type body)
      (response-or-error 403 "Error In Loging")))

(defn get-user [user-type id]
  (-> (controller/get-user user-type (UUID/fromString id))
      (response-or-error 400 "Error getting user")))

(defn set-image [user-type {:keys [params]}]
  (-> (controller/set-image user-type (:id params) (-> params :file))
      (response-or-error 400 "Error Uploading Image")))

(defroutes app-routes
  (context "/student" []
    (POST "/login" request (login :student request))
    (POST "/register" request (register :student request))
    (POST "/:id/image" request (set-image :student request))
    (GET "/:id" [id] (get-user :student id)))
  (context "/teacher" []
    (POST "/login" request (login :teacher request))
    (POST "/register" request (register :teacher request))
    (POST "/:id/image" request (set-image :teacher request))
    (GET "/:id" [id] (get-user :teacher id)))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (cors/wrap-cors #".*")
      (kp/wrap-keyword-params)
      (mp/wrap-multipart-params)
      (wrap-json-body {:keywords? true})
      (wrap-json-response)
      (wrap-defaults api-defaults)))

(defn bootstrap! []
  (db/install-schema!))
