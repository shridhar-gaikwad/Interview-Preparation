package com.preperation.java;

import java.io.IOException;
import java.lang.ref.WeakReference;

/*
 * =====================================================================================
 *                                   JAVA BASICS
 *              (String types, Garbage Collection, JVM memory, Class loading)
 * =====================================================================================
 *
 * >>> NOTE ON THIS FILE <<<
 * The Java below is REAL, runnable code. main() runs one demo per topic so you can SEE
 * each concept from the notes in action (String immutability vs builders, GC eligibility,
 * class loaders, static-init order, throw/throws).
 *
 * -------------------------------------------------------------------------------------
 * STRING vs STRINGBUILDER vs STRINGBUFFER
 * -------------------------------------------------------------------------------------
 *   Feature       | String              | StringBuilder            | StringBuffer
 *   ------------- | ------------------- | ------------------------ | ---------------------
 *   Mutable       | No (immutable)      | Yes                      | Yes
 *   Thread-safe   | Yes (immutable)     | No                       | Yes (synchronized)
 *   Performance   | Slow for edits      | Fastest                  | Slower than Builder
 *   Best use      | Fixed text          | Single-threaded edits    | Multi-threaded edits
 *   Method        | + , concat()        | append()                 | append()
 *
 * intern() returns the reference from the String pool. So "Hello" == new String("Hello").intern() //true.
 * It forces a heap string to point to (or be added to) the pool.
 * -------------------------------------------------------------------------------------
 * GARBAGE COLLECTION (GC)
 * -------------------------------------------------------------------------------------
 * Automatic memory management that removes UNREACHABLE objects from HEAP memory.
 *   Benefits: prevents memory leaks, automatic management, improves stability, no manual deallocation.
 *   "Garbage" = an object no longer reachable from any live reference (if reachable from a thread,
 *     static var or local var it's LIVE, otherwise it's garbage)
 *      Employee e = new Employee();
 *      e = null;   // object now eligible for GC
 *   GC works ONLY on Heap (not Stack).
 *
 *   JVM MEMORY STRUCTURE:
 *      JVM Memory
 *       +-- Heap
 *       |    +-- Young Generation
 *       |    |     +-- Eden        (new objects; when full -> MINOR GC)
 *       |    |     +-- S0 Survivor (objects surviving minor GC)
 *       |    |     +-- S1 Survivor
 *       |    +-- Old Generation    (long-lived: singletons, caches, Spring beans;
 *       |                           when full -> MAJOR / FULL GC)
 *       +-- Stack
 *       +-- Metaspace              (class & method metadata)
 *
 *   COMMON COLLECTORS:
 *      Serial GC      -XX:+UseSerialGC     single thread, small apps
 *      Parallel GC    -XX:+UseParallelGC   multi-thread, good throughput
 *      G1 GC          -XX:+UseG1GC         default & most used (Spring Boot prod)
 *      ZGC            -XX:+UseZGC          ultra-low latency, large systems
 *      Shenandoah     -XX:+UseShenandoahGC low-pause collector
 *
 *   TUNING:  -Xms512m (init heap)  -Xmx2g (max heap)  -Xlog:gc* (GC logs, Java 9+)
 *            -XX:MaxGCPauseMillis=200 (G1 pause target)
 *            -XX:+HeapDumpOnOutOfMemoryError  -XX:HeapDumpPath=/logs/dumps
 *   Prod example: java -Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200
 *                      -XX:+HeapDumpOnOutOfMemoryError -Xlog:gc* -jar app.jar
 *
 * -------------------------------------------------------------------------------------
 * JDK vs JRE vs JVM
 * -------------------------------------------------------------------------------------
 *   JVM = executes bytecode (loads classes, runs bytecode, memory mgmt, GC).
 *   JRE = JVM + libraries  (to RUN Java apps).
 *   JDK = JRE + dev tools  (to DEVELOP Java apps, e.g. javac).
 *      JDK -> JRE -> JVM  (each contains the next).
 *
 *   CODE EXECUTION FLOW:
 *      Test.java -> [javac] -> Test.class (bytecode) -> Class Loader -> JVM Memory
 *                -> Execution Engine -> Output
 *
 * -------------------------------------------------------------------------------------
 * CLASS LOADERS
 * -------------------------------------------------------------------------------------
 *   1. Bootstrap  -> core JDK (java.lang, java.util, java.io)  [shown as null loader]
 *   2. Extension/Platform -> JDK extensions
 *   3. Application -> classes from the classpath (your project's target/classes) /
 *                      loads your application code and third-party JARs.
 *
 *   CLASS LOADING PROCESS (3 phases):
 *      1. LOADING        read .class bytes, create Class object, store metadata in
 *                        Method Area.
 *      2. LINKING
 *           a. Verification -> bytecode valid & safe? (else VerifyError)
 *           b. Preparation  -> allocate static vars with DEFAULT values (int -> 0)
 *           c. Resolution   -> replace symbolic refs with actual memory refs
 *      3. INITIALIZATION -> assign REAL values to static vars, run static blocks.
 *
 * -------------------------------------------------------------------------------------
 * JVM MEMORY AREAS
 * -------------------------------------------------------------------------------------
 *   Heap        -> objects + instance variables (shared by threads, GC applies)
 *   Stack       -> method calls, local variables, references (per-thread, no GC)
 *   Method Area -> class metadata, static variables, method info.
 *
 * -------------------------------------------------------------------------------------
 * QUICK Q&A (INTERVIEW)
 * -------------------------------------------------------------------------------------
 * Q1: Why is String immutable? -> Security, thread safety, String pool optimization,
 *     reliable HashMap keys.
 * Q2: How does GC find garbage? -> Reachability analysis: if reachable from a thread,
 *     static var or local var it's LIVE, otherwise it's garbage.
 * Q3: finalize()? -> Object method called by GC before removal; Was used to close files / DB connections.
                    deprecated since Java 9 (unpredictable).
 * Q4: System.gc()? -> Only REQUESTS a GC; JVM may ignore it.
 * Q5: OutOfMemoryError? -> Thrown when JVM cannot allocate memory (heap exhausted).
 * Q6: Memory leak? -> Objects kept referenced unnecessarily (e.g. a static List that is never cleared)
       so heap keeps growing.
 * Q7: throw vs throws? -> throws DECLARES the exception on the method; throw actually
 *     THROWS an instance.
 * Q8: Heap vs Stack? -> Heap: objects, shared, large, GC, slower. Stack: locals,
 *     per-thread, small, no GC, faster.
 * Q9: Metaspace? -> Stores class & method metadata (replaced PermGen in Java 8).
 *
 * ONE-LINER SUMMARY:
 * The JVM loads/links/initializes classes, runs bytecode, and auto-manages heap memory.
 * via GC (Young/Old generations, G1 by default); Strings are immutable while
 * StringBuilder/StringBuffer are mutable (Buffer being the thread-safe one).
 *
 * -------------------------------------------------------------------------------------
 * EXAMPLE BELOW: each demo* method maps to a section of the notes above.
 * =====================================================================================
 */
