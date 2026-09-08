package com.preperation.java.designPatterns;

/*
 * =====================================================================================
 *                          STRUCTURAL DESIGN PATTERNS
 * =====================================================================================
 *
 * WHAT ARE THEY?
 * --------------
 * Structural Design Patterns focus on HOW classes and objects are composed to form larger structures.
 * They help organize relationships between objects while keeping systems flexible, maintainable and loosely coupled.
 *
 * >>> NOTE ON THIS FILE <<<
 * The Java below is REAL, runnable code. main() runs one demo per pattern
 * (Adapter, Facade, Proxy, Decorator, Composite) so you can SEE each concept
 * from the notes in action.
 *
 * -------------------------------------------------------------------------------------
 * WHY WERE THEY INTRODUCED?
 * -------------------------------------------------------------------------------------
 * Without structural patterns:
 *      - Classes become tightly coupled
 *      - Integrating third-party libraries becomes difficult
 *      - Clients depend on complex subsystems
 *      - Extending behavior requires modifying existing code
 *
 * Structural Patterns help achieve:
 *      - Loose coupling
 *      - Better maintainability
 *      - Code reusability
 *      - Flexible object composition
 *
 * =====================================================================================
 * 1) ADAPTER PATTERN
 * =====================================================================================
 * Converts one interface into another interface expected by the client.
 * Problem:
 *      Existing System expects:    PaymentService
 *
 *      Third-party library provides:   RazorpayClient
 *
 *      Both interfaces are incompatible.
 *
 * Solution:
 *      Create an Adapter.
 *              PaymentService
 *                     ^
 *                     |
 *            RazorpayAdapter
 *                     |
 *                     v
 *              RazorpayClient
 *
 * The adapter translates requests from one interface to another.
 *
 * Real Examples:
 *      Third-party integrations
 *      Legacy system integration
 *      Payment gateway integration
 *      External REST/SOAP clients
 *
 * Benefits:
 *      - Reuse existing code
 *      - Avoid modifying third-party classes
 *      - Reduces coupling
 *
 * =====================================================================================
 * 2) FACADE PATTERN
 * =====================================================================================
 * Provides a simplified interface to a complex subsystem.
 * Problem:
 *      Client must call multiple services.
 *              CustomerService
 *              AccountService
 *              TransactionService
 *              NotificationService
 *
 * Solution:
 *      BankingFacade
 *              transferMoney()
 *
 * Internally:
 *              Debit Account
 *              Credit Account
 *              Save Transaction
 *              Send Notification
 *
 * Client only interacts with a single facade.
 *
 * Real Examples:
 *      Service Layer
 *      Banking Operations
 *      Order Processing
 *      Travel Booking Systems
 *
 * Benefits:
 *      - Hides complexity
 *      - Simplifies client code
 *      - Improves maintainability
 *
 * =====================================================================================
 * 3) PROXY PATTERN
 * =====================================================================================
 * Provides a placeholder or wrapper object that controls access to another object.
 *
 * Instead of:
 *      Client
 *         |
 *         v
 *      Service
 *
 * Use:
 *      Client
 *         |
 *         v
 *      Proxy
 *         |
 *         v
 *      Service
 *
 * The proxy can perform additional tasks before/after forwarding the call.
 *
 * Common Tasks:
 *      Transaction Management
 *      Security Checks
 *      Logging
 *      Caching
 *      Lazy Loading
 *
 * Spring Examples:
 *      @Transactional
 *      @Cacheable
 *      @Async
 *      Spring AOP
 *
 * Benefits:
 *      - Additional behavior without modifying target class
 *      - Better separation of concerns
 *
 * =====================================================================================
 * 4) DECORATOR PATTERN
 * =====================================================================================
 * Adds new behavior to an object dynamically without modifying its code.
 *
 * Instead of inheritance:
 *
 *      FileInputStream
 *           |
 *      BufferedFileInputStream
 *           |
 *      CompressedBufferedFileInputStream
 *
 * Use decorators:
 *
 *      FileInputStream
 *              |
 *              v
 *      BufferedInputStream
 *              |
 *              v
 *      DataInputStream
 *
 * Each decorator adds additional functionality.
 *
 * Java Examples:
 *
 *      BufferedInputStream
 *      BufferedOutputStream
 *      DataInputStream
 *      DataOutputStream
 *
 * Benefits:
 *
 *      - Flexible behavior extension
 *      - Follows Open/Closed Principle
 *      - Avoids deep inheritance hierarchies
 *
 * =====================================================================================
 * 5) COMPOSITE PATTERN
 * =====================================================================================
 *
 * Allows clients to treat individual objects and groups of objects uniformly.
 *
 * Example:
 *
 *      Root Folder
 *          |
 *      ------------------
 *      |                |
 *    File           SubFolder
 *                       |
 *                  ----------
 *                  |        |
 *                File     File
 *
 * Both File and Folder implement a common interface:
 *
 *      FileSystemComponent
 *
 * Client can perform operations on both using the same API.
 *
 * Real Examples:
 *
 *      File Systems
 *      Organization Hierarchies
 *      Menu Structures
 *      UI Component Trees
 *
 * Benefits:
 *
 *      - Uniform object handling
 *      - Easy tree representation
 *      - Simplifies recursive operations
 *
 * -------------------------------------------------------------------------------------
 * QUICK Q&A (INTERVIEW)
 * -------------------------------------------------------------------------------------
 *
 * Q1: Adapter vs Facade?
 *
 *      Adapter converts one interface into another.
 *      Facade simplifies a complex subsystem.
 *
 * Q2: Proxy vs Decorator?
 *
 *      Proxy controls access to an object.
 *      Decorator adds new behavior to an object.
 *
 * Q3: Real Spring example of Proxy?
 *
 *      @Transactional
 *      @Cacheable
 *      @Async
 *
 * Q4: Real Java example of Decorator?
 *
 *      BufferedInputStream
 *      BufferedOutputStream
 *
 * Q5: Real-world Composite example?
 *
 *      File and Folder hierarchy.
 *
 * Q6: When should Adapter be used?
 *
 *      When integrating incompatible third-party or legacy systems.
 *
 * -------------------------------------------------------------------------------------
 * ONE-LINER SUMMARY
 * -------------------------------------------------------------------------------------
 *
 * Structural Design Patterns focus on how objects and classes are composed to build
 * flexible and maintainable systems. The most commonly used patterns in Java and Spring applications are Adapter, Facade, Proxy, Decorator, and Composite.
 */

public class StructuralDesignPatterns {
}
