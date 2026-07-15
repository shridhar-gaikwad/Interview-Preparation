package com.preperation.java.collectionFramework;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * =====================================================================================
 *                          JAVA COLLECTIONS FRAMEWORK (JCF)
 * =====================================================================================
 *
 * WHAT IS IT?
 * -----------
 * The Java Collections Framework (JCF) is a set of interfaces and classes used to store
 * and manipulate groups of objects (add, remove, search, sort, iterate).
 *
 * >>> NOTE ON THIS FILE <<<
 * The Java below is REAL, runnable code. main() runs one small demo per topic
 * (List, Set, Queue, Map, Collections utility, Comparable/Comparator, synchronized
 * collections, fail-fast iterator) so you can SEE each concept from the notes in action.
 *
 * -------------------------------------------------------------------------------------
 * HIERARCHY
 * -------------------------------------------------------------------------------------
 *                          Iterable
 *                              |
 *                          Collection
 *                     /        |         \
 *                  List       Set        Queue
 *                   |          |           |
 *             ArrayList     HashSet     PriorityQueue
 *             LinkedList    LinkedHashSet   LinkedList (also a Deque)
 *             Vector        TreeSet
 *               |-Stack
 *
 *   Map (SEPARATE hierarchy - does NOT extend Collection)
 *     +-- HashMap
 *     +-- LinkedHashMap
 *     +-- Hashtable
 *     +-- TreeMap
 *     +-- ConcurrentHashMap
 *
 * -------------------------------------------------------------------------------------
 * LIST vs SET vs QUEUE vs MAP
 * -------------------------------------------------------------------------------------
 *   Aspect          | List                | Set                 | Queue               | Map
 *   --------------- | ------------------- | ------------------- | ------------------- | -----------------------
 *   Implements      | Collection          | Collection          | Collection          | NOT Collection
 *   Purpose         | Ordered sequence    | Unique elements     | FIFO/LIFO/Priority  | Key-Value storage
 *   Order           | Insertion order     | No guarantee*       | Depends on type     | Depends on impl
 *   Duplicates      | Yes                 | No                  | Yes (depends)       | Keys: No, Values: Yes
 *   Null            | Multiple nulls      | 1 null (HashSet)    | Depends             | 1 null key (HashMap)
 *   Index access    | Yes (get(i))        | No                  | No                  | By key
 *   Impls           | ArrayList,LinkedList| HashSet,TreeSet     | LinkedList,PQueue   | HashMap,TreeMap
 *   Performance     | ArrayList O(1) get  | HashSet O(1) lookup | PQueue O(log n)     | HashMap O(1) put/get
 *   When to use     | Order + duplicates  | Uniqueness          | Processing order    | Map keys to values
 *
 *   *LinkedHashSet keeps insertion order; TreeSet keeps sorted order.
 *
 * -------------------------------------------------------------------------------------
 * ARRAYLIST vs LINKEDLIST
 * -------------------------------------------------------------------------------------
 *   ArrayList  -> backed by a dynamic ARRAY. Fast random access (get O(1)),
 *                 slow insert/delete in the middle (shifting).  Use when READING more.
 *   LinkedList -> backed by a DOUBLY LINKED LIST (prev <- Node -> next).
 *                 Fast insert/delete at ends (O(1)), slow random access (O(n)).
 *                 Use when INSERTING/DELETING frequently. Also implements Deque/Queue.
 *
 * -------------------------------------------------------------------------------------
 * COLLECTIONS UTILITY CLASS  (java.util.Collections)
 * -------------------------------------------------------------------------------------
 *   Collections.sort(list)      Collections.reverse(list)   Collections.shuffle(list)
 *   Collections.max(coll)       Collections.min(coll)       Collections.frequency(...)
 *   Collections.synchronizedList/Set/Map(...)  -> thread-safe wrappers.
 *
 * -------------------------------------------------------------------------------------
 * HASHMAP INTERNAL WORKING - PUT
 * -------------------------------------------------------------------------------------
 *   1. map.put(key, value)
 *   2. compute key.hashCode(), then spread bits (hashing function)
 *   3. bucketIndex = (capacity - 1) & hash
 *   4. bucket empty      -> store new Node(key, value)
 *   5. bucket not empty  -> compare hash; if hash matches, use equals() to check key
 *        - equals() true  -> UPDATE existing value
 *        - equals() false -> add node to bucket (COLLISION handling)
 *   6. bucket length >= 8 AND table capacity >= 64 -> convert LinkedList to Red-Black
 *      Tree (TREEIFICATION) -> lookup improves O(n) -> O(log n)
 *   7. size > capacity * loadFactor (default 0.75) -> RESIZE (capacity doubles) +
 *      REHASH (recompute bucket positions)
 *
 * HASHMAP INTERNAL WORKING - GET
 *   1. map.get(key) -> key.hashCode() -> bucketIndex
 *   2. go to bucket -> compare hash -> use equals() to find exact key
 *   3. found -> return value ; not found -> return null
 *
 *   Collisions: Java 7 used a LinkedList; Java 8+ converts hot buckets to a Red-Black
 *   Tree. hashCode() locates the bucket, equals() identifies the exact key.
 *
 * -------------------------------------------------------------------------------------
 * SYNCHRONIZED COLLECTIONS
 * -------------------------------------------------------------------------------------
 *   Thread-safe wrappers where every method is synchronized (one thread at a time):
 *      List<String> list = Collections.synchronizedList(new ArrayList<>());
 *      Set<String>  set  = Collections.synchronizedSet(new HashSet<>());
 *      Map<K,V>     map  = Collections.synchronizedMap(new HashMap<>());
 *   IMPORTANT: iteration must STILL be synchronized manually, else you may get
 *   ConcurrentModificationException:
 *      synchronized (list) { for (String s : list) { ... } }
 *
 * -------------------------------------------------------------------------------------
 * FAIL-FAST vs FAIL-SAFE ITERATORS
 * -------------------------------------------------------------------------------------
 *   Fail-Fast (ArrayList, HashMap, HashSet): throw ConcurrentModificationException if
 *              the collection is structurally modified during iteration.
 *   Fail-Safe (ConcurrentHashMap, CopyOnWriteArrayList): iterate over a copy/snapshot,
 *              so no exception (may not see latest changes).
 *
 * -------------------------------------------------------------------------------------
 * COMPARABLE vs COMPARATOR
 * -------------------------------------------------------------------------------------
 *   Comparable<T> -> NATURAL ordering, logic INSIDE the class, method compareTo().
 *   Comparator<T> -> CUSTOM/EXTERNAL ordering, method compare(); many can coexist.
 *
 * -------------------------------------------------------------------------------------
 * QUICK Q&A (INTERVIEW)
 * -------------------------------------------------------------------------------------
 * Q1: Why is Map not part of Collection? -> Collection stores single elements [10,20];
 *     Map stores key-value pairs {id=1,name=John}, so it's a separate hierarchy.
 * Q2: Why does Collection extend Iterable? -> So every collection supports traversal
 *     via iterator() (enables the enhanced for-loop).
 * Q3: ArrayList vs LinkedList? -> ArrayList = dynamic array, fast read, slow insert;
 *     LinkedList = nodes, fast insert/delete, slow read.
 * Q4: How does HashSet work internally? -> It wraps a HashMap: add(x) does
 *     map.put(x, PRESENT) where PRESENT is a dummy value.
 * Q5: What is a collision? -> Two keys map to the same bucket index.
 * Q6: How does HashMap handle collisions? -> Java 7: LinkedList; Java 8+: LinkedList
 *     then Red-Black Tree after threshold.
 * Q7: HashMap vs Hashtable? -> HashMap: not synchronized, faster, allows one null key;
 *     Hashtable: synchronized, slower, no null.
 * Q8: Fail-fast vs fail-safe? -> Fail-fast throws CME on modification; fail-safe uses a
 *     snapshot and does not.
 * Q9: Comparable vs Comparator? -> Comparable = natural order (compareTo, in class);
 *     Comparator = custom order (compare, external).
 * Q10: Why override hashCode() with equals()? -> Contract: equal objects must share the
 *     same hashCode so HashMap/HashSet locate them correctly.
 * Q11: Can we access values by index from Set/Queue? -> No, they don't support indexing
 *     (set.get(0) won't compile); use an iterator / poll().
 *
 * ONE-LINER SUMMARY:
 * JCF = Iterable -> Collection (List/Set/Queue) + separate Map hierarchy: pick the
 * structure by your needs (order, uniqueness, key-value, processing order), and remember
 * HashMap's hashCode()+equals() contract and fail-fast iteration semantics.
 *
 * -------------------------------------------------------------------------------------
 * EXAMPLE BELOW: each demo* method maps to a section of the notes above.
 * =====================================================================================
 */
public class CollectionFramework {

    public static void main(String[] args) {
        demoList();
        demoSet();
        demoQueue();
        demoMap();
        demoCollectionsUtility();
        demoComparableComparator();
        demoSynchronizedCollection();
        demoFailFastIterator();
    }

    /** LIST: ordered, allows duplicates, index access. ArrayList vs LinkedList. */
    private static void demoList() {
        System.out.println("\n===== LIST (ordered, duplicates allowed, index access) =====");

        List<String> arrayList = new ArrayList<>();   // dynamic array -> fast get()
        arrayList.add("A");
        arrayList.add("B");
        arrayList.add("A");                            // duplicate allowed
        System.out.println("ArrayList          : " + arrayList);
        System.out.println("get(1) by index    : " + arrayList.get(1));

        // LinkedList (doubly linked list) also works as a Deque -> add at both ends.
        LinkedList<String> linkedList = new LinkedList<>(arrayList);
        linkedList.addFirst("HEAD");
        linkedList.addLast("TAIL");
        System.out.println("LinkedList (deque) : " + linkedList);
    }

    /** SET: unique elements. HashSet (no order), LinkedHashSet (insertion), TreeSet (sorted). */
    private static void demoSet() {
        System.out.println("\n===== SET (unique elements) =====");

        Set<String> hashSet = new HashSet<>();
        hashSet.add("Java");
        hashSet.add("Spring");
        hashSet.add("Java");                           // duplicate ignored
        System.out.println("HashSet (no order)      : " + hashSet);

        Set<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add("C");
        linkedHashSet.add("A");
        linkedHashSet.add("B");
        System.out.println("LinkedHashSet (insertion): " + linkedHashSet);

        Set<Integer> treeSet = new TreeSet<>();
        treeSet.add(30);
        treeSet.add(10);
        treeSet.add(20);
        System.out.println("TreeSet (sorted)         : " + treeSet);
    }

    /** QUEUE: processing order. PriorityQueue (min-heap) and Deque (LIFO stack). */
    private static void demoQueue() {
        System.out.println("\n===== QUEUE (processing order) =====");

        Queue<Integer> priorityQueue = new PriorityQueue<>();   // natural order = min first
        priorityQueue.offer(40);
        priorityQueue.offer(10);
        priorityQueue.offer(30);
        System.out.print("PriorityQueue poll order : ");
        while (!priorityQueue.isEmpty()) {
            System.out.print(priorityQueue.poll() + " ");        // 10 30 40
        }
        System.out.println();

        Deque<String> stack = new LinkedList<>();               // use as LIFO stack
        stack.push("first");
        stack.push("second");
        System.out.println("Deque as stack pop       : " + stack.pop() + " (LIFO)");
    }

    /** MAP: key-value storage. HashMap, LinkedHashMap, TreeMap. */
    private static void demoMap() {
        System.out.println("\n===== MAP (key-value pairs) =====");

        Map<Integer, String> hashMap = new HashMap<>();
        hashMap.put(1, "One");
        hashMap.put(2, "Two");
        hashMap.put(1, "Uno");                          // same key -> value UPDATED
        System.out.println("HashMap (no order)       : " + hashMap);
        System.out.println("get(1)                   : " + hashMap.get(1));
        System.out.println("getOrDefault(9, N/A)     : " + hashMap.getOrDefault(9, "N/A"));

        Map<Integer, String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put(3, "C");
        linkedHashMap.put(1, "A");
        linkedHashMap.put(2, "B");
        System.out.println("LinkedHashMap (insertion): " + linkedHashMap);

        Map<Integer, String> treeMap = new TreeMap<>(linkedHashMap);
        System.out.println("TreeMap (sorted by key)  : " + treeMap);
    }

    /** Collections utility: sort, reverse, shuffle, max, min. */
    private static void demoCollectionsUtility() {
        System.out.println("\n===== Collections UTILITY class =====");

        List<Integer> numbers = new ArrayList<>(List.of(5, 1, 4, 2, 3));
        Collections.sort(numbers);
        System.out.println("sort()    : " + numbers);
        Collections.reverse(numbers);
        System.out.println("reverse() : " + numbers);
        System.out.println("max()     : " + Collections.max(numbers));
        System.out.println("min()     : " + Collections.min(numbers));
    }

    /** Comparable (natural order in Employee) vs Comparator (external custom order). */
    private static void demoComparableComparator() {
        System.out.println("\n===== COMPARABLE vs COMPARATOR =====");

        List<Employee> employees = new ArrayList<>(List.of(
                new Employee(3, "Charlie", 50000),
                new Employee(1, "Alice", 70000),
                new Employee(2, "Bob", 60000)
        ));

        // Comparable: natural ordering (by id) defined inside Employee.compareTo().
        Collections.sort(employees);
        System.out.println("Natural order (by id)    : " + employees);

        // Comparator: external custom ordering (by salary descending).
        employees.sort(Comparator.comparingDouble(Employee::getSalary).reversed());
        System.out.println("Custom order (salary desc): " + employees);

        // Employee overrides equals()/hashCode() by id -> correct HashSet behaviour.
        Set<Employee> unique = new HashSet<>(employees);
        unique.add(new Employee(1, "Alice-Duplicate", 99999));  // same id -> not added
        System.out.println("HashSet size (dedup by id): " + unique.size());
    }

    /** Synchronized wrapper + manual synchronization while iterating. */
    private static void demoSynchronizedCollection() {
        System.out.println("\n===== SYNCHRONIZED COLLECTION =====");

        List<String> syncList = Collections.synchronizedList(new ArrayList<>());
        syncList.add("t1");
        syncList.add("t2");

        // Even though the list is synchronized, iteration must be guarded manually.
        synchronized (syncList) {
            Iterator<String> it = syncList.iterator();
            StringBuilder sb = new StringBuilder();
            while (it.hasNext()) {
                sb.append(it.next()).append(' ');
            }
            System.out.println("Safely iterated          : " + sb.toString().trim());
        }
    }

    /** Fail-fast iterator: structural modification during iteration throws CME. */
    private static void demoFailFastIterator() {
        System.out.println("\n===== FAIL-FAST ITERATOR =====");

        List<String> list = new ArrayList<>(List.of("a", "b", "c"));
        try {
            for (String s : list) {
                if (s.equals("b")) {
                    list.add("d");   // structural change during iteration -> CME
                }
            }
        } catch (java.util.ConcurrentModificationException e) {
            System.out.println("Caught ConcurrentModificationException (fail-fast).");
            System.out.println("Fix: use Iterator.remove(), removeIf(), or a fail-safe " +
                    "collection like CopyOnWriteArrayList.");
        }
    }
}
