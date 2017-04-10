(ns snake-client.core
  (:require [clojure.core.async :as a]
            [environ.core :refer [env]]
            [snake-client.websocket :refer [init-socket game-info]]))

(defn -main
  [& args]
  (let [websocket-uri (env :websocket-uri)
        websocket-username (env :websocket-username)
        websocket-password (env :websocket-password)]
    (if
      (and websocket-uri websocket-username websocket-password)
      (init-socket websocket-uri websocket-username websocket-password (env :ai-option) (= (env :show-visual "false") "true"))
      (prn "could not start because one of websocket-uri, websocket-username or websocker-password was missing"
           websocket-uri websocket-username websocket-password)
      )))