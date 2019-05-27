package expression.parser;

public class Const<T> extends BasicOperation<T> {
    private T value;

    public Const(T value) {
        this.value = value;
    }

    @Override
    protected T basicResult(T x, T y, T z) {
        return value;
    }
}
