(ns clj-halodb.core
  (:require [clojure.spec.alpha :as spec]
            [clj-halodb.bytes :as halodb.bytes])
  (:import [com.oath.halodb HaloDB HaloDBOptions])
  (:refer-clojure :exclude [get]))

(def default-directory "halodb-store")

(spec/fdef
  options
  :args (spec/alt
          :nil (spec/cat)
          :map (spec/cat :opts :clj-halodb.spec/halodb-options))
  :ret #(instance? HaloDBOptions %))

(defn ^HaloDBOptions options
  "Returns a new HaloDBOptions instance.
  The argument opts can contain key-values named as following:

  :compaction-threshold-per-file
  :max-file-size
  :max-tombstone-file-size
  :flush-data-size-bytes
  :sync-write
  :number-of-records
  :compaction-job-rate
  :clean-up-in-memory-index-on-close
  :clean-up-tombstone-during-open
  :use-memory-pool
  :fixed-key-size
  :memory-pool-chunk-size
  :build-index-threads

  Please refer the official documents of yahoo/HaloDB."
  ([]
   (options {}))
  ([{:keys [compaction-threshold-per-file
            max-file-size
            max-tombstone-file-size
            flush-data-size-bytes
            sync-write
            number-of-records
            compaction-job-rate
            clean-up-in-memory-index-on-close
            clean-up-tombstone-during-open
            use-memory-pool
            fixed-key-size
            memory-pool-chunk-size
            build-index-threads] :as opts}]
   (let [options (HaloDBOptions.)]
     (when compaction-threshold-per-file
       (doto options
         (.setCompactionThresholdPerFile compaction-threshold-per-file)))
     (when max-file-size
       (doto options
         (.setMaxFileSize max-file-size)))
     (when max-tombstone-file-size
       (doto options
         (.setMaxTombstoneFileSize max-tombstone-file-size)))
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
     (when build-index-threads
       (doto options
         (.setBuildIndexThreads build-index-threads)))
     options)))

(defn db?
  "Returns whether it is an instance of HaloDB."
  [x]
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
  "Returns a new HaloDB instance.
  The default directory is 'halodb-store'."
  ([]
   (open default-directory))
  ([^String directory]
   (open directory (options)))
  ([^String directory ^HaloDBOptions opts]
   (HaloDB/open directory opts)))

(spec/fdef
  get-bytes
  :args (spec/alt
          :without-fn (spec/cat :db db?
                                :k any?)
          :with-fn (spec/cat :db db?
                             :k any?
                             :f (spec/or :fn fn?
                                         :nil nil?)))
  :ret (spec/or :bytes bytes?
                :nil nil?
                :any any?))

(defn get-bytes
  "Returns a result bytes of fetching value with given key from the db.
  If f is specified, f is applied to the result and returns it."
  ([db k]
   (get-bytes db k nil))
  ([db k f]
   (let [ret (-> db
                 (.get (halodb.bytes/->bytes k)))]
     (cond-> ret
       (and ret f) (f)))))

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
  "Returns a result string of fetching value with given key from the db.
  If f is specified, f is applied to the result and returns it."
  ([db k]
   (get db k nil))
  ([db k f]
   (let [ret (get-bytes db k halodb.bytes/bytes->string)]
     (cond-> ret
       (and ret f) (f)))))

(spec/fdef
  put
  :args (spec/alt
          :without-fn (spec/cat :db db?
                                :m map?)
          :with-fn (spec/cat :db db?
                             :m map?
                             :f (spec/or :fn fn?
                                         :nil nil?))))

(defn put
  "Put the given map into the db.
   If f is specified, f is applied to the value before putting."
  ([db m]
   (put db m nil))
  ([db m f]
   (let [convert (if f
                   (comp halodb.bytes/->bytes f)
                   halodb.bytes/->bytes)
         g (fn [[k v]]
             (doto db
               (.put (halodb.bytes/->bytes k)
                     (convert v))))]
     (map g m))))

(spec/fdef
  delete
  :args (spec/cat :db db?
                  :k any?))

(defn delete [db k]
  "Delete the specified key in the db."
  (doto db
    (.delete (halodb.bytes/->bytes k))))

(spec/fdef
  close
  :args (spec/cat :db db?))

(defn close [db]
  "Close the db."
  (doto db
    (.close)))

(spec/fdef
  size
  :args (spec/cat :db db?)
  :ret int?)

(defn size [db]
  "Returns the size of the db."
  (-> db
      (.size)))

(spec/fdef
  stats
  :args (spec/cat :db db?)
  :ret map?)

(defn stats [db]
  (let [st (-> db
                  (.stats))]
    {:size (.getSize st)
     :number-of-files-pending-compaction (.getNumberOfFilesPendingCompaction st)
     :stale-data-percent-per-file (.getStaleDataPercentPerFile st)
     :rehash-count (.getRehashCount st)
     :number-of-segments (.getNumberOfSegments st)
     :max-size-per-segment (.getMaxSizePerSegment st)
     :number-of-records-copied (.getNumberOfRecordsCopied st)
     :number-of-records-replaced (.getNumberOfRecordsReplaced st)
     :number-of-records-scanned (.getNumberOfRecordsScanned st)
     :size-of-records-copied (.getSizeOfRecordsCopied st)
     :size-of-files-deletes (.getSizeOfFilesDeleted st)
     :size-reclaimed (.getSizeReclaimed st)
     :number-of-data-files (.getNumberOfDataFiles st)
     :number-of-tombstone-files (.getNumberOfTombstoneFiles st)
     :number-of-tombstones-found-during-open (.getNumberOfTombstonesFoundDuringOpen st)
     :number-of-tombstones-cleaned-up-during-open (.getNumberOfTombstonesCleanedUpDuringOpen st)
     :compaction-rate-in-internal (.getCompactionRateInInternal st)
     :compaction-rate-since-beginning (.getCompactionRateSinceBeginning st)
     :compaction-running (.isCompactionRunning st)}))

(spec/fdef
  reset-stats
  :args (spec/cat :db db?))

(defn reset-stats [db]
  (-> db
      (.resetStats)))

(spec/fdef
  pause-compaction
  :args (spec/cat :db db?))

(defn pause-compaction [db]
  (-> db
      (.pauseCompaction)))

(spec/fdef
  resume-compaction
  :args (spec/cat :db db?))

(defn resume-compaction [db]
  (-> db
      (.resumeCompaction)))


(comment

  (options {:number-of-records 10})
  (options {})

  (do
    (def halodb-options (options {:sync-write true}))

    (def halodb
      (open default-directory halodb-options)))

  (put halodb {:x 3 :y 5 :z 6} #(- % 2))

  (->> [:x :y :z]
       (map (fn [x]
              (get halodb x #(Integer/parseInt %)))))

  (do
    (def m {:a :b
            ::c ::d
            "stringkey" "stringvalue"
            1 2
            :m {:a :b :c :d}})
    (put halodb m))

  (->> (keys m)
       (map #(get halodb %)))

  (get halodb :a keyword)
  (get halodb :c keyword)
  (get halodb ::c keyword)
  (get halodb 1 #(Integer/parseInt %))
  (get halodb :c #(Integer/parseInt %))

  (size halodb)

  (stats halodb)

  (reset-stats halodb)

  (pause-compaction halodb)
  (resume-compaction halodb)

  (delete halodb :a)
  (delete halodb 1)
  (delete halodb :m)

  (close halodb)
  )
