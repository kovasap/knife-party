(ns app.interface.traits
  (:require
   [app.interface.malli-schema-registry :refer [register!]]))


(register! ::trait [:enum :aquatic :sword :lance :axe])

(defn calc-damage
  [attacker defender]
  2)

(defn calc-recovery-time
  [character]
  5)
