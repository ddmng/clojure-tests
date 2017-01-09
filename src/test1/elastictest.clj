(ns test1.elastictest
  (:gen-class)
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]
            [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [clojurewerkz.elastisch.rest.document :as esd]))

(declare index-document create-index)

(def conn-string "http://192.168.70.100:9200")
(def mapping-name "person")
(def mapping {mapping-name {:properties {:username   {:type "string" :store "yes"}
                                              :first-name {:type "string" :store "yes"}
                                              :last-name  {:type "string"}
                                              :age        {:type "integer"}
                                              :title      {:type "string" :analyzer "snowball"}
                                              :planet     {:type "string"}
                                              :biography  {:type "string" :analyzer "snowball" :term_vector "with_positions_offsets"}}}})

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Running elastic tests for a second time")

  ; Connects to elastic and does all the stuff
  (let [ conn (esr/connect conn-string)
         index "daniele-test-1"
         doc {:username "happyjoe" :first-name "Joe" :last-name "Smith" :age 30 :title "Teh Boss" :planet "Earth" :biography "N/A"}
        ]

    (println "Connected to elastic at " conn-string conn)

    ; creates a test index w/ default settings
    (esi/delete conn index)
    (println "Index deleted")
    ; (esi/create conn "daniele-test-1" :settings {"number_of_shards" 1 "number_of_replicas" 0} :mappings mapping-types)
    (create-index conn index mapping)

    (index-document conn index mapping-name "1" doc)
    (println "Index created")
    ) ; let
  )

(defn create-index 
  "Creates an index"
  [conn index mapping & {:keys [shards replicas] :or {shards 1 replicas 1} } ]
    (esi/create conn index :settings {"number_of_shards" shards "number_of_replicas" replicas} :mappings mapping)
  )

(defn index-document 
  "Indexes a new document"
  [conn index mapping id doc]
    (esd/put conn index mapping id doc)
    (println "Document added")
  )