package expression.parser;

import expression.generic.MyNumber;

public abstract class BinaryOperation<T> implements TripleExpression<T> {
    protected TripleExpression<T> first, second;
    protected MyNumber<T> myNumber;

    protected abstract T binaryResult(T a, T b);

    public BinaryOperation(TripleExpression<T> first, TripleExpression<T> second, MyNumber<T> myNumber) {
        this.first = first;
        this.second = second;
        this.myNumber = myNumber;
    }

    @Override
    public T evaluate(T x, T y, T z) {
        return binaryResult(this.first.evaluate(x, y, z), this.second.evaluate(x, y, z));
    }
}
