(ns app.interface.abilities
  (:require
    [com.rpl.specter :as sp]
    [malli.core :as m]
    [app.interface.malli-utils :refer [cast-all]]
    [app.interface.locations :refer [get-location-of-character]]
    [app.interface.malli-schema-registry :refer [register!]]
    [app.interface.utils :refer [get-with-id]]
    [app.interface.traits :refer [calc-damage]]
    [app.interface.characters :refer [path-to-character get-ability-ids]]
    [app.interface.messages-to-player :refer [append-log]]
    [re-frame.core :as rf]))

(register! ::ability-id [:enum :attack :move :recover])

(register! ::ability
  [:map
   [:id ::ability-id]
   [:transformer ::transformer]
   [:icon :string]
   [:animation :app.interface.characters/animation]])

; Returns nil if the db could not be modified
(register! ::transformer
  [:->
   :app.interface.db/db
   :app.interface.characters/character-id
   [:maybe :keyword]
   [:maybe :app.interface.db/db]])

(defn attack
  {:malli/schema (m/deref ::transformer)}
  [{:keys [characters] :as db} character-id target-id]
  (let [attacking-character (get-with-id character-id characters)
        target-character (get-with-id target-id characters)
        damage (calc-damage attacking-character target-character)]
    (if (and attacking-character target-character)
      (as-> db tdb
        (sp/transform [:characters sp/ALL #(= target-id (:id %)) :vigor]
                      #(- % damage)
                      tdb)
        (append-log tdb
                    (str (:full-name attacking-character)
                         " strikes "
                         (:full-name target-character)
                         " for "
                         damage
                         " damage!")))
      nil)))

; This ability should always be usable (this function should never return nil).
(defn recover
  {:malli/schema (m/deref ::transformer)}
  [db character-id _]
  (->> db
       (sp/transform (path-to-character character-id) #(update % :vigor inc))
       (sp/transform (path-to-character character-id) #(update % :will inc))))

; TODO return nil if the character is moving too far
(defn move
  {:malli/schema (m/deref ::transformer)}
  [{:keys [locations characters] :as db} character-id new-location-id]
  (let [moving-character (get-with-id character-id characters)
        current-location (get-location-of-character locations character-id)]
    (if (and moving-character new-location-id)
      (as-> db tdb
        ; Remove current location character entry
        (sp/transform [:locations
                       sp/ALL
                       #(= (:id %) (:id current-location))
                       :character-ids]
                      #(disj % character-id)
                      tdb)
        ; Add new location character entry
        (sp/transform
          [:locations sp/ALL #(= (:id %) new-location-id) :character-ids]
          #(conj % character-id)
          tdb)
        (append-log tdb
                    (str (:full-name moving-character)
                         " moves from " (:id current-location)
                         " to "         new-location-id)))
      nil)))

(def abilities
  (memoize
    #(cast-all
       ::ability
       #{{:id :attack :transformer attack :animation :attack}
         {:id :recover :transformer recover :animation :none}
         {:id :move :transformer move :animation :none}})))

(defn is-usable?
  [db ability-id character-id target-id]
  (let [character (get-with-id character-id (:characters db))]
    (and (contains? (get-ability-ids character) ability-id)
         ((:transformer (ability-id (abilities))) db character-id target-id))))

(defn get-usable-ability-event
  [db ability-id character-id target-id]
  (if (is-usable? db :attack character-id target-id)
    [:app.interface.abilities/use-ability ability-id character-id target-id]
    [:app.interface.messages-to-player/log
     (str character-id " cannot use " ability-id " on " target-id)]))

(rf/reg-event-fx
  ::try-ability-with-dragged-character
  (fn [{:keys [db] :as _cofx} [_ ability-id target-id]]
    {:fx [[:dispatch
           (get-usable-ability-event db
                                     ability-id
                                     (:currently-dragged-character-id db)
                                     target-id)]]}))
   
(rf/reg-event-db
  ::apply-transformer
  (fn [db [_ transformer target-id]] (transformer db target-id)))

(rf/reg-event-fx
  ::use-ability
  (fn [{:keys [db] :as _cofx} [_ ability-id character-id target-id]]
    (let [character (get-with-id character-id (:characters db))
          ability   (get-with-id ability-id (abilities))]
      {:fx (mapv (fn [event] [:dispatch event])
             [[:app.interface.messages-to-player/log
               (str (:full-name character " is using ")
                    (:display-name ability))]
              [:app.interface.animations/play-animation
               character
               (:animation ability)]
              [::apply-transformer (:transformer ability) target-id]
              [:app.interface.characters/increment-next-ready-time
               (:id character)]])})))
