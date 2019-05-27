package expression.generic;

public interface MyNumber<T> {
    T getNumber(String number);

    T getNumber(Integer number);

    T multiply(T a, T b);

    T divide(T a, T b);

    T add(T a, T b);

    T subtract(T a, T b);

    T abs(T a);

    T mod(T a, T b);
}
