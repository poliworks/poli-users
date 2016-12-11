(ns poli-users.models.user
  (:require [schema.core :as s]
            [poli-users.models.student :as m-s]
            [poli-users.models.teacher :as m-t]))

(def User (s/either m-s/Student m-t/Teacher))
