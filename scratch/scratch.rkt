;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-advanced-reader.ss" "lang")((modname scratch) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #t #t none #f ())))

(define (square x) (* x x))

square

(define square-lambda (λ (x) (* x x)))

square-lambda


















; if the current number is even, next number is n / 2, if the current number is odd, next number is 3n + 1
; and n >= 1
; This is called Collatz Conjecture.
(define (hailstones n) 
  (if (= n 1) 
      (list 1) 
      (cons n 
        (if (even? n) 
            (hailstones (/ n 2)) 
            (hailstones (+ (* n 3) 1))))))

          