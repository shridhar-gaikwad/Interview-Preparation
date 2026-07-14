package com.preperation.microservice.msPatterns;

/*
 * =====================================================================================
 *                              CIRCUIT BREAKER PATTERN
 * =====================================================================================
 *
 * WHAT IS IT?
 * -----------
 * A resiliency design pattern that PREVENTS a system from repeatedly calling a failing
 * service, thereby avoiding CASCADING FAILURES across the whole system.
 *
 * REAL-LIFE ANALOGY (electric circuit breaker):
 *   - Current overload happens  -> circuit trips
 *   - Stops the flow            -> prevents damage
 *   - After some time           -> allows a retry
 *
 * PROBLEM WITHOUT CIRCUIT BREAKER:
 *   Service A -> Service B (DOWN)
 *              retries again & again
 *              threads get exhausted (blocked waiting on timeouts)
 *              -> the WHOLE system crashes (cascading failure)
 *
 * WITH CIRCUIT BREAKER:
 *   Service A -> [Circuit Breaker] -> Service B
 *                      |
 *               (fail fast + fallback)   <-- returns quickly instead of hanging
 *
 * -------------------------------------------------------------------------------------
 * THE THREE STATES (MOST ASKED IN INTERVIEWS)
 * -------------------------------------------------------------------------------------
 * 1) CLOSED  (Normal):
 *      - Requests flow normally to the downstream service.
 *      - Failures are counted/monitored.
 *      - If failures cross a threshold -> transition to OPEN.
 *
 * 2) OPEN    (Failure mode):
 *      - Breaker is "tripped". All requests are REJECTED IMMEDIATELY (fail fast).
 *      - No calls reach the failing service (gives it time to recover).
 *      - After a "wait duration" -> transition to HALF-OPEN.
 *
 * 3) HALF-OPEN (Recovery / Testing):
 *      - Allows a LIMITED number of trial requests through.
 *      - If they SUCCEED -> go back to CLOSED (service recovered).
 *      - If they FAIL    -> go back to OPEN (keep blocking).
 *
 *   State transition diagram:
 *
 *        failures >= threshold
 *   CLOSED ---------------------> OPEN
 *     ^                            |
 *     | trial success             | wait timeout elapsed
 *     |                            v
 *     +--------------------- HALF-OPEN
 *              (trial failure -> back to OPEN)
 *
 * -------------------------------------------------------------------------------------
 * KEY CONCEPTS
 * -------------------------------------------------------------------------------------
 * - FALLBACK: A default/cached response returned when the service fails or breaker is
 *   open (e.g. "Service temporarily unavailable, please try later").
 * - FAIL FAST: Reject instantly instead of blocking threads on slow/timed-out calls.
 *
 * JAVA LIBRARY: Resilience4j (modern, lightweight). (Older: Netflix Hystrix - deprecated)
 * WHERE TO IMPLEMENT: Service layer (service-to-service calls) AND at the API Gateway.
 *
 * -------------------------------------------------------------------------------------
 * QUICK Q&A
 * -------------------------------------------------------------------------------------
 * Q: What is Circuit Breaker?
 *    -> Prevents repeated calls to failing services by stopping requests temporarily.
 * Q: Explain its states.
 *    -> Closed = normal, Open = blocking (fail fast), Half-open = testing recovery.
 * Q: What is a fallback?
 *    -> A default response returned when the service fails / breaker is open.
 * Q: Which Java library is used?
 *    -> Resilience4j.
 * Q: Where to implement it?
 *    -> Service layer and API Gateway.
 *
 * -------------------------------------------------------------------------------------
 * EXAMPLE BELOW:
 * A minimal, self-contained (no external libs) circuit breaker so you can SEE how the
 * three states behave. In real projects you would use Resilience4j annotations like:
 *
 *      @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
 *      public String callPayment() { ... }
 *
 *      public String paymentFallback(Throwable t) { return "Payment unavailable"; }
 * =====================================================================================
 */
