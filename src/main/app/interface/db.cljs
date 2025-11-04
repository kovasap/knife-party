(ns app.interface.db
  (:require [app.interface.malli-utils :refer [cast-all cast]]
            [app.interface.items :refer [add-inventory]]
            [app.interface.characters :refer [path-to-character]]
            [app.interface.locations :refer [path-to-location]]
            [app.interface.malli-schema-registry :refer [register!]]
            [re-frame.core :as rf]))

(register! ::db
  [:map
   [:message {:default ""}
    :string]
   [:log [:vector :string]]
   ; Use refs here to avoid stack overflow, as it is a recursive
   ; definition.
   [:locations [:set [:ref :app.interface.locations/location]]]
   [:acting-character-id [:ref :app.interface.characters/character-id]]
   [:inventories [:set [:ref :app.interface.items/inventory]]]
   [:items [:set [:ref :app.interface.items/item]]]
   [:current-drag
    [:map
     [:item {:default nil}
      [:maybe :app.interface.items/item]]
     [:inventory-id {:default nil}
      [:maybe :app.interface.items/inventory-id]]]]
   [:characters [:set [:ref :app.interface.characters/character]]]])

(def initial-db
  (cast
    ::db
    (-> {:locations   (cast-all :app.interface.locations/location
                                #{{:id        :farbane
                                   :land-type :forest
                                   :adjacent-location-ids #{:clear :central}
                                   :position  {:x 0 :y 0}}
                                  {:id        :clear
                                   :land-type :clearing
                                   :adjacent-location-ids #{:farbane :central}
                                   :position  {:x 50 :y 0}}
                                  {:id            :central
                                   :land-type     :clearing
                                   :adjacent-location-ids #{:farbane :clear
                                                            :deep :nearbane}
                                   :position      {:x 50 :y 50}
                                   :character-ids #{:hare :tortoise}}
                                  {:id        :deep
                                   :land-type :lake
                                   :adjacent-location-ids #{:central}
                                   :position  {:x 0 :y 100}
                                   :character-ids #{:merchant}}
                                  {:id        :nearbane
                                   :land-type :forest
                                   :adjacent-location-ids #{:central :deep}
                                   :position  {:x 100 :y 100}}})
         :log         ["first message"]
         :acting-character-id :hare
         :inventories #{}
         :characters  (cast-all :app.interface.characters/character
                                #{{:full-name "Hare"
                                   :id        :hare
                                   :vigor     3
                                   :will      2
                                   :class-id  :skirmisher
                                   :faction   :player
                                   :controlled-by-player? true}
                                  {:full-name "Tortoise"
                                   :id        :tortoise
                                   :vigor     5
                                   :will      5
                                   :faction   :bandits
                                   :class-id  :skirmisher
                                   :controlled-by-player? false}
                                  {:full-name "Merchant"
                                   :id        :merchant
                                   :vigor     5
                                   :will      5
                                   :faction   :merchants
                                   :class-id  :scholar
                                   :controlled-by-player? false}})}
        (add-inventory [:mace :boots :nothing] (path-to-character :hare))
        (add-inventory [:boots :nothing :nothing]
                       (path-to-character :tortoise))
        (add-inventory [:nothing :boots] (path-to-character :merchant)))))

; Nice way to generate subsciptions for many keys.
(doseq [kw [:locations
            :player-characters
            :characters
            :message
            :log
            :inventories
            :items]]
  (rf/reg-sub
    kw
    (fn [db _] (kw db))))
