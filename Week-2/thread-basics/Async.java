import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Async {

    // Synchronous vs asynchronous processing for I/O-bound work.
        //
        // Sync: one thread does tasks one after another, idling while each waits
        //       on the slow dependency. Total time = N * latency.
        // Async: virtual threads — each task runs on its own virtual thread, and a
        //       blocking virtual thread does NOT tie up an OS thread, so one carrier
        //       thread keeps many in-flight calls moving. Total time ~= latency.
        //
        // The win only exists for I/O-bound work (waiting on a dependency). For
        // CPU-bound work async gains are minimal, if at all — shown in the third block.

        static final int TASKS = 8;
        static final long IO_LATENCY_MS = 500;   // simulated slow DB/network call

    public static void main(String[] args) throws Exception {
        // Force a single carrier thread so all virtual threads are managed by
        // one OS thread
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");
        System.out.println("I/O-bound work: " + TASKS + " calls, each ~" + IO_LATENCY_MS + "ms latency");
        System.out.println();

        runSyncIo();
        runAsyncIo();
        runCpuBoundContrast();
        System.exit(0);
    }

    // --- Synchronous: one thread, one call at a time -------------------------
    private static void runSyncIo() throws Exception {
        AtomicInteger inFlight = new AtomicInteger();
        AtomicInteger peak = new AtomicInteger();

        long start = System.nanoTime();
        for (int i = 0; i < TASKS; i++) {
            ioCall(inFlight, peak);
        }
        long wallMs = (System.nanoTime() - start) / 1_000_000;

        System.out.println("--- Synchronous (1 thread) ---");
        System.out.println("Wall time:      " + wallMs + " ms");
        System.out.println("Peak in-flight: " + peak.get());
        System.out.println();
    }

    // --- Asynchronous: virtual threads overlap the waits --------------------
    private static void runAsyncIo() throws Exception {
        // Each task runs on its own virtual thread. A virtual thread that
        // blocks (e.g. on the sleep) does NOT block an OS thread — the JVM
        // parks it and frees the carrier to run another. So one carrier thread
        // can keep many in-flight calls moving.
        ExecutorService pool = Executors.newVirtualThreadPerTaskExecutor();
        AtomicInteger inFlight = new AtomicInteger();
        AtomicInteger peak = new AtomicInteger();

        long start = System.nanoTime();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < TASKS; i++) {
            futures.add(CompletableFuture.runAsync(
                    () -> ioCall(inFlight, peak), pool));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long wallMs = (System.nanoTime() - start) / 1_000_000;

        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("--- Asynchronous (virtual threads) ---");
        System.out.println("Wall time:      " + wallMs + " ms");
        System.out.println("Peak in-flight: " + peak.get());
        System.out.println();
    }

    // --- CPU-bound contrast: async should NOT help ---------------------------
    private static void runCpuBoundContrast() throws Exception {
        long work = 200_000_000L;

        long syncStart = System.nanoTime();
        for (int i = 0; i < TASKS; i++) {
            burnCpu(work);
        }
        long syncMs = (System.nanoTime() - syncStart) / 1_000_000;

        ExecutorService pool = Executors.newFixedThreadPool(1); // same 1 thread as sync
        long asyncStart = System.nanoTime();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < TASKS; i++) {
            futures.add(CompletableFuture.runAsync(() -> burnCpu(work), pool));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        long asyncMs = (System.nanoTime() - asyncStart) / 1_000_000;

        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);

        System.out.println("--- CPU-bound contrast (no I/O wait) ---");
        System.out.println("Synchronous:  " + syncMs + " ms");
        System.out.println("Asynchronous: " + asyncMs + " ms");
        System.out.println("(Async gains nothing here - the work is CPU, not wait.)");
        System.out.println();
    }

    // Simulates a slow I/O dependency. Tracks how many calls are in flight at
    // once, which is the "threads stay busy" signal.
    private static void ioCall(AtomicInteger inFlight, AtomicInteger peak) {
        int now = inFlight.incrementAndGet();
        peak.updateAndGet(p -> Math.max(p, now));
        try {
            Thread.sleep(IO_LATENCY_MS); // block on the slow dependency
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        inFlight.decrementAndGet();
    }

    // Pure CPU work: no waiting, so async cannot overlap anything.
    private static void burnCpu(long iterations) {
        long x = 0;
        for (long i = 0; i < iterations; i++) {
            x += i;
        }
        if (x == -1) System.out.println("unreachable");
    }
}
