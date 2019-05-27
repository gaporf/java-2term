package expression.parser;

import expression.generic.MyNumber;

public class Mod<T> extends BinaryOperation<T> {
    public Mod(TripleExpression<T> first, TripleExpression<T> second, MyNumber<T> myNumber) {
        super(first, second, myNumber);
    }

    @Override
    protected T binaryResult(T a, T b) {
        return myNumber.mod(a, b);
    }
}
