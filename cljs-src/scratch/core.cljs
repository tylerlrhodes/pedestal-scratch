
(ns scratch.core
  (:require [goog.dom :as dom]
            [goog.events :as events])
  (:import [goog.events EventType]))

(js/console.log "fuckin work")


(defn test2
  [n]
  (println "hello " n))

(let [btn (dom/getElement "btn1")]
  (events/listen btn EventType.CLICK
                 (fn [evt]
                   (js/alert "it has been clicked..."))))

(js/alert "This is a test..")
