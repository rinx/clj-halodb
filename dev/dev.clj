(ns dev
  (:require
    [clojure.pprint :refer [pprint]]
    [clojure.tools.namespace.repl :as repl :refer [refresh refresh-all]]
    [clojure.spec.alpha :as spec]
    [orchestra.spec.test :as stest]
    [clj-halodb.core :as halodb]))

(defn reset []
  (refresh :after 'stest/instrument))
