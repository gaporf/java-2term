package expression.generic;

import expression.ListOfExceptions.OverflowException;
import expression.exceptions.ExpressionParser;
import expression.parser.TripleExpression;

public class GenericTabulator implements Tabulator {

    private <T> Object getValue(TripleExpression<T> parser, MyNumber<T> myNumber, int x, int y, int z) {
        T X = myNumber.getNumber(x),
                Y = myNumber.getNumber(y),
                Z = myNumber.getNumber(z);
        try {
            return parser.evaluate(X, Y, Z);
        } catch (OverflowException e) {
            return null;
        }
    }

    private MyNumber<?> getNumber(String mode) {
        switch (mode) {
            case "i":
                return new IntegerMyNumber(true);
            case "u":
                return new IntegerMyNumber(false);
            case "d":
                return new DoubleNumber();
            case "f":
                return new FloatNumber();
            case "b":
                return new ByteNumber();
            default:
                return new BigIntegerNumber();
        }
    }

    private <T> Object[][][] getResult(MyNumber<T> myNumber, String expression, int x1, int x2, int y1, int y2, int z1, int z2) {
        TripleExpression<T> tripleExpression = new ExpressionParser<>(myNumber).parse(expression);
        Object[][][] table = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    table[x - x1][y - y1][z - z1] = getValue(tripleExpression, myNumber, x, y, z);
                }
            }
        }
        return table;
    }

    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        return getResult(getNumber(mode), expression, x1, x2, y1, y2, z1, z2);
    }
}
