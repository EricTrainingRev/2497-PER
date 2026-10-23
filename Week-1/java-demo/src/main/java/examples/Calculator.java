package examples;

public class Calculator {
    public static void main(String[] args) {
        MyFunctionalInterface doAddition = (int numOne, int numTwo) -> numOne + numTwo;
        System.out.println(doAddition.doMath(5,5));

        MyFunctionalInterface doSubtraction = (int numOne, int numTwo) -> numOne - numTwo;
        System.out.println(doSubtraction.doMath(5,5));
    }
}
