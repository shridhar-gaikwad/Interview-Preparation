package com.preperation.java;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * =====================================================================================
 *                                 JAVA 8 FEATURES
 * =====================================================================================
 *
 * OVERVIEW
 * --------
 *   Lambda Expressions, Functional Interfaces, Streams API, Optional, Method References,
 *   Default & Static methods in interfaces, CompletableFuture, Collectors, Parallel
 *   Streams, new Date & Time API.
 *
 * >>> NOTE ON THIS FILE <<<
 * The Java below is REAL, runnable code. main() runs one demo per feature so you can SEE
 * each concept from the notes in action.
 *
 * -------------------------------------------------------------------------------------
 * 1) LAMBDA EXPRESSIONS
 * -------------------------------------------------------------------------------------
 * An anonymous function that implements a functional interface: (params) -> { body }
 *      Runnable r = () -> System.out.println("Running");
 *   Why: less boilerplate, functional programming, readability, works with Streams.
 *
 * -------------------------------------------------------------------------------------
 * 2) FUNCTIONAL INTERFACES  (exactly ONE abstract method)
 * -------------------------------------------------------------------------------------
 *      @FunctionalInterface interface Calculator { int add(int a, int b); }
 *   Built-in:
 *      Predicate<T>      test(T)   -> boolean       (condition)
 *      Function<T,R>     apply(T)  -> R             (transform)
 *      Consumer<T>       accept(T) -> void          (consume)
 *      Supplier<T>       get()     -> T             (produce)
 *      BiFunction<T,U,R> apply(T,U)-> R             (two inputs)
 *   Only ONE abstract method so the compiler knows which method the lambda implements.
 *
 * -------------------------------------------------------------------------------------
 * 3) METHOD REFERENCES  (shorthand for a lambda)
 * -------------------------------------------------------------------------------------
 *      Static      -> Integer::parseInt
 *      Instance    -> String::toUpperCase
 *      Constructor -> ArrayList::new
 *
 * -------------------------------------------------------------------------------------
 * 4) STREAMS API
 * -------------------------------------------------------------------------------------
 * Stream API was introduced in Java 8 to process collections in a functional and declarative manner.
 * It supports operations like filter, map, sort, reduce, and collect. Streams do not store data;
 * they process data from a source such as a collection. Stream operations are categorized into
 * intermediate operations like filter and map, and terminal operations like collect and forEach.
 * Stream API also supports parallel processing through parallel streams, making code more readable and
 * efficient.
 * Flow:    Source -> Intermediate ops (lazy) -> Terminal op (triggers execution)
 *
 *   Intermediate (return Stream): filter, map, flatMap, distinct, sorted, peek, limit, skip
 *   Terminal (produce result):    collect, count, forEach, reduce, findFirst, findAny,
 *                                 anyMatch, allMatch
 *      list.stream().filter(x -> x > 2).map(x -> x * 2).collect(Collectors.toList());
 *
 *   reduce (aggregation):  stream.reduce(0, Integer::sum)
 *   Collectors:  toList(), groupingBy(...), counting(), averagingDouble(...), joining(",")
 *
 * -------------------------------------------------------------------------------------
 * 5) OPTIONAL  (avoid NullPointerException)
 * -------------------------------------------------------------------------------------
 *      Optional.of(value)          // value must be non-null
 *      Optional.ofNullable(value)  // value may be null
 *      opt.ifPresent(System.out::println)
 *      opt.orElse("Default")       // eager default
 *      opt.orElseGet(() -> "Default") // lazy default (supplier)
 *
 * -------------------------------------------------------------------------------------
 * QUICK Q&A (INTERVIEW)
 * -------------------------------------------------------------------------------------
 * Q1: Is a lambda a function? -> No; it's an OBJECT implementing a functional interface
 *     (Java has no standalone functions).
 * Q2: Is a method reference faster than a lambda? -> No; it's mostly syntax sugar.
 * Q3: map vs flatMap? -> map: one output per input; flatMap: flattens nested structures
 *     ([[1,2],[3,4]] -> [1,2,3,4]).
 * Q4: filter vs map? -> filter SELECTS records (predicate); map TRANSFORMS records.
 * Q5: Lazy evaluation? -> Intermediate ops run only when a terminal op is invoked.
 * Q6: Why are streams better than loops? -> Cleaner, functional, parallelizable, less
 *     boilerplate.
 * Q7: Can a stream be reused? -> No; it's single-use. After a terminal op it's consumed;
 *     reuse throws IllegalStateException ("stream has already been operated upon").
 * Q8: Why only one abstract method in a functional interface? -> So the lambda has an
 *     unambiguous target method to implement.
 *
 * ONE-LINER SUMMARY:
 * Java 8 brought functional programming to Java: lambdas + functional interfaces power a
 * lazy, composable Streams API and Optional, cutting boilerplate dramatically.
 *
 * -------------------------------------------------------------------------------------
 * EXAMPLE BELOW: each demo* method maps to a feature in the notes above.
 * =====================================================================================
 */
