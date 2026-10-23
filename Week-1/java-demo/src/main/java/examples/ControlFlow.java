package examples;

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



    }

}
