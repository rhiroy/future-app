(ns future-app.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [future-app.handlers]
            [future-app.subs]))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))
(def card-stack (r/adapt-react-class (.-CardStack (.-NavigationExperimental ReactNative))))
(def navigation-header-comp (.-Header (.-NavigationExperimental ReactNative)))
(def navigation-header (r/adapt-react-class navigation-header-comp))
(def header-title (r/adapt-react-class (.-Title (.-Header (.-NavigationExperimental ReactNative)))))
(def logo-img (js/require "./images/cljs.png"))
(def sikh-img (js/require "./images/Sikh.png"))
(def gurkha-img (js/require "./images/Gurkha-Soldiers.jpg"))
(def amazonian-img (js/require "./images/AmazonianRed.jpg"))
(def regimentr-img (js/require "./images/regimentr.png"))
(def Swiper (js/require "react-native-swiper"))
(def swiper (r/adapt-react-class Swiper))

(defn alert [title]
      (.alert (.-Alert ReactNative) title))

(defn nav-title [props]
  [header-title (aget props "scene" "route" "title")])

(defn header
  [props]
  [navigation-header
   (assoc
     (js->clj props)
     :render-title-component #(r/as-element (nav-title %))
     :on-navigate-back #(dispatch [:nav/pop nil
                                   ]))])

(defn greeting-scene []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
       [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} @greeting]
       [image {:source logo-img
               :style  {:width 80 :height 80 :margin-bottom 30}}]

       [touchable-highlight {:style {:background-color "#FF9633" :padding 10 :border-radius 5}
                             :on-press #(dispatch [:set-greeting "omg i hope this works"])}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me please"]]

       [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                             :on-press #(dispatch [:nav/push {:key :hello-route :title "this is my howdy scene"}])}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me please"]]

       [touchable-highlight {:style {:background-color "#999" :padding 10 :border-radius 5}
                             :on-press #(dispatch [:nav/push {:key :swiper-route :title "this is a swiper"}])}
        [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "swiper"]]  ])))


(defn hello-scene []
   [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
       [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} "hello"]])

(defn swiper-scene []
  [swiper {:style {} :shows-buttons true}

   [view {:style {:flex 1 :justify-content "center" :align-items "center" :background-color "skyblue"}}

    [text {:style {:font-size 30 :font-weight "200" :color "black"}} "The Sikh Regiment"]
    [image {:source sikh-img
             :style {:width 300 :height 400 :margin-bottom 20}}]]
    
   [view {:style {:flex 1 :justify-content "center" :align-items "center" :background-color "steelblue"}}

    [text {:style {:font-size 30 :font-weight "200"}} "The Gurkha Regiment"]
    [image {:source gurkha-img
             :style {:width 300 :height 400 :margin-bottom 20}}]]

   [view {:style {:flex 1 :justify-content "center" :align-items "center" :background-color "aquamarine"}}
    [text {:style {:font-size 30 :font-weight "200"}} "Gaddafi's Bodyguard"]
    [image {:source amazonian-img
            :style  {:width 300 :height 400 :margin-bottom 20}}]]])
(defn scene-helper-function
"This is a helper function to return the correct scene to the card stack"
  [props]
  (let [current-key (keyword (aget props "scene" "route" "key"))]
    (case current-key
      :greeting-route [greeting-scene]
      :hello-route [hello-scene]
      :swiper-route [swiper-scene]
      [greeting-scene])))

(defn app-root
"This is the root of the app. It holds a card stack which renders the initial scene"
  []
  (let [nav (subscribe [:nav/state])]
    (fn []
      [card-stack {:on-navigate      #(dispatch [:nav/pop nil])
                   :render-overlay   #(r/as-element (header %))
                   :navigation-state @nav
                   :style            {:flex 1}
                   :render-scene     #(r/as-element
                                       (scene-helper-function %)
                                        )}])))

(defn init []
      (dispatch-sync [:initialize-db])
      (.registerComponent app-registry "FutureApp" #(r/reactify-component app-root)))