public class Java8Features {

    /** A functional interface: exactly one abstract method. */
    @FunctionalInterface
    interface Calculator {
        int add(int a, int b);
    }

    /** Small domain type for stream grouping / salary demos. */
    record Employee(int id, String name, String department, double salary) { }

    public static void main(String[] args) {
        demoLambda();
        demoFunctionalInterfaces();
        demoMethodReferences();
        demoStreams();
        demoCollectors();
        demoOptional();
        demoMapVsFlatMap();
        demoLazyEvaluation();
        demoFindDuplicates();
        demoFirstNonRepeatedChar();
        demoSecondHighestSalary();
    }

    /** Lambda implements a functional interface inline. */
    private static void demoLambda() {
        System.out.println("\n===== LAMBDA =====");

        Runnable r = () -> System.out.println("Runnable running via lambda");
        r.run();

        Calculator add = (a, b) -> a + b;
        System.out.println("Calculator add(2,3) = " + add.add(2, 3));
    }

    /** The five common built-in functional interfaces. */
    private static void demoFunctionalInterfaces() {
        System.out.println("\n===== FUNCTIONAL INTERFACES =====");

        Predicate<Integer> greaterThan10 = x -> x > 10;
        System.out.println("Predicate 15>10       : " + greaterThan10.test(15));

        Function<String, Integer> length = String::length;
        System.out.println("Function length(Java) : " + length.apply("Java"));

        Consumer<String> printer = System.out::println;
        printer.accept("Consumer prints this");

        Supplier<String> supplier = () -> "Java";
        System.out.println("Supplier get()        : " + supplier.get());

        BiFunction<String, String, String> join = (first, last) -> first + "_" + last;
        System.out.println("BiFunction apply(S,G) : " + join.apply("S", "G"));
    }

    /** Static, instance and constructor method references. */
    private static void demoMethodReferences() {
        System.out.println("\n===== METHOD REFERENCES =====");

        Function<String, Integer> parse = Integer::parseInt;         // static
        System.out.println("Integer::parseInt(\"42\") = " + parse.apply("42"));

        Function<String, String> upper = String::toUpperCase;        // instance
        System.out.println("String::toUpperCase      = " + upper.apply("java"));

        Supplier<StringBuilder> newSb = StringBuilder::new;          // constructor
        System.out.println("StringBuilder::new       = " + newSb.get().append("built"));
    }

    /** Core pipeline: filter -> map -> collect, plus reduce. */
    private static void demoStreams() {
        System.out.println("\n===== STREAMS (filter/map/collect/reduce) =====");

        List<Integer> nums = List.of(1, 2, 3, 4, 5);
        List<Integer> result = nums.stream()
                .filter(x -> x > 2)      // keep 3,4,5
                .map(x -> x * 2)         // 6,8,10
                .collect(Collectors.toList());
        System.out.println("filter>2 then map*2 : " + result);

        int sum = nums.stream().reduce(0, Integer::sum);
        System.out.println("reduce sum          : " + sum);
    }