public class JavaBasics {

    public static void main(String[] args) throws Exception {
        demoStringTypes();
        demoStringImmutability();
        demoGarbageCollection();
        demoClassLoaders();
        demoStaticInitialization();
        demoThrowVsThrows();
    }

    /** String (immutable) vs StringBuilder / StringBuffer (mutable). */
    private static void demoStringTypes() {
        System.out.println("\n===== STRING vs STRINGBUILDER vs STRINGBUFFER =====");

        String s = "Java";
        s.concat("8");                          // result discarded: String is immutable
        System.out.println("String after concat (unchanged): " + s);

        StringBuilder sb = new StringBuilder("Java");
        sb.append("8");                          // mutates the SAME object
        System.out.println("StringBuilder after append     : " + sb);

        StringBuffer sbf = new StringBuffer("Java");  // like StringBuilder but synchronized
        sbf.append("8");
        System.out.println("StringBuffer (thread-safe)     : " + sbf);
    }

    /** Immutability + String pool: literals are interned so "==" is true. */
    private static void demoStringImmutability() {
        System.out.println("\n===== STRING IMMUTABILITY & POOL =====");

        String a = "hello";
        String b = "hello";                      // same pooled object
        String c = new String("hello");          // new heap object
        System.out.println("a == b (pool)      : " + (a == b));      // true
        System.out.println("a == c (new)       : " + (a == c));      // false
        System.out.println("a.equals(c)        : " + a.equals(c));   // true
    }

    /** Reachability: nulling the only reference makes an object eligible for GC. */
    private static void demoGarbageCollection() {
        System.out.println("\n===== GARBAGE COLLECTION (reachability) =====");

        Object obj = new Object();
        WeakReference<Object> ref = new WeakReference<>(obj);
        System.out.println("Before: reachable? " + (ref.get() != null));

        obj = null;                              // no strong reference remains
        System.gc();                             // REQUEST (not guaranteed) a collection
        try { Thread.sleep(50); } catch (InterruptedException ignored) { }

        System.out.println("After null + gc(): still alive? " + (ref.get() != null)
                + " (usually collected)");
    }

    /** Class loader hierarchy: Application -> Platform -> Bootstrap (shown as null). */
    private static void demoClassLoaders() {
        System.out.println("\n===== CLASS LOADERS =====");

        ClassLoader appLoader = JavaBasics.class.getClassLoader();
        System.out.println("JavaBasics loader   : " + appLoader);
        System.out.println("  -> parent         : " + appLoader.getParent());
        System.out.println("String.class loader : " + String.class.getClassLoader()
                + " (bootstrap)");
    }

    /** Static block + static field run during class INITIALIZATION (once, on first use). */
    static class Config {
        static int count = 10;                   // real value assigned at init
        static {
            System.out.println("Static block executed during initialization.");
        }
    }

    private static void demoStaticInitialization() {
        System.out.println("\n===== CLASS INITIALIZATION (static) =====");
        System.out.println("Touching Config.count triggers init...");
        System.out.println("Config.count = " + Config.count);   // forces class init
    }

    /** throws DECLARES; throw actually THROWS. */
    private static void demoThrowVsThrows() {
        System.out.println("\n===== throw vs throws =====");
        try {
            readFile(null);
        } catch (IOException e) {
            System.out.println("Caught: " + e.getMessage());
        }
    }

    private static void readFile(String path) throws IOException {   // declares
        if (path == null) {
            throw new IOException("File not found");                 // throws
        }
    }
}
