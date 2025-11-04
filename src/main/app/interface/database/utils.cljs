(ns app.interface.database.utils
  (:require 
    [com.rpl.specter :as sp]))

(defn get-character
  [db id]
  (sp/select-one [:characters sp/ALL #(= id (:id %)) :full-name] db))