    /** groupingBy / counting / averaging / joining. */
    private static void demoCollectors() {
        System.out.println("\n===== COLLECTORS =====");

        List<Employee> employees = List.of(
                new Employee(1, "Alice", "IT", 70000),
                new Employee(2, "Bob", "IT", 60000),
                new Employee(3, "Charlie", "HR", 50000)
        );

        Map<String, List<Employee>> byDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::department));
        System.out.println("groupingBy dept keys : " + byDept.keySet());

        Map<String, Long> countByDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::department, Collectors.counting()));
        System.out.println("counting by dept     : " + countByDept);

        double avg = employees.stream()
                .collect(Collectors.averagingDouble(Employee::salary));
        System.out.println("averaging salary     : " + avg);

        String names = employees.stream()
                .map(Employee::name)
                .collect(Collectors.joining(", "));
        System.out.println("joining names        : " + names);
    }

    /** Optional replaces null checks. */
    private static void demoOptional() {
        System.out.println("\n===== OPTIONAL =====");

        Optional<String> present = Optional.of("Value");
        Optional<String> empty = Optional.ofNullable(null);

        present.ifPresent(v -> System.out.println("ifPresent            : " + v));
        System.out.println("empty.orElse         : " + empty.orElse("Default"));
        System.out.println("empty.orElseGet      : " + empty.orElseGet(() -> "Lazy-Default"));
    }

    /** map = one-to-one; flatMap = flatten nested structure. */
    private static void demoMapVsFlatMap() {
        System.out.println("\n===== map vs flatMap =====");

        List<String> words = List.of("Java", "Spring");
        System.out.println("map(String::length) : "
                + words.stream().map(String::length).collect(Collectors.toList()));

        List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3, 4));
        System.out.println("flatMap flattened   : "
                + nested.stream().flatMap(List::stream).collect(Collectors.toList()));
    }

    /** Lazy: intermediate ops don't run until a terminal op is called. */
    private static void demoLazyEvaluation() {
        System.out.println("\n===== LAZY EVALUATION =====");

        Stream<Integer> pipeline = Stream.of(1, 2, 3, 4)
                .filter(x -> {
                    System.out.println("  filtering " + x);   // prints only when terminal runs
                    return x > 2;
                });
        System.out.println("Pipeline built (nothing printed yet).");
        System.out.println("Terminal collect -> " + pipeline.collect(Collectors.toList()));
    }

    /** Find duplicates: set.add() returns false for an already-seen element. */
    private static void demoFindDuplicates() {
        System.out.println("\n===== FIND DUPLICATES =====");

        List<Integer> list = List.of(1, 2, 2, 3, 4, 4, 5);
        Set<Integer> seen = new HashSet<>();
        Set<Integer> duplicates = list.stream()
                .filter(i -> !seen.add(i))
                .collect(Collectors.toSet());
        System.out.println("duplicates : " + duplicates);
    }

    /** First non-repeated character using an insertion-ordered count map. */
    private static void demoFirstNonRepeatedChar() {
        System.out.println("\n===== FIRST NON-REPEATED CHARACTER =====");

        String str = "swiss";
        Map<Character, Integer> counts = new LinkedHashMap<>();
        for (char ch : str.toCharArray()) {
            counts.put(ch, counts.getOrDefault(ch, 0) + 1);
        }
        Character first = counts.entrySet().stream()
                .filter(e -> e.getValue() == 1)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
        System.out.println("first non-repeated in 'swiss' : " + first);
    }

    /** Second highest distinct salary via distinct -> sorted desc -> skip(1) -> findFirst. */
    private static void demoSecondHighestSalary() {
        System.out.println("\n===== SECOND HIGHEST SALARY =====");

        List<Employee> employees = List.of(
                new Employee(1, "Alice", "IT", 70000),
                new Employee(2, "Bob", "IT", 60000),
                new Employee(3, "Charlie", "HR", 50000)
        );
        Optional<Double> second = employees.stream()
                .map(Employee::salary)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .skip(1)
                .findFirst();
        System.out.println("second highest salary : " + second.orElse(-1.0));

        // Arrays.asList / stream also work on arrays:
        System.out.println("array sum via stream  : "
                + Arrays.asList(1, 2, 3, 4).stream().mapToInt(Integer::intValue).sum());
    }
}
