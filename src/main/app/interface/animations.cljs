(ns app.interface.animations
  (:require
    [re-frame.core :as rf]
    [app.interface.re-frame-utils :refer [dispatch-sequentially-with-timings]]
    [app.interface.characters :refer [character-classes-by-id]]))

(def time-between-frames-ms 80)

(defn get-animation-frames
  [class-id animation]
  (:frames (animation (:animations (class-id character-classes-by-id)))))

(defn get-animation-frame-images
  [class-id animation]
  (conj (for [i (range (get-animation-frames class-id animation))]
          (str "class-images/"
               (name class-id)
               "/"
               (name animation)
               "/"
               (inc i)
               ".png"))
        ; Always end the animation back at idle
        (str "class-images/" (name class-id) "/idle.png")))

; TODO add movement of the actual character to this via some kind of offset
; parameter, so that the character moves in the direction of e.g. their
; attack.
(rf/reg-event-fx
  ::play-animation
  (fn [_ [_ {:keys [id class-id] :as _character} animation]]
    {:fx
     (dispatch-sequentially-with-timings
       (for [image (get-animation-frame-images class-id animation)]
         [[:app.interface.characters/change-image id image]
          time-between-frames-ms]))}))

(defn get-animation-duration
  [{:keys [class-id] :as _character} animation]
  (* time-between-frames-ms (get-animation-frames class-id animation)))
