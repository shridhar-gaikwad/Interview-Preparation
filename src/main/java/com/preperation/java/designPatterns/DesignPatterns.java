package com.preperation.java.designPatterns;

/*
 * =====================================================================================
 *                               DESIGN PATTERNS
 * =====================================================================================
 *
 * WHAT ARE THEY?
 * --------------
 * Design Patterns are standard, reusable solutions to commonly occurring problems in
 * software design. They are blueprints (not copy-paste code) for writing flexible,
 * maintainable and scalable code.
 *
 * >>> NOTE ON THIS FILE <<<
 * The Java below is REAL, runnable code. main() runs one demo per pattern
 * (Singleton, Builder, Factory, Abstract Factory, Prototype) so you can SEE each
 * concept from the notes in action. Supporting types are nested static classes.
 *
 * -------------------------------------------------------------------------------------
 * WHY WERE THEY INTRODUCED?
 * -------------------------------------------------------------------------------------
 *   Without patterns code tends to be: tightly coupled, hard to maintain, hard to
 *   extend, hard to test.
 *   Patterns help achieve: reusability, flexibility, loose coupling, maintainability,
 *   scalability.
 *
 * -------------------------------------------------------------------------------------
 * THREE CATEGORIES
 * -------------------------------------------------------------------------------------
 *   1. Creational  -> object CREATION      (Singleton, Factory, Abstract Factory,
 *                                            Builder, Prototype)
 *   2. Structural  -> class/object STRUCTURE (Adapter, Decorator, Facade, Proxy, ...)
 *   3. Behavioral  -> object COMMUNICATION   (Strategy, Observer, Template, ...)
 *
 * =====================================================================================
 * 1) SINGLETON (Creational)
 * =====================================================================================
 * Ensures only ONE object of a class exists, with a global access point.
 *   Use cases: Logger, Configuration Manager, Cache Manager, Spring beans (singleton scope).
 *
 *   Thread-Safe (Double-Checked Locking):
 *      public final class Singleton {
 *          private static volatile Singleton instance;   // volatile: safe publication
 *          private Singleton() {}
 *          public static Singleton getInstance() {
 *              if (instance == null) {                    // 1st check (no lock)
 *                  synchronized (Singleton.class) {
 *                      if (instance == null) {            // 2nd check (with lock)
 *                          instance = new Singleton();
 *                      }
 *                  }
 *              }
 *              return instance;
 *          }
 *      }
 *
 *   BEST Singleton (Enum) -> thread-safe, serialization-safe, reflection-safe, simple:
 *      public enum Singleton { INSTANCE; }
 *
 * Ways thread-safety/uniqueness can be broken:
 *      Reflection (setAccessible on constructor),
 *      Serialization (creates new instance on deserialize — fix with readResolve()), and Cloning.
 *
 * =====================================================================================
 * 2) BUILDER (Creational)
 * =====================================================================================
 * Constructs COMPLEX objects step-by-step. Useful when an object has many fields /
 * optional parameters and the constructor would explode / be hard to read.
 *
 *   Instead of:  new Employee(name, age, email, department, city)   // easy to mix up
 *   Use:
 *      Employee emp = Employee.builder()
 *              .name("John").age(25).email("john@gmail.com").build();
 *
 *   Each setter returns "this" to enable method chaining; a static nested Builder class
 *   is used so it can be created without an instance of the outer class.
 *   Benefits: easy to read, flexible, supports optional fields, no constructor explosion.
 *
 * =====================================================================================
 * 3) FACTORY (Creational)
 * =====================================================================================
 * Provides a method/interface for creating objects WITHOUT exposing the creation logic
 * to the client. The client asks the factory instead of using "new" directly.
 *
 *      class VehicleFactory {
 *          public static Vehicle getVehicle(String type) {
 *              if (type.equalsIgnoreCase("CAR"))  return new Car();
 *              if (type.equalsIgnoreCase("BIKE")) return new Bike();
 *              throw new IllegalArgumentException("Invalid Vehicle Type");
 *          }
 *      }
 *
 *   Reduces coupling and makes adding new implementations easy.
 *   Spring examples: BeanFactory, FactoryBean.
 *
 * =====================================================================================
 * 4) ABSTRACT FACTORY (Creational)
 * =====================================================================================
 * A "factory of factories": creates FAMILIES of related objects. Factory creates ONE
 * type of product; Abstract Factory creates an entire related family.
 *   Example: MySQLFactory creates MySQLConnection + MySQLQueryExecutor, while
 *            PostgresFactory creates PostgresConnection + PostgresQueryExecutor.
 *
 * =====================================================================================
 * 5) PROTOTYPE (Creational)
 * =====================================================================================
 * Creates new objects by CLONING an existing object instead of building from scratch
 * (useful when creation is expensive).
 *      Employee emp2 = emp1.clone();
 *   Steps: (1) implement Cloneable, (2) override clone().
 *   Shallow copy -> copies references (nested objects shared).
 *   Deep copy    -> copies nested objects too (fully independent).
 *
 * -------------------------------------------------------------------------------------
 * QUICK Q&A (INTERVIEW)
 * -------------------------------------------------------------------------------------
 * Q1: Factory vs Builder? -> Factory decides WHICH object to create
 *     (ShapeFactory.getShape("CIRCLE")); Builder decides HOW to build one complex object
 *     step-by-step.
 * Q2: Factory vs Singleton? -> Singleton ensures ONE object (Logger, Config); Factory
 *     CREATES objects (ShapeFactory).
 * Q3: Shallow vs Deep copy? -> Shallow copies references (nested objects shared, a change
 *     in one reflects in the other); Deep copy duplicates nested objects (independent).
 * Q4: Why is enum the best Singleton? -> Thread-safe, serialization-safe, reflection-safe
 *     and concise by design.
 * Q5: Why volatile + double-checked locking? -> volatile prevents reordering/partial
 *     publication; the two null checks avoid locking on every call after init.
 *
 * ONE-LINER SUMMARY:
 * Design patterns are proven, reusable solutions; the Creational family (Singleton,
 * Builder, Factory, Abstract Factory, Prototype) standardizes HOW objects are created to
 * keep code loosely coupled and maintainable.
 *
 * -------------------------------------------------------------------------------------
 * EXAMPLE BELOW: each demo* method maps to a pattern in the notes above.
 * =====================================================================================
 */
