package com.preperation.systemDesign.hld;

/*
 * =====================================================================================
 *                                   RATE LIMITING
 * =====================================================================================
 *
 * WHAT IS IT?
 * -----------
 * Rate limiting controls HOW MANY requests a user/client can make in a given time.
 *      Example: max 100 requests per minute.
 *      If exceeded -> return HTTP 429 (Too Many Requests).
 *
 * WHY IT MATTERS:
 *   - Prevent system overload
 *   - Avoid DDoS attacks
 *   - Stop API abuse / bots
 *   - Ensure fair usage across many users
 *
 * >>> NOTE ON THIS FILE <<<
 * The Java code below is a CONCEPTUAL / TEACHING MODEL (plain Java, single instance,
 * not thread-safe) implementing the Token Bucket algorithm so you can SEE it work.
 * Real systems use Bucket4j / Guava / Redis / Spring Cloud Gateway (see bottom section).
 *
 * -------------------------------------------------------------------------------------
 * POPULAR ALGORITHMS
 * -------------------------------------------------------------------------------------
 * 1) FIXED WINDOW COUNTER
 *      Count requests in a fixed window (e.g. 1 min), reset each window.
 *      Issue: BURST at boundary (100 calls at 0:59 + 100 at 1:01 = 200 in ~2s).
 *
 * 2) SLIDING WINDOW LOG
 *      Store the timestamp of every request; count those within the last window.
 *      + Very accurate    - Memory heavy (stores every timestamp).
 *
 * 3) SLIDING WINDOW COUNTER (optimized)
 *      Combines fixed-window counters with weighting -> approximates sliding window.
 *      + Most practical balance of accuracy and cost.
 *
 * 4) TOKEN BUCKET  <-- VERY COMMON / most used
 *      Bucket holds tokens (capacity = burst limit). Each request consumes 1 token.
 *      Tokens refill at a fixed rate. If a token is available -> allow, else -> reject.
 *      + Allows short BURSTS while capping the average rate. Used in real systems.
 *
 * 5) LEAKY BUCKET
 *      Requests are processed at a constant rate (queue drains steadily).
 *      + Smooths traffic  - Does not allow bursts.
 *
 * -------------------------------------------------------------------------------------
 * KEY DIMENSIONS (limit can be based on)
 * -------------------------------------------------------------------------------------
 *   User ID | IP Address | API Key | Tenant (multi-tenant) | Endpoint (/login stricter)
 *
 * -------------------------------------------------------------------------------------
 * DISTRIBUTED RATE LIMITING (VERY IMPORTANT)
 * -------------------------------------------------------------------------------------
 *   Problem: with multiple instances, each would track its own counter (limit leaks).
 *   Solution: a CENTRAL store (Redis) holding shared counters/tokens -> one global limit.
 *
 * -------------------------------------------------------------------------------------
 * HTTP RESPONSE & HEADERS (when limit exceeded -> 429)
 * -------------------------------------------------------------------------------------
 *   X-RateLimit-Limit: 100        (max allowed)
 *   X-RateLimit-Remaining: 10     (left in window)
 *   X-RateLimit-Reset: 60         (seconds until reset)
 *
 * REAL-WORLD EXAMPLES:
 *   Login API: 5 req/min per user | Payment API: strict (anti-fraud)
 *   Public API freemium: Free 100 req/min, Paid 1000 req/min.
 *
 * JAVA / SPRING OPTIONS:
 *   In-memory: Bucket4j (very popular), Guava RateLimiter.
 *   Distributed: Redis + Lua scripts, Spring Cloud Gateway RequestRateLimiter.
 *
 * -------------------------------------------------------------------------------------
 * QUICK Q&A (INTERVIEW)
 * -------------------------------------------------------------------------------------
 * Q: What is rate limiting?         -> Control how many requests a client can make per time window.
 * Q: What happens when exceeded?    -> Server returns HTTP 429 (Too Many Requests).
 * Q: Common algorithms?             -> Fixed window, sliding window log, sliding window
 *                                      counter, token bucket, leaky bucket.
 * Q: Which is best?                 -> Token Bucket - allows bursts + smooth control.
 * Q: What is Token Bucket?          -> Tokens refill at a fixed rate; each request takes
 *                                      one; allow if available else reject.
 * Q: What is distributed rate limiting? -> Shared limit across instances via Redis.
 * Q: What is burst traffic?         -> Sudden spike in a short time (token bucket handles it).
 * Q: How to avoid bypass?           -> Don't rely on IP alone; use API key / JWT identity.
 * Q: Idempotency in rate limiting?  -> Retried requests must not cause duplicate operations.
 * Q: When NOT to use it?            -> Real-time uninterrupted flows; internal trusted systems.
 *
 * ONE-LINER SUMMARY:
 * Rate limiting protects systems by controlling request flow using algorithms like token
 * bucket, often implemented with Redis in distributed systems.
 *
 * -------------------------------------------------------------------------------------
 * EXAMPLE BELOW: a Token Bucket rate limiter demonstrating allow/reject + refill.
 * =====================================================================================
 */
