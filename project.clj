(defproject clj-halodb #=(clojure.string/trim #=(slurp "CLJ_HALODB_VERSION"))
  :description "A clojure library for managing yahoo/HaloDB embedded key-value-store."
  :url "http://github.com/rinx/clj-halodb"
  :license {:name "EPL-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :repositories [["yahoo-bintray" "https://yahoo.bintray.com/maven"]]
  :deploy-repositories [["clojars" {:sign-releases false
                                    :username :env/clojars_user
                                    :password :env/clojars_token
                                    :url "https://clojars.org/repo"}]]
  :dependencies [[org.clojure/clojure "1.10.2-alpha1"]
                 [org.clojure/spec.alpha "0.2.176"]
                 [com.oath.halodb/halodb "0.5.3"]]
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "0.2.11"]
                                  [cheshire "5.9.0"]
                                  [orchestra "2019.02.06-1"]
                                  [org.clojure/test.check "0.10.0-alpha3"]]
                   :source-paths ["dev"]
                   :repl-options {:init-ns user}}})
