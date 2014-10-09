; ====================================
; Clojure syntax...

(def hello "a STRING")
(def nums [2 3 5])
(+ 2 3 5)

(def hello "another string")

(def an-int 123)


DEF MULT(N)
;; def mult(n)
;;  (n * 2)
;; end
(defn multiplier [n]
  (* 2 n))

(multiplier 5)
(+          1 2)


(defn adder [a, b]
  (+ a b))

(adder 5 3)


(first [1 2 3])
(rest [ 1 2 3])
(empty? [1 2 3])

; ====================================
;; Let's write our Ruby sum-rec in CLOJURE

(defn sum2 [arr]
  (if (empty? arr)
    0
    (+ (first arr)
       (sum2 (rest arr)))))

(sum2 [2 3 5 7])

; ====================================
; Let's write a COUNT-REC function...
;; this time, lets START
;; with describing the process...

;; (count-rec [2 3 5])

;; (+ 1 (count-rec [3 5]))

;; (+ 1 (+ 1 (count-rec [5])))

;; (+ 1 (+ 1 (+ 1 (count-rec []))))

;; (+ 1 (+ 1 (+ 1 (0))))

;; (+ 1 (+ 1 (1)))

;; (+ 1 (2))

;; 3

; ====================================
(defn count-rec [arr]
  (if (empty? arr)
    0
    (+ 1 (count-rec (rest arr)))))

;; (count-rec [2 3 5])
;; (+ 1 (count-rec [3 5]))
;; (+ 1 (+ 1 (count-rec [5])))
;; ...
;; (+ 1 (+ 1 (+ (...10M...)))) ...BOOOM!!!
;; ...
;; (+ 1 (+ 1 (+ 1 (0))))
;; (+ 1 (+ 1 (1)))
;; (+ 1 (2))
;; 3


; ====================================
;; let's find an ITERATIVE process instead

;; imagine a recursive function
;; that keeps an accumulator...

;; (count-iter 0 [2 3 5])

;; (count-iter (+ 1 0) [3 5])

;; (count-iter (+ 1 1) [5])

;; (count-iter (+ 1 2) [])

;; 3


; ====================================
;; Iterative Process
;; (count-iter 0 [2 3 5])
;; (count-iter (+ 1 0) [3 5])
;; (count-iter (+ 1 1) [5])
;; (count-iter (+ 1 2) [])
;; 3

(defn count-iter [acc arr]
  (if (= [] arr)
    acc
    (recur (+ 1 acc) (rest arr))))

(defn count- [arr]
  (count-iter 0 arr))

(count- [2 3 5])




; ====================================
(defn count-iter- [acc arr]
  (if (= [] arr)
    acc
    (recur (+ 1 acc)
           (rest arr))))

(defn sum-iter- [acc arr]
  (if (= [] arr)
    acc
    (recur (+ acc (first arr))
           (rest arr))))
;;; ---------------------

(defn reduce- [f acc arr]
  (if (= [] arr)
    acc
    (recur f
           (f acc (first arr))
           (rest arr))))

(defn count-iter-2 [arr]
  (reduce- (fn [acc e] (+ 1 acc))
           0
           arr))
(defn sum-iter-2 [arr]
  (reduce- +
           0
           arr))
; ====================================
