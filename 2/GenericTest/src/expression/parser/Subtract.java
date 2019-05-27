package expression.parser;

import expression.generic.MyNumber;

public class Subtract<T> extends BinaryOperation<T> {
    public Subtract(TripleExpression<T> first, TripleExpression<T> second, MyNumber<T> myNumber) {
        super(first, second, myNumber);
    }

    @Override
    protected T binaryResult(T a, T b) {
        return myNumber.subtract(a, b);
    }
}
