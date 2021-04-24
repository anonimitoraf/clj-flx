# clj-flx

[![clj-flx](https://img.shields.io/clojars/v/anonimitoraf/clj-flx.svg)](https://clojars.org/anonimitoraf/clj-flx)

Flexible "fuzzy" matching.

Inspired by http://scottfrazersblog.blogspot.com/2009/12/emacs-better-ido-flex-matching.html?m=1 with the scoring simplified.
Scoring is done via the "Longest common substring" algorithm.

## Usage

``` clojure
(ns your-ns
  (:require [anonimitoraf.clj-flx :as flx]))

;; Note that results are (stable) sorted desc by score. Non-matches are not returned.

(flx/fuzzy-match "abc" ["abc" "!abc" "a!b!c" "abbc" "ababcab" "blah"] :with-scores? true)
  ;; => (["abc" 3] ["!abc" 3] ["ababcab" 3] ["abbc" 2] ["a!b!c" 1])

(flx/fuzzy-match "abc" ["abc" "!abc" "a!b!c" "abbc" "ababcab" "blah"])
  ;; => ("abc" "!abc" "ababcab" "abbc" "a!b!c")
  
;; Alternatively, if you just want to get the score of a candidate.
;; A candidate's score is the length of the longest common substring with the search
;; string, `nil` for non-matches.

(flx/score "abcd" "abcd")
  ;; => 4 - longest substring is the whole search string

(flx/score "abcd" "abbcd")
  ;; => 3 - longest substring is bcd

(flx/score "abcd" "a!b!c!d")
  ;; => 1 - longest substring is either "a", "b", "c" or "d"

(flx/score "abcd" "abde")
  ;; => nil - a non-match because "c" is missing

(flx/score "abcd" "abdc")
  ;; => nil - a non-match because "cd" are in the wrong order
```

## Algorithm Space/Time Complexity

* Time - `O(m * n)` where `m` and `n` are the search and candidate's string lengths respectively.
* Space - `O(m * n)` due to maintaining a dynamic programming matrix.

## Benchmarks

Given a search string and a candidate, how long does it take to calculate the score of the candidate?

I'm using the lib criterium for these benchmarks. The benchmark code are in ".../benchmark/anonimitoraf/clj_flx_benchmark.clj".

I'm not too experienced with benchmarking code so any suggestions on how to do this more effectively and accurately, suggestions are welcome!

### TL;DR

Note that these length measurements are subjective. The main goal here is to give a rough performance approximations.

* Very short search/candidate (< 3 chars) takes < 0.03 ms
* Long-ish search/candidate (~60 - ~120 chars) takes < 5 ms
* Very long search/candidate (~120 - ~300 chars ) takes 

### Verbose Results

```
"------------------------------------"
"About to benchmark: search=abc, candidate=abc"
Evaluation count : 4005480 in 60 samples of 66758 calls.
             Execution time mean : 15.051607 µs
    Execution time std-deviation : 118.104205 ns
   Execution time lower quantile : 14.980782 µs ( 2.5%)
   Execution time upper quantile : 15.184849 µs (97.5%)
                   Overhead used : 8.913376 ns

Found 6 outliers in 60 samples (10.0000 %)
	low-severe	 5 (8.3333 %)
	low-mild	 1 (1.6667 %)
 Variance from outliers : 1.6389 % Variance is slightly inflated by outliers
"------------------------------------"
"------------------------------------"
"About to benchmark: search=abc, candidate=!!a!!b!!c!!"
Evaluation count : 1698120 in 60 samples of 28302 calls.
             Execution time mean : 35.392918 µs
    Execution time std-deviation : 339.217594 ns
   Execution time lower quantile : 35.223332 µs ( 2.5%)
   Execution time upper quantile : 36.091115 µs (97.5%)
                   Overhead used : 8.913376 ns

Found 8 outliers in 60 samples (13.3333 %)
	low-severe	 3 (5.0000 %)
	low-mild	 5 (8.3333 %)
 Variance from outliers : 1.6389 % Variance is slightly inflated by outliers
"------------------------------------"
"------------------------------------"
"About to benchmark: search=abc, candidate=aabbcc"
Evaluation count : 2486220 in 60 samples of 41437 calls.
             Execution time mean : 24.216629 µs
    Execution time std-deviation : 275.524152 ns
   Execution time lower quantile : 24.061736 µs ( 2.5%)
   Execution time upper quantile : 25.090411 µs (97.5%)
                   Overhead used : 8.913376 ns

Found 5 outliers in 60 samples (8.3333 %)
	low-severe	 2 (3.3333 %)
	low-mild	 3 (5.0000 %)
 Variance from outliers : 1.6389 % Variance is slightly inflated by outliers
"------------------------------------"
"------------------------------------"
"About to benchmark: search=aaaaabbbbbcccccdddddeeeefffffffff, candidate=!!aa!aaa!bbb!bbccc!ccdddccdddccddd!ddee!eefffffddee!f!!fff!"
Evaluation count : 50580 in 60 samples of 843 calls.
             Execution time mean : 1.191359 ms
    Execution time std-deviation : 6.520328 µs
   Execution time lower quantile : 1.187033 ms ( 2.5%)
   Execution time upper quantile : 1.198014 ms (97.5%)
                   Overhead used : 8.913376 ns

Found 3 outliers in 60 samples (5.0000 %)
	low-severe	 2 (3.3333 %)
	low-mild	 1 (1.6667 %)
 Variance from outliers : 1.6389 % Variance is slightly inflated by outliers
"------------------------------------"
"------------------------------------"
"About to benchmark: search=asdfuapsioefuywerouqweurowgeflhkszjdvhxzlcvkhjxcvhuiawyeriuyqweuyi, candidate=asxucpouwerdfuapsioefasdfiuxpov897394234uywerouqweurowgeflaou9871298yhkszjdvhxzlcvkhjxweuoruwhkdsfjlsa1898123cvhuiawyeriuyqweuyi"
Evaluation count : 12480 in 60 samples of 208 calls.
             Execution time mean : 4.829704 ms
    Execution time std-deviation : 41.180404 µs
   Execution time lower quantile : 4.796905 ms ( 2.5%)
   Execution time upper quantile : 4.902178 ms (97.5%)
                   Overhead used : 8.913376 ns

Found 6 outliers in 60 samples (10.0000 %)
	low-severe	 4 (6.6667 %)
	low-mild	 2 (3.3333 %)
 Variance from outliers : 1.6389 % Variance is slightly inflated by outliers
"------------------------------------"
"------------------------------------"
"About to benchmark: search=asdfuapsioefuywerouqweurowgeflhkszjdvhxzlcvkhjxcvhuiawyeriuyqweuyidvhxzlcvkhjxcvhuiawyeriuydfuapsioefuywerouqweurowgeflhkszjdvhrouqweurowgeflhkszjdvhxzlcvkhjxcvhuiawyeriuyqweuyidvhxzlcvkhjxcvhuiawyeriuydfuapsioefuywe, candidate=asdfuapsioe23u10283hasfsdjl;xvfuywerouqweurowgeflhkszjdvhxzsdfuoux8cvy9x8cvy923lcvkhjxcvhuiawyeriuyqweuyidvhxzlcvkhjxcvhuiawyeriuydfuapsioefuywerouqweurowgeflhkszjdvhrouqweurowgeflhkszjdvhxzlcvkhjxcvhuiawyeriuyqweuas0f8s0df87q9231h2illzxjvdyidvhxz9u28384729347hxcovuilcvkhjxcvhuiawyeriuydasdf;jlxkcvjoifuapsioefuasdfywe"
Evaluation count : 1620 in 60 samples of 27 calls.
             Execution time mean : 37.381051 ms
    Execution time std-deviation : 501.772461 µs
   Execution time lower quantile : 37.086434 ms ( 2.5%)
   Execution time upper quantile : 38.164722 ms (97.5%)
                   Overhead used : 8.913376 ns

Found 6 outliers in 60 samples (10.0000 %)
	low-severe	 3 (5.0000 %)
	low-mild	 3 (5.0000 %)
 Variance from outliers : 1.6389 % Variance is slightly inflated by outliers
"------------------------------------"
```


## License

Copyright © 2021 anonimitoraf

Distributed under the Eclipse Public License version 1.0.
