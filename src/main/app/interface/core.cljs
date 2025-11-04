(ns app.interface.core
  (:require ["react-dom/client" :refer [createRoot]]
            ; Import everything to make sure all event handlers are registered
            [app.interface.abilities]
            [app.interface.action]
            [app.interface.animations]
            [app.interface.characters]
            [app.interface.factions]
            [app.interface.items]
            [app.interface.traits]
            [app.interface.locations]
            [app.interface.db :refer [initial-db]]
            [app.interface.messages-to-player]
            [app.interface.view.main :refer [main]]
            [cljs.pprint]
            [day8.re-frame.http-fx]
            [goog.dom :as gdom]
            [re-frame.core :as rf]
            [reagent.core :as r]))

;; ----------------------------------------------------------------------------
;; Setup

(rf/reg-event-db
  ::setup
  (fn [_db _] initial-db))

;; -- Entry Point -------------------------------------------------------------

(defonce root (createRoot (gdom/getElement "app")))

(defn init
  []
  (rf/dispatch [::setup])
  (.render root (r/as-element [main])))

(defn- ^:dev/after-load re-render
  "The `:dev/after-load` metadata causes this function to be called after
  shadow-cljs hot-reloads code. This function is called implicitly by its
  annotation."
  []
  (rf/clear-subscription-cache!)
  (init))
