package examples;

import java.util.Scanner;

public class Exceptions {

    public static void main(String[] args) {
        /*
            When something goes wrong with your code Java will throw an Exception: these are simply programatic indicators
            that something has gone wrong. You can make your own exceptions if you have custom scenarios where, from
            Java's standpoint, nothing has gone wrong, but according to your business rules something did go wrong
         */

        // use try/catch blocks anytime you think an exception may be thrown. A finally block can be used if you have
        // any code that needs to be executed whether an exception was thrown or not
        try{
            int oops = 10 / 0;
            System.out.println(oops);
        } catch (ArithmeticException e){
            System.out.println("Can't divide by 0");
        } finally {
            System.out.println("this block triggers whether or not an exception is caught");
        }

        // You can make try with resources blocks that automatically close resources that implement the AutoCloseable interface
        try(Scanner scanner = new Scanner(System.in)){
            var shouldContinue = true;
            while(shouldContinue){
                System.out.print("Tell me your name: ");
                String userInput = scanner.nextLine();
                if(userInput.equals("Slagathor")){
                    throw new SlagathorNotAllowed("No Slagathors allowed!");
                }
                System.out.println("Hello " + userInput);
            }
        } catch (SlagathorNotAllowed e){
            System.out.println(e.getMessage());
        }
    }
}
