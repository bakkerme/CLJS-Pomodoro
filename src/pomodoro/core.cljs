(ns ^:figwheel-always pomodoro.core
    (:require [reagent.core :as reagent]
              [cljs-time.core :as time]
              [cljs-time.format :as format]
              [cljs-time.periodic :as periodic]))

 
(enable-console-print!)

(def app-state
  (reagent/atom
    {:current-time (time/time-now)
     :time-at-pom-start (time/plus- (time/time-now) (time/minutes 20))
     :clock-state js/Date.
     :isRunning true
     :isResting false
     :iterationsFinished 0
    }))

(def time-formatter (format/formatter "HH:mm:ss"))
(defn format-time [t]
  (format/unparse time-formatter t))

(defn tick []
  (swap! app-state assoc :current-time (time/time-now)))

(defn get-clock-running-state []
  (get @app-state :isRunning))

;; -------------------------
;; Views

(defn clock-running
  [clockStatus]
     (str "Clock is " (if clockStatus "running" "not running" )))

(defn clock [time]
  (format-time time))

(defn get-time-since [since current]
  (if (time/after? since current)
    (time/in-seconds (time/interval current since))))

(defn app-wrapper []
  (let [current-time (:current-time @app-state)
        since-time (:time-at-pom-start @app-state)]
    [:div
     [:h1 (clock-running (get-clock-running-state))]
     [:h2 (clock current-time)]
     [:h2 (get-time-since since-time current-time)]]))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [app-wrapper] (.getElementById js/document "app")))

(.setInterval js/window tick 1000)

(defn init! []
  (mount-root))
