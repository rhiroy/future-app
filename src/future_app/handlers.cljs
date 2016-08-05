(ns future-app.handlers
  (:require
    [re-frame.core :refer [register-handler after]]
    [schema.core :as s :include-macros true]
    [future-app.db :refer [app-db schema]]))

;; -- Helpers
;; ------------------------------------------------------------

(defn dec-to-zero
  "Same as dec if not zero"
  [arg]
  (if (< 0 arg)
    (dec arg)
    arg))

;; -- Middleware ------------------------------------------------------------
;;
;; See https://github.com/Day8/re-frame/wiki/Using-Handler-Middleware
;;
(defn check-and-throw
  "throw an exception if db doesn't match the schema."
  [a-schema db]
    (when-let [problems (s/check a-schema db)]
      (throw (js/Error. (str "schema check failed: " problems)))))

(def validate-schema-mw
  (if goog.DEBUG
    (after (partial check-and-throw schema))
    []))

;; -- Handlers --------------------------------------------------------------

(register-handler
  :initialize-db
  validate-schema-mw
  (fn [_ _]
    app-db))

(register-handler
  :set-greeting
  validate-schema-mw
  (fn [db [_ value]]
    (assoc db :greeting value)))

(register-handler
  :nav/push
  validate-schema-mw
  (fn [db [_ value]]
    (-> db
      (update-in [:nav :index] inc)
      (update-in [:nav :routes] #(conj % value)))))

(register-handler
  :nav/pop
  validate-schema-mw
  (fn [db [_ _]]
    (-> db
      (update-in [:nav :index] dec-to-zero)
      (update-in [:nav :routes] pop))))

(register-handler
  :nav/home
  validate-schema-mw
  (fn [db [_ _]]
    (-> db
      (assoc-in [:nav :index] 0)
      (assoc-in [:nav :routes] (vector (get-in db [:nav :routes 0]))))))
