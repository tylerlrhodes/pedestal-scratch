(ns scratch.core
  (:require [scratch.hello])
  (:gen-class))
 
(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))


;; set-mark-command

(defn -main [& args]
  (scratch.hello/start-dev))
  
