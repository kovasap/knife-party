(ns app.interface.characters
  (:require [malli.core :as m]
            [malli.transform :as mt]
            [com.rpl.specter :as sp]
            [app.interface.utils :refer [associate-by get-with-id]]
            [app.interface.malli-schema-registry :refer [register!]]
            [clojure.set :refer [union]]
            [re-frame.core :as rf]))

(register! ::character-id :keyword)

(register! ::character
  [:map
   [:id ::character-id]
   [:full-name :string]
   [:image {:default-fn
            '#(str "class-images/" (name (:class-id %)) "/idle.png")}
    :string]
   [:class-id ::character-class-ids]
   [:inventory-id {:default-fn #(keyword (str (name (:id %)) "-inventory"))}
    :app.interface.items/inventory-id]
   ; Whether or not to show details about this character in the ui.  the
   ; details menu allows you to select an ability, change items, etc.
   [:show-details? {:default false}
    :boolean]
   [:injuries {:default 0}
    :int]
   [:vigor :int]
   [:traumas {:default 0}
    :int]
   [:will :int]
   [:is-dead {:default false}
    :boolean]
   [:next-ready-time {:default 0}
    :int]
   [:innate-abilities {:default #{:recover}}
    [:set :app.interface.abilities/ability-id]]
   [:innate-traits [:set :app.interface.traits/trait]]
   [:controlled-by-player? :boolean]
   [:faction :app.interface.factions/faction]])

(register! ::animation
  [:enum :attack :none])

(register! ::animation-data
  [:map
   [:frames :int]])

(register! ::character-class
  [:map
   [:id :keyword]
   [:animations 
    [:map-of ::animation ::animation-data]]])

; TODO automatically determine animation :frames based on the files in the
; resources/public/class-images/<class-name> directories
(def character-classes
  [{:id :skirmisher
    :animations 
    {:attack {:frames 5}}}
   {:id :scholar
    :animations 
    {:attack {:frames 2}}}])

; More sprites and animations can be found at
; https://github.com/wesnoth/wesnoth/tree/master/data/core/images/units

(register! ::character-class-ids
  (into [:enum] (map :id character-classes)))

(def character-classes-by-id
  (associate-by :id character-classes))

(defn get-traits
  [{:keys [innate-traits inventory] :as _character}]
  (union innate-traits (apply union (map :traits inventory))))

(defn get-ability-ids
  [{:keys [innate-abilities inventory] :as _character}]
  (union innate-abilities
         (apply union (map :abilities inventory))))

(defn path-to-character
  [character-id]
  [:characters sp/ALL #(= character-id (:id %))])

(rf/reg-event-db
  ::change-image
  (fn [db [_ character-id new-image]]
    (sp/transform (path-to-character character-id)
                  #(assoc % :image new-image)
                  db)))

(rf/reg-event-db
  ::increment-next-ready-time
  [rf/debug]
  (fn [db [_ character-id]]
    (sp/transform (path-to-character character-id)
                  #(update % :next-ready-time + 5)
                  db)))

(rf/reg-event-db
  ::show-details?
  (fn [db [_ character-id value]]
    (sp/transform (path-to-character character-id)
                  #(assoc % :show-details? value)
                  db)))

(rf/reg-event-db
  ::set-currently-dragged-character-id
  (fn [db [_ character-id]]
    (assoc db :currently-dragged-character-id character-id)))