public class DesignPatterns {

    public static void main(String[] args) {
        demoSingleton();
        demoBuilder();
        demoFactory();
        demoAbstractFactory();
        demoPrototype();
    }

    // ================================================================================
    // 1) SINGLETON
    // ================================================================================

    /** BEST singleton: enum is thread-safe, serialization-safe and reflection-safe. */
    enum EnumSingleton {
        INSTANCE;

        void log(String msg) {
            System.out.println("[EnumSingleton] " + msg);
        }
    }

    /** Classic thread-safe singleton using volatile + double-checked locking. */
    static final class DclSingleton {
        private static volatile DclSingleton instance;

        private DclSingleton() { }

        static DclSingleton getInstance() {
            if (instance == null) {                       // 1st check (no lock)
                synchronized (DclSingleton.class) {
                    if (instance == null) {               // 2nd check (with lock)
                        instance = new DclSingleton();
                    }
                }
            }
            return instance;
        }
    }

    private static void demoSingleton() {
        System.out.println("\n===== SINGLETON =====");

        EnumSingleton.INSTANCE.log("only one instance ever exists");
        EnumSingleton e1 = EnumSingleton.INSTANCE;
        EnumSingleton e2 = EnumSingleton.INSTANCE;
        System.out.println("Enum same instance? " + (e1 == e2));

        DclSingleton a = DclSingleton.getInstance();
        DclSingleton b = DclSingleton.getInstance();
        System.out.println("DCL same instance?  " + (a == b));
    }

    // ================================================================================
    // 2) BUILDER
    // ================================================================================

    /** Immutable object built step-by-step via a fluent, static nested Builder. */
    static final class Employee {
        private final String name;
        private final int age;
        private final String email;

        private Employee(Builder builder) {
            this.name = builder.name;
            this.age = builder.age;
            this.email = builder.email;
        }

        static Builder builder() {
            return new Builder();
        }

        /** Static so it can be used without an instance of the outer Employee. */
        static final class Builder {
            private String name;
            private int age;
            private String email;

            Builder name(String name)  { this.name = name;  return this; }  // chaining
            Builder age(int age)       { this.age = age;    return this; }
            Builder email(String email){ this.email = email; return this; }

            Employee build() {
                return new Employee(this);
            }
        }

        @Override
        public String toString() {
            return "Employee{name='" + name + "', age=" + age + ", email='" + email + "'}";
        }
    }

