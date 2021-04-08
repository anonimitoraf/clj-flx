(ns anonimitoraf.clj-flx
  (:require [clojure.string :as s]))

;; One common arg that most of these functions take in is `occurrences`
;;
;; So, what is it?
;; It is a seq of indices of the input chars in the context of a choice.
;; For example:
;;   Given input: \"abcde\"
;;   Choice: \"acbcdae\"
;;   Occurrences: [[0 5] [2] [1 3] [4] [6]]"
;;
;; Note that there is a many-to-many mapping.
;; That is: an input char may occur multiple times in the input and also
;; occur multiple times in a choice.

(defn ^:private chars-all-present?
  [occurrences]
  ;; An empty occ set means the corresponding input char does not exist
  ;; in the choice
  (every? not-empty occurrences))

(defn ^:private chars-in-order?
  [occurrences]
  ;; Basically, if the curr occ set has a higher value than all
  ;; of the prev occ set, it means the char order is violated
  (let [correct-order? (fn [prev-occs curr]
                         (some #(< % curr) prev-occs))]
    (->> occurrences
         (reduce (fn [[prev-occs satisfied?] curr-occs]
                   [curr-occs (and satisfied?
                                   (some #(correct-order? prev-occs %) curr-occs))])
                 [[-1] true])
         (second))))

;; TODO Document scoring algo
(defn ^:private calc-score
  [occurrences]
  (let [contiguous? (fn [prev-occs curr]
                      (some #(= % (dec curr)) prev-occs))]
    (->> occurrences
         (reduce (fn [[prev-occs score] curr-occs]
                   [curr-occs (if (some #(contiguous? prev-occs %) curr-occs)
                                (inc score)
                                score)])
                 [[-1] 0])
         (second))))

(defn ^:private all-indices-of
  [haystack needle]
  (->> (s/split haystack #"")
       (map-indexed vector)
       (filter #(= (second %) needle))
       (map first)))

(defn fuzzy-match
  "Given an `search` string and a seq of `candidates`, returns (fuzzy) matched
  `candidates` ordered desc by score.

  Optional arguments:
  * `with-scores?` - Returns the scores along with the matches.

  Usage examples:
  (fuzzy-match \"abc\" [\"ab\" \"a!b!c!\" \"abc\" \"ab!\"])
    => (\"abc\" \"a!b!c!\")

  (fuzzy-match \"abc\" [\"ab\" \"a!b!c!\" \"abc\" \"ab!\"] :with-scores? true)
    => ([\"abc\" 3] [\"a!b!c!\" 1])

  Algorithm:
  * A candidate is a match if all of `search`'s chars are in the same order
    (note: not necessarily consecutively) in the candidate.
    E.g. Given `search` \"abc\", candidate \"a1b2c3\" is a match, but \"bca\" is not.
  * The more consecutive `search` chars are in a candidate, the higher it's score is.
    The higher the score, the better of a match a candidate is.
    E.g. Given `search` \"abc\", candidate \"ab!c\" is considered a better match than \"a!b!c\"."
  [search candidates & {:keys [with-scores?]}]
  (if (empty? search)
    candidates
    (let [occurrences (map #(map (fn [i] (all-indices-of % i))
                                 (s/split search #""))
                           candidates)
          occs-by-choice (->> occurrences (zipmap candidates) (into []))
          ;; E.g. (["abc" 3] ["a!b!c!" 1])
          candidate-occs-tuples
          (->> occs-by-choice
               (filter (fn [[_ occs]] (and (chars-all-present? occs)
                                           (chars-in-order? occs))))
               (map (fn [[candidate occs]] [candidate (calc-score occs)]))
               (sort-by second >))]
      (if with-scores?
        candidate-occs-tuples
        (map first candidate-occs-tuples)))))

(comment (fuzzy-match "abc" ["ab" "abc" "a!b!c!" "ab!"]))
(comment (fuzzy-match "abc" ["ab" "abc" "a!b!c!" "ab!"] :with-scores? false))
(comment (fuzzy-match "abc" ["ab" "abc" "a!b!c!" "ab!"] :with-scores? true))
