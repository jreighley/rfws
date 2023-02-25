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
  ::constraints
  (fn [db]
    (let [gl (:guess-list db)
          dl (:default-wordlist db)
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
       :all possible})))