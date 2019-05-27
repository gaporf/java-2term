package expression.parser;

import expression.generic.MyNumber;

public class Multiply<T> extends BinaryOperation<T> {
    public Multiply(TripleExpression<T> first, TripleExpression<T> second, MyNumber<T> myNumber) {
        super(first, second, myNumber);
    }

    @Override
    protected T binaryResult(T a, T b) {
        return myNumber.multiply(a, b);
    }
}
