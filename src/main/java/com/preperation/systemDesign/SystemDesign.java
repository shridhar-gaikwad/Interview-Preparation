package com.preperation.systemDesign;

/*
 * =====================================================================================
 *                      SYSTEM DESIGN - OVERVIEW & FUNDAMENTALS
 * =====================================================================================
 *
 * WHAT IS IT?
 * -----------
 * System Design = deciding the ARCHITECTURE, COMPONENTS and DATA FLOW of a system so it
 * meets requirements and scales (users, data, traffic) reliably.
 *
 * >>> NOTE ON THIS FILE <<<
 * This is a HIGH-LEVEL INDEX of the fundamentals (interview perspective). It intentionally
 * stays shallow - the DEEP notes live in the sibling classes:
 *      hld.Caching, hld.LoadBalancing, hld.RateLimiting,
 *      hld.NetworkingAndProtocols, hld.AsynchronousCommunication
 *      lld.SolidPrinciples, lld.DesignPatterns
 *
 * -------------------------------------------------------------------------------------
 * HLD vs LLD (know the difference)
 * -------------------------------------------------------------------------------------
 *   HLD (High-Level Design)  -> the big picture: services, DBs, caches, queues, APIs,
 *                               how data flows. "What are the boxes and arrows?"
 *   LLD (Low-Level Design)   -> class/method design of ONE component: OOP, SOLID,
 *                               design patterns. "How is this module coded?"
 *
 * -------------------------------------------------------------------------------------
 * NON-FUNCTIONAL GOALS (the vocabulary interviewers expect)
 * -------------------------------------------------------------------------------------
 *   - Scalability      (handle more load: VERTICAL = bigger machine, HORIZONTAL = more machines)
 *   - Availability     (system stays up; measured in "nines", e.g. 99.9%)
 *   - Reliability      (works correctly, no data loss)
 *   - Latency          (time for ONE request)     vs
 *     Throughput       (requests handled per second)
 *   - Consistency      (every read sees the latest write)
 *   - Durability       (once saved, data survives crashes)
 *   - Maintainability  (easy to change/observe/debug)
 *
 * -------------------------------------------------------------------------------------
 * CAP THEOREM (classic interview question)
 * -------------------------------------------------------------------------------------
 *   In a distributed system you can guarantee only 2 of 3 during a network partition:
 *      C - Consistency | A - Availability | P - Partition tolerance
 *   Network partitions are unavoidable -> you really choose between C and A:
 *      CP (e.g. HBase, MongoDB*) -> stay consistent, may reject requests
 *      AP (e.g. Cassandra, DynamoDB) -> stay available, may serve stale data
 *   Related: BASE (eventual consistency) vs ACID (strong DB transactions).
 *
 * -------------------------------------------------------------------------------------
 * CORE BUILDING BLOCKS (each maps to a deeper note)
 * -------------------------------------------------------------------------------------
 *   - Load Balancer     -> spread traffic across servers        (see hld.LoadBalancing)
 *   - Caching           -> serve hot data fast, cut DB load      (see hld.Caching)
 *   - Rate Limiting     -> protect against abuse/overload        (see hld.RateLimiting)
 *   - Async / Messaging -> decouple via queues/events (Kafka)    (see hld.AsynchronousCommunication)
 *   - Networking        -> HTTP, TCP/UDP, DNS, REST vs gRPC      (see hld.NetworkingAndProtocols)
 *   - API Gateway       -> single entry: routing, auth, limits
 *   - CDN               -> cache static content near users
 *   - Database          -> SQL (relational, ACID) vs NoSQL (scale, flexible schema)
 *   - Object/Blob store -> large files/media (S3-style)
 *
 * -------------------------------------------------------------------------------------
 * DATABASE SCALING (very common follow-up)
 * -------------------------------------------------------------------------------------
 *   - Indexing          -> speed up reads
 *   - Replication       -> copies for read scaling + failover (leader/follower)
 *   - Sharding/Partition-> split data across nodes by a key (horizontal scaling)
 *   - Read/Write split  -> writes to primary, reads from replicas
 *   - Denormalization   -> trade storage for fewer joins (faster reads)
 *
 * -------------------------------------------------------------------------------------
 * HOW TO ANSWER A SYSTEM DESIGN QUESTION (a repeatable framework)
 * -------------------------------------------------------------------------------------
 *   1. Requirements    -> clarify FUNCTIONAL + NON-FUNCTIONAL; define scope early.
 *   2. Estimations     -> back-of-envelope: users, QPS, storage, bandwidth.
 *   3. APIs            -> define the key endpoints (inputs/outputs).
 *   4. Data model      -> pick SQL vs NoSQL; sketch main tables/entities.
 *   5. High-level design-> draw components: client -> LB -> services -> cache/DB -> queue.
 *   6. Deep dive       -> zoom into 1-2 areas (sharding, caching, consistency).
 *   7. Bottlenecks     -> discuss scaling, single points of failure, trade-offs.
 *
 * -------------------------------------------------------------------------------------
 * QUICK Q&A (INTERVIEW)
 * -------------------------------------------------------------------------------------
 * Q1: Vertical vs horizontal scaling? -> Bigger machine vs more machines (horizontal
 *     scales further and is fault tolerant).
 * Q2: Latency vs throughput? -> Time per request vs requests per second.
 * Q3: What is the CAP theorem? -> Pick 2 of Consistency/Availability/Partition-tolerance;
 *     under a partition you choose C or A.
 * Q4: SQL vs NoSQL? -> SQL = structured + ACID + joins; NoSQL = flexible schema + easy
 *     horizontal scale + eventual consistency.
 * Q5: How to scale reads? -> Caching + read replicas + CDN.
 * Q6: How to scale writes? -> Sharding/partitioning + async processing (queues).
 * Q7: How to avoid a single point of failure? -> Redundancy, replication, load balancing,
 *     health checks + failover.
 * Q8: HLD vs LLD? -> Architecture (boxes & arrows) vs class-level design (SOLID/patterns).
 *
 * ONE-LINER SUMMARY:
 * System design = pick components and data flow that meet functional needs while balancing
 * scalability, availability and consistency - always a game of TRADE-OFFS.
 *
 * -------------------------------------------------------------------------------------
 * The main() below just prints the answering framework as a quick revision checklist.
 * =====================================================================================
 */
public class SystemDesign {

    public static void main(String[] args) {
        String[] steps = {
                "1. Requirements  (functional + non-functional, scope)",
                "2. Estimations   (users, QPS, storage, bandwidth)",
                "3. APIs          (key endpoints)",
                "4. Data model    (SQL vs NoSQL, entities)",
                "5. High-level    (client -> LB -> services -> cache/DB -> queue)",
                "6. Deep dive     (sharding, caching, consistency)",
                "7. Bottlenecks   (scaling, SPOF, trade-offs)"
        };
        System.out.println("== System Design Interview Framework ==");
        for (String step : steps) {
            System.out.println("  " + step);
        }
    }
}
