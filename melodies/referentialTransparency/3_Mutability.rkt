;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-advanced-reader.ss" "lang")((modname 3_Mutability) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #t #t none #f ())))
;I define a structure for calculator
(define-struct calculator (memory))

; Constructor
(define C (make-calculator 0))

; Selector
(calculator-memory C)

; Predicate
(calculator? C)
(calculator? "Hello")

; Mutator
(set-calculator-memory! C 3)

(calculator-memory C)

(define (add x y) (+ x y))

(define (memory-plus calc n) 
  (set-calculator-memory! calc (add (calculator-memory calc) n)))

(memory-plus C 2)
(calculator-memory C)

