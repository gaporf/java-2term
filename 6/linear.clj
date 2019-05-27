(defn equalLength? [vector1 & vectors] (every? (fn [vector] (== (count vector) (count vector1))) vectors))

(defn basicOperation [func]
  (fn [& arguments]
    {:pre [(or (empty? arguments) (apply equalLength? arguments))]}
    (if (empty? arguments)
      0
      (apply mapv func arguments))))

(def v+ (basicOperation +))

(def v- (basicOperation -))

(def v* (basicOperation *))

(defn v*s
  ([vector]
   {:pre [(vector? vector)]}
   vector)
  ([vector scalar]
   {:pre [(and (vector? vector) (number? scalar))]}
   (mapv (fn [element] (* element scalar)) vector))
  ([vector scalar & scalars]
   {:pre [(number? scalar)]}
   (apply v*s (v*s vector scalar) scalars)))

(defn scalar [vector1 vector2]
  {:pre [(equalLength? vector1 vector2)]}
  (apply + (v* vector1 vector2)))

(defn vect
  ([vector]
   {:pre [(vector? vector)]}
   vector)
  ([vector1 vector2]
   {:pre [(and (vector? vector1) (vector? vector2) (== 3 (count vector1)) (equalLength? vector1 vector2))]}
   (vector (- (* (vector1 1) (vector2 2)) (* (vector2 1) (vector1 2)))
           (- (* (vector1 2) (vector2 0)) (* (vector2 2) (vector1 0)))
           (- (* (vector1 0) (vector2 1)) (* (vector2 0) (vector1 1)))))
  ([vector1 vector2 & vectors]
   (apply vect (vect vector1 vector2) vectors)))

(def m+ (basicOperation v+))

(def m- (basicOperation v-))

(def m* (basicOperation v*))

(defn matrix? [matrix] (and (vector? matrix) (every? vector? matrix) (equalLength? matrix)))

(defn m*s
  ([matrix]
   {:pre [(matrix? matrix)]}
   matrix)
  ([matrix & scalars]
   {:pre [(and (matrix? matrix) (every? number? scalars))]}
   (mapv (fn [vector] (apply v*s vector scalars)) matrix)))

(defn m*v [matrix vector]
  {:pre [(and (matrix? matrix) (vector? vector))]}
  (mapv (fn [element] (scalar element vector)) matrix))

(defn transpose [matrix]
  {:pre [(matrix? matrix)]}
  (apply mapv vector matrix))

(defn m*m
  ([matrix]
   {:pre [(matrix? matrix)]}
   matrix)
  ([matrix1 matrix2]
   {:pre [(and (matrix? matrix1) (matrix? matrix2) (equalLength? (first matrix1) matrix2))]}
   (transpose (mapv (fn [vector] (m*v matrix1 vector)) (transpose matrix2))))
  ([matrix1 matrix2 & matrices]
   (apply m*m (m*m matrix1 matrix2) matrices)))

(defn dimension
  [tensor] (if (number? tensor)
             []
             (conj (dimension (first tensor)) (count tensor))))

(defn tensor?
  [tensor] (if (number? tensor)
             true
             (and (every? tensor? tensor) (every? (fn [element] (= (dimension element) (dimension (first tensor)))) tensor))))

(defn tensorOperation [func]
  (fn [& tensors]
    {:pre [(or (empty? tensors) (tensor? tensors))]}
    (if (empty? tensors)
      []
      (if (every? number? tensors)
        (apply func tensors)
        (apply mapv (tensorOperation func) tensors)))))

(def t+ (tensorOperation +))

(def t- (tensorOperation -))

(def t* (tensorOperation *))