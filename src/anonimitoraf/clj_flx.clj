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
  (->> occurrences
       (reduce (fn [[visited satisfied?] curr-occs]
                 (let [unvisited (filter #(not (contains? visited %))
                                         curr-occs)
                       new-visited (conj visited (first unvisited))]
                   (if (empty? unvisited)
                     [visited false]
                     [new-visited (and true satisfied?)])))
               [#{} true])
       (second)))

(defn ^:private chars-in-order?
  [occurrences]
  ;; Basically, if the curr occ set has a higher value than all
  ;; of the prev occ set, it means the char order is violated
  (let [correct-order? (fn [prev-occs curr]
                         (some #(< % curr) prev-occs))]
    (->> occurrences
         (reduce (fn [[prev-occs visited satisfied?] curr-occs]
                   (let [curr-occs (filter #(not (contains? visited %)) curr-occs)
                         satisfied? (and satisfied?
                                         (not-empty curr-occs)
                                         (some #(correct-order? prev-occs %) curr-occs))
                         visited (if (not-empty curr-occs)
                                   (conj visited (first curr-occs))
                                   visited)]
                     [curr-occs visited satisfied?]))
                 [[-1] #{} true])
         (last))))

(defn ^:private calc-score
  [occurrences prev-occ score]
  (let [curr-occs (first occurrences)]
    (if (empty? curr-occs)
      score
      (let [new-occs (drop 1 occurrences)]
        (->> curr-occs
             (map (fn [occ]
                    (calc-score
                     new-occs
                     occ
                     (if (= prev-occ (dec occ))
                       (inc score)
                       score))))
             (apply max))))))

(defn ^:private all-indices-of
  [haystack needle]
  (->> (s/split haystack #"")
       (map-indexed vector)
       (filter #(= (second %) needle))
       (map first)))

(defn ^:private get-occurrences
  [search candidate]
  (map #(all-indices-of candidate %)
       (s/split search #"")))

(defn score
  "Given a non-empty `search` string and a `candidate` match,
  calculates the match score, as follows:

  * A score of nil stands for a non-match.
  * If `search`'s chars don't all exist or are not in the same order
  in `candidate`, then we have a non-match.
  For example:
    * search: 'abc', candidate:'abd' ('c' not in candidate)
    * search: 'abc', candidate: 'bca' (search's chars are in the same order in candidate)
  * 1 point for every consecutive search's chars in candidate. Although
  the first char of search counts as 1 'honorary' point.
  For example:
    * search: 'abc', candidate: 'ab!c' = 2 points, 'ab' are consecutive
    * search: 'abc', candidate: 'a!b!c' = 1 points, 'a' counts as an honorary 1 point
    * search: 'abc', candidate: '!abc!' = 3 points, 'abc' are consecutive
  "
  [search candidate]
  {:pre [(and (not-empty search) (not-empty candidate))]}
  (let [occs (get-occurrences search candidate)]
    (if (and (chars-all-present? occs)
             (chars-in-order? occs))
      (calc-score occs -2 1)
      nil)))

(defn fuzzy-match
  "Given a non-empty `search` string and a seq of `candidates`, returns (fuzzy) matched
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
  {:pre [(not-empty search)]}
  (let [occurrences (map #(get-occurrences search %) candidates)
        occs-by-choice (map vector candidates occurrences)
        ;; E.g. '(["abc" 3] ["a!b!c!" 1])
        candidate-occs-tuples
        (->> occs-by-choice
             (filter (fn [[_ occs]] (and (chars-all-present? occs)
                                         (chars-in-order? occs))))
             ;; Rationale for the defaults to `calc-score`:
             ;; * -2 for the starting occurrence so that the
             ;; score doesn't get affected by whether a candidate's
             ;; starts with `search`'s first char
             ;; * score of 1 to account for the fact that the `search`'s
             ;; first char is automatically counted in the score
             (map (fn [[candidate occs]] [candidate (calc-score occs -2 1)]))
             (sort-by second >))]
    (if with-scores?
      candidate-occs-tuples
      (map first candidate-occs-tuples))))
