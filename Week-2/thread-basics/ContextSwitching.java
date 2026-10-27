import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ContextSwitching {

    // Demonstrates context-switch overhead: the same total CPU work done by
    // one thread vs. many threads competing for limited cores. The many-thread
    // case burns more wall time and more context switches for identical work,
    // because the OS keeps saving/restoring thread state instead of doing work.

    // Tune so the many-thread run lasts a few seconds.
    static final long WORK_PER_THREAD = 400_000_000L;

    public static void main(String[] args) throws Exception {
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Available cores: " + cores);
        System.out.println();

        runScenario("1 thread", 1);
        runScenario("200 threads", 200);
        System.exit(0);
    }

    private static void runScenario(String label, int threadCount) throws Exception {
        CountDownLatch start = new CountDownLatch(1);       // release all workers together
        CountDownLatch reachedGate = new CountDownLatch(threadCount); // all done working but alive
        CountDownLatch hold = new CountDownLatch(1);        // keep workers alive until sampled
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(() -> {
                try {
                    start.await();      // all ready, then released at once
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                burnCpu(WORK_PER_THREAD);
                reachedGate.countDown(); // signal: work done, still alive
                try {
                    hold.await();        // park so main can snapshot counters first
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            threads.add(t);
            t.start();
        }

        // Snapshot counters before the run (workers not started yet).
        long[] before = contextSwitches();

        long wallStart = System.nanoTime();
        start.countDown();               // maximum contention from the first tick
        reachedGate.await();             // all workers finished work, still alive
        long wallMs = (System.nanoTime() - wallStart) / 1_000_000;

        // Sample while every worker is still parked
        long[] after = contextSwitches();
        hold.countDown();                // let workers exit

        long voluntary = after[0] - before[0];
        long nonVoluntary = after[1] - before[1];
        long total = voluntary + nonVoluntary;
        // Rate normalizes for run length.
        long perSecond = total * 1000 / Math.max(wallMs, 1);

        System.out.println("--- " + label + " ---");
        System.out.println("Wall time:        " + wallMs + " ms");
        System.out.println("Context switches: " + total
                + "  (voluntary=" + voluntary + ", nonvoluntary=" + nonVoluntary + ")");
        System.out.println("Switch rate:      " + perSecond + " / sec");
        System.out.println();
    }

    // Pure CPU work: no I/O, no blocking, so threads stay runnable and contend
    // for cores — the condition that drives context switching.
    private static void burnCpu(long iterations) {
        long x = 0;
        for (long i = 0; i < iterations; i++) {
            x += i; // cheap op; the loop itself is the load
        }
        if (x == -1) System.out.println("unreachable"); // keep the compiler honest
    }

    // Reads this process's context-switch counters, summed across ALL live
    // threads, from /proc/self/task/*/status. Returns {voluntary, nonvoluntary}.
    // Works on Linux/WSL. Must be called while the worker threads are still
    // alive — a thread that has exited no longer appears here, so sampling too
    // late silently drops its switches.
    private static long[] contextSwitches() throws Exception {
        long voluntary = 0, nonVoluntary = 0;
        java.nio.file.Path taskDir = java.nio.file.Path.of("/proc/self/task");
        try (var stream = java.nio.file.Files.list(taskDir)) {
            for (java.nio.file.Path tid : (Iterable<java.nio.file.Path>) stream::iterator) {
                try {
                    for (String line : java.nio.file.Files.readAllLines(tid.resolve("status"))) {
                        if (line.startsWith("voluntary_ctxt_switches:")) {
                            voluntary += Long.parseLong(line.split("\\s+")[1]);
                        } else if (line.startsWith("nonvoluntary_ctxt_switches:")) {
                            nonVoluntary += Long.parseLong(line.split("\\s+")[1]);
                        }
                    }
                } catch (java.nio.file.NoSuchFileException e) {
                    // Thread raced between listing and reading; skip it.
                }
            }
        }
        return new long[]{voluntary, nonVoluntary};
    }
}
