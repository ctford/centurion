# Centurion
A hundred line Lisp.

[![CI](https://github.com/ctford/centurion/actions/workflows/ci.yml/badge.svg)](https://github.com/ctford/centurion/actions/workflows/ci.yml)

## Running the tests

    lein test

## Running the repl

    lein run

## Examples

### Lambdas and Functions

Simple lambda:
```lisp
((function x (plus x 1)) 5)
; => 6
```

Curried functions with multiple parameters:
```lisp
((function x y (plus x y)) 3 4)
; => 7
```

### Lists and Higher-Order Functions

Build a list:
```lisp
(cons 1 (cons 2 (cons 3 nil)))
```

Map - add 1 to each element:
```lisp
(map (plus 1) (cons 1 (cons 2 (cons 3 nil))))
; => (2 3 4)
```

Reduce - sum a list:
```lisp
(reduce plus 0 (cons 1 (cons 2 (cons 3 nil))))
; => 6
```

Compose functions:
```lisp
((compose (plus 1) (plus 2)) 3)
; => 6
```

### Recursion with the Y Combinator

Fibonacci numbers:
```lisp
(let fib
  (recursive
    (function recurse n
      (case n 0 0
      (case n 1 1
        (plus (recurse (minus n 1)) (recurse (minus n 2)))))))
  (fib 7))
; => 13
```

Check if a number is even:
```lisp
(let is-even?
  (recursive
    (function recurse n
      (case n 0 yes
      (case n 1 no
        (recurse (minus n 2))))))
  (is-even? 4))
; => yes
```

### Building Your Own Functions

Length of a list:
```lisp
(let length
  (reduce (function x acc (plus acc 1)) 0)
  (length (cons 1 (cons 2 (cons 3 nil)))))
; => 3
```

Reverse a list:
```lisp
(let reverse
  ((recursive
    (function recurse xs ys
      (case ys nil xs
        (recurse (cons (ys head) xs) (ys tail)))))
   nil)
  (reverse (cons one (cons two (cons three nil)))))
; => (three two one)
```
