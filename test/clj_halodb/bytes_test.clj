(ns clj-halodb.bytes-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as spec]
            [orchestra.spec.test :as stest]
            [clojure.test.check.clojure-test :as ct]
            [clojure.test.check.properties :as prop]
            [clj-halodb.bytes :refer :all]))

(def n 100)

(stest/instrument 'clj-halodb.bytes)

(ct/defspec string->bytes-test
  n
  (prop/for-all
    [s (spec/gen string?)]
    (bytes? (string->bytes s))))

(ct/defspec bytes->string-test
  n
  (prop/for-all
    [s (spec/gen string?)]
    (string? (bytes->string (.getBytes s)))))

(ct/defspec keyword->bytes-test
  n
  (prop/for-all
    [k (spec/gen keyword?)]
    (bytes? (keyword->bytes k))))

(comment
  (run-tests))
