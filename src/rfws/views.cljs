(ns rfws.views
  (:require
   [re-frame.core :as re-frame]
   [rfws.subs :as subs]
   [rfws.events :as events]))

(def colors {:correct "#f5793a"
             :misplaced "#86bff9"
             :absent "#787c7e"
             :undefined "white"})

(defn make-button [ row col]
    (let [tile-status (re-frame/subscribe [::subs/tile-status])
          guesses (re-frame/subscribe [::subs/guess-list])
          letters (->> @guesses
                       (apply str))
          letter-index (+ col (* row 5))
          letter (subs letters letter-index (inc letter-index))
          status (nth @tile-status letter-index)]
        [:button {:key (str "letter-" letter-index)
                  :style {:height 70 :width 60 :font-size 30 :background-color (status colors)}
                  :on-click #(re-frame/dispatch [::events/change-status letter-index])}
         (str (.toUpperCase letter))]))

(defn button-row [n]
  [:div {:key (str "br-" n)}
   (doall (map #(make-button  n %)(range 5)))
   [:br]])

(defn suggestion-list [sug-list]
  [:select {:name "Suggestions" :size 20 :style {:width "60px"}}
   (for [option sug-list]
     [:option {:key (str "suggestion-" option)
               :on-double-click #(re-frame/dispatch [::events/add-guess  option])}
      option])])
;(defn ad-hoc-guess []
;  ()
;  [:form
;   [:input {:on-change #(re-frame/dispatch [:new-guess (.-value (.-target %))])}]
;   [:button {:enabled? (re-find #"\b[a-z]{5}\b" @adhocguess)} "Submit"]])

(defn main-panel []
  (let [constraints (re-frame/subscribe [::subs/constraints])
        guesslist (re-frame/subscribe [::subs/guess-list])
        pending-guess (re-frame/subscribe [::subs/pending-guess])
        legal-guess  (re-frame/subscribe [::subs/guess-legal?])]
    (fn []
      [:div
       [:h1
        "Wordle Calculator"
        [:div
         (doall (for [n (range (count @guesslist))]
                  (button-row  n)))]]
       [:form {:onSubmit (fn [e] (do (.preventDefault e)
                                     (identity false)))}
        [:input {:auto-focus true
                 :value @pending-guess
                 :on-change #(re-frame/dispatch [::events/set-pending-guess (-> % .-target .-value)])}]
        [:button
         {:disabled (if @legal-guess false true)
          :on-click #(re-frame/dispatch [::events/submit-text-guess @pending-guess])}
         "Submit"]]

       [:table
        [:thead
         [:td {:width "60px"}"eliminators"]
         [:td {:width "60px"}"   best    "]
         [:td {:width "60px"} "    all     "]]
        [:tbody
         [:tr
          [:td
            (suggestion-list (->>  @constraints
                                   :eliminators
                                   (take 60)
                                   (map first)))]
          [:td
            (suggestion-list (->>  @constraints
                                   :best
                                   (take 60)
                                   (map first)))]
          [:td
            (suggestion-list (->>  @constraints
                                   :all
                                   (take 2400)))]]]]

       [:div
         [:p (str (count (:all @constraints)) " words possible.")]
         [:button {:on-click #(re-frame/dispatch [::events/initialize-db])} "Reset"]
         #_[:div (str (reverse (sort-by val (:frequencies @constraints))))]
         #_[:div (str (:freq-by-n @constraints))]]])))


