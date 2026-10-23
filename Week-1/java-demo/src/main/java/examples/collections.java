package examples;

import java.util.*;

public class collections {

    /*
        The Collection API is a standardized set of containers provided in the base JDK for storing data in your application
        in memory. These collections are used to organize and iterate over our data in order to properly facilitate our
        applications activities. There are 3 primary collections that inherit from the Collection Interface, and one that
        is included in the list but instead is a Map:
        - Lists
        - Sets
        - Queues
        - Map (does not inherit from Collection)
     */

    public static void main(String[] args) {

        // our humans we are working with
        var billy = new Human("Billy", 26);
        var sally = new Human("Sally", 32);
        var slagathor = new Human("Slagathor", 378439);

        // Lists are indexable, maintain order of insertion, and allow duplicates
        // List is the interface, ArrayList is the implementation of that interface and its associated behaviors (methods)
        // <Human> is a generic declaration: it indicates the list will store Human objects
        List<Human> people = new ArrayList<>();
        people.addAll(List.of(slagathor, sally, billy, slagathor));
        System.out.println(people);

        System.out.println(people.get(1));

        // Sets are not indexable, do not guarantee order of insertion being maintained, do not allow duplicates
        // use sets when the uniqueness of the group is what is important
        Set<Human> uniquePeople = new HashSet<>(people);
        System.out.println(uniquePeople);

        for (Human h :  uniquePeople){
            if(h.name.equals("Slagathor")){
                System.out.println("How did you get in here????");
            }
        }

        // Queues are intended to be used to manage Last-In-First-Out (LIFO) and First-In-First-Out (FIFO) collections.

        Deque<Human> lineToTheClub = new ArrayDeque<>(uniquePeople);
        System.out.println(lineToTheClub);

        System.out.println(lineToTheClub.peekFirst());
        System.out.println(lineToTheClub);
        System.out.println(lineToTheClub.pollFirst());
        System.out.println(lineToTheClub);

        System.out.println(lineToTheClub.pollLast());
        System.out.println(lineToTheClub);


    }

}
