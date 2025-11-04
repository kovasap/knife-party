(ns app.interface.malli-schema-registry
  (:require 
    [malli.core :as m]
    [malli.registry :as mr]))

(defonce *registry (atom {}))

(defn register! [type ?schema]
  (swap! *registry assoc type ?schema))

(mr/set-default-registry!
  (mr/composite-registry
     (m/default-schemas)
     (mr/mutable-registry *registry)))
