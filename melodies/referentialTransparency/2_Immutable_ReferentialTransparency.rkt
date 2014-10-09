;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-intermediate-reader.ss" "lang")((modname 2_Immutable_ReferentialTransparency) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f ())))

;I define a structure for calculator
(define-struct calculator (memory) )

; Constructor
(define C (make-calculator 0))

; Selector
(calculator-memory C)

; Predicate
(calculator? C)
(calculator? "Hello")

(define (add x y) (+ x y))

(define (memory-plus calc n) 
  (make-calculator (add (calculator-memory calc) n)))

(define C2 (memory-plus C 2))
(calculator-memory (memory-plus C2 2))

(define C3 (memory-plus C2 2))
(calculator-memory (memory-plus C3 2))
