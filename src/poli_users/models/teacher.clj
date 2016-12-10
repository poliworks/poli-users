(ns poli-users.models.teacher
  (:require [schema.core :as s]))

(defn- convert-to-schema [schema-map key]
  (if (:required schema-map)
    (s/required-key key)
    (s/optional-key key)))

(defn gen-schema [skeleton]
  (->> (map (fn [[k v]] [(convert-to-schema v k) (:schema v)]) skeleton)
       (into {})))

(def student-skeleton {:student/name        {:schema s/Str :required true}
                       :student/email       {:schema s/Str :required true}
                       :student/id          {:schema s/Int :required true}
                       :student/cpf         {:schema s/Str :required false}
                       :student/rg          {:schema s/Str :required false}
                       :student/num-usp     {:schema s/Str :required true}
                       :student/course      {:schema s/Str :required true}
                       :student/semester    {:schema s/Int :required true}
                       :student/picture-url {:schema s/Str :required false}
                       :student/sex         {:schema s/Str :required true}})
(def Student (gen-schema student-skeleton))

(def teacher-skeleton {:teacher/name        {:schema s/Str :required true}
                       :teacher/email       {:schema s/Str :required true}
                       :teacher/id          {:schema s/Int :required true}
                       :teacher/cpf         {:schema s/Str :required false}
                       :teacher/rg          {:schema s/Str :required false}
                       :teacher/num-usp     {:schema s/Str :required true}
                       :teacher/department  {:schema s/Str :required true}
                       :teacher/picture-url {:schema s/Str :required false}
                       :teacher/sex         {:schema s/Str :required true}})
(def Teacher (gen-schema teacher-skeleton))