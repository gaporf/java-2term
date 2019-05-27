package expression.parser;

import expression.generic.MyNumber;

public abstract class UnaryOperation<T> implements TripleExpression<T> {
    protected TripleExpression<T> tripleExpression;
    protected MyNumber<T> myNumber;

    protected abstract T unaryResult(T a);

    public UnaryOperation(TripleExpression<T> tripleExpression, MyNumber<T> myNumber) {
        this.tripleExpression = tripleExpression;
        this.myNumber = myNumber;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return unaryResult(this.tripleExpression.evaluate(x, y, z));
    }
}