public class CircuitBreakerPatter {

    /* The three states a breaker can be in. */
    enum State { CLOSED, OPEN, HALF_OPEN }

    /**
     * A tiny hand-written circuit breaker to demonstrate the mechanics.
     * NOTE: Not production-grade (not thread-safe, no sliding window) - purely educational.
     */
    static class SimpleCircuitBreaker {
        private State state = State.CLOSED;
        private int failureCount = 0;

        private final int failureThreshold;   // failures allowed before OPEN
        private final long openDurationMs;     // how long to stay OPEN before HALF_OPEN
        private long openedAtMs = 0;           // timestamp when breaker opened

        SimpleCircuitBreaker(int failureThreshold, long openDurationMs) {
            this.failureThreshold = failureThreshold;
            this.openDurationMs = openDurationMs;
        }

        /**
         * Executes the given call through the breaker.
         * @param call     the (possibly failing) downstream operation
         * @param fallback the default response used when the breaker blocks or the call fails
         */
        String execute(java.util.concurrent.Callable<String> call, String fallback) {
            // If OPEN, check whether the wait window has elapsed -> move to HALF_OPEN.
            if (state == State.OPEN) {
                if (System.currentTimeMillis() - openedAtMs >= openDurationMs) {
                    state = State.HALF_OPEN;
                    System.out.println("   [breaker] wait elapsed -> HALF_OPEN (allowing a trial)");
                } else {
                    // Still OPEN -> FAIL FAST, return fallback without touching the service.
                    System.out.println("   [breaker] OPEN -> fail fast, returning fallback");
                    return fallback;
                }
            }

            try {
                String result = call.call();   // actually call the downstream service
                onSuccess();                   // success path
                return result;
            } catch (Exception e) {
                onFailure();                   // failure path (count it, maybe trip)
                System.out.println("   [breaker] call failed: " + e.getMessage() + " -> fallback");
                return fallback;
            }
        }

        private void onSuccess() {
            // A success in HALF_OPEN means the service recovered -> close the breaker.
            if (state == State.HALF_OPEN) {
                System.out.println("   [breaker] trial success -> CLOSED (recovered)");
            }
            state = State.CLOSED;
            failureCount = 0;
        }

        private void onFailure() {
            failureCount++;
            // A failure in HALF_OPEN immediately re-opens the breaker.
            // In CLOSED, we open only after crossing the threshold.
            if (state == State.HALF_OPEN || failureCount >= failureThreshold) {
                state = State.OPEN;
                openedAtMs = System.currentTimeMillis();
                System.out.println("   [breaker] threshold reached -> OPEN (blocking calls)");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Open after 3 failures; stay open for 2 seconds before allowing a trial.
        SimpleCircuitBreaker breaker = new SimpleCircuitBreaker(3, 2000);

        // Simulated remote service that is DOWN for the first several calls, then recovers.
        final boolean[] serviceHealthy = { false };
        java.util.concurrent.Callable<String> paymentService = () -> {
            if (!serviceHealthy[0]) throw new RuntimeException("PaymentService DOWN");
            return "Payment OK";
        };
        String fallback = "FALLBACK: payment queued, try later";

        System.out.println("== Phase 1: service is DOWN (watch it trip to OPEN) ==");
        for (int i = 1; i <= 5; i++) {
            System.out.println("Call " + i + " -> " + breaker.execute(paymentService, fallback));
        }

        System.out.println("\n==s Phase 2: wait for OPEN window to elapse, then service recovers ==");
        Thread.sleep(2100);          // let the OPEN window pass -> next call becomes a HALF_OPEN trial
        serviceHealthy[0] = true;     // downstream service is healthy again

        for (int i = 6; i <= 8; i++) {
            System.out.println("Call " + i + " -> " + breaker.execute(paymentService, fallback));
        }
    }
}
