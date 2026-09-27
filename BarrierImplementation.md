# Barrier and Parallel Performance Comparison

This project demonstrates the difference between a linear execution flow and a parallel execution flow using Java threads and a `CyclicBarrier`.

## Goal

The main objective is to compare:

- Linear execution time: tasks run one after another
- Parallel execution time: tasks run concurrently across multiple threads

The comparison helps understand how synchronization and parallelism affect performance.

## Program Overview

The program contains the following parts:

1. `doWork(int taskId)`
   - Simulates work by sleeping for a small amount of time.

2. `runLinear()`
   - Executes all tasks sequentially.
   - Measures the total time using `System.nanoTime()`.

3. `runParallel()`
   - Creates a fixed thread pool.
   - Submits tasks to multiple threads.
   - Uses a `CyclicBarrier` to synchronize the threads before continuing.
   - Measures the total elapsed time.

4. `main()`
   - Runs both versions.
   - Prints the timing results in milliseconds.

## Why the Barrier Matters

A barrier forces multiple threads to wait until all of them arrive at the same point before continuing. This is useful when threads must complete a phase before moving to the next stage of work.

However, the barrier count must match the actual number of waiting threads. If the barrier expects more parties than are available, the threads can become stuck waiting forever.

## Example Output

A typical output may look like this:

```text
All worker threads reached the barrier.
All worker threads reached the barrier.
...
Linear execution time: 6513 ms
Parallel execution time: 2208 ms
```

This shows that, for this example, the parallel version completed faster than the linear version.

## Conclusion

This example illustrates that parallelism can improve performance for tasks that can be executed independently, as long as proper synchronization is used.

It also highlights an important concept in concurrent programming:

- synchronization adds coordination overhead
- the number of threads and barrier size must be chosen carefully
- parallel code is not always faster if the workload is too small or the coordination overhead dominates
