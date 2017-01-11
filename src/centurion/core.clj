(ns centurion.core
  (:require [clojure.core.match :refer [match]]))

(defn evaluate [term env]
  (match term
         ; Lambda function definition
         (['function param body]   :seq) (fn [arg] (evaluate body (assoc env param arg)))
         ; Curried lambda function definition
         (['function param & more] :seq) (evaluate ['function param (cons 'function more)] env)
         ; Single variable let binding
         (['let param value body]  :seq) (evaluate [['function param body] value] env)
         ; Lazy case switching
         (['case x y then else]    :seq) (if (= (evaluate x env) (evaluate y env))
                                           (evaluate then env)
                                           (evaluate else env))
         ; Variable binding export
         (['export & variables]    :seq) (select-keys env variables)
         ; Function application
         ([f arg]                  :seq) ((evaluate f env) (evaluate arg env))
         ; Curried function application
         ([f arg & other-args]     :seq) (evaluate (cons [f arg] other-args) env)
         ; Variable binding lookup
         :else                           (get env term term)))

; Parsers take text input and (if they succeed) return a pair of a result and the remaining input.
(defn single [character]
  (fn [input]
    (when (= (first input) character)
      [character (rest input)])))

(defn token [relevant? construct]
  (fn [input]
    (when-let [characters (->> input (take-while relevant?) seq)]
      [(construct (apply str characters)) (drop-while relevant? input)])))

; Parsers can be combined to form new parsers.
(defn conjunction [combine-results parser1 parser2]
  (fn [input]
    (when-let [[result1 remainder1] (parser1 input)]
      (when-let [[result2 remainder2] (parser2 remainder1)]
        [(combine-results [result1 result2]) remainder2]))))

(def      &                     (partial conjunction second))     ; Right-biased conjunction
(def      &left                 (partial conjunction first))      ; Left-biased conjunction
(defmacro |   [parser1 parser2] `#(or (~parser1 %) (~parser2 %))) ; Lazy disjunction
(defn nothing [input]           [nil input])
(defn many    [parser]          (| (conjunction #(apply cons %) parser (many parser)) nothing))

; Whitespace and comments are noise and will be ignored.
(def line-comment (& (single \;) (token #(not= \newline %) identity)))
(def whitespace   (token #(Character/isWhitespace %) identity))
(def noise        (many (| line-comment whitespace)))

; The atoms are natural numbers and variables.
(def natural      (token #(Character/isDigit %) #(Integer/parseInt %)))
(def variable     (token #(or (Character/isLowerCase %) (#{\? \-} %)) symbol))

; Terms and s-expressions are defined in terms (ahem) of each other.
(declare s-expression)
(def term         (& noise (| natural (| variable s-expression))))
(def s-expression (& (single \() (&left (many term) (& noise (single \))))))

(def standard-library
  "; Lists are encoded as functions that return head or tail depending on the supplied operation.
   (let cons
     (function x xs operation
       (case operation head x xs))

   ; The Y combinator creates recursive functions from ordinary functions that have an extra argument.
   (let recursive
     (function ordinary
       ((function f (f f))
         (function g
           (ordinary (function arg (g g arg))))))

   ; Reduce folds the list recursively from the right.
   (let reduce
     (recursive
       (function recurse f value list
         (case list nil value
           (f (list head) (recurse f value (list tail))))))

   (let compose
     (function f g x
       (f (g x)))

   ; Map by taking the list apart, applying f to each element and then putting it back together.
   (let map
     (function f
       (reduce (compose cons f) nil))

   (export cons recursive reduce compose map))))))")

(def built-ins {'plus  (fn [x] (fn [y] (+ x y)))
                'minus (fn [x] (fn [y] (- x y)))})

(defn -main
  ([] (print "repl> ") (flush) (-> (read-line) -main println) (-main))
  ([input] (-> input (-main (merge built-ins (-main standard-library {})))))
  ([input env] (-> input term first (evaluate env))))
