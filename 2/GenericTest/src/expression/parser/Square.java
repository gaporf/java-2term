package expression.parser;

import expression.generic.MyNumber;

public class Square<T> extends UnaryOperation<T> {
    public Square(TripleExpression<T> tripleExpression, MyNumber<T> myNumber) {
        super(tripleExpression, myNumber);
    }

    @Override
    protected T unaryResult(T a) {
        return myNumber.multiply(a, a);
    }
}
