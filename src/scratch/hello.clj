;; 12-27-19 goals
;;  * simple auth working - not done
;;  * cljs served from static file - done


;; 12-28-19 goals
;;  * simple auth working
;;  * get CSP and CSRF chugging

;; 12-30-19 goals
;;  * move auth to it's own file
;;  * cookie-auth
;;  * CSRF
;;  * rum managing component state
;;  * beginning of RSS main screen


(ns scratch.hello
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.csrf :as csrf]
            [clojure.pprint :as pprint]
            [io.pedestal.http.ring-middlewares :as middlewares]
            [ring.middleware.session.cookie :as cookie]
            [clojure.data.json :as json]
            [buddy.hashers :as hs]
            [scratch.login :as login])
  (:gen-class))

(def users
  {"tyler" {:password "letmein"
            :display-name "Tyler Rhodes"
            :role :admin}})

(defn ok
  ([body]
   (println "response without session being passed...")
   {:status 200 :body (json/write-str body)
    :headers ["Content-Type" "application/json"]
    :session {"session-empty" {:value "shouldn't see this!"}}
    :cookies {"test" {:value "cookie tiest"}}})
  ([body req m]
   (merge {:status 200 :body (json/write-str body)
           :headers ["Content-Type" "application/json"]
           :session (get req :session)
           :cookies (get req :cookies)}
          m)))

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

(def login-page (slurp (clojure.java.io/resource "public/index.html")))

(defn login-get [request]
  (print "xsrf: " (::csrf/anti-forgery-token request))
  (let [txt (clojure.string/replace login-page #"\{xsrf\}" (::csrf/anti-forgery-token request))]
    (print txt)
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body txt}))
            
(defn login-post [request]
  (pprint/pprint request)
  (let [username (:un (:json-params request))
        password (:pw (:json-params request))]
    (println username " " password)
    (if-let [user (login-user username password)]
      (ok {:logged-in true
           :url "/index.html?loggedin=true"
           :user user}
          request
          {:session
           (merge (:session request)
                  user)})
      (ok false))))

(defonce cnt (atom 0))

(defn session-test [request]
  (let [val (get-in request [:session "session-test" :value] "fudge")]
    (println val)
    (println request)
    (println (:session request))
    (ok {:req "somestuff"} request {})))

(def routes
  (route/expand-routes
   #{["/greet" :get respond-hello :route-name :greet]
     ["/login" :get [(body-params/body-params) login-get] :route-name ::login-get]
     ["/echo"  :any [(body-params/body-params) echo]]
     ["/session-test" :any [(body-params/body-params) login/auth session-test] :route-name :session-test]
     ["/login" :post [login-post] :route-name :login-post]}))

(def service-map
  {::http/routes routes
   ::http/type   :jetty
   ::http/port   8890
   ::http/secure-headers {:content-security-policy-settings
                          {:default-src
                           "* 'unsafe-inline' 'unsafe-eval'"
                           :script-src
                           "* 'unsafe-inline' 'unsafe-eval'"}}
   ::http/enable-csrf {}
   ::http/resource-path "/public"
   ::http/file-path "target/public"
   ::http/enable-session {:store (cookie/cookie-store {:key "a 16-byte secret"})
                          :cookie-attrs {:max-age (* 60 60 24)}}
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








