(ns rfws.subs
  (:require
   [re-frame.core :as re-frame]
   [rfws.logic :as logic]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
  ::answer-list
  (fn [db]
    (:default-wordlist db)))

(re-frame/reg-sub
  ::guess-list
  (fn [db]
    (:guess-list db)))

(re-frame/reg-sub
  ::tile-status
  (fn [db]
    (:tile-statuses db)))

(re-frame/reg-sub
  ::guess-legal?
  (fn [db]
    (and (= 5 (count (:pending-guess db)))
         (or (contains? (:legal-words db)
                        (:pending-guess db))
             (contains? (:default-wordlist db)
                        (:pending-guess db))))))


(re-frame/reg-sub
  ::pending-guess
  (fn [db]
    (:pending-guess db)))

(re-frame/reg-sub
  ::color-scheme
  (fn [db]
    (:color-scheme db)))

(re-frame/reg-sub
  ::answer-list-cycle
  (fn [db]
    (:answer-list db)))

(re-frame/reg-sub
  ::constraints
  (fn [db]
    (let [gl (:guess-list db)
          dl ((first (:answer-list db)) db)
          ts (:tile-statuses db)
          letter-pairs (map-indexed vector (apply str gl))
          tiles   (take (count letter-pairs) ts)
          groups (zipmap letter-pairs tiles)
          possible (->> groups
                       (logic/reduce-tiles)
                       (logic/merge-absents)
                       (logic/process-constraint-list dl))]
      {:eliminators (logic/eliminators possible)
       :best (logic/best-words possible)
       :all possible
       :frequencies (frequencies (apply str possible))
       :freq-by-n (logic/freq-by-n possible)})))

