(ns clj-halodb.core
  (:require [clojure.spec.alpha :as spec])
  (:import [com.oath.halodb HaloDB HaloDBOptions]))

(defn ^HaloDBOptions options
  "Returns the HaloDBOptions instance."
  [opts]
  (HaloDBOptions.))

(defn ^HaloDB open
  "Returns the HaloDB instance."
  [^String directory ^HaloDBOptions opts]
  (HaloDB/open directory opts))

(comment
  (def directory "directory")
  (def halodb-options (options {}))

  (def halodb
    (open directory halodb-options))

  (-> halodb
      (.put (.getBytes "a") (.getBytes "value of a")))

  (-> halodb
      (.get (.getBytes "a"))
      (String.))

  (-> halodb
      (.close))
  )
