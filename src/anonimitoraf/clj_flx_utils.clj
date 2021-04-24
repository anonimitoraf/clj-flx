(ns anonimitoraf.clj-flx-utils
  (:require
   [clojure.string :as s]
   [anonimitoraf.clj-flx :refer [calc-score]]))

(defn ^:private left-pad
  ([s max-len]
   (left-pad s max-len " "))
  ([s max-len pad-char]
   (let [pad-count (- max-len (count s))
         pad (s/join (repeat pad-count pad-char))]
     (str pad s))))

(defn ^:private print-matrix
  [matrix]
  (let [max-val (->> matrix (apply concat) (apply max))]
    (doseq [row matrix]
      (prn (s/join " | "
                   (map (fn [cell]
                          (left-pad (str cell)
                                    (-> max-val str count)))
                        row))))
    (prn)))

(comment (time (calc-score "longer string"
                           "a lot lot longer string - candidate string"))
         (print-matrix (first (calc-score "abc"
                                          "a!bc")))
         (print-matrix (first (calc-score "aaa"
                                          "a!a!a!")))
         (print-matrix (first (calc-score "aabc"
                                          "aabbc"))))
