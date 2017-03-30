(ns snake-client.core
  (:require [environ.core :refer [env]]
            [snake-client.visuals :refer [start-visual]]
            [snake-client.websocket :refer [init-socket]]))

(defn -main
  [& args]
  (let [websocket-uri (env :websocket-uri)
        websocket-username (env :websocket-username)
        websocket-password (env :websocket-password)]
    (if
      (and websocket-uri websocket-username websocket-password)
      (do
        (init-socket websocket-uri websocket-username websocket-password)
        (if (= (env :show-visual "false") "true")
          (start-visual)))
      (prn "could not start because one of websocket-uri, websocket-username or websocker-password was missing"
           websocket-uri websocket-username websocket-password)
      )))