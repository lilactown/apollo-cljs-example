(ns apollo-example.core
  (:require [hx.react :as hx :refer [defnc]]
            ["react-dom" :as rdom]
            ["react-apollo" :as apollo]
            ["apollo-boost" :default ApolloClient :refer [gql]]
            [applied-science.js-interop :as j]))

(def client (ApolloClient. #js {:uri "https://48p1r2roz4.sse.codesandbox.io"}))

(def exchange-query (gql "{
                            rates(currency: \"USD\") {
                              currency
                              rate
                            }
                           }"))

(defnc ExchangeRates [_]
  [apollo/Query {:query exchange-query :asdf-jkl "foo"}
   (fn [result]
     ;; For some reason, react-apollo results won't conver to CLJS data cleanly
     ;; Possibly some weird prototype thing.
     ;; So, we use the js-interop library for easy access to the result object
     (let [{:keys [loading error data]} (j/lookup result)]
       (cond
         loading [:div "Loading..."]
         error [:div "Error :("]
         :else [:div (for [entry (j/get data :rates)]
                       (let [{:keys [currency rate]} (j/lookup entry)]
                         [:div {:key currency}
                          [:p currency ": " rate]]))])))])

(defnc App [_]
  [apollo/ApolloProvider {:client client}
   [:div
    [ExchangeRates]]])

(rdom/render (hx/f [App]) (.getElementById js/document "app"))
