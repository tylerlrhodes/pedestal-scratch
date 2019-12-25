
(ns scratch.hello
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [clojure.pprint :as pprint]
            [io.pedestal.http.ring-middlewares :as middlewares]
            [ring.middleware.session.cookie :as cookie]
            [buddy.hashers :as hs])
  (:gen-class))

(def users
  {"tyler" {:password "letmein"
            :display-name "Tyler Rhodes"
            :role :admin}})
(defn ok [body]
  {:status 200 :body (with-out-str (pprint/pprint body))
   :headers ["Content-Type" "text/html"]
   :session {"session-test" {:value "what the hell"}}
   :cookies {"test" {:value "cookie tiest"}}})

(defn respond-hello [request]
  (if request
    (ok (with-out-str (str (pprint/pprint request) (::anti-forgery request))))
    {:status 500 :body "error"}))

(def echo
  {:name ::echo
   :enter (fn [ctx]
            (let [request (:request ctx)
                  response (ok ctx)]
              (assoc ctx :response response)))})

(defn login-get [request]
  (ok "This would be the login form"))

(defn login-post [request])
  
(def routes
  (route/expand-routes
   #{["/greet" :get respond-hello :route-name :greet]
     ["/echo"  :any [(body-params/body-params) echo]]}))

(def service-map
  {::http/routes routes
   ::http/type   :jetty
   ::http/port   8890
;;   ::http/enable-csrf {}
   ::http/enable-session {:store (cookie/cookie-store {:key "a 16-byte secret"})}
   ::http/host   "0.0.0.0"})

(defn start []
  (http/start (http/create-server service-map)))

(defonce server (atom nil))

(defn start-dev []
  (reset! server
          (http/start (http/create-server
                       (assoc service-map
                              ::http/join? false)))))

(defn stop-dev []
  (http/stop @server))

(defn restart []
  (stop-dev)
  (start-dev))








