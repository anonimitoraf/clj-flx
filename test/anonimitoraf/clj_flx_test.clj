(ns anonimitoraf.clj-flx-test
  (:require [clojure.test :refer [deftest testing is]]
            [anonimitoraf.clj-flx :refer [fuzzy-match]]))

(deftest non-matches
  (testing "Non-matches"
    (is (= '()
           (fuzzy-match "abc" ["" "a" "ab" "abd" "bca" "cba" "aaa"])))))

(deftest matches
  (testing "Contiguous search char occurrences"
    (let [search "abc"
          candidates ["abc!"
                      "!abc"
                      "!abc!"
                      "!!abc!!"
                      "aabc"
                      "abcc"]
          expected '(["abc!" 3]
                     ["!abc" 3]
                     ["!abc!" 3]
                     ["!!abc!!" 3]
                     ["aabc" 3]
                     ["abcc" 3])]
      (is (= expected (fuzzy-match search candidates :with-scores? true)))))
  (testing "Semi-contiguous search char occurrences"
    (let [search "abc"
          candidates ["ab!c"
                      "a!bc"
                      "!a!bc"
                      "ab!c!"
                      "aabbc"
                      "aabbcc"]
          expected '(["ab!c" 2]
                     ["a!bc" 2]
                     ["!a!bc" 2]
                     ["ab!c!" 2]
                     ["aabbc" 2]
                     ["aabbcc" 2])]
      (is (= expected (fuzzy-match search candidates :with-scores? true)))))
  (testing "contiguous search char occurrences"
    (let [search "abc"
          candidates ["a!b!c"
                      "a!!b!!c"
                      "!a!b!c!"
                      "aa!bb!cc"]
          expected '(["a!b!c" 1]
                     ["a!!b!!c" 1]
                     ["!a!b!c!" 1]
                     ["aa!bb!cc" 1])]
      (is (= expected (fuzzy-match search candidates :with-scores? true))))))
