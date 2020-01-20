(ns apollo-example.core
  (:require [helix.core :refer [defnc $]]
            [helix.dom :as d]
            ["react-dom" :as rdom]
            ["apollo-boost" :default ApolloClient :refer [gql]]
            ["@apollo/react-hooks" :as apollo :refer [useQuery]]
            [cljs-bean.core :as b]))

(def client (ApolloClient. #js {:uri "https://48p1r2roz4.sse.codesandbox.io"}))

(def exchange-query (gql "{
                            rates(currency: \"USD\") {
                              currency
                              rate
                            }
                           }"))

(defnc ExchangeRates [_]
  (let [{:keys [loading error data]} (-> (useQuery exchange-query)
                                         ;; wrap result in a bean to destructure
                                         (b/bean :recursive true))]
    (cond
      loading (d/div "Loading...")
      error (d/div "Error :(")
      :else (d/div "Success"
             (for [entry (:rates data)]
               (let [{:keys [currency rate]} entry]
                 (d/div {:key currency}
                        (d/p currency ": " rate))))))))

(defnc App [_]
  ($ apollo/ApolloProvider
     {:client client}
     (d/div ($ ExchangeRates))))

(defn ^:dev/after-load start []
  (rdom/render ($ App) (.getElementById js/document "app")))
