; Following
; https://cljdoc.org/d/metosin/malli/0.8.6/doc/clojurescript-function-instrumentation
(ns app.interface.dev-preload
  {:dev/always true}
  (:require [app.interface.core]
            [malli.dev.cljs :as dev]
            [malli.dev.pretty :as mdpretty]))

(dev/start! {:report (mdpretty/thrower)})
