(ns snake-client.visuals
  (:require [clojure.core.async :as a]
            [quil.core :as q]
            [quil.middleware :as m]
            [snake.sketchpure :refer [draw]]))

(defonce view-channel (atom nil))

(defn setup
  [user-key]
  (reset! view-channel (a/chan (a/sliding-buffer 5)))
  (q/frame-rate 10)
  {:game-state nil :user-key user-key :show-names (atom true) :show-scores (atom true)})

(defn update-state [state]
  ; Update sketch state by changing circle color and position.
  (assoc state :game-state (a/<!! @view-channel)))

(defn start-visual [user-key]
  (q/defsketch game-state
               :title "Showing your bot running around"
               :size [500 400]
               ; setup function called only once, during sketch initialization.
               :setup #(setup user-key)
               ; update-state is called on each iteration before draw-state.
               :update update-state
               :draw (partial draw 10)
               :features [:keep-on-top]
               ; This sketch uses functional-mode middleware.
               ; Check quil wiki for more info about middlewares and particularly
               ; fun-mode.
               :middleware [m/fun-mode]))
