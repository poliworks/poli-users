(ns poli-users.db.datomic
  (:require [datomic.api :as d]
            [io.rkn.conformity :as c]
            [poli-users.models.student :as m-s]
            [poli-users.models.teacher :as m-t]
            [schema.core :as s]
            [poli-users.models.user :as m-u]))

(def prod-uri "datomic:free://localhost:4334/poli-users")
(def test-uri "datomic:mem://test")

(def datomic-uri test-uri)                                  ;; Change here the datomic URI

(d/create-database datomic-uri)
(def conn (d/connect datomic-uri))

(s/defn ^:private gen-uuid :- s/Str []
  (d/squuid))

(s/defn ^:private user-type->primary-key :- s/Keyword
  [user-type :- s/Keyword]
  (keyword (name user-type) "id"))

(defn ^:private prepare-entity [entity primary-key]
  (assoc entity :db/id (d/tempid :db.part/user)
                primary-key (gen-uuid)))

(defn install-schema! []
  (let [schema (c/read-resource "schema.edn")]
    (c/ensure-conforms conn schema)))

(s/defn user-by-id :- (s/either m-s/Student m-t/Teacher)
  [user-type :- s/Keyword, id :- s/Uuid]
  (d/q '[:find (d/pull ?t [*])
         :in $ ?u-id ?u-type-key
         :where [?u ?u-type-key ?u-id]] conn id (user-type->primary-key user-type)))

(s/defn student-by-id :- m-s/Student
  [id :- s/Uuid]
  (user-by-id :student id))

(s/defn teacher-by-id :- m-t/Teacher
  [id :- s/Uuid]
  (user-by-id :teacher id))

(s/defn create-user :- m-u/User
  [user-type :- s/Keyword user :- (s/pred map?)]
  (let [prepared-user (prepare-entity user (user-type->primary-key user-type))]
    @(d/transact conn [prepared-user])
    (dissoc prepare-entity :db/id)))

(s/defn create-student :- m-s/Student
  [student :- m-s/Student]
  (create-user :student student))

(s/defn create-teacher :- m-t/Teacher
  [teacher :- m-t/Teacher]
  (create-user :teacher teacher))
