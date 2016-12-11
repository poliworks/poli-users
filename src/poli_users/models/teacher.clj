(ns poli-users.models.teacher
  (:require [schema.core :as s]
            [poli-users.models.common :refer :all]))

(def teacher-skeleton {:teacher/name        {:schema s/Str :required true}
                       :teacher/email       {:schema s/Str :required true}
                       :teacher/id          {:schema s/Uuid :required false} ;; Until not created
                       :teacher/cpf         {:schema s/Str :required false}
                       :teacher/rg          {:schema s/Str :required false}
                       :teacher/num-usp     {:schema s/Str :required true}
                       :teacher/department  {:schema s/Str :required true}
                       :teacher/picture-url {:schema s/Str :required false}
                       :teacher/sex         {:schema s/Str :required true}
                       :teacher/token       {:schema s/Str :required false}})
(def Teacher (gen-schema teacher-skeleton))
