(ns snake-client.websocket
  (:require [clojure.core.async :as a]
            [cognitect.transit :as t]
            [gniazdo.core :as ws]
            [snake.ai :refer [predict-next-best-move]]
            [snake-client.visuals :refer [start-visual view-channel]])
  (:import (java.io ByteArrayOutputStream ByteArrayInputStream)))

(defonce socket (atom nil))
(defonce game-info (atom {}))
(defonce game-state (atom nil))

(defn readjson [data]
  (let [in (ByteArrayInputStream. (.getBytes data))
        reader (t/reader in :json)
        result (t/read reader)]
    (.reset in)
    result))

(defn writejson [data]
  (let [baos (ByteArrayOutputStream.)
        writer (t/writer baos :json)
        - (t/write writer data)
        result (.toString baos)]
    (.reset baos)
    result))

(defn send-json
  [msg]
  (ws/send-msg @socket (writejson msg)))

(defn game-state-watcher
  [k r os ns]
  (if (and
        (get-in os [:snakes (:user-key @game-info)])
        (not (get-in ns [:snakes (:user-key @game-info)])))
    (send-json {:start true})
    (send-json {:new-direction (predict-next-best-move ns (:user-key @game-info) 20)}))
  (if @view-channel (a/>!! @view-channel ns)))

(add-watch game-state :game-state-watcher game-state-watcher)

(defn visual-start-watcher
  [k r os ns]
  (if (and (not (contains? os :user-key)) (contains? ns :user-key))
     (start-visual (:user-key ns))))

(defn re-start-watcher
  [k r os ns]
  (send-json {:start true}))

(add-watch game-info :re-start-watcher re-start-watcher)

(defn handle-message
  [new-message]
  (cond
    (string? new-message) (prn new-message)
    (contains? new-message :step) (reset! game-state new-message)
    (contains? new-message :user-key) (swap! game-info merge new-message)))

(defn init-socket
  [uri username password ai-option start-visual-option]
  (if
    start-visual-option
    (add-watch game-info :visual-start-watcher visual-start-watcher))
  (reset! socket (ws/connect
                   uri
                   :on-receive #(handle-message (readjson %))))
  (let [registration-map {:username username :password password}]
    (swap! game-info assoc :registration-map registration-map)
    (send-json registration-map))
  )

(defn close-socket
  []
  (if-let [socketconnection @socket]
    (ws/close @socket)
    (reset! socket nil)))