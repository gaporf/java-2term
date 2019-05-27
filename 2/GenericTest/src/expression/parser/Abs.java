package expression.parser;

import expression.generic.MyNumber;

public class Abs<T> extends UnaryOperation<T> {
    public Abs(TripleExpression<T> trippleExpression, MyNumber<T> myNumber) {
        super(trippleExpression, myNumber);
    }

    @Override
    protected T unaryResult(T a) {
        return myNumber.abs(a);
    }
}
