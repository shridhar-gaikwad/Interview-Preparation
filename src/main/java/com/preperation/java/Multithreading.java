package com.preperation.java;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * =====================================================================================
 *                                 MULTITHREADING
 * =====================================================================================
 *
 * WHAT IS IT?
 * -----------
 * Multithreading is the ability to execute multiple threads concurrently within a
 * process (e.g. a browser: one thread for UI, one for downloads, one for video).
 *
 * >>> NOTE ON THIS FILE <<<
 * The Java below is REAL, runnable code. main() runs one demo per topic. Every demo
 * joins its threads / shuts down its pools so the program terminates deterministically.
 * (The classic deadlock is described in the notes but NOT executed - it would hang; the
 *  demo instead shows deadlock AVOIDANCE via ordered locking.)
 *
 * -------------------------------------------------------------------------------------
 * PROCESS vs THREAD
 * -------------------------------------------------------------------------------------
 *   Process              | Thread
 *   -------------------- | --------------------------
 *   Heavyweight          | Lightweight
 *   Separate memory      | Shared memory (within process)
 *   Costly creation      | Fast creation
 *   Independent          | Dependent on its process
 *
 * -------------------------------------------------------------------------------------
 * CREATING THREADS
 * -------------------------------------------------------------------------------------
 *   1. Extend Thread          -> class MyThread extends Thread { public void run(){} }
 *   2. Implement Runnable      -> new Thread(new MyTask()).start();   (PREFERRED)
 *   3. Lambda (Java 8)         -> new Thread(() -> System.out.println("Running")).start();
 *   Runnable is preferred because Java has single inheritance (extend Thread blocks
 *   extending another class).
 *
 * THREAD LIFECYCLE:
 *   NEW -> RUNNABLE -> RUNNING -> (BLOCKED/WAITING via wait()/join()/sleep()) -> TERMINATED
 *   The Thread Scheduler decides who gets the CPU; Java does NOT guarantee order.
 *
 *   join(): makes the current thread WAIT until the target thread finishes (useful when
 *           results/ordering from another thread are required).
 *
 * -------------------------------------------------------------------------------------
 * SYNCHRONIZATION  (mutual exclusion + memory visibility)
 * -------------------------------------------------------------------------------------
 *   1. Synchronized method  -> public synchronized void m() {}      (lock on 'this')
 *   2. Synchronized block   -> synchronized(this){ ... }            (lock only critical
 *                              section -> better performance, PREFERRED)
 *   3. Static synchronized  -> public static synchronized void m(){} (lock on Class object)
 *
 * -------------------------------------------------------------------------------------
 * DEADLOCK
 * -------------------------------------------------------------------------------------
 *   Two+ threads wait on each other forever:
 *      T1 holds Lock A, waits for Lock B ; T2 holds Lock B, waits for Lock A.
 *   Avoidance: always acquire locks in the SAME global order.
 *
 * -------------------------------------------------------------------------------------
 * volatile & ATOMIC
 * -------------------------------------------------------------------------------------
 *   volatile -> VISIBILITY only: latest value read from main memory (no atomicity).
 *   Atomic classes (AtomicInteger/Long/Boolean) -> lock-free thread safety via CAS
 *   (Compare-And-Swap): count.incrementAndGet();
 *
 * -------------------------------------------------------------------------------------
 * wait() / notify() / notifyAll()  (Object class, inter-thread communication)
 * -------------------------------------------------------------------------------------
 *   Must be called inside a synchronized block on the same monitor.
 *   wait()      -> releases the lock, moves thread to WAITING.
 *   notify()    -> wakes ONE waiting thread (it must re-acquire the lock).
 *   notifyAll() -> wakes ALL waiting threads (they compete for the lock).
 *   Always wait() inside a while(condition) loop (guards against spurious wakeups).
 *
 * -------------------------------------------------------------------------------------
 * EXECUTOR FRAMEWORK  (separates task submission from execution via a thread pool)
 * -------------------------------------------------------------------------------------
 *   Interfaces: Executor -> ExecutorService -> ScheduledExecutorService.
 *   Use a pool instead of new Thread():
 *      ExecutorService ex = Executors.newFixedThreadPool(5);
 *      Future<Integer> f = ex.submit(() -> 100);   // Callable -> Future
 *      f.get();                                     // blocks until result
 *      ex.shutdown();
 *   Future = result of an async computation. CompletableFuture (Java 8) = non-blocking
 *   async with composition: supplyAsync(...).thenApply(...).thenCombine(...).
 *
 * -------------------------------------------------------------------------------------
 * CONCURRENT COLLECTIONS
 * -------------------------------------------------------------------------------------
 *   ConcurrentHashMap, CopyOnWriteArrayList, BlockingQueue (used for producer-consumer).
 *
 * -------------------------------------------------------------------------------------
 * QUICK Q&A (INTERVIEW)
 * -------------------------------------------------------------------------------------
 * Q1: Runnable preferred over Thread? -> Java has single inheritance; Runnable keeps the
 *     class free to extend something else and is more flexible.
 * Q2: start() vs run()? -> start() creates a NEW thread (JVM calls run()); run() is just a
 *     normal method call (no new thread).
 * Q3: Does sleep() release the lock? -> No; the thread keeps the monitor while sleeping.
 * Q4: Why prefer synchronized block? -> Locks only the critical section -> better perf.
 * Q5: Intrinsic monitor vs Lock? -> synchronized: auto release, no tryLock/fairness;
 *     ReentrantLock (java.util.concurrent.locks): manual release, tryLock, fairness,
 *     interruptible, multiple conditions.
 * Q6: Race condition? -> Multiple threads modify shared data (e.g. count++) without sync
 *     -> unpredictable results.
 * Q7: volatile vs synchronized? -> volatile: visibility only. synchronized: visibility +
 *     atomicity.
 * Q8: AtomicInteger vs synchronized? -> Atomic usually faster for simple ops (CAS).
 * Q9: sleep() vs wait()? -> sleep() (Thread) keeps lock; wait() (Object) releases lock.
 * Q10: Producer-Consumer? -> Producer adds to a shared buffer, consumer removes; needs
 *     sync to avoid overflow/underflow/races. Modern solution: BlockingQueue.
 * Q11: Why while instead of if around wait()? -> Guards against spurious wakeups /
 *     multiple threads waking; recheck condition before proceeding.
 * Q12: Callable vs Runnable? -> Runnable.run() returns void; Callable.call() returns a
 *     value and may throw checked exceptions.
 * Q13: Is Future.get() blocking? -> Yes, until the result is ready.
 * Q14: execute() vs submit()? -> execute(Runnable) returns nothing; submit(Runnable/
 *     Callable) returns a Future.
 * Q15: shutdown() vs shutdownNow()? -> shutdown() stops accepting new tasks but finishes
 *     existing ones (safer); shutdownNow() tries to stop everything immediately.
 *
 * ONE-LINER SUMMARY:
 * Multithreading runs tasks concurrently over shared memory; correctness needs
 * synchronization (locks/volatile/atomics/wait-notify), and the Executor framework +
 * BlockingQueue give safe, reusable, higher-level concurrency.
 *
 * -------------------------------------------------------------------------------------
 * EXAMPLE BELOW: each demo* method maps to a topic in the notes above.
 * =====================================================================================
 */
