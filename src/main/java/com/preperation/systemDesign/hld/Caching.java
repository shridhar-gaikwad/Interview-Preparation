package com.preperation.systemDesign.hld;

import java.util.LinkedHashMap;
import java.util.Map;

/*
 * =====================================================================================
 *                                     CACHING
 * =====================================================================================
 *
 * WHAT IS IT?
 * -----------
 * Caching = storing frequently used data in a FAST-ACCESS place so future requests are
 * served faster (without hitting the slow source, e.g. a database).
 *
 *      First time  -> data comes from DB   (slow)
 *      Next time   -> data comes from cache (fast)
 *
 * GOALS: reduce latency, reduce database load, improve performance.
 *
 * >>> NOTE ON THIS FILE <<<
 * The Java code below is a CONCEPTUAL / TEACHING MODEL (plain Java) that implements
 * cache-aside + an LRU cache so you can SEE how caching behaves. Real systems use
 * Redis / Memcached / Spring Cache (@Cacheable). See the bottom section.
 *
 * -------------------------------------------------------------------------------------
 * WHERE CAN CACHING HAPPEN? (layers)
 * -------------------------------------------------------------------------------------
 *   - Browser cache
 *   - CDN (edge cache)
 *   - Application (in-memory)
 *   - Distributed cache (Redis, Memcached)
 *   - Database query cache
 *
 * -------------------------------------------------------------------------------------
 * CACHING STRATEGIES (VERY IMPORTANT)
 * -------------------------------------------------------------------------------------
 * 1) CACHE ASIDE (Lazy Loading)  <-- MOST COMMON
 *      App checks cache first; on MISS -> fetch from DB -> store in cache -> return.
 *      Flow: Request -> Cache -> (miss) -> DB -> store in cache -> return.
 *
 * 2) WRITE THROUGH
 *      Write goes to cache AND DB together.
 *      + Cache always up-to-date   - Slower writes.
 *
 * 3) WRITE BACK (Write Behind)
 *      Write to cache only; DB updated asynchronously later.
 *      + Very fast writes          - Risk of data loss / inconsistency.
 *
 * 4) READ THROUGH
 *      The cache itself loads from DB on a miss; the app never talks to DB directly.
 *
 * -------------------------------------------------------------------------------------
 * CACHE EVICTION POLICIES (when the cache is full, what to remove?)
 * -------------------------------------------------------------------------------------
 *   - LRU (Least Recently Used)   <-- MOST POPULAR / most asked
 *   - LFU (Least Frequently Used)
 *   - FIFO (First In First Out)
 *   - TTL (Time-based expiration)
 *
 * -------------------------------------------------------------------------------------
 * DISTRIBUTED CACHING (used in microservices)
 * -------------------------------------------------------------------------------------
 *   Tools: Redis (most popular), Memcached.
 *   Why: multiple servers/instances SHARE the same cache (consistent + scalable).
 *
 * CACHE CONSISTENCY (keep cache & DB from mismatching):
 *   - Strong consistency
 *   - Eventual consistency  <-- most common
 *
 * -------------------------------------------------------------------------------------
 * QUICK Q&A (INTERVIEW)
 * -------------------------------------------------------------------------------------
 * Q1: Which caching strategy would you use?  -> Cache Aside (most flexible & widely used).
 * Q2: How do you handle stale data?          -> TTL + invalidation.
 * Q3: Redis vs in-memory cache?              -> Redis = distributed & scalable;
 *                                               in-memory = fast but limited to one node.
 * Q4: What is cache stampede?                -> Many requests hit the DB at once right
 *                                               after a hot key expires.
 * Q5: When NOT to use caching?               -> Frequently changing data; real-time
 *                                               systems needing strong consistency.
 * Q6: Why Redis over in-memory?              -> Distributed, shared across instances, scalable.
 * Q7: What is @CacheEvict?                   -> Removes an entry when data changes, e.g.:
 *        @CacheEvict(value = "products", key = "#id")
 *        public void deleteProduct(int id) { ... }
 *
 * ONE-LINER SUMMARY:
 * Caching improves performance by storing frequently accessed data, but requires careful
 * handling of consistency, eviction and invalidation.
 *
 * -------------------------------------------------------------------------------------
 * EXAMPLE BELOW:
 * (1) An LRU cache built on LinkedHashMap (access-order) - demonstrates eviction.
 * (2) A cache-aside lookup showing MISS -> DB -> store, then HIT on the next call.
 * =====================================================================================
 */
