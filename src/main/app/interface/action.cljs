(ns app.interface.action
  (:require
    [app.interface.abilities :refer [get-usable-ability-event]]
    [app.interface.utils :refer [get-with-id]]
    [day8.re-frame.undo :as undo :refer [undoable]]  
    [app.interface.locations :refer [get-location-of-character]]
    [app.interface.factions :refer [are-enemies?]]
    [app.interface.characters :refer [get-ability-ids]]
    [com.rpl.specter :as sp]
    [re-frame.core :as rf]))

; ------ Computer Decisions -------------

(defn is-valid-attack-target?
  [attacking-character-id query-character-id characters]
  (let [attacking-character (get-with-id attacking-character-id characters)
        query-character (get-with-id query-character-id characters)]
    (and (are-enemies? attacking-character query-character)
         (not (:is-dead query-character)))))

(defn get-single-melee-target-id
  [{:keys [locations characters] :as _db} attacking-character-id]
  (->> (:character-ids (get-location-of-character locations
                                                  attacking-character-id))
       (filter #(is-valid-attack-target? attacking-character-id % characters))
       (first)))

(defn get-location-id-to-move-to
  [{:keys [locations] :as _db} moving-character-id]
  ; Move in arbitrary directions for now
  (first (:adjacent-location-ids
           (get-location-of-character locations moving-character-id))))

(defn get-ability-event
  [{:keys [acting-character-id] :as db}]
  (->> [{:ability-id :attack
         :target-id  (get-single-melee-target-id db acting-character-id)}
        {:ability-id :move
         :target-id  (get-location-id-to-move-to db acting-character-id)}
        {:ability-id :recover}]
       (map (fn [{:keys [ability-id target-id]}]
              (get-usable-ability-event db
                                        ability-id
                                        acting-character-id
                                        target-id)))
       ; If we did not get a use-ability event, we ignore it (the ability is
       ; unusable).
       (remove #(not (= (first %) :app.interface.abilities/use-ability)))
       (first)))
    
; ------ End Computer Decisions -------------

(rf/reg-event-db
  ::advance-acting-character
  [rf/debug]
  (fn [{:keys [characters] :as db} _]
    (assoc db
          :acting-character-id (:id (first (sort-by :next-ready-time
                                                    characters))))))

(rf/reg-event-fx
  ::use-next-ability
  [rf/debug]
  (fn [{{:keys [characters acting-character-id] :as db} :db :as _cofx} _]
    (let [{:keys [controlled-by-player?] :as _acting-character}
          (get-with-id acting-character-id characters)]
      {:fx (mapv (fn [event] [:dispatch event])
             [(if controlled-by-player?
                ; Wait for the player to manually trigger
                ; :app.interface.abilties/use-ability via the UI.
                nil
                (get-ability-event db))])})))

; For now, the player will have to manually step through actions using this
; event.
(rf/reg-event-fx
  ::take-next-action
  [rf/debug (undoable "Take next action")]
  (fn [_ _]
    {:fx [[:dispatch [::advance-acting-character]]
          [:dispatch [::use-next-ability]]]}))
