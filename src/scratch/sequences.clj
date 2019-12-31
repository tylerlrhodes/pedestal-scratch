
(ns scratch.sequences)

(defn test []
  (println "hello"))

(print "hi")

(defn inf [n] (lazy-seq (cons n (inf (inc n)))))

(defn inf-nuts [n] (lazy-seq (lazy-seq (lazy-seq (cons n (inf-nuts (inc n)))))))

(defn up-to-10
  ([] (up-to-10 1))
  ([n]
   (lazy-seq
    (when (> 10 n)
      (print \.)
      (cons n (up-to-10 (inc n)))))))

(up-to-10)

(take 3 (up-to-10))

;;(let [[t d] (split-with #(< % 12) (range 1e8))]
;;      [(count t) (count d)])
; [12 99999988]

;;(let [[t d] (split-with #(< % 12) (range 1e8))]
;;  [(count d) (count t)])
