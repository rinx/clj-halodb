(ns user
  (:require
    [clojure.tools.namespace.repl :as repl :refer [refresh refresh-all]]))

(defn dev []
  (require 'dev)
  (in-ns 'dev)
  :ok)

