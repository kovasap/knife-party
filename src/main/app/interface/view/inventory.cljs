(ns app.interface.view.inventory
  (:require [re-frame.core :as rf]
            [app.interface.utils :refer [get-with-id]]
            [clojure.string :as s]
            [reagent.core :as r]))

(defn item-hover-view
  [item-ids]
  (let [items (filter #(contains? (set item-ids) (:id %))
                @(rf/subscribe [:items]))
        {:keys [display-name traits] :as _hovered-item}
        (first (filter #(:hovered %) items))]
    [:div display-name [:p (s/join ", " traits)]]))

(defn item-view
  [item-id inventory-id]
  (let [{:keys [display-name image hovered]}
        (get-with-id item-id @(rf/subscribe [:items]))]
    [:div {:style         {:border           "1px solid #ddd"
                           :borderRadius     "5px"
                           :background-color (if hovered
                                               "teal"
                                               "rgba(234, 219, 203, 1.0)")
                           :boxShadow        "0 2px 5px rgba(0,0,0,0.1)"}
           :key           display-name
           :on-mouse-over #(rf/dispatch [:app.interface.items/set-hovered
                                         item-id])
           :on-mouse-out  #(rf/dispatch [:app.interface.items/set-not-hovered
                                         item-id])
           :draggable     "true"
           ; Without this, the :on-drop function will never be called.
           :on-drag-over  (fn [e] (.preventDefault e))
           :on-drag-start #(rf/dispatch
                             [:app.interface.items/set-currently-dragged-item
                              item-id
                              inventory-id])
           :on-drag-end   #(rf/dispatch
                             [:app.interface.items/set-currently-dragged-item
                              nil
                              inventory-id])
           :on-drop       #(rf/dispatch [:app.interface.items/swap-items
                                         inventory-id
                                         item-id])}
     [:img {:src image :style {:max-width "100%" :height "auto"}}]]))

(defn inventory-view
  [id]
  (let [{:keys [contents] :as _inventory}
        (get-with-id id @(rf/subscribe [:inventories]))]
    [:div
     [:div.container {:key id}
      (into [:div.row]
            (for [item-id contents]
              [:div.col-4 {:style {:padding "0px"}}
               [item-view item-id id]]))]
     [item-hover-view contents]]))
