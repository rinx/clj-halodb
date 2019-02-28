(ns clj-halodb.spec
  (:require [clojure.spec.alpha :as spec]))

(spec/def ::compaction-threshold-per-file float?)
(spec/def ::max-file-size int?)
(spec/def ::flush-data-size-bytes int?)
(spec/def ::sync-write boolean?)
(spec/def ::number-of-records int?)
(spec/def ::compaction-job-rate int?)
(spec/def ::clean-up-in-memory-index-on-close boolean?)
(spec/def ::clean-up-tombstone-during-open boolean?)
(spec/def ::use-memory-pool boolean?)
(spec/def ::fixed-key-size int?)
(spec/def ::memory-pool-chunk-size int?)

(spec/def ::halodb-options
  (spec/keys :opt-un [::compaction-threshold-per-file
                      ::max-file-size
                      ::flush-data-size-bytes
                      ::sync-write
                      ::number-of-records
                      ::compaction-job-rate
                      ::clean-up-in-memory-index-on-close
                      ::clean-up-tombstone-during-open
                      ::use-memory-pool
                      ::fixed-key-size
                      ::memory-pool-chunk-size]))

