(ns poli-users.db.datomic
  (:require [datomic.api :as d]
            [io.rkn.conformity :as c]
            [poli-users.models.student :as m-s]
            [poli-users.models.teacher :as m-t]
            [schema.core :as s]))

(def prod-uri "datomic:free://localhost:4334/poli-users")
(def test-uri "datomic:mem://test")

(def datomic-uri test-uri)                                  ;; Change here the datomic URI

(d/create-database datomic-uri)
(def conn (d/connect datomic-uri))

(s/defn ^:private gen-uuid :- s/Str []
  (d/squuid))

(defn ^:private prepare-entity [entity primary-key]
  (assoc entity :db/id (d/tempid :db.part/user)
                primary-key (gen-uuid)))

(defn install-schema! []
  (let [schema (c/read-resource "schema.edn")]
    (c/ensure-conforms conn schema)))

(s/defn student-by-id :- m-s/Student
  [id :- s/Uuid]
  (d/q '[:find (d/pull ?s [*])
         :in $ ?d-id
         :where [?s :student/id ?s-id]]) conn id)

(s/defn teacher-by-id :- m-t/Teacher
  [id :- s/Uuid]
  (d/q '[:find (d/pull ?t [*])
         :in $ ?t-id
         :where [?t :student/id ?t-id]]) conn id)

(s/defn create-student :- m-s/Student
  [student :- m-s/Student]
  (let [prepared-student (prepare-entity student :student/id)]
    @(d/transact conn [prepare-entity])))

(s/defn create-teacher :- m-t/Teacher
  [teacher :- m-t/Teacher]
  (let [prepared-teacher (prepare-entity teacher :teacher/id)]
    @(d/transact conn [prepared-teacher])))
