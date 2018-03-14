(ns perception.core
    (:require [cljsjs.d3]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:data [3 5 13 6 8]}))
(def viz-height 125)
(def viz-width 150)
(def margin 10)


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)

(def colors (js->clj (.-schemeCategory10 js/d3)))

(defn select-svg [class-selector]
  (.. (.select js/d3 (str ".viz-container" class-selector " svg"))
      (attr "height" viz-height)
      (attr "width" viz-width)
      ))

(def height-scale
  (.. (.scaleLinear js/d3)
      (domain (into-array [0 13]))
      (range (into-array [(- viz-height margin) margin]))))

(.. (select-svg ".position")
    (selectAll "cirle.points")
    (data (into-array (:data @app-state)))
    (enter)
    (append "circle")
    (classed "points" true)
    (attr "cx" (fn [d i] (* (inc i) 25)))
    (attr "cy" #(height-scale %))
    (attr "r" 3))


(.. (select-svg ".length")
    (selectAll "line.lengths")
    (data (into-array (:data @app-state)))
    (enter)
    (append "line")
    (classed "lengths" true)
    (attr "x1" (fn [d i] (* (inc i) 25)))
    (attr "x2" (fn [d i] (* (inc i) 25)))
    (attr "y2" (- viz-height margin))
    (attr "y1" #(height-scale %)))


(.. (select-svg ".area")
    (selectAll "circle.areas")
    (data (into-array (:data @app-state)))
    (enter)
    (append "circle")
    (classed "areas" true)
    (attr "shape-rendering" "geometricPrecision")
    (attr "cx" (fn [d i] (* (inc i) 25)))
    (attr "cy" (/ viz-height 2))
    (attr "r" #(/ ( * 3.14 %) 3))
    (attr "fill" (fn [d i] (nth colors (+ i 3)))))


(defonce pie (.pie js/d3))

(defonce path
  (.. (.arc js/d3)
      (outerRadius 40)
      (innerRadius 0)))

(.. (select-svg ".angle")
    (selectAll "path.angle")
    (data (pie (into-array (:data @app-state))))
    (enter)
    (append "g")
    (append "path")
    (attr "d" path)
    (attr "fill" (fn [d i] (nth colors i)))
    (attr "transform" (str "translate(" (/ viz-width 2) "," (/ viz-height 2) ")")))


(def color-scale-saturation
  (.. (.scaleLinear js/d3)
      (domain (clj->js [0 (apply max (:data @app-state))]))
      (range (clj->js ["#deebf7" "#3182bd"]))))

(.. (select-svg ".saturation")
    (selectAll "rect.saturation")
    (data (into-array (:data @app-state)))
    (enter)
    (append "rect")
    (attr "x" (fn [d i] (* (inc i) 22)))
    (attr "y" (/ viz-height 2))
    (attr "width" 20)
    (attr "height" 20)
    (attr "offset" "10%")
    (attr "fill" #(color-scale-saturation %)))
