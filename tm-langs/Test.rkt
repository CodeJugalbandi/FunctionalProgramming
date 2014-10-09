#reader(lib "htdp-advanced-reader.ss" "lang")
((modname Test) (read-case-sensitive #t) (teachpacks ()) (htdp-settings #(#t constructor repeating-decimal #f #t none #f ())))

(define L (rest (build-list 25 (lambda (x) (* x 2)))))
L
