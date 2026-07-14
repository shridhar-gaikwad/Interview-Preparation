package com.preperation.microservice.msPatterns;

import java.util.ArrayDeque;
import java.util.Deque;

/*
 * =====================================================================================
 *                                    SAGA PATTERN
 * =====================================================================================
 *
 * WHAT IS IT?
 * -----------
 * A way to manage DISTRIBUTED TRANSACTIONS across multiple microservices using a
 * SEQUENCE OF SMALLER LOCAL TRANSACTIONS, where each step has a COMPENSATING ACTION
 * that undoes it if a later step fails.
 *
 * WHY IS IT NEEDED?
 * -----------------
 * In microservices:
 *   - Each service has its OWN database.
 *   - There is NO global ACID transaction across services (no 2-phase commit in practice).
 *
 * Example: Order Service -> Payment Service -> Inventory Service
 *   Q: If Payment fails, how do you undo the Order that was already created?
 *   A: Saga -> break one big transaction into steps; on failure, run compensations
 *      (undo) for the steps already completed.
 *
 * -------------------------------------------------------------------------------------
 * SIMPLE EXAMPLE (order flow)
 * -------------------------------------------------------------------------------------
 *   Create Order        (OK)
 *   Deduct Inventory    (OK)
 *   Process Payment     (FAILS)
 *   ---- now compensate in REVERSE order ----
 *   Undo Inventory      (add stock back)
 *   Cancel Order
 *
 *   Forward:  Step1 -> Step2 -> Step3 (fails)
 *   Undo:            Undo2  <- Undo1     (reverse order = LIFO)
 *
 * -------------------------------------------------------------------------------------
 * TWO TYPES OF SAGA (VERY IMPORTANT)
 * -------------------------------------------------------------------------------------
 * 1) CHOREOGRAPHY (event-driven):
 *      - No central coordinator. Each service listens to events and emits new events.
 *      - Order -> emits "OrderCreated" -> Payment reacts -> emits "PaymentDone" -> ...
 *      + Loosely coupled, no single point of control.
 *      - Hard to track/debug the overall flow (logic is spread across services).
 *
 * 2) ORCHESTRATION (MOST ASKED):
 *      - A central ORCHESTRATOR tells each service what to do, step by step.
 *          Orchestrator -> Order Service
 *                       -> Payment Service
 *                       -> Inventory Service
 *      + Easy to manage, centralized control, easy to see the whole flow.
 *      - The orchestrator is a single point of control (must be made resilient).
 *
 * -------------------------------------------------------------------------------------
 * INTERVIEW GOLD
 * -------------------------------------------------------------------------------------
 * - COMPENSATING TRANSACTION: an action that UNDOES a completed step.
 *     e.g. Payment failed -> Refund -> Add inventory back -> Cancel order.
 * - EVENTUAL CONSISTENCY: data is not consistent instantly; it becomes consistent
 *   after all steps (or compensations) finish. Saga trades strong consistency for it.
 * - USED IN: E-commerce, Banking, Booking/Reservation systems.
 *
 * REAL-WORLD ARCHITECTURE (choreography with Kafka):
 *      Client -> API Gateway -> Order Service -> Kafka -> Payment -> Kafka -> Inventory
 *
 * -------------------------------------------------------------------------------------
 * QUICK Q&A
 * -------------------------------------------------------------------------------------
 * Q: What is the Saga Pattern?
 *    -> Manage distributed transactions via a series of local transactions plus
 *       compensating actions for rollback.
 * Q: Choreography vs Orchestration?
 *    -> Event-driven (no coordinator) vs central controller.
 * Q: What is eventual consistency?
 *    -> Data becomes consistent after some time, not immediately.
 *
 * -------------------------------------------------------------------------------------
 * EXAMPLE BELOW:
 * A minimal ORCHESTRATION-style saga. Each step registers its compensation on a stack;
 * if any step throws, we pop the stack and run compensations in REVERSE (LIFO) order.
 * =====================================================================================
 */
public class SagaPattern {

    /** One step of a saga: an action to run and the compensation that undoes it. */
    static class SagaStep {
        final String name;
        final Runnable action;         // forward action (local transaction)
        final Runnable compensation;   // undo action (compensating transaction)

        SagaStep(String name, Runnable action, Runnable compensation) {
            this.name = name;
            this.action = action;
            this.compensation = compensation;
        }
    }

    /** A very small orchestrator that runs steps and rolls back on failure. */
    static class SagaOrchestrator {
        // Stack of compensations for the steps that succeeded so far (LIFO undo).
        private final Deque<SagaStep> completed = new ArrayDeque<>();

        void run(java.util.List<SagaStep> steps) {
            try {
                for (SagaStep step : steps) {
                    System.out.println("-> Executing: " + step.name);
                    step.action.run();            // may throw -> triggers compensation
                    completed.push(step);         // remember it so we can undo later
                }
                System.out.println("SAGA COMPLETED SUCCESSFULLY\n");
            } catch (RuntimeException failure) {
                System.out.println("!! Step failed: " + failure.getMessage());
                System.out.println("Rolling back completed steps (reverse order):");
                compensate();
            }
        }

        private void compensate() {
            // Undo in the exact reverse order of execution.
            while (!completed.isEmpty()) {
                SagaStep step = completed.pop();
                System.out.println("<- Compensating: " + step.name);
                step.compensation.run();
            }
            System.out.println("SAGA ROLLED BACK (eventual consistency restored)\n");
        }
    }

    public static void main(String[] args) {
        // ---- Scenario 1: happy path (everything succeeds) ----
        System.out.println("== Scenario 1: all steps succeed ==");
        new SagaOrchestrator().run(java.util.List.of(
                new SagaStep("Create Order",
                        () -> System.out.println("   order created"),
                        () -> System.out.println("   order cancelled")),
                new SagaStep("Deduct Inventory",
                        () -> System.out.println("   inventory deducted"),
                        () -> System.out.println("   inventory restored")),
                new SagaStep("Process Payment",
                        () -> System.out.println("   payment charged"),
                        () -> System.out.println("   payment refunded"))
        ));

        // ---- Scenario 2: payment fails -> earlier steps get compensated ----
        System.out.println("== Scenario 2: payment fails -> rollback ==");
        new SagaOrchestrator().run(java.util.List.of(
                new SagaStep("Create Order",
                        () -> System.out.println("   order created"),
                        () -> System.out.println("   order cancelled")),
                new SagaStep("Deduct Inventory",
                        () -> System.out.println("   inventory deducted"),
                        () -> System.out.println("   inventory restored")),
                new SagaStep("Process Payment",
                        () -> { throw new RuntimeException("card declined"); }, // fails here
                        () -> System.out.println("   payment refunded"))
        ));
    }
}
