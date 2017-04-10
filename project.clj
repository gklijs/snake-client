(defproject snake-client "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.cognitect/transit-clj "0.8.300"]
                 [environ "1.1.0"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.3.442"]
                 [quil "2.6.0"]
                 [snake "0.1.0-SNAPSHOT"]
                 [stylefruits/gniazdo "1.0.0"]]
  :min-lein-version "2.0.0"
  :jvm-opts ["-server" "-Dconf=.lein-env"]
  :source-paths ["src/clj"]
  :target-path "target/%s/"
  :main snake-client.core
  :plugins [[lein-environ "1.1.0"]]
  :profiles
  {:dev  [:profiles/dev]
   :test [:profiles/test]
   :profiles/dev  {:env {:show-visual "true"
                         :websocket-uri "ws://localhost:3000/game"
                         :websocket-username "test1234"
                         :websocket-password "test1234"}}
   :profiles/test {:env {:show-visual "false"
                         :websocket-uri "ws://localhost:3000/game"}}})
