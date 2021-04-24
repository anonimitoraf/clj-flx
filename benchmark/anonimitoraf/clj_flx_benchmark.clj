(ns anonimitoraf.clj-flx-benchmark
  (:require
   [anonimitoraf.clj-flx :refer [score]]
   [criterium.core :refer [bench]]))

(defn run
  [_]
  (doseq [[search candidate]
          [["abc" "abc"]
           ["abc" "!!a!!b!!c!!"]
           ["abc" "aabbcc"]
           ["aaaaabbbbbcccccdddddeeeefffffffff"
            "!!aa!aaa!bbb!bbccc!ccdddccdddccddd!ddee!eefffffddee!f!!fff!"]
           ["asdfuapsioefuywerouqweurowgeflhkszjdvhxzlcvkhjxcvhuiawyeriuyqweuyi"
            "asxucpouwerdfuapsioefasdfiuxpov897394234uywerouqweurowgeflaou9871298yhkszjdvhxzlcvkhjxweuoruwhkdsfjlsa1898123cvhuiawyeriuyqweuyi"]
           ["asdfuapsioefuywerouqweurowgeflhkszjdvhxzlcvkhjxcvhuiawyeriuyqweuyidvhxzlcvkhjxcvhuiawyeriuydfuapsioefuywerouqweurowgeflhkszjdvhrouqweurowgeflhkszjdvhxzlcvkhjxcvhuiawyeriuyqweuyidvhxzlcvkhjxcvhuiawyeriuydfuapsioefuywe"
            "asdfuapsioe23u10283hasfsdjl;xvfuywerouqweurowgeflhkszjdvhxzsdfuoux8cvy9x8cvy923lcvkhjxcvhuiawyeriuyqweuyidvhxzlcvkhjxcvhuiawyeriuydfuapsioefuywerouqweurowgeflhkszjdvhrouqweurowgeflhkszjdvhxzlcvkhjxcvhuiawyeriuyqweuas0f8s0df87q9231h2illzxjvdyidvhxz9u28384729347hxcovuilcvkhjxcvhuiawyeriuydasdf;jlxkcvjoifuapsioefuasdfywe"]]]
    (prn "------------------------------------")
    (prn (str "About to benchmark: " "search=" search ", candidate=" candidate))
    (bench (score search candidate))
    (prn "------------------------------------")))


(time (score "asdfuapsioefuywerouqweurowgeflhkszjdvhxzlcvkhjxcvhuiawyeriuyqweuyidvhxzlcvkhjxcvhuiawyeriuydfuapsioefuywerouqweurowgeflhkszjdvhrouqweurowgeflhkszjdvhxzlcvkhjxcvhuiawyeriuyqweuyidvhxzlcvkhjxcvhuiawyeriuydfuapsioefuywe"
        "asdfuapsioe23u10283hasfsdjl;xvfuywerouqweurowgeflhkszjdvhxzsdfuoux8cvy9x8cvy923lcvkhjxcvhuiawyeriuyqweuyidvhxzlcvkhjxcvhuiawyeriuydfuapsioefuywerouqweurowgeflhkszjdvhrouqweurowgeflhkszjdvhxzlcvkhjxcvhuiawyeriuyqweuas0f8s0df87q9231h2illzxjvdyidvhxz9u28384729347hxcovuilcvkhjxcvhuiawyeriuydasdf;jlxkcvjoifuapsioefuasdfywe"))
