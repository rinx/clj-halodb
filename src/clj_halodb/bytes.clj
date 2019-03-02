(ns clj-halodb.bytes
  (:require [clojure.spec.alpha :as spec]))

(spec/fdef
  string->bytes
  :args (spec/cat :s string?)
  :ret bytes?)

(defn string->bytes [s]
  (-> s
      (.getBytes)))

(spec/fdef
  bytes->string
  :args (spec/cat :b bytes?)
  :ret string?)

(defn bytes->string [b]
  (String. b))

(spec/fdef
  keyword->bytes
  :args (spec/cat :k keyword?)
  :ret bytes?)

(defn keyword->bytes [k]
  (let [namespace (namespace k)
        name (name k)]
    (-> (if namespace
          (str namespace "/" name)
          name)
        (string->bytes))))

(spec/fdef
  ->bytes
  :ret bytes?)

(defn ->bytes [x]
  (cond
    (string? x) (string->bytes x)
    (keyword? x) (keyword->bytes x)
    :else (-> (str x) (string->bytes))))

(comment
  (-> (string->bytes "string")
      (bytes->string))

  (-> (keyword->bytes ::keyword)
      (bytes->string))
  (-> (keyword->bytes :keyword)
      (bytes->string))

  (-> (->bytes 1)
      (bytes->string))
  (-> (->bytes 1.1)
      (bytes->string))
  (-> (->bytes "abc")
      (bytes->string))
  (-> (->bytes ::keyword)
      (bytes->string))
  (-> (->bytes :keyword)
      (bytes->string))

  )
