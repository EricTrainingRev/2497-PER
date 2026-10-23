package examples.shapes;

// a class extending a sealed class may itself be sealed, which then requires it to "permits" one more classes
public sealed class Rectanglee extends Shape permits Square {
}