public class RateLimiting {

    /**
     * TOKEN BUCKET limiter (single-instance, educational).
     * - capacity   = max tokens (also the max burst size)
     * - refillRate = tokens added per second
     * Each allowed request consumes one token; tokens regenerate over time.
     */
    static class TokenBucket {
        private final int capacity;
        private final double refillPerSecond;
        private double tokens;              // current tokens (fractional as they refill)
        private long lastRefillNanos;

        TokenBucket(int capacity, double refillPerSecond) {
            this.capacity = capacity;
            this.refillPerSecond = refillPerSecond;
            this.tokens = capacity;        // start full so an initial burst is allowed
            this.lastRefillNanos = System.nanoTime();
        }

        /** @return true if a token was available (request allowed), false -> HTTP 429. */
        boolean tryConsume() {
            refill();
            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }

        /** Add tokens based on elapsed time since the last check, capped at capacity. */
        private void refill() {
            long now = System.nanoTime();
            double elapsedSeconds = (now - lastRefillNanos) / 1_000_000_000.0;
            tokens = Math.min(capacity, tokens + elapsedSeconds * refillPerSecond);
            lastRefillNanos = now;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Capacity 5 (burst of 5), refills 2 tokens/second.
        TokenBucket bucket = new TokenBucket(5, 2);

        System.out.println("== Burst of 7 requests (only 5 tokens available) ==");
        for (int i = 1; i <= 7; i++) {
            boolean allowed = bucket.tryConsume();
            System.out.println("req " + i + " -> " + (allowed ? "200 OK" : "429 Too Many Requests"));
        }

        System.out.println("\n== Wait 1.5s so ~3 tokens refill (2/sec), then retry ==");
        Thread.sleep(1500);
        for (int i = 8; i <= 11; i++) {
            boolean allowed = bucket.tryConsume();
            System.out.println("req " + i + " -> " + (allowed ? "200 OK" : "429 Too Many Requests"));
        }
    }
}

/*
 * =====================================================================================
 *          HOW THIS IS DONE IN A BIG (REAL) APPLICATION  -- CONCEPTUAL
 * =====================================================================================
 *
 *   CONCERN                REAL TOOL / TECH
 *   --------------------   --------------------------------
 *   In-memory limiter      Bucket4j / Guava RateLimiter (single instance)
 *   Distributed limiter    Redis counters/tokens (+ Lua for atomicity)
 *   At the edge/gateway    Spring Cloud Gateway RequestRateLimiter, Nginx, API gateways
 *   Identity/key           API key / JWT (not IP alone) to prevent bypass
 *   Client feedback        HTTP 429 + X-RateLimit-* headers
 *
 * WHY REDIS FOR DISTRIBUTED:
 *   Many gateway/app instances must share ONE global counter. A local in-memory bucket
 *   per instance would let a user exceed the true limit N-fold. Redis holds the shared
 *   state; Lua scripts make "check-and-decrement" atomic to avoid race conditions.
 *
 * SPRING CLOUD GATEWAY (Redis-backed) EXAMPLE:
 *   filters:
 *     - name: RequestRateLimiter
 *       args:
 *         redis-rate-limiter.replenishRate: 5    # tokens added per second
 *         redis-rate-limiter.burstCapacity: 10   # max burst
 *         key-resolver: "#{@apiKeyResolver}"     # limit per API key / user
 *
 * KEY TAKEAWAY: pick Token Bucket for most cases, key the limit on user/API key, return
 * 429 with rate headers, and back it by Redis so the limit is global across instances.
 * =====================================================================================
 */
