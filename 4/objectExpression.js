"use strict";

function Const(value) {
    this.isConst = true;
    this.toString = function () {
        return value.toString();
    };
    this.evaluate = function () {
        return value;
    };
    this.diff = function () {
        return new Const(0);
    };
    this.simplify = function () {
        return new Const(value);
    };
}

function Variable(value) {
    this.toString = function () {
        return value.toString();
    };
    this.evaluate = function (x, y, z) {
        return (value === 'x') ? (x) : (value === 'y') ? (y) : (z);
    };
    this.diff = function (nameToDiff) {
        return (nameToDiff === value) ? new Const(1) : new Const(0);
    };
    this.simplify = function () {
        return new Variable(value);
    };
}

function BinaryOperation(first, second, func, sign) {
    this.first = first;
    this.second = second;
    this.func = func;
    this.sign = sign;
}

BinaryOperation.prototype.evaluate = function (...args) {
    return this.func(this.first.evaluate(...args), this.second.evaluate(...args));
};

BinaryOperation.prototype.toString = function () {
    return this.first.toString() + " " + this.second.toString() + " " + this.sign;
};

BinaryOperation.prototype.simplify = function () {
    this.first.simple = this.first.simplify();
    this.second.simple = this.second.simplify();
    if (this.first.simple.isConst && this.second.simple.isConst) {
        return new Const(this.func(this.first.simple.evaluate(), this.second.simple.evaluate()));
    } else {
        return this.goodSimplify();
    }
};

function Add(first, second) {
    BinaryOperation.call(this, first, second, (first, second) => (first + second), '+');
    this.diff = function (name) {
        return new Add(this.first.diff(name), this.second.diff(name));
    };
    this.goodSimplify = function () {
        if (this.first.simple.isConst && this.first.simple.evaluate() === 0) {
            return this.second.simple;
        } else if (this.second.simple.isConst && this.second.simple.evaluate() === 0) {
            return this.first.simple;
        } else if (this.first.simple === this.second.simple) {
            return new Multiply(new Const(2), this.first.simple);
        } else {
            return new Add(this.first.simple, this.second.simple);
        }
    };
}

Add.prototype = Object.create(BinaryOperation.prototype);

function Subtract(first, second) {
    BinaryOperation.call(this, first, second, (first, second) => (first - second), '-');
    this.diff = function (name) {
        return new Subtract(this.first.diff(name), this.second.diff(name));
    };
    this.goodSimplify = function () {
        if (this.first.simple.isConst && this.first.simple.evaluate() === 0) {
            return new Negate(this.second.simplify());
        } else if (this.second.simple.isConst && this.second.simple.evaluate() === 0) {
            return this.first.simple;
        } else if (this.first.simple === this.second.simple) {
            return new Const(0);
        } else {
            return new Subtract(this.first.simple, this.second.simple);
        }
    };
}

Subtract.prototype = Object.create(BinaryOperation.prototype);

function Multiply(first, second) {
    BinaryOperation.call(this, first, second, (first, second) => (first * second), '*');
    this.diff = function (name) {
        return new Add(
            new Multiply(this.first.diff(name), this.second),
            new Multiply(this.first, this.second.diff(name))
        );
    };
    this.goodSimplify = function () {
        if (this.first.simple.isConst && this.first.simple.evaluate() === 0 ||
            this.second.simple.isConst && this.second.simple.evaluate() === 0) {
            return new Const(0);
        } else if (this.first.simple.isConst && this.first.simple.evaluate() === 1) {
            return this.second.simple;
        } else if (this.second.simple.isConst && this.second.simple.evaluate() === 1) {
            return this.first.simple;
        } else {
            return new Multiply(this.first.simple, this.second.simple);
        }
    };
}

Multiply.prototype = Object.create(BinaryOperation.prototype);

