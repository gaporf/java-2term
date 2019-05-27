package expression.parser;

import expression.generic.MyNumber;

public class Add<T> extends BinaryOperation<T> {
    public Add(TripleExpression<T> first, TripleExpression<T> second, MyNumber<T> myNumber) {
        super(first, second, myNumber);
    }

    @Override
    protected T binaryResult(T a, T b) {
        return myNumber.add(a, b);
    }
}
