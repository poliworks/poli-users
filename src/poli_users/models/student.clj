(ns poli-users.models.student
  (:require [schema.core :as s]
            [poli-users.models.common :refer :all]))

(def student-skeleton {:student/name        {:schema s/Str :required true}
                       :student/email       {:schema s/Str :required true}
                       :student/id          {:schema s/Uuid :required false} ;; Until not created
                       :student/cpf         {:schema s/Str :required false}
                       :student/rg          {:schema s/Str :required false}
                       :student/num-usp     {:schema s/Str :required true}
                       :student/course      {:schema s/Str :required true}
                       :student/semester    {:schema s/Int :required true}
                       :student/picture-url {:schema s/Str :required false}
                       :student/sex         {:schema s/Str :required true}})
(def Student (gen-schema student-skeleton))
