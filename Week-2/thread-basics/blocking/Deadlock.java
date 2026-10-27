package blocking;

/**
 * Minimal demonstration of a thread deadlock.
 *
 * A deadlock needs four conditions at once:
 *   1. Mutual exclusion  — each lock is held by only one thread at a time.
 *   2. Hold and wait     — a thread holds one lock while waiting for another.
 *   3. No preemption     — a lock can't be yanked away; the holder must release it.
 *   4. Circular wait     — thread A waits on a lock B holds, and B waits on a lock A holds.
 *
 * Here the four conditions are produced by having two threads acquire the same
 * two locks in OPPOSITE order. A small sleep after the first acquire makes the
 * cross-over (and hence the cycle) effectively guaranteed in practice.
 */
public class Deadlock {

    // Two independent monitors that act as the competing "resources".
    private static final Object lockA = new Object();
    private static final Object lockB = new Object();

    public static void main(String[] args) throws InterruptedException {

        Thread first = new Thread(Deadlock::runFirst, "thread-first");
        Thread second = new Thread(Deadlock::runSecond, "thread-second");

        first.start();
        second.start();

        // Give both threads time to reach the deadlock...
        first.join(3000);
        second.join(3000);

        System.out.println("main: both threads are still alive -> deadlock. Terminating without cleanup.");
        System.exit(0);
    }

    private static void runFirst() {
        System.out.println(Thread.currentThread().getName()
                + ": acquiring lockA ...");
        synchronized (lockA) {
            pause();
            System.out.println(Thread.currentThread().getName()
                    + ": holding lockA, waiting for lockB ...");
            // The other thread is now holding lockB and waiting for lockA:
            // neither side can make progress.
            synchronized (lockB) {
                System.out.println("never reached");
            }
        }
    }

    private static void runSecond() {
        System.out.println(Thread.currentThread().getName()
                + ": acquiring lockB ...");
        synchronized (lockB) {
            pause();
            System.out.println(Thread.currentThread().getName()
                    + ": holding lockB, waiting for lockA ...");
            synchronized (lockA) {
                System.out.println("never reached");
            }
        }
    }

    // Sleep a short while to widen the window in which both threads hold one
    // lock each, making the deadlock reproducible instead of a race.
    private static void pause() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
