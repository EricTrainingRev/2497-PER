// This is the address for the class: it is needed for importing the class in other places
package examples;

// Java is an Object-Oriented Programming language, so everything we do is based around Classes and their Objects
public class Human {
    /*
        Below we have two examples of attributes, or fields, of the class. These are declared here in the class to
        tell Java that Human objects have names and ages: this are not shared among objects, they belong to the
        individual objects themselves
    */
    String name;
    int age;

    // this "constructor" provides instructions for what to do when an object of this class is created
    public Human(String name, int age){
        this.name = name;
        this.age = age;
    }

    public String giveGreeting(){
        return "Hello! my name is %s and I am %d years old".formatted(name, age);
    }

    @Override
    public String toString() {
        return "Human{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }

    public static void main(String[] args) {
        /*
            There are two ways you can initialize your variables in Java:
            - the common/traditional way is to state the type first and then give a name to the variable before assigning
              a value to it (see sally variable below)
            - Java has a special type called "var" that tells the compiler to infer the variables type based on the
              value you assign to it (see billy below)
        */
        var billy = new Human("Billy", 23);
        Human sally = new Human("Sally", 26);
        System.out.println(billy.name);
        System.out.println(sally.name);
        System.out.println(billy.giveGreeting());
        System.out.println(sally.giveGreeting());
    }
}
