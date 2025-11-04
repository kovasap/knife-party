(ns app.interface.re-frame-utils
  (:require
    [re-frame.core :as rf]))


(def buffer-ms 10)

(defn dispatch-sequentially-with-timings
  [events-with-timings]
  (into []
        (for [[i [event timing]] (map-indexed vector events-with-timings)]
          [:dispatch-later
           {:ms       (reduce +
                        (map #(+ buffer-ms (last %))
                          (subvec (vec events-with-timings) 0 (inc i))))
            :dispatch event}])))

; Convienient for mutating the db from a reg-event-fx fx list.
(rf/reg-event-db
  ::apply-db-transformer
  (fn [db [_ transformer]] (transformer db)))

; Makes some reg-event-fx calls more succinct
(defn dispatch-fx
  [events]
  {:fx (mapv (fn [e] [:dispatch
                      (cond (fn? e)
                            [:app.interface.re-frame-utils/apply-db-transformer
                             e]
                            (vector? e) e
                            :else (throw "Incorrect value"))])
         events)})
