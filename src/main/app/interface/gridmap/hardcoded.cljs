(ns app.interface.gridmap.hardcoded
  (:require [clojure.string :as st]))

(def default-tile
  {:})

(def default-map
  {:grid
   "
FFFFFFFFFFFFFF
FFFFFFFFFFFFFF
FFFFFFFFFFFFFF
FFFF1FFFFFFFFF
FFFFFFFFFFFFFF
   "
   :codes {"F" {:tile-id :forest}
           "1" {:tile-id :forest
                :character-ids [:fred]}}
   :characters
   [{:id :fred}]})

(defn parse-map
  [{:keys [grid codes characters]}]
  {:gridmap    (->> grid
                    (st/split-lines)
                    (map-indexed (fn [row-idx line]
                                   (map-indexed (fn [col-idx symb]
                                                  (-> (get codes symb)
                                                      (assoc :row-idx row-idx)
                                                      (assoc :col-idx
                                                             col-idx)))
                                                line)))
                    (flatten))
   :characters characters})
      
