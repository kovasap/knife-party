(ns app.interface.malli-utils
  (:require 
    [malli.core :as m]
    [malli.transform :as mt]))

; From https://github.com/metosin/malli/blob/master/docs/tips.md#default-value-from-a-function
(defn default-fn-value-transformer
  ([] (default-fn-value-transformer nil))
  ([{:keys [key] :or {key :default-fn}}]
   (let [add-defaults
         {:compile
          (fn [schema _]
            (let [->k-default (fn [[k {default key :keys [optional]} v]]
                                (when-not optional
                                  (when-some [default (or default
                                                          (some-> v
                                                                  m/properties
                                                                  key))]
                                    [k default])))
                  defaults    (into {} (keep ->k-default) (m/children schema))
                  exercise    (fn [x defaults]
                                (reduce-kv (fn [acc k v]
                                             ; the key difference compare
                                             ; to default-value-transformer
                                             ; we evaluate v instead of
                                             ; just passing it
                                             (if-not (contains? x k)
                                               (-> (assoc acc k ((m/eval v) x))
                                                   (try (catch :default _ acc)))
                                               acc))
                                           x
                                           defaults))]
              (when (seq defaults)
                (fn [x] (if (map? x) (exercise x defaults) x)))))}]
     (mt/transformer {:decoders {:map add-defaults}
                      :encoders {:map add-defaults}}))))

(defn cast
  [schema data]
  (as-> data d
    (m/decode schema
              d
              (mt/transformer (default-fn-value-transformer)
                              (mt/default-value-transformer)))
    (doto d #(m/validate schema %))))

(defn cast-all
  [schema datas]
  (into (cond (= (type []) (type datas))  []
              (= (type #{}) (type datas)) #{}
              :else                       #{})
        (map #(cast schema %) datas)))
