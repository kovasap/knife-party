(ns app.interface.view.character
  (:require
   [app.interface.view.inventory :refer [inventory-view]]
   [app.interface.abilities :refer [get-usable-ability-event]]
   [clojure.string :as s]
   [re-frame.core :as rf]))

(defn detail-view
  [{:keys
    [image id is-dead controlled-by-player? full-name vigor will inventory-id]
    :as character}
   index-in-location]
  [:div {:on-mouse-over #(rf/dispatch
                           [:app.interface.characters/show-details? id true])
         :on-mouse-out  #(rf/dispatch
                           [:app.interface.characters/show-details? id false])
         :style         {:position         "absolute"
                         :background-color "rgba(234, 219, 203, 1.0)"
                         :margin-top       "-50px"
                         :margin-left      (cond (= index-in-location 0)
                                                 "-130px"
                                                 (= index-in-location 1)
                                                 "70px"
                                                 :else nil)
                         :width            "200%"
                         :z-index          100
                         :boxShadow        "0 2px 5px rgba(0,0,0,0.1)"}}
   [:strong full-name]
   [:div.container
    [:div.row [:div.col "Vigor"] [:div.col vigor]]
    [:div.row [:div.col "Will"] [:div.col will]]]
   [inventory-view inventory-id]])

(defn character-view
  [{:keys [image
           id
           is-dead
           controlled-by-player?
           full-name
           vigor
           show-details?
           inventory-id]
    :as   character}
   index-in-location]
  [:div {:id id
         :style {:position "relative" :z-index 20}
         :draggable "true"
         ; Without this, the :on-drop function will never be called.
         :on-drag-over (fn [e] (.preventDefault e))
         :on-drag-start
         #(rf/dispatch
            [:app.interface.characters/set-currently-dragged-character-id id])
         :on-drag-end
         #(rf/dispatch
            [:app.interface.characters/set-currently-dragged-character-id nil])
         :on-drop
         #(rf/dispatch
            [:app.interface.abilities/try-ability-with-dragged-character
             :attack
             id])}
   (if show-details? [detail-view character index-in-location] nil)
   [:img {:style {:transform (s/join
                               " "
                               (remove s/blank?
                                 [(if controlled-by-player? nil "scaleX(-1)")
                                  (if is-dead "rotate(90deg)" nil)]))}
          ; :filter    "drop-shadow(0px 0px 20px red)"}
          :src   image
          :alt   full-name}]
   [:div {:style {:position "absolute" :top "0%" :left "0%"}}
    vigor]])
