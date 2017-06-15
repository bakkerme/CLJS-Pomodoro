(ns ^:figwheel-always pomodoro.core
    (:require [reagent.core :as reagent]
              [cljs-time.core :as time]
              [cljs-time.format :as format]
              [cljs-time.periodic :as periodic]))

 
(enable-console-print!)

(def app-state
  (reagent/atom
    {:current-time (time/time-now)
     :time-at-pom-end (time/time-now)
     :clock-state js/Date.
     :isRunning false
     :isResting false
     :iterationsFinished 0
    }))

(def time-formatter (format/formatter "HH:mm:ss"))
(defn format-time [t]
  (format/unparse time-formatter t))

(defn get-clock-running-state []
  (get @app-state :isRunning))

(defn clock [time]
  (format-time time))

(defn get-time-since [since current]
  (if (time/after? since current)
    (time/in-seconds (time/interval current since))))

(defn get-time-til-pom-end []
  (let [current-time (:current-time @app-state)
        since-time (:time-at-pom-end @app-state)]
  (get-time-since since-time current-time)))

(defn start-clock []
  (swap! app-state assoc :time-at-pom-end  (time/plus- (time/time-now) (time/seconds 10)))
  (swap! app-state assoc :isRunning true))

(defn stop-clock []
  (swap! app-state assoc :time-at-pom-end  (time/time-now))
  (swap! app-state assoc :isRunning false))

(defn toggle-clock-status []
  (if (not (get @app-state :isRunning)) (start-clock) (stop-clock) ))

(defn tick []
  (swap! app-state assoc :current-time (time/time-now))
  (if (get @app-state :isRunning) 
    (if (= (get-time-til-pom-end) 0)
      (stop-clock))))

(defn start-tick []
  (.setInterval js/window tick 1000))

;; -------------------------
;; Views

(defn toggle-clock-wrapper [onClick]
  [:button {:on-click (fn [] (onClick))}
   "Toggle clock state"])


(defn clock-running
  [clockStatus]
     (str "Clock is " (if clockStatus "running" "not running" )))

(defn app-wrapper []
  (let [current-time (:current-time @app-state)
        since-time (:time-at-pom-end @app-state)]
    [:div
     [:h1 (clock-running (get-clock-running-state))]
     [:h2 (clock current-time)]
     [:h2 (if(get @app-state :isRunning) (get-time-since since-time current-time))]
     [:div (toggle-clock-wrapper toggle-clock-status)]]))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [app-wrapper] (.getElementById js/document "app")))


(defn init! []
  (start-tick)
  (mount-root))
