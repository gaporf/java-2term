package expression.exceptions;

import expression.generic.MyNumber;
import expression.parser.*;

public class ExpressionParser<T> implements Parser<T> {
    private String expression;
    private MyNumber<T> myNumber;
    private int ptr;

    private void nextChar() {
        nextChar(1);
    }

    private void nextChar(int mov) {
        ptr += mov;
        missSpaces();
    }

    private char getChar() {
        return (ptr < expression.length()) ? expression.charAt(ptr) : '\0';
    }

    private void missSpaces() {
        while (ptr < expression.length() && Character.isWhitespace(getChar())) {
            ptr++;
        }
    }

    private boolean match(char pattern) {
        return getChar() == pattern;
    }

    private boolean isDigit() {
        return Character.isDigit(getChar()) || getChar() == 'e' || getChar() == '.';
    }

    private boolean isVar() {
        return match('x') || match('y') || match('z');
    }

    private T getValue(String number) {
        return myNumber.getNumber(number);
    }

    private String getNumber(boolean positive) {
        StringBuilder ans = new StringBuilder();
        if (!positive) {
            ans.append("-");
        }
        while (isDigit()) {
            ans.append(getChar());
            nextChar();
        }
        return ans.toString();
    }

    private TripleExpression<T> prim() {
        TripleExpression<T> result = null;
        if (isDigit()) {
            result = new Const<>(getValue(getNumber(true)));
        } else if (isVar()) {
            result = new Variable<>(getChar());
            nextChar();
        } else if (match('-')) {
            nextChar();
            if (isDigit()) {
                result = new Const<>(getValue(getNumber(false)));
            } else {
                result = new Multiply<>(new Const<>(getValue("-1")), prim(), myNumber);
            }
        } else if (match('(')) {
            nextChar();
            result = plusMinus();
            nextChar();
            return result;
        } else if (match('a')) {
            nextChar(3);
            result = prim();
            return new Abs<>(result, myNumber);
        } else if (match('s')) {
            nextChar(6);
            result = prim();
            return new Square<>(result, myNumber);
        }
        return result;
    }

    private TripleExpression<T> mulDiv() {
        TripleExpression<T> left = prim(),
                right;
        while (true) {
            if (match('*')) {
                nextChar();
                right = prim();
                left = new Multiply<>(left, right, myNumber);
            } else if (match('/')) {
                nextChar();
                right = prim();
                left = new Divide<>(left, right, myNumber);
            } else if (match('m')) {
                nextChar(3);
                right = prim();
                left = new Mod<>(left, right, myNumber);
            } else {
                break;
            }
        }
        return left;
    }

    private TripleExpression<T> plusMinus() {
        TripleExpression<T> left = mulDiv(),
                right;
        while (true) {
            if (match('+')) {
                nextChar();
                right = mulDiv();
                left = new Add<>(left, right, myNumber);
            } else if (match('-')) {
                nextChar();
                right = mulDiv();
                left = new Subtract<>(left, right, myNumber);
            } else {
                break;
            }
        }
        return left;
    }

    public ExpressionParser(final MyNumber<T> myNumber) {
        this.myNumber = myNumber;
    }

    @Override
    public TripleExpression<T> parse(String expression) {
        this.expression = expression;
        ptr = 0;
        return plusMinus();
    }
}
