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

(defn button-row [ n]
  [:div {:key (str "br-" n)}
   (doall (map #(make-button  n %)(range 5)))
   [:br]])

(defn suggestion-list [sug-list]
  [:select {:name "Suggestions" :size 20}
   (for [option sug-list]
     [:option {:key (str "suggestion-" option)
               :on-double-click #(re-frame/dispatch [::events/add-guess  option])}
      option])])

(defn main-panel []
  (let [constraints (re-frame/subscribe [::subs/constraints])
        guesslist (re-frame/subscribe [::subs/guess-list])]
    (fn []
      [:div
       [:h1
        "wordle solver"
        [:div
         (doall (for [n (range (count @guesslist))]
                  (button-row  n)))]]
       [:div {:style {:width "200px"}}
        [:div {:style {:width "50%" :float :left}}
         "eliminators"
         [:br]
         (suggestion-list (->>  @constraints
                                :eliminators
                                (take 60)
                                (map first)))]
        [:div {:width "50%" :float :right}
         "all"
         [:br]
         (suggestion-list (->>  @constraints
                                :all
                                (take 60)))]]


       [:div
        [:p (str (count (:all @constraints)) " words possible.")]
        [:button {:on-click #(re-frame/dispatch [::events/initialize-db])} "Reset"]]])))


