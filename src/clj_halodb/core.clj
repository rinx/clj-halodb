(ns clj-halodb.core
  (:require [clojure.spec.alpha :as spec]
            [clj-halodb.bytes :as halodb.bytes])
  (:import [com.oath.halodb
            HaloDB
            HaloDBOptions
            HaloDBStats])
  (:refer-clojure :exclude [get]))

(def default-directory "halodb-store")

(spec/fdef
  options
  :args (spec/alt
          :nil (spec/cat)
          :map (spec/cat :opts :clj-halodb.spec/halodb-options))
  :ret #(instance? HaloDBOptions %))

(defn ^HaloDBOptions options
  "Returns a new HaloDBOptions instance."
  ([]
   (options {}))
  ([{:keys [compaction-threshold-per-file
            max-file-size
            flush-data-size-bytes
            sync-write
            number-of-records
            compaction-job-rate
            clean-up-in-memory-index-on-close
            clean-up-tombstone-during-open
            use-memory-pool
            fixed-key-size
            memory-pool-chunk-size] :as opts}]
   (let [options (HaloDBOptions.)]
     (when compaction-threshold-per-file
       (doto options
         (.setCompactionThresholdPerFile compaction-threshold-per-file)))
     (when max-file-size
       (doto options
         (.setMaxFileSize max-file-size)))
     (when flush-data-size-bytes
       (doto options
         (.setFlushDataSizeBytes flush-data-size-bytes)))
     (when sync-write
       (doto options
         (.enableSyncWrites sync-write)))
     (when number-of-records
       (doto options
         (.setNumberOfRecords number-of-records)))
     (when compaction-job-rate
       (doto options
         (.setCompactionJobRate compaction-job-rate)))
     (when clean-up-in-memory-index-on-close
       (doto options
         (.setCleanUpInMemoryIndexOnClose clean-up-in-memory-index-on-close)))
     (when clean-up-tombstone-during-open
       (doto options
         (.setCleanUpTombstonesDuringOpen clean-up-tombstone-during-open)))
     (when use-memory-pool
       (doto options
         (.setUseMemoryPool use-memory-pool)))
     (when fixed-key-size
       (doto options
         (.setFixedKeySize fixed-key-size)))
     (when memory-pool-chunk-size
       (doto options
         (.setMemoryPoolChunkSize memory-pool-chunk-size)))
     options)))

(defn db? [x]
  (instance? HaloDB x))

(spec/fdef
  open
  :args (spec/alt
          :nil (spec/cat)
          :dir (spec/cat :directory string?)
          :mul (spec/cat :directory string?
                         :opts #(instance? HaloDBOptions %)))
  :ret db?)

(defn ^HaloDB open
  "Returns a new HaloDB instance."
  ([]
   (open default-directory))
  ([^String directory]
   (open directory (options)))
  ([^String directory ^HaloDBOptions opts]
   (HaloDB/open directory opts)))

(spec/fdef
  get-raw
  :args (spec/cat :db db?
                  :k any?)
  :ret (spec/or :bytes bytes?
                :nil nil?))

(defn get-raw [db k]
  (-> db
      (.get (halodb.bytes/->bytes k))))

(spec/fdef
  get
  :args (spec/alt
          :without-fn (spec/cat :db db?
                                :k any?)
          :with-fn (spec/cat :db db?
                             :k any?
                             :f (spec/or :fn fn?
                                         :nil nil?)))
  :ret (spec/or :string string?
                :nil nil?
                :any any?))

(defn get
  ([db k]
   (get db k nil))
  ([db k f]
   (let [ret (get-raw db k)]
     (cond-> ret
       ret (halodb.bytes/bytes->string)
       (and ret f) (f)))))

(spec/fdef
  put
  :args (spec/cat :db db?
                  :m map?))

(defn put [db m]
  (let [f (fn [[k v]]
            (doto db
              (.put (halodb.bytes/->bytes k)
                    (halodb.bytes/->bytes v))))]
    (map f m)))

(spec/fdef
  delete
  :args (spec/cat :db db?
                  :k any?))

(defn delete [db k]
  (doto db
    (.delete (halodb.bytes/->bytes k))))

(spec/fdef
  close
  :args (spec/cat :db db?))

(defn close [db]
  (doto db
    (.close)))

(spec/fdef
  size
  :args (spec/cat :db db?)
  :ret int?)

(defn size [db]
  (-> db
      (.size)))

(comment

  (options {:number-of-records 10})
  (options {})

  (do
    (def halodb-options (options {}))

    (def halodb
      (open default-directory halodb-options)))

  (put halodb {:a :b
               ::c ::d
               "stringkey" "stringvalue"
               1 2})

  (get halodb :a)
  (get halodb :c)
  (get halodb ::c)
  (get halodb "stringkey")
  (get halodb 1)

  (get halodb :a keyword)
  (get halodb :c keyword)
  (get halodb ::c keyword)
  (get halodb 1 #(Integer/parseInt %))
  (get halodb :c #(Integer/parseInt %))

  (size halodb)

  (delete halodb :a)
  (delete halodb 1)

  (close halodb)
  )
