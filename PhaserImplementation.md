# Phaser vs CyclicBarrier Timing Comparison

This project demonstrates how to compare two synchronization mechanisms in Java:

- `CyclicBarrier`
- `Phaser`

The program measures the execution time of parallel task execution when using each mechanism.

## Objective

The main goal is to observe how different synchronization tools affect performance in a multi-threaded program.

A barrier is used to coordinate threads so that they wait until all participating threads arrive at the same synchronization point before continuing.

A `Phaser` is a more flexible version of a barrier that supports dynamic registration and deregistration of threads.

## Program Structure

The program contains the following major parts:

1. `doWork(int taskId)`
   - Simulates a task by sleeping for a random amount of time.
   - Prints a message when the task is completed.

2. `runParralelWithBarrier()`
   - Creates a fixed thread pool with 4 threads.
   - Submits 100 tasks.
   - Uses a `CyclicBarrier` to stop threads until all reach the same point.
   - Measures execution time using `System.nanoTime()`.

3. `runParallelWithPhaser()`
   - Uses the same thread pool and workload.
   - Replaces the barrier with a `Phaser`.
   - Demonstrates how `Phaser` can coordinate multiple thread phases.
   - Measures the time taken for this version as well.

4. `main()`
   - Executes both versions.
   - Prints timing for each synchronization method.

## Key Difference Between Barrier and Phaser

### CyclicBarrier
- A fixed number of parties must arrive before the barrier is released.
- It is useful when the number of threads is known in advance.
- It is easier to use for simple synchronization points.

### Phaser
- More flexible than a barrier.
- Threads can register or deregister dynamically.
- It supports multiple phases of synchronization.
- It is more suitable when the number of participants changes during execution.

## Example Output

A typical output may look like this:

```text
Time taken with CyclicBarrier: 123456789 ns
Time taken with Phaser: 98765432 ns
```

These values vary depending on system speed and load, but the comparison helps understand which synchronization mechanism performs better for the given workload.

## Conclusion

This example shows how synchronization affects parallel performance. The two implementations use the same number of tasks and threads, but they differ in coordination style.

In many cases:

- `CyclicBarrier` is simpler and easier to understand.
- `Phaser` is more powerful and scalable when the number of participants changes.

This program is useful for understanding the trade-off between simplicity and flexibility in concurrent Java programming.
