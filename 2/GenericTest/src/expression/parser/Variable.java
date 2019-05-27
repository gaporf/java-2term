package expression.parser;

public class Variable<T> extends BasicOperation<T> {
    private char var;

    public Variable(char var) {
        this.var = var;
    }

    @Override
    protected T basicResult(T x, T y, T z) {
        if (var == 'x') {
            return x;
        } else if (var == 'y') {
            return y;
        } else {
            return z;
        }
    }
}
