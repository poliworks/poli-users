(ns poli-users.server
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [poli-users.db.datomic :as db]
            [poli-users.controller :as controller]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :refer :all]
            [ring.middleware.keyword-params :as kp]
            [ring.middleware.multipart-params :as mp]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [poli-users.adapters :as adapters])
  (:import (java.util UUID)))

(defn debug [v]
  (println v)
  v)

(defn register [user-type {:keys [body params] :as request}]
  (-> (controller/register-user user-type body (-> (get params "image") :tempfile))
      debug
      (adapters/model->external)
      debug
      response))

(defn login [user-type {:keys [body] :as request}]
  (-> (controller/login-user user-type body)
      debug
      (adapters/model->external)
      response))

(defn get-user [user-type id]
  (-> (controller/get-user user-type (UUID/fromString id))
      (adapters/model->external)
      response))

(defn set-image [user-type {:keys [params]}]
  (controller/set-image user-type (:id params) (-> params :file))
  {:status 200 :body {:ok "OKAY"}})

(defroutes app-routes
  (context "/student" []
    (POST "/login" request (login :student request))
    (POST "/register" request (debug (register :student (debug request))))
    (POST "/:id/image" request (set-image :student (debug request)))
    (GET "/:id" [id] (get-user :student id)))
  (context "/teacher" []
    (POST "/login" request (login :teacher request))
    (POST "/register" request (register :teacher request))
    (POST "/:id/image" request (set-image :teacher request))
    (GET "/:id" [id] (get-user :teacher id)))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (kp/wrap-keyword-params)
      (mp/wrap-multipart-params)
      (wrap-json-body {:keywords? true})
      (wrap-json-response)
      (wrap-defaults api-defaults)))

(defn bootstrap! []
  (db/install-schema!))
