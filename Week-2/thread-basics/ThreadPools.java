import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPools {

    // A thread pool reuses a fixed set of worker threads instead of
    // spawning a new Thread for every task. Spawning threads is expensive;
    // the pool amortizes that cost and limits how many run at once.
    public static void main(String[] args) throws InterruptedException {
        // A pool with 3 worker threads. Tasks queue up and are handed to
        // whichever worker is free.
        ExecutorService pool = Executors.newFixedThreadPool(3);

        // Submit 6 tasks. Only 3 run at a time; the rest wait in the queue.
        for (int i = 1; i <= 6; i++) {
            final int taskId = i;
            pool.submit(() -> doWork(taskId));
        }

        // Stop accepting new tasks and wait for the queued ones to finish.
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("All tasks done.");
        System.exit(0);
    }

    private static void doWork(int taskId) {
        System.out.println("Task " + taskId + " starting on "
                + Thread.currentThread().getName());
        try {
            Thread.sleep(500); // simulate some work
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Task " + taskId + " finished on "
                + Thread.currentThread().getName());
    }
}
