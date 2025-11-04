(ns app.interface.abilities.move
  (:require
    [com.rpl.specter :as sp]
    [app.interface.re-frame-utils :refer [dispatch-fx]]
    [app.interface.database.utils :refer [get-character]]
    [re-frame.core :as rf]))


(rf/reg-event-fx
  ::move
  (fn [{:keys [db] :as _cofx} [_ character-id new-coordinates]]
    (dispatch-fx
      ;
      [[:app.interface.messages-to-player/log
        (str (:full-name (get-character db character-id))
             " moves to "
             new-coordinates)]
       (fn [db]
         (sp/setval [:characters sp/ALL #(= character-id (:id %)) :coordinates]
                    new-coordinates
                    db))
       [:app.interface.control-flow/pass-turn]])))
