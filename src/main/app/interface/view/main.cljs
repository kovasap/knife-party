(ns app.interface.view.main
  (:require [re-frame.core :as rf]
            [reagent.core :as r]
            [app.interface.view.locations :refer [locations-view]]
            [app.interface.view.undo :refer [undo-button]]
            [app.interface.view.character :refer [character-view]]
            [cljs.pprint]))

(defn main
  "Main view for the application."
  []
  [:div.container
   [:h1 "My App"]
   [:div @(rf/subscribe [:message])]
   [:div {:style {:display "flex"}}
    [:button.btn.btn-outline-primary {:on-click
                                      #(rf/dispatch
                                         [:app.interface.core/setup])}
     "Reset App"]
    [undo-button]]
   [:br]
   [:button.btn.btn-outline-primary
    {:on-click #(rf/dispatch [:app.interface.action/take-next-action])}
    "Next Character Actions"]
   [locations-view
    @(rf/subscribe [:locations])
    @(rf/subscribe [:characters])]
   [:br]
   [:br]
   [:br]
   [:br]
   [:div {:style {:display "flex"}}
     (into [:div
            [:h3 "Action Log"]]
           (for [log-item @(rf/subscribe [:log])]
             [:p log-item]))
     (into [:div
            [:h3 "Turn Order"]]
           (for [{:keys [full-name next-ready-time]} 
                 (sort-by :next-ready-time @(rf/subscribe [:characters]))]
              [:p next-ready-time "    " full-name]))]])


;; Troubleshooting UI issues

; Make sure all components (functions) are wrapped with [] in the returned
; datastructure, otherwise you will get "Functions are not valid as a React
; child" errors!

; If a function defining a component returns a callback function, note that all
; ratom dereferences up the stack are ignored; if a parent component is
; triggered to re-render, a child component returning a callback function will
; NOT re-render!
