(ns dev
  (:require
    [clojure.pprint :refer [pprint]]
    [clojure.tools.namespace.repl :as repl :refer [refresh refresh-all]]
    [clojure.spec.alpha :as spec]
    [orchestra.spec.test :as stest]
    [cheshire.core :as cheshire]
    [clj-halodb.core :as halodb]))

(def db (atom nil))

(def dir ".dev-store")

(defn open []
  (reset! db (halodb/open dir)))

(defn close []
  (when @db
    (halodb/close @db)))

(defn reset []
  (close)
  (refresh :after 'stest/instrument))

(comment
  (deref db)
  (def m {:a :b
          ::c ::d
          "key" "val"
          1 2
          1.0 2.0
          :m {:a :b :c :d}})

  (halodb/put @db m cheshire/generate-string)

  (let [parse #(cheshire/parse-string % true)]
    (->> (keys m)
         (map (fn [x]
                (halodb/get @db x parse)))))

  (let [encode #(-> {:t (-> % type str) :v %}
                    (cheshire/generate-string))]
    (halodb/put @db m encode))

  (let [decode #(let [{:keys [t v]} (cheshire/parse-string % true)]
                  (condp = t
                    "class clojure.lang.Keyword" (keyword v)
                    v))]
    (->> (keys m)
         (map #(halodb/get @db % decode))))
  )

