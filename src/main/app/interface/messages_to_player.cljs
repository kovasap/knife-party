(ns app.interface.messages-to-player
  (:require
    [com.rpl.specter :as sp]
    [re-frame.core :as rf]))


(defn append-log
  [db log-message]
  (update db :log #(conj % log-message)))
  

(rf/reg-event-db
  ::log
  (fn [db [_ log-message]] (append-log db log-message)))
