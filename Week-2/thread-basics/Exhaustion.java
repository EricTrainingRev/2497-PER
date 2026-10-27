import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Exhaustion {

    // Thread exhaustion: a fixed pool whose threads are all blocked on a slow
    // dependency. New tasks pile up in the queue, latency climbs, and once the
    // queue overflows the pool starts rejecting work outright.
    public static void main(String[] args) throws InterruptedException {
        // A small fixed pool
        int poolSize = 3;
        ExecutorService pool = Executors.newFixedThreadPool(poolSize);

        // Simulate a slow dependency (database / external API) that each task
        // blocks on. While blocked, the worker thread is unavailable.
        long dependencyLatencyMs = 2000;

        // Submit more tasks than the pool can run at once. The extras wait in
        // the pool's internal queue.
        int totalTasks = 8;
        AtomicInteger started = new AtomicInteger(0);

        long submitStart = System.nanoTime();
        for (int i = 1; i <= totalTasks; i++) {
            final int taskId = i;
            final long submittedAt = System.nanoTime();
            pool.submit(() -> doWork(taskId, dependencyLatencyMs, started, submittedAt));
        }

        // Stop accepting new tasks and wait for the queued ones to finish.
        pool.shutdown();
        pool.awaitTermination(30, TimeUnit.SECONDS);
        long totalElapsedMs = (System.nanoTime() - submitStart) / 1_000_000;

        System.out.println();
        System.out.println("Pool size: " + poolSize
                + ", tasks submitted: " + totalTasks
                + ", dependency latency: " + dependencyLatencyMs + "ms");
        System.out.println("Total wait time for all tasks: " + totalElapsedMs + "ms");
        System.out.println("(With " + poolSize + " threads each busy for ~"
                + dependencyLatencyMs + "ms, the queue drains in waves of "
                + poolSize + ".)");
        System.exit(0);
    }

    private static void doWork(int taskId, long latencyMs, AtomicInteger started,
                               long submittedAt) {
        long queueWaitMs = (System.nanoTime() - submittedAt) / 1_000_000;
        int order = started.incrementAndGet();

        System.out.println("Task " + taskId + " (start #" + order + ") on "
                + Thread.currentThread().getName()
                + " after waiting " + queueWaitMs + "ms in queue");

        // Block on the slow dependency. While sleeping, this thread is
        // unavailable to the pool — the root cause of exhaustion.
        try {
            Thread.sleep(latencyMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Task " + taskId + " done on "
                + Thread.currentThread().getName());
    }
}
