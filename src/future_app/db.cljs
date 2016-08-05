(ns future-app.db
  (:require [schema.core :as s :include-macros true]))

;; schema of app-db

(def NavigationRoute
  {:key s/Keyword
   :title s/Str})

(def NavigationState
 {:index s/Int
  :routes [NavigationRoute]})

;; schema of app-db
(def schema {:greeting s/Str
             :nav NavigationState})

;; initial state of app-db
(def app-db {:greeting "This is a greeting"
             :nav {:index    0
                   :routes [{:key :greeting-route
                             :title "First route"}]}})
