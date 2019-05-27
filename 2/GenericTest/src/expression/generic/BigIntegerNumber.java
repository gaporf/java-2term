package expression.generic;

import expression.ListOfExceptions.OverflowException;

import java.math.BigInteger;

public class BigIntegerNumber implements MyNumber<BigInteger> {
    @Override
    public BigInteger getNumber(String number) {
        return new BigInteger(number);
    }

    @Override
    public BigInteger getNumber(Integer number) {
        return BigInteger.valueOf(number);
    }

    @Override
    public BigInteger multiply(BigInteger a, BigInteger b) {
        return a.multiply(b);
    }

    @Override
    public BigInteger divide(BigInteger a, BigInteger b) {
        if (b.compareTo(BigInteger.ZERO) == 0) {
            throw new OverflowException("");
        }
        return a.divide(b);
    }

    @Override
    public BigInteger add(BigInteger a, BigInteger b) {
        return a.add(b);
    }

    @Override
    public BigInteger subtract(BigInteger a, BigInteger b) {
        return a.subtract(b);
    }

    @Override
    public BigInteger abs(BigInteger a) {
        return a.abs();
    }

    @Override
    public BigInteger mod(BigInteger a, BigInteger b) {
        if (b.compareTo(BigInteger.ZERO) <= 0) {
            throw new OverflowException("");
        } else {
            return a.mod(b);
        }
    }
}
