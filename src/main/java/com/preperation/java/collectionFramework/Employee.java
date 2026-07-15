package com.preperation.java.collectionFramework;

import java.util.Objects;

/**
 * Helper domain object used by CollectionFramework demos.
 *
 * Demonstrates:
 *   - Comparable  -> NATURAL ordering (compareTo), sorting logic INSIDE the class.
 *   - equals()/hashCode() -> required contract so the object behaves correctly as a
 *                            HashMap key / HashSet element.
 *
 * CONTRACT: if two objects are equal by equals(), they MUST return the same hashCode().
 */
public class Employee implements Comparable<Employee> {

    private final int id;
    private final String name;
    private final double salary;

    public Employee(int id, String name, double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getSalary() { return salary; }

    /** Natural ordering: by id (ascending). Used by TreeSet/TreeMap/Collections.sort. */
    @Override
    public int compareTo(Employee other) {
        return Integer.compare(this.id, other.id);
    }

    /** Two employees are the same identity when their id matches. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id;
    }

    /** Must be consistent with equals(): same id -> same hashCode. */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Employee{id=" + id + ", name='" + name + "', salary=" + salary + '}';
    }
}
