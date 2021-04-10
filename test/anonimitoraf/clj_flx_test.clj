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
  (testing "Non-contiguous search char occurrences"
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

(deftest matches-and-non-matches
  (testing "Duplicates chars"
    (let [search "aaa"
          candidates ["aaa"
                      "aaaa"
                      "aa"
                      "a!a!a"]
          expected '(["aaa" 3]
                     ["aaaa" 3]
                     ;; "aa" - non-match
                     ["a!a!a" 1])]
      (is (= expected (fuzzy-match search candidates :with-scores? true))))
    (let [search "aabc"
          candidates ["aabc"
                      "abac"
                      "abc"
                      "aaabc"
                      "ababc"
                      "aabaabc"]
          expected '(["aabc" 4]
                     ;; "abac" - non-match
                     ;; "abc" - non-match
                     ["aaabc" 4]
                     ["aabaabc" 4]
                     ["ababc" 3])]
      (is (= expected (fuzzy-match search candidates :with-scores? true))))
    (let [search "abab"
          candidates ["abab"
                      "abbabb"
                      "abba"
                      "abbab"
                      "ababb"]
          expected '(["abab" 4]
                     ["ababb" 4]
                     ["abbabb" 3]
                     ;; "abba"- non-match
                     ["abbab" 3])]
      (is (= expected (fuzzy-match search candidates :with-scores? true))))))
