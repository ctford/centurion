(ns centurion.core-test
  (:require [clojure.test :refer :all]
            [centurion.core :refer :all]))

(deftest parsing
  (testing
    (is (= ['(foo bar (baz 9)) [\m \o \r \e]] (term " (foo bar (baz 9))more")))))

(defn to-list [l]
  (when (fn? l)
    (cons (l 'head) (to-list (l 'tail)))))

(deftest executing
  (testing
    (is (= 'one (interpret "one")))
    (is (= 'one ((interpret "(function x x)") 'one)))
    (is (= '(one two) (to-list (interpret "(cons one (cons two nil))"))))
    (is (= 1 (interpret "(case foo foo 1 2)")))
    (is (= 2 (interpret "(case foo bar 1 2)")))
    (is (= 'foo (interpret "((cons foo nil) head)")))
    (is (= (symbol "nil") (interpret "((cons foo nil) tail)")))
    (is (= 2 (interpret "(plus 1 1)")))
    (is (= 0 (interpret "(minus 1 1)")))
    (is (= '{x three} (interpret "(let x three (export x))")))
    (is (= 2 (interpret "((function f (f (f 0))) (plus 1))")))
    (is (= 0 (interpret " ; comments are fun
                        (plus 0 0)")))))

(deftest programs
  (testing
    (let [program
            "(let is-even?
               (recursive
                 (function recurse n
                   (case n 0 yes
                   (case n 1 no
                     (recurse (minus n 2))))))
               (cons (is-even? 3)
               (cons (is-even? 4)
                 nil)))"]
    (is (= '(no yes) (to-list (interpret program)))))
    (let [program
            "(let reverse
               ((recursive
                 (function recurse xs ys
                    (case ys nil xs
                      (recurse (cons (ys head) xs) (ys tail)))))
                 nil)
               (reverse (cons one (cons two (cons three nil)))))"]
    (is (= '(three two one) (to-list (interpret program)))))
    (let [program
            "(let max
               (recursive
                 (function recurse x y
                   (case x 0 y
                   (case y 0 x
                     (plus 1 (recurse (minus x 1) (minus y 1)))))))
                 (reduce max 0 (cons 2 (cons 1 nil))))"]
    (is (= 2 (interpret program))))
    (let [program
            "(map (plus 1) (cons 1 (cons 2 (cons 3 nil))))"]
    (is (= '(2 3 4) (to-list (interpret program)))))))
