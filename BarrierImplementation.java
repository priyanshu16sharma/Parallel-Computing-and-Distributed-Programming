import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BarrierImplementation {
    // Total work items used for the comparison.
    private static final int TOTAL_TASKS = 100;

    // We will use 4 worker threads for the parallel version.
    private static final int THREAD_COUNT = 4;

    // Simulates one unit of work for a task.
    private static void doWork(int taskId) throws InterruptedException {
        Thread.sleep(20 + (taskId % 10) * 10L);
    }

    // Runs all tasks sequentially and measures elapsed time.
    private static long runLinear() {
        long start = System.nanoTime();

        for (int i = 0; i < TOTAL_TASKS; i++) {
            try {
                doWork(i);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while running linear workload", e);
            }
        }

        return System.nanoTime() - start;
    }

    // Runs the same work in parallel and synchronizes threads using a barrier.
    private static long runParallel() throws InterruptedException {
        // Thread pool for parallel execution.
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        long start = System.nanoTime();

        List<Callable<Void>> tasks = new ArrayList<>();

        // All worker threads wait here until all have reached the barrier.
        CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT, () -> {
            System.out.println("All worker threads reached the barrier.");
        });

        for (int taskId = 0; taskId < TOTAL_TASKS; taskId++) {
            final int currentTask = taskId;
            tasks.add(() -> {
                try {
                    // Each thread performs its assigned work.
                    doWork(currentTask);

                    // After work, all threads wait together before continuing.
                    barrier.await();
                    return null;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while running parallel workload", e);
                } catch (Exception e) {
                    throw new RuntimeException("Barrier failed during parallel execution", e);
                }
            });
        }

        // Executes all tasks using the thread pool.
        executor.invokeAll(tasks);
        executor.shutdown();

        // Return time spent in parallel execution.
        return System.nanoTime() - start;
    }

    public static void main(String[] args) {
        // Measure the linear version first.
        long linearTime = runLinear();

        long parallelTime;
        try {
            // Measure the parallel version next.
            parallelTime = runParallel();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Parallel benchmark interrupted", e);
        }

        // Print both timings for comparison.
        System.out.println("Linear execution time: " + TimeUnit.NANOSECONDS.toMillis(linearTime) + " ms");
        System.out.println("Parallel execution time: " + TimeUnit.NANOSECONDS.toMillis(parallelTime) + " ms");
    }
}