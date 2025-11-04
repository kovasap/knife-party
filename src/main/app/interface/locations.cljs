(ns app.interface.locations
  (:require [app.interface.malli-schema-registry :refer [register!]]
            [com.rpl.specter :as sp]))

(def land-types
  {:forest {}
   :clearing {}
   :lake {}})

(register! ::land-type
  (into [:enum] (keys land-types)))

(register! ::location-id :keyword)

(register! ::location
  [:map
   [:id ::location-id]
   [:land-type ::land-type]
   [:image {:default-fn
            '#(str "land-images/" (name (:land-type %)) ".png")}
    :string]
   ; Absolute position on map, on a scale from 0-100% of the total map
   ; size
   [:position [:map [:x :int] [:y :int]]]
   [:adjacent-location-ids [:set ::location-id]]
   ; This probably should be deleted in favor of just using merchant characters
   ; instead
   [:inventory-id {:default-fn #(keyword (str (name (:id %)) "-inventory"))}
    :app.interface.items/inventory-id]
   [:traits {:default #{}}
    [:set :app.interface.traits/trait]]
   [:character-ids {:default #{}}
    [:set :app.interface.characters/character-id]]])

(defn path-to-location
  [location-id]
  [:locations sp/ALL #(= location-id (:id %))])

(defn get-location-of-character
  [locations character-id]
  (sp/select-one [sp/ALL
                  (fn [{:keys [character-ids] :as _location}]
                    (contains? character-ids character-id))]
                 locations))
