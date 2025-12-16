# Centurion
A hundred line Lisp.

[![CI](https://github.com/ctford/centurion/actions/workflows/ci.yml/badge.svg)](https://github.com/ctford/centurion/actions/workflows/ci.yml)

## Running the tests

    lein test

## Running the Centurion REPL

    lein run

## Features

- **Currying** - Multi-parameter functions are automatically curried
- **Recursion via Y Combinator** - The `recursive` form enables recursive functions without explicit self-reference
- **Church-encoded Lists** - Lists represented as functions that return head or tail
- **Higher-order Functions** - Built-in `map`, `reduce`, and `compose` in the standard library
- **Lexical Scoping** - `let` bindings with proper closure semantics

## Examples

### Lambdas and Functions

Simple lambda:
```lisp
((function x (plus x 1)) 5)  ; ((fn [x] (+ x 1)) 5)
; => 6
```

Curried functions with multiple parameters:
```lisp
((function x y (plus x y)) 3 4)  ; ((fn [x y] (+ x y)) 3 4)
; => 7
```

### Lists and Higher-Order Functions

Build a list:
```lisp
(cons 1 (cons 2 (cons 3 nil)))  ; '(1 2 3)
```

Map - add 1 to each element:
```lisp
(map (plus 1) (cons 1 (cons 2 (cons 3 nil))))  ; (map inc '(1 2 3))
; => (2 3 4)
```

Reduce - sum a list:
```lisp
(reduce plus 0 (cons 1 (cons 2 (cons 3 nil))))  ; (reduce + 0 '(1 2 3))
; => 6
```

Compose functions:
```lisp
((compose (plus 1) (plus 2)) 3)  ; ((comp #(+ % 1) #(+ % 2)) 3)
; => 6
```

### Recursion with the Y Combinator

Fibonacci numbers:
```lisp
(let fib                             ; (defn fib [n]
  (recursive                         ;   (cond
    (function recurse n              ;     (= n 0) 0
      (case n 0 0                    ;     (= n 1) 1
      (case n 1 1                    ;     :else (+ (fib (- n 1))
        (plus                        ;              (fib (- n 2)))))
          (recurse (minus n 1))      ;
          (recurse (minus n 2))))))) ;
  (fib 7))                           ; (fib 7)
; => 13                              ; => 13
```

Check if a number is even:
```lisp
(let is-even?                       ; (defn is-even? [n]
  (recursive                        ;   (cond
    (function recurse n             ;     (= n 0) true
      (case n 0 yes                 ;     (= n 1) false
      (case n 1 no                  ;     :else (is-even? (- n 2))))
        (recurse (minus n 2)))))))  ;
  (is-even? 4))                     ; (is-even? 4)
; => yes                            ; => true
```

### Building Your Own Functions

Length of a list:
```lisp
(let length                                 ; (defn length [lst]
  (reduce (function x acc (plus acc 1)) 0)  ;   (reduce (fn [acc _] (inc acc)) 0 lst))
  (length (cons 1 (cons 2 (cons 3 nil)))))  ; (length '(1 2 3))
; => 3                                      ; => 3
```

Reverse a list:
```lisp
(let reverse                                       ; (defn reverse [lst]
  ((recursive                                      ;   (loop [xs '() ys lst]
    (function recurse xs ys                        ;     (if (empty? ys)
      (case ys nil xs                              ;       xs
        (recurse (cons (ys head) xs) (ys tail))))  ;       (recur (cons (first ys) xs) (rest ys)))))
   nil)                                            ;
  (reverse (cons one (cons two (cons three nil)))));  (reverse '(one two three))
; => (three two one)                               ; => (three two one)
```
