(ns app.interface.gridmap.data)

(def tile-data
  [{:id            :forest
    :perlin-cutoff 0.35
    :aspects       {:fire 0 :air 1 :earth 1 :water 1 :light 0 :dark 1}
    :style         {:background-color "forestgreen"}}
   {:id            :plains
    :perlin-cutoff 0.3
    :aspects       {:fire 1 :air 1 :earth 1 :water 1 :light 1 :dark 0}
    :style         {:background-color "LightGreen"}}
   {:id            :water
    :perlin-cutoff 0.0
    :aspects       {:fire 0 :air 0 :earth 0 :water 3 :light 0 :dark 0}
    :style         {:background-color "MediumTurquoise"}}
   {:id            :mountain
    :perlin-cutoff 0.75
    :aspects       {:fire 0 :air 1 :earth 2 :water 0 :light 0 :dark 0}
    :style         {:background-color "grey"}}
   {:id            :sand
    :perlin-cutoff 0.2
    :aspects       {:fire 2 :air 1 :earth 0 :water 0 :light 1 :dark 0}
    :style         {:background-color "yellow"}}
   {:id      :wall
    :aspects {:fire 0 :air 0 :earth 2 :water 0 :light 0 :dark 1}
    :style   {:background-color "DimGrey"}}
   {:id      :road
    :aspects {:fire 0 :air 0 :earth 0 :water 0 :light 1 :dark 0}
    :style   {:background-color "AntiqueWhite"}}
   {:id      :bridge
    :aspects {:fire 0 :air 0 :earth 1 :water 0 :light 1 :dark 0}
    :style   {:background-color "Brown"}}
   {:id            :void
    :perlin-cutoff 10.0
    :aspects       {:fire 0 :air 0 :earth 0 :water 0 :light 0 :dark 3}
    :style         {:background-color "black"}}])
