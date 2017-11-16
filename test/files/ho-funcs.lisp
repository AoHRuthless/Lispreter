;; higher-order function examples
;; currently DOES NOT PASS
;; TODO: fix
;; /test/files/ho-funcs.lisp -> /test/files/ho-funcs.txt
(defun map (func lst)
    (if (endp lst)
        NIL
        (cons (f (car lst))
              (map f (cdr lst)))))
              
(defun andmap (func lst)
    (if (endp lst)
        T
        (and (func (car lst))
             (andmap f (cdr lst)))))
             
(defun ormap (func lst)
    (if (endp lst)
        NIL
        (or (func (car lst))
            (ormap f (cdr lst)))))

(defun filter (pred lst)
    (cond [(endp lst) NIL]
          [(pred (car lst)) (cons (car lst) (filter pred (cdr lst)))]
          [t (filter pred (cdr lst))]))
          
(map integerp (list 1 2 "abc" T))
(andmap integerp (list 1 2 "abc" T))
(andmap integerp (list 1 2 3 4))
(ormap integerp (list 1 2 "abc" T))
(ormap integerp (list NIL T "xyz"))
(filter integerp (list 1 2 "abc" 4))
(filter integerp (list 1 2 3))