(ns clj-halodb.core
  (:require [clojure.spec.alpha :as spec]
            [clj-halodb.spec :as halodb.spec]
            [clj-halodb.bytes :as halodb.bytes])
  (:import [com.oath.halodb HaloDB HaloDBOptions]))

(def default-directory "halodb-store")

(defn ^HaloDBOptions options
  "Returns a new HaloDBOptions instance."
  ([]
   (options {}))
  ([opts]
   (HaloDBOptions.)))

(defn ^HaloDB open
  "Returns a new HaloDB instance."
  ([]
   (open default-directory))
  ([^String directory]
   (open directory (options)))
  ([^String directory ^HaloDBOptions opts]
   (HaloDB/open directory opts)))

(comment
  (def halodb-options (options {}))

  (def halodb
    (open default-directory halodb-options))

  (-> halodb
      (.put (.getBytes "a") (.getBytes "value of a")))

  (-> halodb
      (.get (.getBytes "a"))
      (String.))

  (-> halodb
      (.close))
  )