function Divide(first, second) {
    BinaryOperation.call(this, first, second, (first, second) => (first / second), '/');
    this.diff = function (name) {
        return new Divide(
            new Subtract(
                new Multiply(
                    this.first.diff(name),
                    this.second),
                new Multiply(
                    this.first,
                    this.second.diff(name))),
            new Multiply(
                this.second,
                this.second)
        );
    };
    this.goodSimplify = function () {
        if (this.first.simple.isConst && this.first.simple.evaluate() === 0) {
            return new Const(0);
        } else if (this.second.simple.isConst && this.second.simple.evaluate() === 1) {
            return this.first.simple;
        } else if (this.first.simple === this.second.simple) {
            return new Const(1);
        } else {
            return new Divide(this.first.simple, this.second.simple);
        }
    };
}

Divide.prototype = Object.create(BinaryOperation.prototype);

function ArcTan2(first, second) {
    BinaryOperation.call(this, first, second, (first, second) => Math.atan2(first, second), 'atan2');
    this.diff = function (name) {
        return new Divide(
            new Subtract(
                new Multiply(
                    this.first.diff(name),
                    this.second
                ),
                new Multiply(
                    this.first,
                    this.second.diff(name)
                )
            ),
            new Add(
                new Multiply(
                    this.first,
                    this.first
                ),
                new Multiply(
                    this.second,
                    this.second
                )
            )
        )
    };
    this.goodSimplify = function () {
        return new ArcTan(new Divide(first, second).simplify()).simplify();
    };
}

ArcTan2.prototype = Object.create(BinaryOperation.prototype);

function UnaryOperation(argument, func, name) {
    this.argument = argument;
    this.func = func;
    this.name = name;
}

UnaryOperation.prototype.evaluate = function (...args) {
    return this.func(this.argument.evaluate(...args));
};

UnaryOperation.prototype.toString = function () {
    return this.argument.toString() + " " + this.name;
};

UnaryOperation.prototype.simplify = function () {
    this.argument.simple = this.argument.simplify();
    if (this.argument.simple.isConst) {
        return new Const(this.func(this.argument.simple));
    } else {
        return new UnaryOperation(this.argument.simple, this.func, this.name);
    }
};

function Negate(argument) {
    UnaryOperation.call(this, argument, (a) => (-a), "negate");
    this.diff = function (name) {
        return new Negate(this.argument.diff(name));
    };
}

Negate.prototype = Object.create(UnaryOperation.prototype);

function ArcTan(argument) {
    UnaryOperation.call(this, argument, (a) => Math.atan(a), "atan");
    this.diff = function (name) {
        return new Divide(
            this.argument.diff(name),
            new Add(
                new Const(1),
                new Multiply(
                    this.argument,
                    this.argument
                )
            )
        );
    };
}

ArcTan.prototype = Object.create(UnaryOperation.prototype);

const getBinaryOperation = obj => (stack) => {
    const second = stack.pop();
    const first = stack.pop();
    return new obj(first, second);
};

const getUnaryOperation = obj => (stack) => {
    const argument = stack.pop();
    return new obj(argument);
};

const getVariable = name => () => {
    return new Variable(name);
};

const getConst = key => () => {
    return new Const(key);
};

const getResult = a => {
    const stack = [];
    const operation = new Map([
        ['+', getBinaryOperation(Add)],
        ['-', getBinaryOperation(Subtract)],
        ['*', getBinaryOperation(Multiply)],
        ['/', getBinaryOperation(Divide)],
        ['atan2', getBinaryOperation(ArcTan2)],
        ['negate', getUnaryOperation(Negate)],
        ['atan', getUnaryOperation(ArcTan)],
        ['x', getVariable('x')],
        ['y', getVariable('y')],
        ['z', getVariable('z')]
    ]);
    operation.getOrDefault = key => operation.has(key) ? operation.get(key) : getConst(Number(key));
    a.forEach(function (element) {
        stack.push(operation.getOrDefault(element)(stack));
    });
    return stack.pop();
};

const parse = a => getResult(a.split(" ").filter(v => v !== ''));