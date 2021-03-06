# clj-halodb

[![Clojars Project](https://img.shields.io/clojars/v/clj-halodb.svg)](https://clojars.org/clj-halodb)
[![GitHub Actions: lein test](https://github.com/rinx/clj-halodb/workflows/lein%20test/badge.svg)](https://github.com/rinx/clj-halodb/actions)

A clojure library for managing [yahoo/HaloDB](https://github.com/yahoo/HaloDB) embedded key-value-store.

## Links

- [Clojars Repository](https://clojars.org/clj-halodb)
- [cljdoc](https://cljdoc.org/d/clj-halodb)

## Usage

```clojure
;; clj-halodb 0.0.2 uses yahoo/HaloDB 0.5.3

[clj-halodb "0.0.2"]
```

```clojure
(require '[clj-halodb.core :as halodb])

(def halodb-options
  (halodb/options
    {:max-file-size 131072
     :sync-write true}))

(def db
  (halodb/open ".halodb" halodb-options))

(def m {:a :b
        ::c ::d
        "key" "val"
        1 2
        1.0 2.0
        :m {:a :b :c :d}})

(halodb/put db m)

(halodb/get db :a)                          ;; => "b"
(halodb/get db 1)                           ;; => "2"
(halodb/get db :a keyword)                  ;; => :b
(halodb/get db 1 #(Integer/parseInt %))     ;; => 2

(halodb/size db)                            ;; => 6

(halodb/delete db :a)
(halodb/get db :a)                          ;; => nil
(halodb/size db)                            ;; => 5

(halodb/put db {:x 3 :y 5 :z 6} #(- % 2))
(->> [:x :y :z]
     (map (fn [x]
            (halodb/get db x #(Integer/parseInt %)))))
;; => (1 3 4)

(halodb/close db)

```

```clojure
;; use cheshire for encoding/decoding json
(require '[cheshire.core :as cheshire])

(halodb/put db m cheshire/generate-string)

(let [parse #(cheshire/parse-string % true)]
  (->> (keys m)
       (map #(halodb/get db % parse))))     ;; => ("b" "ns/d" "val" 2 2.0 {:a "b", :c "d"})

```

```clojure
;; also you can use your own format like following

(let [encode #(-> {:t (-> % type str) :v %}
                  (cheshire/generate-string))]
  (halodb/put db m encode))

(let [decode #(let [{:keys [t v]} (cheshire/parse-string % true)]
                (condp = t
                  "class clojure.lang.Keyword" (keyword v)
                  v))]
  (->> (keys m)
       (map #(halodb/get db % decode))))    ;; => (:b ::d "val" 2 2.0 {:a "b", :c "d"})

```

## License

Copyright © 2019 rinx

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.
