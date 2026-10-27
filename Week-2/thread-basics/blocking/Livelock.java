package blocking;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Minimal demonstration of a thread livelock.
 *
 * A livelock is the "polite" cousin of a deadlock: the threads are NOT
 * blocked — they are all runnable and actively doing work — yet no thread
 * can make progress. Each one keeps retrying, sees the other is in the way,
 * backs off, and retries again.
 *
 * The classic analogy is two people meeting in a narrow corridor: each steps
 * aside to let the other pass, but they both step to the same side, so they
 * keep blocking each other while both are "trying hard" to get out of the way.
 *
 * Contrast with {@link Deadlock}: there the threads are stuck waiting on
 * locks they can never get. Here the threads are never waiting — they are
 * running flat out, but their mutual politeness guarantees no progress.
 *
 * Each worker PERMANENTLY holds its own lock and keeps retrying to grab the
 * other's. {@code tryLock()} is the non-blocking acquire: it returns false
 * immediately instead of waiting, so these are active busy loops, not blocked
 * threads. If they used plain {@code synchronized} they would DEADLOCK
 * instead: blocked, not busy.
 *
 * Attempts are capped so the demo ends; uncapped the livelock would spin
 * forever.
 *
 * A {@link ReentrantLock} plays the same guard role as a synchronized block
 * but is an explicit object from {@code java.util.concurrent}. It offers what
 * {@code synchronized} cannot: non-blocking {@code tryLock()}, timed lock
 * acquisition, and interruptible waiting. Reentrant means the owning thread
 * may acquire the same lock again without deadlocking on itself. Every
 * {@code lock()} must be paired with an {@code unlock()}, typically in a
 * finally block — there is no automatic release as with {@code synchronized}.
 */
public class Livelock {

    private static final ReentrantLock lockA = new ReentrantLock();
    private static final ReentrantLock lockB = new ReentrantLock();
    private static final int MAX_ATTEMPTS = 10;

    public static void main(String[] args) {
        Thread first = new Thread(() -> workerHolding(lockA, lockB, "thread-first"));
        Thread second = new Thread(() -> workerHolding(lockB, lockA, "thread-second"));

        first.start();
        second.start();

        try {
            first.join();
            second.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("main: both workers gave up after " + MAX_ATTEMPTS + " attempts.");
        System.exit(0);
    }

    private static void workerHolding(ReentrantLock homeLock, ReentrantLock otherLock, String name) {
        homeLock.lock();
        sleep(100);
        try {
            for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
                if (otherLock.tryLock()) {
                    try {
                        System.out.println(name + ": got both locks on attempt " + attempt);
                        return;
                    } finally {
                        otherLock.unlock();
                    }
                }
                // Could not get the other lock. Both workers back off for the
                // same short time, so they stay in step and keep colliding.
                System.out.println(name + ": holding home lock, cannot grab the other (attempt " + attempt + ")");
                sleep(10);
            }
        } finally {
            homeLock.unlock();
        }
        System.out.println(name + ": gave up");
    }

    private static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
