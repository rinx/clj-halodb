(defproject clj-halodb "0.1.0-SNAPSHOT"
  :description ""
  :url "http://github.com/rinx/clj-halodb"
  :license {:name "EPL-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :repositories [["yahoo-bintray" "https://yahoo.bintray.com/maven"]]
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/spec.alpha "0.2.176"]
                 [com.oath.halodb/halodb "0.5.1"]]
  :plugins [[lein-ancient "0.6.15"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]]
                   :source-paths ["dev"]
                   :repl-options {:init-ns user}}})
