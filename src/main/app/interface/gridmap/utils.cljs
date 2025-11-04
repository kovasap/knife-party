(ns app.interface.gridmap.utils
  (:require
    [app.interface.tiles :refer [tiles]]
    [app.interface.utils :refer [get-only]]
    [app.interface.character-stats :refer [can-view-character-intention?]]
    [clojure.string :as st]))


(defn update-tiles
  "Applies update-fn to all tiles in the gridmap for which tile-selector
  returns true. Returns a new gridmap."
  ([gridmap update-fn] (update-tiles gridmap any? update-fn))
  ([gridmap tile-selector update-fn]
   (into []
         (for [row gridmap]
           (into []
                 (for [tile row]
                   (if (tile-selector tile) (update-fn tile) tile)))))))

(defn get-tiles
  "Get all tiles for which tile-selector is true."
  ([gridmap] (get-tiles gridmap any?))
  ([gridmap tile-selector]
   (reduce concat
     (for [row gridmap]
       (for [tile row
             :when (tile-selector tile)]
         tile)))))

(defn get-tile
  [gridmap tile-selector]
  (first (get-tiles gridmap tile-selector)))

(defn get-characters-current-tile
  [gridmap {:keys [full-name]}]
  (get-tile gridmap (fn [{:keys [character-full-name]}]
                      (= full-name character-full-name))))

(defn get-adjacent-tiles
  [gridmap {:keys [row-idx col-idx]}]
  (filter #(not (nil? %))
    (for [[row-idx-shift col-idx-shift] [[1 0] [0 1] [-1 0] [0 -1]]]
      (get-in gridmap [(+ row-idx row-idx-shift) (+ col-idx col-idx-shift)]))))
