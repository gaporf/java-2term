package expression.generic;

public class FloatNumber implements MyNumber<Float> {
    @Override
    public Float getNumber(String number) {
        return Float.valueOf(number);
    }

    @Override
    public Float getNumber(Integer number) {
        return Float.valueOf(number);
    }

    @Override
    public Float multiply(Float a, Float b) {
        return a * b;
    }

    @Override
    public Float divide(Float a, Float b) {
        return a / b;
    }

    @Override
    public Float add(Float a, Float b) {
        return a + b;
    }

    @Override
    public Float subtract(Float a, Float b) {
        return a - b;
    }

    @Override
    public Float abs(Float a) {
        if (a < 0) {
            a = -a;
        }
        return a;
    }

    @Override
    public Float mod(Float a, Float b) {
        return a % b;
    }
}
