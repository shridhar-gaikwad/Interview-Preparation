package com.preperation.java.designPatterns;

/*
 * =====================================================================================
 *                           BEHAVIORAL DESIGN PATTERNS
 * =====================================================================================
 *
 * WHAT ARE THEY?
 * --------------
 * Behavioral Design Patterns focus on COMMUNICATION and INTERACTION between objects. They define how objects collaborate,
 * exchange responsibilities and execute behavior.
 *
 * Instead of object creation (Creational) or object structure (Structural),
 * Behavioral patterns answer:
 *      "How do objects communicate and behave?"
 *
 * -------------------------------------------------------------------------------------
 * WHY WERE THEY INTRODUCED?
 * -------------------------------------------------------------------------------------
 * Without behavioral patterns:
 *      - Large if/else blocks
 *      - Tight coupling
 *      - Hard to extend behavior
 *      - Difficult communication between objects
 *
 * Behavioral patterns provide:
 *      - Loose coupling
 *      - Better maintainability
 *      - Easier extensibility
 *      - Reusable business behavior
 *
 * -------------------------------------------------------------------------------------
 * MOST IMPORTANT BEHAVIORAL PATTERNS
 * -------------------------------------------------------------------------------------
 * 1. Strategy
 * 2. Observer
 * 3. Template Method
 * 4. Chain of Responsibility
 * 5. Command
 * 6. State
 * 7. Iterator
 *
 * =====================================================================================
 * 1) STRATEGY PATTERN
 * =====================================================================================
 *
 * Defines a family of algorithms and allows selecting one at runtime.
 *
 * Instead of:
 *
 *      if(paymentType.equals("UPI")) {}
 *      else if(paymentType.equals("CARD")) {}
 *      else if(paymentType.equals("NET_BANKING")) {}
 *
 * Use:
 *
 *      PaymentStrategy strategy = new UpiPayment();
 *      strategy.pay();
 *
 * Benefits:
 *
 *      - Removes large if/else blocks
 *      - Open/Closed Principle
 *      - Easy to add new strategies
 *
 * Real Examples:
 *
 *      Payment Methods
 *      Discount Calculation
 *      Notification Channels
 *      Tax Calculation
 *
 * Spring Example:
 *
 *      Multiple implementations injected by Spring
 *
 * =====================================================================================
 * 2) OBSERVER PATTERN
 * =====================================================================================
 *
 * Defines a one-to-many dependency.
 *
 * When one object changes state,
 * all dependent objects are notified automatically.
 *
 * Example:
 *
 *      YouTube Channel
 *              |
 *      -------------------
 *      |        |        |
 *   User1    User2    User3
 *
 * New Video Uploaded
 *      ↓
 * All Subscribers Notified
 *
 * Real Examples:
 *
 *      Event Publishing
 *      Email Notifications
 *      Stock Market Updates
 *
 * Spring Example:
 *
 *      ApplicationEventPublisher
 *      @EventListener
 *
 * =====================================================================================
 * 3) TEMPLATE METHOD PATTERN
 * =====================================================================================
 *
 * Defines the skeleton of an algorithm in a base class
 * while allowing subclasses to customize specific steps.
 *
 * Example:
 *
 *      processData()
 *          |
 *          |--- Read Data
 *          |--- Validate Data
 *          |--- Save Data
 *
 * Subclasses override individual steps.
 *
 * Real Examples:
 *
 *      File Processing
 *      Batch Jobs
 *      Data Import Frameworks
 *
 * Spring Examples:
 *
 *      JdbcTemplate
 *      RestTemplate
 *      JmsTemplate
 *
 * =====================================================================================
 * 4) CHAIN OF RESPONSIBILITY
 * =====================================================================================
 *
 * A request passes through multiple handlers until processed.
 *
 * Example:
 *
 *      Request
 *          |
 *          v
 *      Auth Filter
 *          |
 *          v
 *      Logging Filter
 *          |
 *          v
 *      Validation Filter
 *          |
 *          v
 *      Controller
 *
 * Benefits:
 *
 *      - Decouples sender and receiver
 *      - Flexible processing pipeline
 *
 * Spring Example:
 *
 *      Spring Security Filter Chain
 *
 * =====================================================================================
 * 5) COMMAND PATTERN
 * =====================================================================================
 *
 * Encapsulates a request as an object.
 *
 * Sender does not know how request is executed.
 *
 *      Button Click
 *           |
 *           v
 *      Command Object
 *           |
 *           v
 *      Receiver
 *
 * Real Examples:
 *
 *      Runnable
 *      ExecutorService
 *      Queue-Based Processing
 *
 * =====================================================================================
 * 6) STATE PATTERN
 * =====================================================================================
 *
 * Allows an object to change behavior when its internal state changes.
 *
 * Example:
 *
 *      Order
 *        |
 *      CREATED
 *        |
 *      PAID
 *        |
 *      SHIPPED
 *        |
 *      DELIVERED
 *
 * Different states allow different operations.
 *
 * Real Examples:
 *
 *      Order Management
 *      Workflow Engines
 *      ATM Machine States
 *
 * =====================================================================================
 * 7) ITERATOR PATTERN
 * =====================================================================================
 *
 * Provides a way to access collection elements sequentially
 * without exposing internal structure.
 *
 * Example:
 *
 *      Iterator<String> itr = list.iterator();
 *
 *      while(itr.hasNext()) {
 *          System.out.println(itr.next());
 *      }
 *
 * Real Examples:
 *
 *      ArrayList
 *      HashSet
 *      LinkedList
 *
 * =====================================================================================
 * QUICK Q&A (INTERVIEW)
 * =====================================================================================
 *
 * Q1: Strategy vs Factory?
 *
 *      Factory creates objects.
 *      Strategy chooses behavior/algorithm at runtime.
 *
 * Q2: Observer real-world Spring example?
 *
 *      ApplicationEventPublisher
 *      @EventListener
 *
 * Q3: Chain of Responsibility real-world example?
 *
 *      Spring Security Filter Chain.
 *
 * Q4: Template Method real-world example?
 *
 *      JdbcTemplate, RestTemplate.
 *
 * Q5: Command pattern example in Java?
 *
 *      Runnable and ExecutorService.
 *
 * Q6: State vs Strategy?
 *
 *      Strategy changes algorithm.
 *      State changes behavior based on object's current state.
 *
 * -------------------------------------------------------------------------------------
 * ONE-LINER SUMMARY
 * -------------------------------------------------------------------------------------
 *
 * Behavioral Design Patterns define how objects communicate and collaborate.
 * The most common patterns used in Java and Spring applications are Strategy,
 * Observer, Template Method, Chain of Responsibility, Command, State and Iterator.
 *
 * =====================================================================================
 */
public class BehavioralDesignPatterns {
}
