package expression.exceptions;

import expression.parser.TripleExpression;

public interface Parser<T> {
    TripleExpression<T> parse(String expression);
}
