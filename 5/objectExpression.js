"use strict";

const TWO = new Const(2);
const ONE = new Const(1);
const ZERO = new Const(0);

function Const(value) {
    if (isNaN(value)) {
        throw Error("Expected number");
    }
    this.postfix = function () {
        return value.toString();
    };
    this.evaluate = function () {
        return value;
    };
    this.diff = function () {
        return ZERO;
    };
}

function Variable(value) {
    this.postfix = function () {
        return value;
    };
    this.evaluate = function (x, y, z) {
        return (value === 'x') ? (x) : (value === 'y') ? (y) : (z);
    };
    this.diff = function (nameToDiff) {
        return (nameToDiff === value) ? ONE : ZERO;
    };
}

function BinaryOperation(func, sign, ...args) {
    if (args.length !== 2) {
        throw Error("Invalid number of arguments, expected 2, found " + args.length);
    }
    this.first = args[0];
    this.second = args[1];
    this.func = func;
    this.sign = sign;
}

BinaryOperation.prototype.evaluate = function (...args) {
    return this.func(this.first.evaluate(...args), this.second.evaluate(...args));
};

BinaryOperation.prototype.postfix = function () {
    return "(" + this.first.postfix() + " " + this.second.postfix() + " " + this.sign + ")";
};

function Add(...args) {
    BinaryOperation.call(this, (first, second) => (first + second), '+', ...args);
    this.diff = function (name) {
        return new Add(this.first.diff(name), this.second.diff(name));
    };
}

Add.prototype = Object.create(BinaryOperation.prototype);

function Subtract(...args) {
    BinaryOperation.call(this, (first, second) => (first - second), '-', ...args);
    this.diff = function (name) {
        return new Subtract(this.first.diff(name), this.second.diff(name));
    };
}

Subtract.prototype = Object.create(BinaryOperation.prototype);

function Multiply(...args) {
    BinaryOperation.call(this, (first, second) => (first * second), '*', ...args);
    this.diff = function (name) {
        return new Add(
            new Multiply(this.first.diff(name), this.second),
            new Multiply(this.first, this.second.diff(name))
        );
    };
}

Multiply.prototype = Object.create(BinaryOperation.prototype);

function Divide(...args) {
    BinaryOperation.call(this, (first, second) => (first / second), '/', ...args);
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
                this.second));
    };
}

Divide.prototype = Object.create(BinaryOperation.prototype);

function UnaryOperation(func, name, ...args) {
    if (args.length !== 1) {
        throw Error("Invalid number of arguments, expected 1, found " + args.length);
    }
    this.argument = args[0];
    this.func = func;
    this.name = name;
}

UnaryOperation.prototype.evaluate = function (...args) {
    return this.func(this.argument.evaluate(...args));
};

UnaryOperation.prototype.postfix = function () {
    return "(" + this.argument.postfix() + " " + this.name + ")";
};

function Negate(...args) {
    UnaryOperation.call(this, (a) => (-a), "negate", ...args);
    this.diff = function (name) {
        return new Negate(this.argument.diff(name));
    }
}

Negate.prototype = Object.create(UnaryOperation.prototype);

function ArcTan(...args) {
    UnaryOperation.call(this, (a) => Math.atan(a), "atan", ...args);
    this.diff = function (name) {
        return new Divide(
            this.argument.diff(name),
            new Add(
                ONE,
                new Multiply(
                    this.argument,
                    this.argument
                )
            )
        );
    };
}

ArcTan.prototype = Object.create(UnaryOperation.prototype);

const sqr = a => a * a;

const sum = (args) => args.reduce((a, b) => a + b, 0);

const sumObjects = (args) => args.reduce((a, b) => new Add(a, b), ZERO);

const getPostfix = (args) => (args.length === 0) ? " " : args.reduce((a, b) => a + b + " ", "");

function InfinityOperation(func, name, ...args) {
    this.args = args;
    this.func = func;
    this.name = name;
}

InfinityOperation.prototype.evaluate = function (...args) {
    return this.func(sum(this.args.map(element => sqr(element.evaluate(...args)))));
};

InfinityOperation.prototype.postfix = function () {
    return "(" + getPostfix(this.args.map(element => element.postfix())) + this.name + ')';
};

function Sumsq(...args) {
    InfinityOperation.call(this, (a => a), 'sumsq', ...args);
    this.diff = function (name) {
        return sumObjects(this.args.map(element => new Multiply(new Multiply(TWO, element), element.diff(name))));
    }
}

Sumsq.prototype = Object.create(InfinityOperation.prototype);

function Length(...args) {
    InfinityOperation.call(this, (a => Math.sqrt(a)), 'length', ...args);
    this.diff = function (name) {
        return (this.args.length === 0) ? ZERO : new Divide(new Sumsq(...this.args).diff(name), new Multiply(TWO, this));
    }
}

Length.prototype = Object.create(InfinityOperation.prototype);

const operations = new Map([
    ['+', Add],
    ['-', Subtract],
    ['*', Multiply],
    ['/', Divide],
    ['negate', Negate],
    ['atan', ArcTan],
    ['sumsq', Sumsq],
    ['length', Length],
    ['x', new Variable('x')],
    ['y', new Variable('y')],
    ['z', new Variable('z')]
]);
operations.getOrDefault = key => (operations.has(key)) ? operations.get(key) : new Const(Number(key));

let cur;

const missSpaces = (str) => {
    while (cur < str.length && str[cur] === ' ') {
        cur++;
    }
};

const handle = (str) => {
    missSpaces(str);
    if (cur === str.length) {
        throw Error("Unexpected end of string");
    }
    let pos = 1;
    try {
        if (str[cur] === '(') {
            cur++;
            let args = [];
            while (str[cur] !== ')') {
                args.push(handle(str));
                missSpaces(str);
            }
            const y = args.pop();
            cur++;
            pos = cur;
            try {
                return new y(...args);
            } catch (e) {
                throw Error("Don't know the symbol");
            }
        } else {
            let name = "";
            pos = cur;
            while (cur < str.length && str[cur] !== ' ' && str[cur] !== '(' && str[cur] !== ')') {
                name += str[cur++];
            }
            return operations.getOrDefault(name);
        }
    } catch (e) {
        throw Error(e.message + " on position " + pos.toString());
    }
};

const parsePostfix = str => {
    cur = 0;
    const result = handle(str);
    missSpaces(str);
    result.evaluate(0, 0, 0);
    if (cur !== str.length) {
        throw Error("Unexpected symbols after the end, expected end on the " + cur.toString());
    } else {
        return result;
    }
};