;; The first three lines of this file were inserted by DrRacket. They record metadata
;; about the language level of this file in a form that our tools can easily process.
#reader(lib "htdp-intermediate-lambda-reader.ss" "lang")((modname 3_FunctionsAreEntities) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f ())))
(require 2htdp/image)
(define (draw-circle radius) (circle radius "solid" "blue"))
(draw-circle 40)


(define L (rest (build-list 25 identity)))
(map draw-circle L)