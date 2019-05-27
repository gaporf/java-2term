"use strict";

const cnst = a => () => a;

const one = cnst(1);

const two = cnst(2);

const variable = a => (x, y, z) => (a === 'x') ? x : (a === 'y') ? y : z;

const unaryFunction = func => arg => (...args) => func(arg(...args));

const negate = unaryFunction(a => -a);

const abs = unaryFunction(a => Math.abs(a));

const binaryFunction = func => (first, second) => (...args) => func(first(...args), second(...args));

const add = binaryFunction((first, second) => first + second);

const subtract = binaryFunction((first, second) => first - second);

const multiply = binaryFunction((first, second) => first * second);

const divide = binaryFunction((first, second) => first / second);

const tripleFunction = func => (first, second, third) => (...args) => func(first(...args), second(...args), third(...args));

const iff = tripleFunction((first, second, third) => first >= 0 ? second : third);

const getTripleOperation = func => () => {
    const third = stack.pop();
    const second = stack.pop();
    const first = stack.pop();
    return func(first, second, third);
};

const getBinaryOperation = func => () => {
    const second = stack.pop();
    const first = stack.pop();
    return func(first, second);
};

const getUnaryOperation = func => () => {
    return func(stack.pop());
};

const getVariable = name => () => {
    return variable(name);
};

const getConst = number => () => {
    return cnst(number);
};

let stack = [];

let operation = new Map([
    ['iff', getTripleOperation(iff)],
    ['+', getBinaryOperation(add)],
    ['-', getBinaryOperation(subtract)],
    ['*', getBinaryOperation(multiply)],
    ['/', getBinaryOperation(divide)],
    ['negate', getUnaryOperation(negate)],
    ['abs', getUnaryOperation(abs)],
    ['x', getVariable('x')],
    ['y', getVariable('y')],
    ['z', getVariable('z')],
    ['one', getConst(1)],
    ['two', getConst(2)]
]);
operation.getOrDefault = key => operation.has(key) ? operation.get(key) : getConst(Number(key));

const getResult = a => {
    a.forEach(function (element) {
        stack.push(operation.getOrDefault(element)());
    });
    return stack.pop();
};

let parse = a => (...args) => getResult(a.split(" ").filter(v => v !== ''))(...args);