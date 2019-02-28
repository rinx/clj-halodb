(ns dev
  (:require
    [clojure.pprint :refer [pprint]]
    [clojure.tools.namespace.repl :as repl :refer [refresh refresh-all]]
    [clojure.spec.alpha :as spec]
    [clj-halodb.core :as halodb]))

(defn reset []
  (refresh))

