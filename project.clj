(defproject clj-halodb "0.0.2"
  :description "A clojure library for managing yahoo/HaloDB embedded key-value-store."
  :url "http://github.com/rinx/clj-halodb"
  :license {:name "EPL-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :repositories [["yahoo-bintray" "https://yahoo.bintray.com/maven"]]
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/spec.alpha "0.2.176"]
                 [org.clojure/test.check "0.10.0-alpha3"]
                 [orchestra "2019.02.06-1"]
                 [com.oath.halodb/halodb "0.5.3"]]
  :plugins [[lein-ancient "0.6.15"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [cheshire "5.9.0"]]
                   :source-paths ["dev"]
                   :repl-options {:init-ns user}}})
