# clj-flx

[![clj-flx](https://img.shields.io/clojars/v/anonimitoraf/clj-flx.svg)](https://clojars.org/anonimitoraf/clj-flx)

Flexible "fuzzy" matching.

Inspired by http://scottfrazersblog.blogspot.com/2009/12/emacs-better-ido-flex-matching.html?m=1.

## Usage

``` clojure
(ns your-ns
  (:require [anonimitoraf.clj-flx :refer [fuzzy-match]]))

;; Note that results are (stable) sorted desc by score

(fuzzy-match "abc" ["abc" "!abc" "a!b!c" "abbc" "ababcab" "blah"] :with-scores? true)
  ;; => (["abc" 3] ["!abc" 3] ["ababcab" 3] ["abbc" 2] ["a!b!c" 1])

(fuzzy-match "abc" ["abc" "!abc" "a!b!c" "abbc" "ababcab" "blah"])
  ;; => ("abc" "!abc" "ababcab" "abbc" "a!b!c")
```

## License

Copyright © 2021 anonimitoraf

Distributed under the Eclipse Public License version 1.0.
