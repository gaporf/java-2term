package expression.generic;

public class DoubleNumber implements MyNumber<Double> {
    @Override
    public Double getNumber(String number) {
        return Double.parseDouble(number);
    }

    @Override
    public Double getNumber(Integer number) {
        return Double.valueOf(number);
    }

    @Override
    public Double multiply(Double a, Double b) {
        return a * b;
    }

    @Override
    public Double divide(Double a, Double b) {
        return a / b;
    }

    @Override
    public Double add(Double a, Double b) {
        return a + b;
    }

    @Override
    public Double subtract(Double a, Double b) {
        return a - b;
    }

    @Override
    public Double abs(Double a) {
        if (a < 0) {
            a = -a;
        }
        return a;
    }

    @Override
    public Double mod(Double a, Double b) {
        return a % b;
    }
}
