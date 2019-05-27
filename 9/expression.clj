(definterface Expression
  (evaluate [vars])
  (string [])
  (diff [vars])
  (infix []))

(defn evaluate [expr vars] (.evaluate expr vars))

(defn toString [expr] (.string expr))

(defn diff [expr vars] (.diff expr vars))

(defn toStringInfix [expr] (.infix expr))

(defn toStringSuffix [expr] (.suffix expr))

(declare ZERO)

(deftype Const [const]
  Expression
  (evaluate [_ _] const)
  (string [_] (format "%.1f" (double const)))
  (diff [_ _] ZERO)
  (infix [this] (.string this)))

(def ZERO (Const. 0.0))
(def ONE (Const. 1.0))

(deftype Var [var]
  Expression
  (evaluate [_ vars] (vars (clojure.string/lower-case (subs (str var) 0 1))))
  (string [_] (str var))
  (diff [_ vars] (if (= (clojure.string/lower-case (subs (str var) 0 1)) vars) ONE ZERO))
  (infix [this] (.string this)))

(defn Constant [const] (Const. const))

(defn Variable [var] (Var. var))

(deftype Operation [operation, sign, args, dif]
  Expression
  (evaluate [_ vars] (apply operation (mapv (fn [element] (evaluate element vars)) (vec args))))
  (string [_] (str "(" sign (apply str (mapv (fn [element] (str " " (toString element))) (vec args))) ")"))
  (diff [_ vars] (dif vars args))
  (infix [_] (str "(" (toStringInfix (first args)) " " sign " " (toStringInfix (second args)) ")")))

(deftype UnaryOperation [operation, sign, arg, dif]
  Expression
  (evaluate [_ vars] (operation (evaluate arg vars)))
  (string [_] (str "(" sign (toString arg) ")"))
  (diff [_ vars] (dif vars arg))
  (infix [_] (str sign "(" (toStringInfix arg) ")")))

(defn Add [& args] (Operation. + "+" args (fn [vars args] (apply Add (mapv (fn [element] (diff element vars)) (vec args))))))

(defn Subtract [& args] (Operation. - "-" args (fn [vars args] (apply Subtract (mapv (fn [element] (diff element vars)) (vec args))))))

(defn Multiply [& args] (Operation. * "*" args (fn [vars args]
                                                 (let [v (vec args)]
                                                   (if (= (count v) 1)
                                                     (diff (first v) vars)
                                                     (Add (apply Multiply (diff (first v) vars) (rest v))
                                                          (Multiply (diff (apply Multiply (rest v)) vars) (first v))))))))

(defn division
  ([first] first)
  ([first & args] (/ first (double (apply * args)))))

(defn Divide [& args] (Operation. division "/" args (fn [vars args]
                                                      (let [v (vec args)]
                                                        (if (== (count v) 1)
                                                          (diff (first v) vars)
                                                          (Divide
                                                            (Subtract
                                                              (Multiply (diff (first v) vars) (apply Multiply (rest v)))
                                                              (Multiply (first v) (diff (apply Multiply (rest v)) vars)))
                                                            (Multiply (apply Multiply (rest v)) (apply Multiply (rest v)))))))))

(defn pow [a b] (Math/pow a b))

(defn Pow [& args] (Operation. pow "**" args (fn [vars args] (let [v (vec args)]
                                                               (Multiply
                                                                 (Multiply
                                                                   (second v)
                                                                   (Pow (first v)
                                                                        (Subtract
                                                                          (second v)
                                                                          (Constant 1))))
                                                                 (diff (second v) vars))))))

(defn log [a b] (/ (Math/log (Math/abs b)) (Math/log (Math/abs a))))

(defn Log [& args] (Operation. log "//" args (fn [vars args] (let [v (vec args)]
                                                               (Divide
                                                                 (Multiply
                                                                   (diff (first v) vars)
                                                                   (diff (second v) vars))
                                                                 (Multiply
                                                                   (first v)
                                                                   (Log (second v) (Constant (Math/exp 1)))))))))

(defn Negate [arg] (UnaryOperation. - "negate" arg (fn [vars arg] (Negate (diff arg vars)))))

(defn -return [value tail] {:value value :tail tail})
(def -valid? boolean)
(def -value :value)
(def -tail :tail)

(defn _show [result]
  (if (-valid? result)
    (str "-> " (pr-str (-value result)) " | " (pr-str (apply str (-tail result))))
    "!"))

