package examples.shapes;

// sealed classes only allow their  "permits" classes to extend them, and they MUST extend the sealed class
public sealed class Shape permits Circle, Rectanglee, Triangle {
}
