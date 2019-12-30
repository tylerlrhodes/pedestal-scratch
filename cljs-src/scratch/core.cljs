
(ns scratch.core
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require [goog.dom :as dom]
            [goog.events :as events]
            [rum.core :as rum]          
            [ajax.core :as ajax])
            ;;[cljs-ajax.core :as ajax])  
  (:import [goog.events EventType]
           [goog.html SafeHtml]))

(js/console.log "RSS Zombie starting...")

(defonce clicks (atom 0))

(def login-url "/login")

(rum/defc login-btn [text on-click-fn]
  [
   [:button {:class "label"
             :on-click on-click-fn}
    text]
   [:br]])

(defn login [un pw cb]
  (ajax/POST
   login-url
   {:handler cb
    :params {:un un
             :pw pw}
    :format (ajax/json-request-format)
    :response-format (ajax/json-response-format {:keywords? true})}))

(defn handle-login
  [resp]
  (let [resp (js->clj resp)
        logged-in (:logged-in resp)
        url (:url resp)]
    (if logged-in
      (js/window.open url "_self")
      (js/console.log "blerg"))))

(rum/defcs component <
  (rum/local "" ::username)
  (rum/local "" ::password)
  (rum/local "" ::status-msg)
  [state]
  (let [username (::username state)
        password (::password state)
        status-msg (::status-msg state)
        handle-login
        (fn [resp]
          (let [resp (js->clj resp)
                logged-in (:logged-in resp)
                url (:url resp)]
            (if logged-in
              (js/window.open url "_self")
              (reset! status-msg "Login failed..."))))]
    [:div
     [:h1 "RSS Zombie"]
     [:hr]
     [:form
      [:label "Username:"]
      [:input
       {:type "text"
        :on-change (fn [evt]
                     (reset! username (.. evt -target -value)))}]
      [:br]
      [:label "Password:"]
      [:input 
       {:type "text"
        :on-change (fn [evt]
                     (reset! password (.. evt -target -value)))}]]
     (login-btn "Login"
                (fn [evt]
                  (login @username @password
                         handle-login)))
     [:br]
     [:span "Status: " @status-msg]]))

(rum/mount (component "test rum..") (dom/getElement "app"))

(defn f []
  (if true
    false
    true))
