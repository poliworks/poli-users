(defproject poli-users "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-json "0.4.0"]
                 [com.datomic/datomic-free "0.9.5407"]
                 [prismatic/schema "1.1.3"]
                 [crypto-password "0.2.0"]
                 [clj-http "2.3.0"]
                 [cheshire]
                 [crouton]
                 [org.clojure/tools.reader]
                 [ring/ring-defaults "0.2.1"]
                 [io.rkn/conformity "0.4.0"]
                 [clj-jwt "0.1.1"]]
  :plugins [[lein-ring "0.9.7"]]
  :resources-paths ["resources"]
  :ring {:handler poli-users.server/app
         :init poli-users.server/bootstrap!}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
