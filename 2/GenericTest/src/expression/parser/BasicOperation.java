package expression.parser;

public abstract class BasicOperation<T> implements TripleExpression<T> {
    protected abstract T basicResult(T x, T y, T z);

    @Override
    public T evaluate(T x, T y, T z) {
        return basicResult(x, y, z);
    }
}
