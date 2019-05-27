package expression.generic;

import expression.ListOfExceptions.OverflowException;

public class IntegerMyNumber implements MyNumber<Integer> {
    private boolean overflow;

    public IntegerMyNumber(boolean overflow) {
        this.overflow = overflow;
    }

    @Override
    public Integer getNumber(String number) {
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new OverflowException("");
        }
    }

    @Override
    public Integer getNumber(Integer number) {
        return number;
    }

    @Override
    public Integer multiply(Integer a, Integer b) {
        if (overflow) {
            if (a > 0 && b > 0) {
                if (b > Integer.MAX_VALUE / a) {
                    throw new OverflowException("");
                }
            } else if (a > 0 && b < 0) {
                if (b < Integer.MIN_VALUE / a) {
                    throw new OverflowException("");
                }
            } else if (a < 0 && b > 0) {
                if (a < Integer.MIN_VALUE / b) {
                    throw new OverflowException("");
                }
            } else if (a < 0 && b < 0) {
                if (b < Integer.MAX_VALUE / a) {
                    throw new OverflowException("");
                }
            }
        }
        return a * b;
    }

    @Override
    public Integer divide(Integer a, Integer b) {
        if (overflow) {
            if (a == Integer.MIN_VALUE && b == -1) {
                throw new OverflowException("");
            }
        }
        if (b == 0) {
            throw new OverflowException("");
        }
        return a / b;
    }

    @Override
    public Integer add(Integer a, Integer b) {
        if (overflow) {
            if (a > 0 && b > 0) {
                if (Integer.MAX_VALUE - a < b) {
                    throw new OverflowException("");
                }
            } else if (a < 0 && b < 0) {
                if (b < Integer.MIN_VALUE - a) {
                    throw new OverflowException("");
                }
            }
        }
        return a + b;
    }

    @Override
    public Integer subtract(Integer a, Integer b) {
        if (overflow) {
            if (a >= 0 && b < 0) {
                if (a - Integer.MIN_VALUE > b) {
                    throw new OverflowException("");
                }
            }
            if (a < 0 && b > 0) {
                if (a - Integer.MIN_VALUE < b) {
                    throw new OverflowException("");
                }
            }
        }
        return a - b;
    }

    @Override
    public Integer abs(Integer a) {
        if (overflow && a == Integer.MIN_VALUE) {
            throw new OverflowException("");
        }
        if (a < 0) {
            a = -a;
        }
        return a;
    }

    @Override
    public Integer mod(Integer a, Integer b) {
        if (b == 0) {
            throw new OverflowException("");
        } else {
            return a % b;
        }
    }
}
