;; 12-27-19 goals
;;  * simple auth working - not done
;;  * cljs served from static file - done


;; 12-28-19 goals
;;  * simple auth working
;;  * get CSP and CSRF chugging


(ns scratch.hello
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [clojure.pprint :as pprint]
            [io.pedestal.http.ring-middlewares :as middlewares]
            [ring.middleware.session.cookie :as cookie]
            [clojure.data.json :as json]
            [buddy.hashers :as hs])
  (:gen-class))

(def users
  {"tyler" {:password "letmein"
            :display-name "Tyler Rhodes"
            :role :admin}})
(defn ok [body]
  {:status 200 :body (json/write-str body)
   :headers ["Content-Type" "application/json"]
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

(defn login-post [request]
  (let [username (:un (:json-params request))
        password (:pw (:json-params request))]
;;    (println "some req" (pprint/pprint request))
    (println username " " password)
    (if (and (contains? users username)
             (= (:password (get users username)) password))
      (ok {:logged-in true
           :url "/index.html?loggedin=true"})
      (ok false))))
  
(def routes
  (route/expand-routes
   #{["/greet" :get respond-hello :route-name :greet]
     ["/echo"  :any [(body-params/body-params) echo]]
     ["/login" :post [(body-params/body-params) login-post] :route-name :login-post]}))

(def service-map
  {::http/routes routes
   ::http/type   :jetty
   ::http/port   8890
   ::http/secure-headers {:content-security-policy-settings
                          {:default-src "*"
                           :script-src
                           "* 'unsafe-inline' 'unsafe-eval'"}}
   ;;   ::http/enable-csrf {}
   ::http/resource-path "/public"
   ::http/file-path "target/public"
   ::http/enable-session {:store (cookie/cookie-store {:key "a 16-byte secret"})}
   ::http/host   "0.0.0.0"})

(defn service
  [service-map]
  (-> service-map
      (http/default-interceptors)
      (update ::http/interceptors conj (middlewares/file-info))))
      
(defn start []
  (http/start (http/create-server (service service-map))))

(defonce server (atom nil))

(defn start-dev []
  (reset! server
          (http/start (http/create-server
                       (assoc (service service-map)
                              ::http/join? false)))))

(defn stop-dev []
  (http/stop @server))

(defn restart []
  (stop-dev)
  (start-dev))








