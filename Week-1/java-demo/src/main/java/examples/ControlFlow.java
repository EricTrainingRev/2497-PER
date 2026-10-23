package examples;

import examples.shapes.*;

import java.util.Scanner;

public class ControlFlow {

    public static void main(String[] args) {
        // Control flow is how we refer to the tools that allow us to control the execution of our code
        var billy = new Human("Billy", 15);
        Human sally = new Human("Sally", 15);

        // an if statement is used to check if a condition is logically true, if so the associated code block is executed,
        // otherwise the code block is skipped
        if(billy.age >= 18){
            System.out.println("welcome to the movie " + billy.name);
        // an else block can be added which is only triggered if the associated if block's condition is not met
        } else {
            System.out.println("You are only %d years old, come back when you are 18".formatted(billy.age));
        }

        if(sally.age >= 18){
            System.out.println("welcome to the movie " + sally.name);
        } else {
            System.out.println("You are only %d years old, come back when you are 18".formatted(sally.age));
        }

        if(sally.age >= 60){
            System.out.println("You get the senior discount: 10$ please");
        // if you have multiple checks to make you can add on "else if" blocks to make secondary checks. Keep in mind
        // only one block of code will execute, and the order of operations goes from first if down to the else
        } else if (sally.age >= 18) {
            System.out.println("You get the general adult price: 15$ please");
        } else {
            System.out.println("You get the child price: 7$ please");
        }


        /*
            You will often need to perform similar actions multiple times in sequence: in these situations you have two
            options for how to manage your "loops" of code: "for" statements and "while" statements
            - if you know how many times you need to loop (think iterating through a collection) a for loops is going
              to be your best option
            - if you don't know how many times you need to loop (think a user is interacting with your service) a while
              loop is going to be your best option
         */

        /*
            int x = 1   -> this initializes a control variable for the loop. It only exists for the lifespan of the loop
            x <= 10     -> this sets the condition under which the loop continues
            x++         -> this tells Java to increment the value of x by 1 and the end of each loop
         */
        for(int x = 1; x <= 10; x++){
            System.out.println(x);
        }

        // iterable collections support "enhanced" for loops: you can use simplified syntax to go over each element of
        // the collection and have the loop stop after all elements have passed through the loop
        Human[] humans = {billy, sally};
        for(Human h : humans){
            System.out.println(h.giveGreeting());
        }


        // while loops are useful when you don't know how many times your loop needs to execute
        var keepGoing = true;
        // this is a convinient option to get user input from the terminal
        var scanner = new Scanner(System.in);
        while(keepGoing){
            System.out.print("Enter your name or q to quit: ");
            String userInput = scanner.nextLine();
            if(userInput.equals("q")){
                System.out.println("Goodbye!");
                keepGoing = false;
            } else {
                System.out.println("Hello " + userInput);
            }
        }
        // make sure to close your scanner when done with it
        scanner.close();

        // if you want to check your logical condition after your loop has executed instead of before execution you can
        // use a do-while loop. This is also useful if you want the code to always execute at least one time
        do{
            System.out.println("This will print even though we hard-code a false boolean in the while check");
        } while(false);

        // Switch statements are a way of performing pattern matching: you provide a value to match against and depening
        // on whether or not you have coded a match for the input one or more actions may occur
        var slagathor = "Slagathor";
        switch (slagathor){
            case "billy":
                System.out.println("Hello Billy");
                break;
            case "Slagathor":
                System.out.println("Watch out for Slagathor!");
                break;
            case "Sally":
                System.out.println("Hello Sally");
                break;
            default:
                System.out.println("I don't know you");
        }

        // When working with sealed classes (see shapes package) you can use a switch statement to cover all possible
        // types your sealed class could be. The compiler will require you to accomodate all possible options
        Shape myShape = new Triangle();
        switch (myShape){
            case Circle c: 
                System.out.println("This is a circle shape!"); 
                break;
            case Rectanglee r:
                System.out.println("This is a Rectanglee shape!");
                break;
            case Triangle t:
                System.out.println("This is a Triangle shape!");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + myShape);
        }

    }

}
