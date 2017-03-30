(ns snake-client.websocket
  (:require [cognitect.transit :as t]
            [gniazdo.core :as ws])
  (:import (java.io ByteArrayOutputStream ByteArrayInputStream)))

(defonce socket (atom nil))
(defonce game-info  (atom {}))
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

(defn handle-message
  [new-message]
  (cond
    (string? new-message) (prn new-message)
    (contains? new-message :step) (reset! game-state new-message)
    (contains? new-message :user-key) (do
                                        (swap! game-info merge new-message)
                                        (send-json {:start true})
                                        )))

(defn init-socket
  [uri username password]
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