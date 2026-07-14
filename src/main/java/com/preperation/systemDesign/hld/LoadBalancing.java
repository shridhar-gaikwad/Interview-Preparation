package com.preperation.systemDesign.hld;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * =====================================================================================
 *                                  LOAD BALANCING
 * =====================================================================================
 *
 * WHAT IS IT?
 * -----------
 * Load balancing = distributing incoming requests/traffic across MULTIPLE servers so
 * that no single server gets overloaded -> better performance, availability & scalability.
 *
 * In Java it isn't a language feature; it's achieved via algorithms, frameworks, or
 * infrastructure (proxy/gateway/service discovery).
 *
 * >>> NOTE ON THIS FILE <<<
 * The Java code below is a CONCEPTUAL / TEACHING MODEL (plain Java) implementing a few
 * client-side algorithms (round robin, least connections) + health checks so you can SEE
 * how a balancer picks a server. Real systems use Nginx / AWS ELB / Spring Cloud
 * LoadBalancer + service discovery (see bottom section).
 *
 * -------------------------------------------------------------------------------------
 * TYPES / APPROACHES
 * -------------------------------------------------------------------------------------
 * 1) CLIENT-SIDE LB (very common):
 *      The client keeps a list of servers and picks one using an algorithm.
 *          int index = (counter++) % servers.size();
 *      Tools: Spring Cloud LoadBalancer, Netflix Ribbon (older).
 *
 * 2) SERVER-SIDE LB:
 *      A separate component receives all requests and forwards to backends.
 *      Tools: Nginx, HAProxy, AWS ELB / ALB.
 *
 * 3) DNS-BASED LB:
 *      One domain resolves to multiple IPs; client picks one.
 *          api.example.com -> 10.1.1.1, 10.1.1.2
 *
 * 4) LB via SERVICE DISCOVERY (microservices):
 *      Services register; client queries the registry and picks an instance.
 *      Tools: Eureka, Consul, Kubernetes service discovery.
 *
 * 5) LB via REVERSE PROXY / API GATEWAY:
 *      The gateway distributes requests. Tools: Spring Cloud Gateway, Zuul, Kong.
 *
 * 6) THREAD-LEVEL LB (inside a Java app):
 *      Thread pools distribute TASKS (not HTTP requests).
 *          ExecutorService executor = Executors.newFixedThreadPool(5);
 *
 * 7) MESSAGE-QUEUE-BASED LB:
 *      Multiple workers/consumers pull jobs from a queue; each picks the next task.
 *      Tools: Kafka (distributes partitions across a consumer group), RabbitMQ.
 *
 * -------------------------------------------------------------------------------------
 * LOAD BALANCING ALGORITHMS
 * -------------------------------------------------------------------------------------
 *   - Round Robin              (cycle through servers in order)
 *   - Weighted Round Robin     (bigger servers get more traffic)
 *   - Least Connections        (send to the server with fewest active connections) *asked*
 *   - Least Response Time      (send to the fastest responder)
 *   - IP Hash                  (same client IP -> same server, sticky sessions)
 *   - Random
 *
 * -------------------------------------------------------------------------------------
 * SUPPORTING CONCEPTS
 * -------------------------------------------------------------------------------------
 * HEALTH CHECKS: the LB must know which servers are alive.
 *      Active (ping/heartbeat) | Passive (monitor failures). Dead server -> removed from pool.
 * HORIZONTAL vs VERTICAL SCALING:
 *      Vertical = bigger machine (more CPU/RAM); Horizontal = add more servers (used WITH LB).
 *      Load balancing is mainly used with HORIZONTAL scaling.
 * AUTO SCALING: LB + dynamically add/remove instances as traffic rises/falls.
 * RATE LIMITING & THROTTLING: often handled at the LB / API Gateway to prevent overload.
 *
 * -------------------------------------------------------------------------------------
 * QUICK Q&A (INTERVIEW)
 * -------------------------------------------------------------------------------------
 * Q: What is load balancing?         -> Distribute requests across servers for performance,
 *                                       availability and scalability.
 * Q: Types?                          -> Client-side (Spring Cloud LoadBalancer),
 *                                       server-side (Nginx, AWS ELB).
 * Q: How does Kafka load balance?    -> Distributes topic partitions across consumers in a group.
 * Q: Role of partitions?             -> Enable parallel processing and load distribution.
 * Q: What is a Single Point of Failure? -> If the LB fails, the whole system fails.
 *                                       Solution: multiple load balancers (HA setup).
 * Q: Can an API Gateway load balance?-> Yes, using service discovery + an algorithm.
 *
 * ONE-LINER SUMMARY:
 * In Java, load balancing is achieved via client-side logic, infrastructure
 * (proxy/gateway), service discovery, or internal task distribution.
 *
 * -------------------------------------------------------------------------------------
 * EXAMPLE BELOW:
 * A client-side balancer supporting ROUND ROBIN and LEAST CONNECTIONS, plus a simple
 * health check that removes a dead server from the pool.
 * =====================================================================================
 */
