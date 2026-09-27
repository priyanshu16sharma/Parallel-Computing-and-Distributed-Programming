import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Phaser;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhaserImplementation {
    private static final int TOTAL_TASKS = 100;
    private static final int THREAD_COUNT = 4;

    private static void doWork(int taskId) throws InterruptedException {
        Thread.sleep((long) (Math.random() * 100));
        System.out.println("Task " + taskId + " completed.");
    }

    private static long runParralelWithBarrier() {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        long start = System.nanoTime();
        List<Callable<Void>> tasks = new ArrayList<>();
        CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT, () -> {
            System.out.println("All worker threads reached the barrier.");
        });

        for (int taskId = 0; taskId < TOTAL_TASKS; taskId++) {
            final int currentTask = taskId;
            tasks.add(() -> {
                try {
                    doWork(currentTask);
                    Thread.sleep(10);
                    barrier.await();
                    return null;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while running parallel workload", e);
                }
            });
        }

        try {
            executor.invokeAll(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while invoking tasks", e);
        } finally {
            executor.shutdown();
        }
        barrier.reset(); // Reset the barrier

        return System.nanoTime() - start;
    }

    private static long runParallelWithPhaser() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        long start = System.nanoTime();

        List<Callable<Void>> tasks = new ArrayList<>();

        Phaser phaser = new Phaser(THREAD_COUNT);

        for (int taskId = 0; taskId < TOTAL_TASKS; taskId++) {
            final int currentTask = taskId;
            tasks.add(() -> {
                try {
                    doWork(currentTask);
                    int phase = phaser.arrive();
                    Thread.sleep(10);
                    phaser.awaitAdvance(phase);
                    return null;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while running parallel workload", e);
                }
            });
        }

        executor.invokeAll(tasks);
        phaser.arriveAndDeregister(); // Deregister the main thread

        return System.nanoTime() - start;
    }

    public static void main(String[] args) throws InterruptedException {
        long barrierTime = runParralelWithBarrier();
        System.out.println("Time taken with CyclicBarrier: " + barrierTime + " ns");

        long phaserTime = runParallelWithPhaser();
        System.out.println("Time taken with Phaser: " + phaserTime + " ns");
    }
}
