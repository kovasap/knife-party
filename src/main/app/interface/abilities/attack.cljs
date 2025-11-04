(ns app.interface.abilities.attack
  (:require
    [com.rpl.specter :as sp]
    [app.interface.re-frame-utils :refer [dispatch-fx]]
    [app.interface.database.utils :refer [get-character]]
    [re-frame.core :as rf]))


(rf/reg-event-fx
  ::attack
  (fn [{:keys [db] :as _cofx} [_ attacker-id target-id]]
    (dispatch-fx
      ;
      [[:app.interface.messages-to-player/log
        (str (:full-name (get-character db attacker-id))
             " attacks "
             (:full-name (get-character db target-id)))]
       [:app.interface.abilities.utils.animations/play-animation
        (get-character db attacker-id)
        :attack]
       (fn [db]
         (sp/transform [:characters sp/ALL #(= target-id (:id %)) :vigor]
                       #(- % 1)
                       db))
       [:app.interface.control-flow/pass-turn]])))