(defn tabulate [parser inputs]
  (run! (fn [input] (printf "    %-10s %s\n" input (_show (parser input)))) inputs))

(defn _empty [value] (partial -return value))

(defn _char [p]
  (fn [[c & cs]]
    (if (and c (p c)) (-return c cs))))

(defn _map [f]
  (fn [result]
    (if (-valid? result)
      (-return (f (-value result)) (-tail result)))))

(defn _combine [f a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar)
        ((_map (partial f (-value ar)))
          ((force b) (-tail ar)))))))

(defn _either [a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar) ar ((force b) str)))))

(defn _parser [p]
  (fn [input]
    (-value ((_combine (fn [v _] v) p (_char #{\u0000})) (str input \u0000)))))
(mapv (_parser (_combine str (_char #{\a \b}) (_char #{\x}))) ["ax" "ax~" "bx" "bx~" "" "a" "x" "xa"])

(defn +char [chars] (_char (set chars)))

(defn +char-not [chars] (_char (comp not (set chars))))

(defn +map [f parser] (comp (_map f) parser))

(def +parser _parser)

(def +ignore (partial +map (constantly 'ignore)))

(defn iconj [coll value]
  (if (= value 'ignore) coll (conj coll value)))
(defn +seq [& ps]
  (reduce (partial _combine iconj) (_empty []) ps))

(defn +seqf [f & ps] (+map (partial apply f) (apply +seq ps)))

(defn +seqn [n & ps] (apply +seqf (fn [& vs] (nth vs n)) ps))

(defn +or [p & ps]
  (reduce (partial _either) p ps))

(defn +opt [p]
  (+or p (_empty nil)))

(defn +star [p]
  (letfn [(rec [] (+or (+seqf cons p (delay (rec))) (_empty ())))] (rec)))
(defn +plus [p] (+seqf cons p (+star p)))
(defn +str [p] (+map (partial apply str) p))

(def *space (+char " \t\n\r"))
(def *ws (+ignore (+star *space)))

(def *minus (+seqf (constantly '-) (+char "-")))
(def *digit (+char "0123456789.E"))
(def *number (+map read-string (+str (+seq (+opt *minus) (+str (+plus *digit))))))


(def *var (+char "xyz"))

(def *negate (+seqf (constantly 'negate) (+char "n") (+char "e") (+char "g") (+char "a") (+char "t") (+char "e")))
(def *plus (+seqf (constantly '+) (+char "+")))
(def *plusOrMinus (+or *plus *minus))
(def *mul (+seqf (constantly '*) (+char "*")))
(def *div (+seqf (constantly '/) (+char "/")))
(def *mulOrDiv (+or *mul *div))
(def *pow (+seqf (constantly 'pow) (+char "*") (+char "*")))
(def *log (+seqf (constantly 'log) (+char "/") (+char "/")))
(def *powOrLog (+or *pow *log))

(declare *plusMinus)

(def *prim
  (fn [expr]
    ((+or *number *var (+seqn 0 (+ignore (+char "(")) *ws *plusMinus *ws (+ignore (+char ")")))) expr)))

(def *getNext
  (fn [*next *operation] (+seqf cons *ws *next (+star (+seq *ws *operation *ws *next *ws)))))

(def left
  (fn [*next *operation]
    (+map (partial reduce (fn [cur next] (list (first next) cur (second next)))) (*getNext *next *operation))))

(def *unaryOperation
  (fn [expr]
    ((+or (+map (fn [vec] (list (first vec) (second vec))) (+seq *ws *negate *ws *unaryOperation *ws)) *prim) expr)))

(def *powLog
  (fn [expr]
    ((+map
       (comp
         (partial reduce (fn [cur next] (if (not (vector? next))
                                          (list (first cur) next (second cur))
                                          (vector (first next) (list (first cur) (second next) (second cur))))))
         reverse) (*getNext *unaryOperation *powOrLog)) expr)))

(def *mulDiv
  (fn [expr]
    ((left *powLog *mulOrDiv) expr)))

(defn *plusMinus [expr]
  ((left *mulDiv *plusOrMinus) expr))

(def getOperation {'+ Add '- Subtract '* Multiply '/ Divide 'negate Negate 'pow Pow 'log Log})

(def parsePrefix
  (fn [expr]
    (if (number? expr)
      (Constant expr)
      (if (char? expr)
        (Variable expr)
        (apply (getOperation (first expr)) (mapv parsePrefix (rest expr)))))))

(def parseObjectInfix
  (fn [expr]
    (parsePrefix (-value (*plusMinus expr)))))