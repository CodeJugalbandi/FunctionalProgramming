;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-advanced-reader.ss" "lang")((modname Sequencing) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #t #t none #f ())))

(require racket/string)

(define s "All mimsy were the borogoves")

(define (capitalize s) (map (λ (w) (string-upcase w)) s))
(define (words<=3 s) (filter (λ (w) (<= (string-length w) 3)) s))
(define (sentence->words s) (string-split s)) 
(define (join-words ws) (string-join ws))  

(join-words 
  (capitalize  
    (words<=3 
      (sentence->words s))))

(define sentence-with-3-words 
  (compose join-words capitalize words<=3 sentence->words))
  
(sentence-with-3-words s)  

(require rackjure)
(require rackjure/threading)

; Flows with the grain of thought
(~> s sentence->words words<=3 capitalize join-words)