    private static void demoBuilder() {
        System.out.println("\n===== BUILDER =====");

        Employee emp = Employee.builder()
                .name("John")
                .age(25)
                .email("john@gmail.com")
                .build();
        System.out.println("Built: " + emp);
    }

    // ================================================================================
    // 3) FACTORY
    // ================================================================================

    interface Vehicle {
        String drive();
    }

    static final class Car implements Vehicle {
        public String drive() { return "Driving a Car"; }
    }

    static final class Bike implements Vehicle {
        public String drive() { return "Riding a Bike"; }
    }

    /** Encapsulates object creation; client never uses "new" for concrete types. */
    static final class VehicleFactory {
        static Vehicle getVehicle(String type) {
            if ("CAR".equalsIgnoreCase(type))  return new Car();
            if ("BIKE".equalsIgnoreCase(type)) return new Bike();
            throw new IllegalArgumentException("Invalid Vehicle Type: " + type);
        }
    }

    private static void demoFactory() {
        System.out.println("\n===== FACTORY =====");

        System.out.println(VehicleFactory.getVehicle("CAR").drive());
        System.out.println(VehicleFactory.getVehicle("BIKE").drive());
    }

    // ================================================================================
    // 4) ABSTRACT FACTORY (families of related products)
    // ================================================================================

    interface DbConnection {
        String connect();
    }

    interface QueryExecutor {
        String execute();
    }

    /** The abstract factory: creates a related FAMILY (connection + executor). */
    interface DatabaseFactory {
        DbConnection createConnection();
        QueryExecutor createQueryExecutor();
    }

    static final class MySQLConnection implements DbConnection {
        public String connect() { return "MySQL connected"; }
    }

    static final class MySQLQueryExecutor implements QueryExecutor {
        public String execute() { return "MySQL query executed"; }
    }

    static final class MySQLFactory implements DatabaseFactory {
        public DbConnection createConnection()       { return new MySQLConnection(); }
        public QueryExecutor createQueryExecutor()   { return new MySQLQueryExecutor(); }
    }

    static final class PostgresConnection implements DbConnection {
        public String connect() { return "Postgres connected"; }
    }

    static final class PostgresQueryExecutor implements QueryExecutor {
        public String execute() { return "Postgres query executed"; }
    }

    static final class PostgresFactory implements DatabaseFactory {
        public DbConnection createConnection()       { return new PostgresConnection(); }
        public QueryExecutor createQueryExecutor()   { return new PostgresQueryExecutor(); }
    }

    private static void demoAbstractFactory() {
        System.out.println("\n===== ABSTRACT FACTORY =====");

        for (DatabaseFactory factory : new DatabaseFactory[]{new MySQLFactory(), new PostgresFactory()}) {
            DbConnection connection = factory.createConnection();
            QueryExecutor executor = factory.createQueryExecutor();
            System.out.println(connection.connect() + " | " + executor.execute());
        }
    }

    // ================================================================================
    // 5) PROTOTYPE (clone) + shallow vs deep copy
    // ================================================================================

    static final class Address {
        String city;
        Address(String city) { this.city = city; }
    }

    static final class Person implements Cloneable {
        int id;
        String name;
        Address address;   // nested mutable object -> shallow vs deep copy matters here

        Person(int id, String name, Address address) {
            this.id = id;
            this.name = name;
            this.address = address;
        }

        /** SHALLOW copy: nested Address reference is SHARED with the original. */
        Person shallowCopy() {
            try {
                return (Person) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        /** DEEP copy: nested Address is duplicated so the clone is fully independent. */
        Person deepCopy() {
            Person copy = shallowCopy();
            copy.address = new Address(this.address.city);
            return copy;
        }

        @Override
        public String toString() {
            return "Person{id=" + id + ", name='" + name + "', city='" + address.city + "'}";
        }
    }

    private static void demoPrototype() {
        System.out.println("\n===== PROTOTYPE (clone) =====");

        Person original = new Person(1, "John", new Address("Chennai"));

        Person shallow = original.shallowCopy();
        Person deep = original.deepCopy();
        System.out.println("original != clone (new object)? " + (original != shallow));

        // Mutate the shared nested object through the shallow copy.
        shallow.address.city = "Bangalore";
        System.out.println("After changing shallow copy's city:");
        System.out.println("  original (AFFECTED, shared): " + original);
        System.out.println("  shallow                    : " + shallow);
        System.out.println("  deep (INDEPENDENT)         : " + deep);
    }
}
