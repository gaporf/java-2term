(definterface Expression
  (evaluate [vars])
  (string [])
  (diff [vars]))

(defn evaluate [expr vars] (.evaluate expr vars))

(defn toString [expr] (.string expr))

(defn diff [expr vars] (.diff expr vars))

(declare ZERO)

(deftype Const [const]
  Expression
  (evaluate [_ _] const)
  (string [_] (format "%.1f" const))
  (diff [_ _] ZERO))

(def ZERO (Const. 0.0))
(def ONE (Const. 1.0))

(deftype Var [var]
  Expression
  (evaluate [_ vars] (vars (str var)))
  (string [_] (str var))
  (diff [_ vars] (if (= (str var) vars) ONE ZERO)))

(defn Constant [const] (Const. const))

(defn Variable [var] (Var. var))

(deftype Operation [operation, sign, args, dif]
  Expression
  (evaluate [_ vars] (apply operation (mapv (fn [element] (evaluate element vars)) (vec args))))
  (string [_] (str "(" sign (apply str (mapv (fn [element] (str " " (toString element))) (vec args))) ")"))
  (diff [_ vars] (dif vars args))
  )

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

(defn Negate [& args] (Operation. - "negate" args (fn [vars args] (Negate (diff (first args) vars)))))

(declare Cosh)

(defn sinh [arg] (Math/sinh arg))

(defn cosh [arg] (Math/cosh arg))

(defn Sinh [& args] (Operation. sinh "sinh" args (fn [vars args] (Multiply (Cosh (first args)) (diff (first args) vars)))))

(defn Cosh [& args] (Operation. cosh "cosh" args (fn [vars args] (Multiply (Sinh (first args)) (diff (first args) vars)))))

(def getOperation {'+ Add '- Subtract '* Multiply '/ Divide 'negate Negate 'sinh Sinh 'cosh Cosh})

(defn parseObject [expression]
  (if (string? expression)
    (parseObject (read-string expression))
    (if (number? expression)
      (Constant expression)
      (if (symbol? expression)
        (Variable expression)
        (apply (getOperation (first expression)) (mapv parseObject (rest expression)))))))