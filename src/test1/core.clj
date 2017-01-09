(ns test1.core
  (:gen-class)
  (:require [clj-http.client :as client] [clojure.data.json :as json]))

(declare search-string)

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (def engines {:bing "http://www.bing.com/?q=" :google "http://www.google.com/search?q="})
  (search-string "Daniele" engines)
)

(println
  (loop [a 1 b 2]
         (if (>= a 20) a (recur (+ a 1) 1 ))))

(defn search-string [arg engines]
    ( doseq [engine engines]
      (let [result (future (client/get (str (val engine) arg)))]
        (println "--> " (key engine) (count (:body @result))) (spit (str (key engine) "-" arg ".html") (:body @result))
    )
  ))

(defn future-test []
  (let [a 5000 result (future (println "Going to sleep for " a "msecs") (Thread/sleep a) (println "this prints once")
                       (+ 1 1))]
    (println "@: " @result)))

(def yak-butter-international
  {:store "Yak Butter International"
    :price 90
    :smoothness 90})
(def butter-than-nothing
  {:store "Butter Than Nothing"
   :price 150
   :smoothness 83})
;; This is the butter that meets our requirements
(def baby-got-yak
  {:store "Baby Got Yak"
   :price 94
   :smoothness 99})

(defn mock-api-call
  [result]
  (println "Fake api call")
  (Thread/sleep 1000)
  result)

(defn satisfactory?
  "If the butter meets our criteria, return the butter, else return false"
  [butter]
  (println "Trying if " butter " satisfies us")
  (and (<= (:price butter) 100)
       (>= (:smoothness butter) 97)
       butter))

(defn promises-and-futures []
  (time
    (let [butter-promise (promise)]
      (doseq [butter [yak-butter-international butter-than-nothing baby-got-yak]]
        (future (if-let [satisfactory-butter (satisfactory? (mock-api-call butter))]
            (deliver butter-promise satisfactory-butter))))
      (println "And the winner is:" (deref butter-promise 1015 "timeout!")))))

(defn wisdom-test []
    (let [ferengi-wisdom-promise (promise)]
      (future (println "Here's some Ferengi wisdom:" @ferengi-wisdom-promise))
      (Thread/sleep 100)
      (deliver ferengi-wisdom-promise "Whisper your way to success.")))

(defmacro wait
  "Sleep `timeout` seconds before evaluating body"
  [timeout & body]
  `(do (Thread/sleep ~timeout) ~@body))

;  (let [saying3 (promise)]
;    (future (deliver saying3 (wait 100 "Cheerio!")))
;    @(let [saying2 (promise)]
;       (future (deliver saying2 (wait 400 "Pip pip!")))
;          @(let [saying1 (promise)]
;          (future (deliver saying1 (wait 200 "'Ello, gov'na!")))
;          (println @saying1)
;          saying1)
;       (println @saying2)
;       saying2)
;    (println @saying3)
;    saying3)

(defmacro enqueue
    ([q concurrent-promise-name concurrent serialized]
     `(let [~concurrent-promise-name (promise)]
      (future (deliver ~concurrent-promise-name ~concurrent))
      (deref ~q)
      ~serialized
      ~concurrent-promise-name))
    ([concurrent-promise-name concurrent serialized]
   `(enqueue (future) ~concurrent-promise-name ~concurrent ~serialized)))

;(-> (enqueue saying (wait 200 "'Ello, gov'na!") (println @saying))
;    (enqueue saying (wait 400 "Pip pip!") (println @saying))
;    (enqueue saying (wait 100 "Cheerio!") (println @saying)))
