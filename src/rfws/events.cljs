(ns rfws.events
  (:require
   [re-frame.core :as re-frame]
   [rfws.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]))

(defn next-status [status]
  (cond
    (= :undefined status) :absent
    (= :absent status) :misplaced
    (= :misplaced status) :correct
    (= :correct status) :absent
    :else :absent))

(re-frame/reg-event-db
  ::change-status
  (fn [db [_ let-idx]]
      (-> db
          (update-in [:tile-statuses let-idx] next-status))))

(defn default-row-tiles [row word]
  (into {}
        (for [letter (range 5)]
          [row letter {:letter (nth word letter)
                       :status :absent}])))

(re-frame/reg-event-db
  ::add-guess
  (fn [db [_  guess]]
      (-> db
          (assoc :guess-list (conj (:guess-list db) guess)))))

(re-frame/reg-event-db
  ::reset
  (fn [db [_ _]]
    (-> db
        (conj {:guess-list "" :title-statuses nil}))))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
   db/default-db))

(re-frame/reg-event-db
  ::set-pending-guess
  (fn [db [_ pending-guess]]
    (if (<  (count pending-guess) 6)
      (-> db
        (assoc :pending-guess pending-guess)))))

(re-frame/reg-event-db
  ::toggle-colors
  (fn [db [_ _]]
    (let [current-color (:color-scheme db)
          new-color (->> #{:high-contrast :dark}
                         (remove #(= current-color %))
                         first)]
      (-> db
          (assoc :color-scheme new-color)))))

(re-frame/reg-event-db
  ::toggle-answerlist
  (fn [db [_ _]]
    (let [new-answerlist (conj (vec (rest (:answer-list db))) (first (:answer-list db)))]
      (-> db
          (assoc :answer-list new-answerlist)))))

(re-frame/reg-event-db
  ::submit-text-guess
  (fn [db [_ _]]
    (-> db
        (assoc :guess-list (conj (:guess-list db) (str (:pending-guess db))))
        (dissoc :pending-guess))))