public class Caching {

    /**
     * LRU cache: keeps at most 'capacity' entries; the LEAST RECENTLY USED entry is
     * evicted when full. Built on LinkedHashMap with accessOrder=true, which reorders
     * entries on every get() so the eldest = least recently used.
     */
    static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private final int capacity;
        LRUCache(int capacity) {
            super(capacity, 0.75f, true); // true = access-order (LRU behaviour)
            this.capacity = capacity;
        }
        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            boolean evict = size() > capacity;
            if (evict) System.out.println("   [evict LRU] removing " + eldest.getKey());
            return evict; // when true, LinkedHashMap drops the eldest entry
        }
    }

    /** Stands in for a slow database. */
    static class Database {
        String read(String key) {
            System.out.println("   [DB] slow read for " + key);
            return "value-of-" + key;
        }
    }

    /**
     * CACHE-ASIDE: check cache first; on a miss, load from DB and populate the cache.
     * This is the most common real-world strategy.
     */
    static class ProductService {
        private final LRUCache<String, String> cache = new LRUCache<>(2);
        private final Database db = new Database();

        String getProduct(String id) {
            String cached = cache.get(id);
            if (cached != null) {                 // CACHE HIT
                System.out.println("   [cache HIT] " + id);
                return cached;
            }
            System.out.println("   [cache MISS] " + id);
            String value = db.read(id);            // fallback to source of truth
            cache.put(id, value);                  // populate cache for next time
            return value;
        }
    }

    public static void main(String[] args) {
        ProductService service = new ProductService();

        System.out.println("== Cache-aside: first call misses, second call hits ==");
        service.getProduct("p1"); // MISS -> DB
        service.getProduct("p1"); // HIT  -> cache

        System.out.println("\n== LRU eviction (capacity = 2) ==");
        service.getProduct("p2"); // cache now holds {p1, p2}
        service.getProduct("p3"); // adding p3 evicts the least recently used (p1)
        service.getProduct("p1"); // p1 was evicted -> MISS again
    }
}

/*
 * =====================================================================================
 *          HOW THIS IS DONE IN A BIG (REAL) APPLICATION  -- CONCEPTUAL
 * =====================================================================================
 *
 *   CONCERN                REAL TOOL / TECH
 *   --------------------   --------------------------------
 *   Distributed cache      Redis (most common) / Memcached
 *   Spring integration     Spring Cache abstraction (@Cacheable, @CachePut, @CacheEvict)
 *   Eviction               Redis maxmemory-policy (allkeys-lru, etc.) / TTL per key
 *   Consistency            TTL + explicit invalidation on writes (eventual consistency)
 *   Cache stampede guard   request coalescing / locks / slightly randomized TTL
 *   CDN / edge cache       Cloudflare / Akamai / CloudFront for static & GET responses
 *
 * TYPICAL FLOW (cache-aside with Redis):
 *   Request -> check Redis -> HIT: return
 *                          -> MISS: read DB -> write to Redis with TTL -> return
 *   On update/delete: write DB then evict/refresh the Redis key (@CacheEvict).
 *
 * SPRING EXAMPLE:
 *      @Cacheable(value = "products", key = "#id")   // cache the result
 *      public Product getProduct(int id) { ... }
 *
 *      @CacheEvict(value = "products", key = "#id")   // invalidate on change
 *      public void deleteProduct(int id) { ... }
 *
 * KEY TAKEAWAY: In production you use Redis + Spring Cache annotations, choose an
 * eviction policy (usually LRU) and a TTL, and invalidate on writes to stay consistent.
 * =====================================================================================
 */