public class Multithreading {

    public static void main(String[] args) throws Exception {
        demoThreadCreation();
        demoJoin();
        demoSynchronization();
        demoVolatile();
        demoAtomic();
        demoWaitNotify();
        demoExecutorFramework();
        demoCompletableFuture();
        demoBlockingQueue();
        demoDeadlockAvoidance();
    }

    /** Three ways to create a thread. */
    private static void demoThreadCreation() throws InterruptedException {
        System.out.println("\n===== CREATING THREADS =====");

        Thread t1 = new MyThread();                                  // extend Thread
        Thread t2 = new Thread(new MyTask());                        // implement Runnable
        Thread t3 = new Thread(() -> System.out.println("lambda thread running")); // lambda

        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();
    }

    static class MyThread extends Thread {
        @Override public void run() { System.out.println("extends Thread running"); }
    }

    static class MyTask implements Runnable {
        @Override public void run() { System.out.println("implements Runnable running"); }
    }

    /** join(): main waits for a worker to finish before continuing. */
    private static void demoJoin() throws InterruptedException {
        System.out.println("\n===== join() =====");

        Thread worker = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("  worker count: " + i);
            }
        });
        worker.start();
        worker.join();                                              // wait for worker
        System.out.println("worker finished, main continues");
    }

    /** synchronized guarantees a correct final count with concurrent increments. */
    private static void demoSynchronization() throws InterruptedException {
        System.out.println("\n===== SYNCHRONIZATION =====");

        Counter counter = new Counter();
        Runnable job = () -> { for (int i = 0; i < 1000; i++) counter.increment(); };
        Thread a = new Thread(job);
        Thread b = new Thread(job);
        a.start(); b.start();
        a.join(); b.join();
        System.out.println("synchronized count (expect 2000): " + counter.get());
    }

    static class Counter {
        private int count = 0;
        synchronized void increment() { count++; }                  // lock on 'this'
        int get() { return count; }
    }

    /** volatile flag makes the worker see the updated stop signal promptly. */
    private static void demoVolatile() throws InterruptedException {
        System.out.println("\n===== volatile =====");

        Flag flag = new Flag();
        Thread worker = new Thread(() -> {
            long spins = 0;
            while (flag.running) { spins++; }                       // sees latest value
            System.out.println("  worker stopped after " + (spins > 0 ? "some" : "no") + " spins");
        });
        worker.start();
        Thread.sleep(50);
        flag.running = false;                                       // visible immediately
        worker.join();
    }

    static class Flag {
        volatile boolean running = true;
    }

    /** AtomicInteger: thread-safe increments without synchronized (CAS). */
    private static void demoAtomic() throws InterruptedException {
        System.out.println("\n===== ATOMIC (CAS) =====");

        AtomicInteger count = new AtomicInteger();
        Runnable job = () -> { for (int i = 0; i < 1000; i++) count.incrementAndGet(); };
        Thread a = new Thread(job);
        Thread b = new Thread(job);
        a.start(); b.start();
        a.join(); b.join();
        System.out.println("atomic count (expect 2000): " + count.get());
    }

    /** wait()/notify() producer-consumer over a one-slot buffer. */
    private static void demoWaitNotify() throws InterruptedException {
        System.out.println("\n===== wait() / notify() =====");

        SharedBuffer buffer = new SharedBuffer();
        Thread producer = new Thread(() -> {
            try { for (int i = 1; i <= 3; i++) buffer.produce(i); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        Thread consumer = new Thread(() -> {
            try { for (int i = 1; i <= 3; i++) buffer.consume(); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        producer.start(); consumer.start();
        producer.join(); consumer.join();
    }

    static class SharedBuffer {
        private int data;
        private boolean available = false;

        synchronized void produce(int value) throws InterruptedException {
            while (available) wait();                              // wait if full
            data = value;
            available = true;
            System.out.println("  produced: " + value);
            notify();                                             // wake consumer
        }

        synchronized int consume() throws InterruptedException {
            while (!available) wait();                            // wait if empty
            available = false;
            System.out.println("  consumed: " + data);
            notify();                                            // wake producer
            return data;
        }
    }

    /** ExecutorService thread pool + Callable + Future. */
    private static void demoExecutorFramework() throws ExecutionException, InterruptedException {
        System.out.println("\n===== EXECUTOR FRAMEWORK =====");

        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.execute(() -> System.out.println("  execute(): fire-and-forget task"));

        Future<Integer> future = executor.submit(() -> 10 + 20);  // Callable -> Future
        System.out.println("  submit() Future.get() = " + future.get());  // blocks

        executor.shutdown();                                      // finish existing tasks
        executor.awaitTermination(2, TimeUnit.SECONDS);
    }

    /** CompletableFuture: non-blocking async composition. */
    private static void demoCompletableFuture() {
        System.out.println("\n===== COMPLETABLE FUTURE =====");

        CompletableFuture<String> future = CompletableFuture
                .supplyAsync(() -> "hello")
                .thenApply(String::toUpperCase);                  // transform

        CompletableFuture<Integer> combined = CompletableFuture.supplyAsync(() -> 2)
                .thenCombine(CompletableFuture.supplyAsync(() -> 3), Integer::sum); // 5

        System.out.println("  thenApply   : " + future.join());
        System.out.println("  thenCombine : " + combined.join());
    }

    /** BlockingQueue producer-consumer (thread-safe, no manual sync). Poison pill stops it. */
    private static void demoBlockingQueue() throws InterruptedException {
        System.out.println("\n===== BLOCKING QUEUE (producer-consumer) =====");

        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);
        final int POISON = -1;

        Thread producer = new Thread(() -> {
            try {
                for (int v = 1; v <= 3; v++) {
                    queue.put(v);                                 // blocks if full
                    System.out.println("  produced: " + v);
                }
                queue.put(POISON);                                // signal end
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        Thread consumer = new Thread(() -> {
            try {
                int v;
                while ((v = queue.take()) != POISON) {            // blocks if empty
                    System.out.println("  consumed: " + v);
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });

        producer.start(); consumer.start();
        producer.join(); consumer.join();
    }

    /** Deadlock AVOIDANCE: both threads take locks in the same order (lock1 then lock2). */
    private static void demoDeadlockAvoidance() throws InterruptedException {
        System.out.println("\n===== DEADLOCK AVOIDANCE (ordered locking) =====");

        final Object lock1 = new Object();
        final Object lock2 = new Object();

        Runnable ordered = () -> {
            synchronized (lock1) {
                synchronized (lock2) {
                    System.out.println("  " + Thread.currentThread().getName()
                            + " acquired lock1 then lock2");
                }
            }
        };
        Thread t1 = new Thread(ordered, "T1");
        Thread t2 = new Thread(ordered, "T2");
        t1.start(); t2.start();
        t1.join(); t2.join();
        System.out.println("no deadlock: both finished");
    }
}
