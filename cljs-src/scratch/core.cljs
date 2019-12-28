
(ns scratch.core
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require [goog.dom :as dom]
            [goog.events :as events]
            [rum.core :as rum])  
  (:import [goog.events EventType]
           [goog.html SafeHtml]))

(js/console.log "fuckin work.")


(defn test2
  [n]
  (js/alert "clicked.."))

(let [btn (dom/getElement "btn1")]
  (events/listen btn EventType.CLICK
                 (fn [evt]
                   (js/alert "it has been clicked..."))))

(defonce clicks (atom 0))

(rum/defc label [text]
  [:button {:class "label"
            :on-click (fn [evt] (do
                                  (swap! clicks inc)
                                  (js/alert "hi")))} text])

(rum/mount (label "test rum..") (dom/getElement "app"))





