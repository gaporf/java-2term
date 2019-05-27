package expression.parser;

import expression.generic.MyNumber;

public class Divide<T> extends BinaryOperation<T> {
    public Divide(TripleExpression<T> first, TripleExpression<T> second, MyNumber<T> myNumber) {
        super(first, second, myNumber);
    }

    @Override
    protected T binaryResult(T a, T b) {
        return myNumber.divide(a, b);
    }
}
