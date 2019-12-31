(ns scratch.login
  (:require
   [clojure.data.json :as json]
   [buddy.hashers :as hash]))


;; database - validate credentials against
;; database - add a user

(defonce users-db
  {"admin" {:password (hash/derive "letmein")
            :roles [:admin :user]}})

(defn get-user-and-roles [u]
  {:user {:username u
          :roles (get-in users-db [u :roles] nil)}})

(defn login-user [u p]
  (if (and (get-in users-db [u] false)
           (hash/check
            p
            (get-in users-db [u :password] "")))
    (get-user-and-roles u)
    false))

(def auth
  {:name ::zombi-auth
   :enter
   (fn [ctx]
     (if (get-in ctx [:request :session :user] false)
       ctx
       (assoc ctx :response
              {:status 403
               :body (json/write-str {:message "Login Failed"})
               :headers {"Content-Type" "application/json"}})))})

