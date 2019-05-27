package expression.generic;

import expression.ListOfExceptions.OverflowException;

public class ByteNumber implements MyNumber<Byte> {
    @Override
    public Byte subtract(Byte a, Byte b) {
        return (byte) (a - b);
    }

    @Override
    public Byte add(Byte a, Byte b) {
        return (byte) (a + b);
    }

    @Override
    public Byte divide(Byte a, Byte b) {
        if (b == 0) {
            throw new OverflowException("");
        } else {
            return (byte) (a / b);
        }
    }

    @Override
    public Byte multiply(Byte a, Byte b) {
        return (byte) (a * b);
    }

    @Override
    public Byte getNumber(String number) {
        return Byte.valueOf(number);
    }

    @Override
    public Byte getNumber(Integer number) {
        return number.byteValue();
    }

    @Override
    public Byte abs(Byte a) {
        if (a < 0) {
            a = (byte) (-a);
        }
        return a;
    }

    @Override
    public Byte mod(Byte a, Byte b) {
        if (b == 0) {
            throw new OverflowException("");
        } else {
            return (byte) (a % b);
        }
    }
}
