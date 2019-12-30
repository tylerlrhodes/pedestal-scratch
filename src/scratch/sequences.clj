
(ns scratch.sequences)

(defn test []
  (println "hello"))


(defn inf [n] (lazy-seq (cons n (inf (inc n)))))

(defn inf-nuts [n] (lazy-seq (lazy-seq (lazy-seq (cons n (inf-nuts (inc n)))))))