public class LoadBalancing {

    /** A backend server with a health flag and a live connection count. */
    static class Server {
        final String name;
        boolean healthy = true;
        int activeConnections = 0;
        Server(String name) { this.name = name; }
    }

    /** Client-side load balancer over a pool of servers. */
    static class LoadBalancer {
        private final List<Server> servers;
        private final AtomicInteger rrCounter = new AtomicInteger(0);

        LoadBalancer(List<Server> servers) { this.servers = servers; }

        /** Only servers that pass the health check are eligible. */
        private List<Server> healthy() {
            return servers.stream().filter(s -> s.healthy).toList();
        }

        /** ROUND ROBIN: cycle through healthy servers in order. */
        Server roundRobin() {
            List<Server> pool = healthy();
            int idx = Math.floorMod(rrCounter.getAndIncrement(), pool.size());
            return pool.get(idx);
        }

        /** LEAST CONNECTIONS: pick the healthy server with the fewest active connections. */
        Server leastConnections() {
            return healthy().stream()
                    .min((a, b) -> Integer.compare(a.activeConnections, b.activeConnections))
                    .orElseThrow();
        }
    }

    public static void main(String[] args) {
        List<Server> servers = List.of(new Server("S1"), new Server("S2"), new Server("S3"));
        LoadBalancer lb = new LoadBalancer(servers);

        System.out.println("== Round Robin (cycles S1 -> S2 -> S3 -> ...) ==");
        for (int i = 1; i <= 6; i++) {
            System.out.println("req " + i + " -> " + lb.roundRobin().name);
        }

        System.out.println("\n== Health check: mark S2 DOWN, it is skipped ==");
        servers.get(1).healthy = false; // S2 goes down
        for (int i = 1; i <= 4; i++) {
            System.out.println("req " + i + " -> " + lb.roundRobin().name);
        }
        servers.get(1).healthy = true; // recover for the next demo

        System.out.println("\n== Least Connections (uneven existing load) ==");
        Map<String, Server> byName = servers.stream()
                .collect(java.util.stream.Collectors.toMap(s -> s.name, s -> s));
        byName.get("S1").activeConnections = 5;
        byName.get("S2").activeConnections = 2;
        byName.get("S3").activeConnections = 8;
        Server chosen = lb.leastConnections();
        System.out.println("connections -> S1=5, S2=2, S3=8; least-connections picks: " + chosen.name);
    }
}

/*
 * =====================================================================================
 *          HOW THIS IS DONE IN A BIG (REAL) APPLICATION  -- CONCEPTUAL
 * =====================================================================================
 *
 *   CONCERN                REAL TOOL / TECH
 *   --------------------   --------------------------------
 *   Server-side LB         Nginx / HAProxy / AWS ELB / ALB
 *   Client-side LB         Spring Cloud LoadBalancer (Ribbon is legacy)
 *   Service discovery      Eureka / Consul / Kubernetes (resolve healthy instances)
 *   Gateway LB             Spring Cloud Gateway / Kong (lb://SERVICE-NAME)
 *   Queue-based LB         Kafka consumer groups / RabbitMQ workers
 *   Health checks          LB probes (HTTP/TCP) + auto-remove/auto-add on recovery
 *   Elastic capacity       Auto Scaling groups (K8s HPA, AWS ASG)
 *
 * TYPICAL FLOW (microservices):
 *   Client -> DNS -> external LB (Nginx/ELB) -> API Gateway
 *          -> Spring Cloud LoadBalancer asks service discovery for healthy instances
 *          -> picks one (round robin / least connections) -> forwards the request
 *   Dead instances fail health checks and are removed; autoscaling adds instances on load.
 *   Run MULTIPLE load balancers (HA) so the LB itself isn't a single point of failure.
 *
 * KEY TAKEAWAY: real load balancing = infrastructure LBs + service discovery + health
 * checks + autoscaling; you rarely hand-code the algorithm, you configure these tools.
 * =====================================================================================
 */
