package com.preperation.microservice.msPatterns;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * =====================================================================================
 *                                 API GATEWAY PATTERN
 * =====================================================================================
 * WHAT IS IT?
 * -----------
 * A design pattern where a SINGLE ENTRY POINT handles all client requests and ROUTES
 * them to the appropriate microservice. The client talks to only ONE component.
 *
 * INSTEAD OF (client knows every service):
 *      Client -> User Service
 *      Client -> Order Service
 *      Client -> Payment Service
 *
 * WE USE (client knows only the gateway):
 *      Client -> API Gateway -> Microservices
 *
 * -------------------------------------------------------------------------------------
 * WHY DO WE NEED IT? (cross-cutting concerns handled in ONE place)
 * -------------------------------------------------------------------------------------
 *   - Hides complexity of many services from the client
 *   - Centralized request handling
 *   - Security (authentication, JWT validation)
 *   - Load balancing
 *   - Rate limiting
 *   - Logging & monitoring
 *
 * -------------------------------------------------------------------------------------
 * CORE RESPONSIBILITIES (FEATURES)
 * -------------------------------------------------------------------------------------
 * 1) REQUEST ROUTING:        /users -> User Service, /orders -> Order Service
 * 2) AUTHENTICATION/AUTHZ:   validate JWT tokens; central security layer
 * 3) RATE LIMITING:          prevent abuse (DoS, bots); limits per user/API
 * 4) LOAD BALANCING:         spread traffic across service instances
 * 5) REQUEST AGGREGATION:    combine multiple service responses into one
 *                            (e.g. GET /dashboard -> user + order + payment -> merged)
 * 6) CACHING:                cache frequent responses
 * 7) LOGGING & MONITORING:   track all requests centrally
 *
 * FLOW:  Client -> API Gateway -> Service Discovery -> Microservices
 *
 * -------------------------------------------------------------------------------------
 * ADVANTAGES (INTERVIEW MUST)          |  DISADVANTAGES (also mention)
 * -------------------------------------------------------------------------------------
 *   + Simplifies client interaction    |  - Single point of failure
 *   + Centralized cross-cutting concerns|  - Extra network hop -> added latency
 *   + Improved security                |  - Complexity concentrated in gateway logic
 *   + Better scalability               |
 *   SOLUTION to SPOF: run MULTIPLE gateway instances (High Availability).
 *
 * -------------------------------------------------------------------------------------
 * TOOLS (Java):
 *   - Spring Cloud Gateway (modern, recommended)
 *   - Netflix Zuul (older)
 *   - Kong / Nginx
 *
 * SPRING CLOUD GATEWAY CONFIG (YAML) EXAMPLE:
 *   spring:
 *     cloud:
 *       gateway:
 *         routes:
 *           - id: user-service
 *             uri: lb://USER-SERVICE      # lb:// = load-balanced via service discovery
 *             predicates:
 *               - Path=/users/**          # /users/** is routed to the user service
 *
 * -------------------------------------------------------------------------------------
 * QUICK Q&A
 * -------------------------------------------------------------------------------------
 * Q: What is an API Gateway?
 *    -> A single entry point that routes client requests to microservices and handles
 *       cross-cutting concerns.
 * Q: Why use it?
 *    -> Simplifies client communication, centralizes security, supports rate limiting/caching.
 * Q: What is the BFF pattern?
 *    -> Backend For Frontend: a separate gateway per client type (mobile vs web).
 * Q: How does the gateway handle security?
 *    -> JWT validation at the gateway before forwarding.
 * Q: How does it load balance?
 *    -> Integrates with service discovery and uses a load-balancing algorithm across instances.
 * Q: What does lb:// mean?
 *    -> Enables load-balanced routing using service discovery.
 *
 * -------------------------------------------------------------------------------------
 * EXAMPLE BELOW:
 * A minimal in-memory gateway showing routing, JWT check, rate limiting, round-robin
 * load balancing, and request aggregation. No frameworks - purely to illustrate the ideas.
 * =====================================================================================
 * * >>> IMPORTANT NOTE ON THIS FILE <<<
 * The Java code below is a CONCEPTUAL / TEACHING MODEL - it makes every gateway
 * responsibility visible in ONE runnable file (plain Java, no frameworks, no real HTTP).
 * It is NOT how a real API Gateway is built. In real applications you do NOT hand-write
 * this logic; you use a config-driven, reactive, network proxy such as Spring Cloud
 * Gateway / Kong / Nginx that plugs into service discovery, Redis and a circuit breaker.
 * See the "HOW THIS IS DONE IN A BIG (REAL) APPLICATION" section at the BOTTOM of the file.
 *
 */
public class APIGateway {

    /** A downstream service exposing several instances (for load balancing). */
    static class Service {
        final String name;
        final List<String> instances;   // e.g. host:port of each running instance
        Service(String name, List<String> instances) {
            this.name = name;
            this.instances = instances;
        }
    }

    /** The gateway: single entry point that applies cross-cutting concerns then routes. */
    static class Gateway {
        // Routing table: path prefix -> target service (feature #1 Request Routing).
        private final Map<String, Service> routes;
        // Round-robin counter per service (feature #4 Load Balancing).
        private final Map<String, AtomicInteger> roundRobin = new java.util.HashMap<>();
        // Simple per-client request counter (feature #3 Rate Limiting).
        private final Map<String, Integer> callCounts = new java.util.HashMap<>();
        private final int rateLimit;

        Gateway(Map<String, Service> routes, int rateLimit) {
            this.routes = routes;
            this.rateLimit = rateLimit;
            routes.values().forEach(s -> roundRobin.put(s.name, new AtomicInteger(0)));
        }

        /** Handle one client request end-to-end. Returns the response string. */
        String handle(String clientId, String path, String jwtToken) {
            // ---- Logging & Monitoring (feature #7) ----
            System.out.println("[gateway] " + clientId + " -> " + path);

            // ---- Authentication (feature #2): reject if JWT is missing/invalid ----
            if (jwtToken == null || !jwtToken.startsWith("valid-")) {
                return "401 Unauthorized (invalid JWT)";
            }

            // ---- Rate Limiting (feature #3): block clients over their quota ----
            int count = callCounts.merge(clientId, 1, Integer::sum);
            if (count > rateLimit) {
                return "429 Too Many Requests (rate limit exceeded)";
            }

            // ---- Request Routing (feature #1): match the path prefix to a service ----
            Service target = null;
            for (Map.Entry<String, Service> route : routes.entrySet()) {
                if (path.startsWith(route.getKey())) {
                    target = route.getValue();
                    break;
                }
            }
            if (target == null) return "404 Not Found (no route)";

            // ---- Load Balancing (feature #4): pick an instance round-robin ----
            String instance = pickInstance(target);
            return "200 OK <- " + target.name + " @ " + instance;
        }

        /** Round-robin instance selection across the service's instances. */
        private String pickInstance(Service service) {
            int idx = roundRobin.get(service.name).getAndIncrement() % service.instances.size();
            return service.instances.get(idx);
        }

        /**
         * Request Aggregation (feature #5): one client call fans out to several services
         * and the gateway merges the responses (e.g. a dashboard screen).
         */
        String aggregateDashboard(String clientId, String jwtToken) {
            String user = handle(clientId, "/users/me", jwtToken);
            String orders = handle(clientId, "/orders/recent", jwtToken);
            String payments = handle(clientId, "/payments/summary", jwtToken);
            return "DASHBOARD { user=[" + user + "], orders=[" + orders + "], payments=[" + payments + "] }";
        }
    }

    public static void main(String[] args) {
        // Each service has 2 instances so we can see load balancing in action.
        Map<String, Service> routes = Map.of(
                "/users",    new Service("USER-SERVICE",    List.of("10.0.0.1:8081", "10.0.0.2:8081")),
                "/orders",   new Service("ORDER-SERVICE",   List.of("10.0.0.3:8082", "10.0.0.4:8082")),
                "/payments", new Service("PAYMENT-SERVICE", List.of("10.0.0.5:8083", "10.0.0.6:8083"))
        );
        Gateway gateway = new Gateway(routes, /* rateLimit per client */ 5);

        System.out.println("== Routing + Load Balancing (note the alternating instances) ==");
        System.out.println(gateway.handle("clientA", "/users/123",  "valid-jwt"));
        System.out.println(gateway.handle("clientA", "/users/456",  "valid-jwt"));   // 2nd instance
        System.out.println(gateway.handle("clientA", "/orders/789", "valid-jwt"));

        System.out.println("\n== Authentication (missing/invalid JWT) ==");
        System.out.println(gateway.handle("clientB", "/users/1", null));
        System.out.println(gateway.handle("clientB", "/users/1", "bad-token"));

        System.out.println("\n== Unknown route ==");
        System.out.println(gateway.handle("clientA", "/unknown/1", "valid-jwt"));

        System.out.println("\n== Request Aggregation (dashboard) ==");
        System.out.println(gateway.aggregateDashboard("clientC", "valid-jwt"));

        System.out.println("\n== Rate Limiting (clientD limit = 5) ==");
        for (int i = 1; i <= 7; i++) {
            System.out.println("req " + i + ": " + gateway.handle("clientD", "/users/1", "valid-jwt"));
        }
    }
}

/*
 * =====================================================================================
 *            HOW THIS IS DONE IN A BIG (REAL) APPLICATION  -- CONCEPTUAL
 * =====================================================================================
 *
 * The class above is a teaching model. Below is how each concern is ACTUALLY achieved
 * at scale. Use this section as interview talking points.
 *
 * -------------------------------------------------------------------------------------
 * MODEL (above)              vs        REAL APPLICATION
 * -------------------------------------------------------------------------------------
 *  returns Strings                     receives/forwards real HTTP over the network
 *  routes in a hardcoded Map           declarative YAML config or dynamic route store
 *  manual round-robin                  load balancer + SERVICE DISCOVERY (lb://NAME)
 *  in-memory rate counter              distributed limiter backed by REDIS
 *  jwt.startsWith("valid-")            real signature/expiry/issuer/claims verification
 *  blocking single thread              REACTIVE non-blocking (Netty + WebFlux)
 *  no failure handling                 timeouts + retries + CIRCUIT BREAKER + fallback
 *
 * -------------------------------------------------------------------------------------
 * BIG-PICTURE ARCHITECTURE
 * -------------------------------------------------------------------------------------
 *   Client (web / mobile)
 *        |                                    (optionally a BFF gateway per client type)
 *        v
 *   [ Load Balancer / Nginx ]  --> spreads traffic across MANY gateway instances (HA,
 *        |                          removes the single-point-of-failure problem)
 *        v
 *   [ API Gateway cluster ]  (Spring Cloud Gateway / Kong / Nginx / AWS API Gateway)
 *        |   applies cross-cutting concerns as a pipeline of FILTERS:
 *        |     1. Auth filter        -> validate JWT (via Spring Security / JWKS)
 *        |     2. Rate limiter       -> Redis token/leaky-bucket, shared across instances
 *        |     3. Routing predicate  -> match path/header/method to a route
 *        |     4. Circuit breaker    -> Resilience4j, with fallbackUri
 *        |     5. Logging/tracing    -> correlation id, metrics (Micrometer/Prometheus)
 *        v
 *   [ Service Discovery ]  (Eureka / Consul / Kubernetes DNS)
 *        |   resolves lb://ORDER-SERVICE to healthy instance IPs dynamically
 *        v
 *   [ Microservice instances ]  (auto-scaled; instances come and go, discovery tracks them)
 *
 * -------------------------------------------------------------------------------------
 * HOW EACH FEATURE IS ACHIEVED (REAL)
 * -------------------------------------------------------------------------------------
 * 1) ROUTING          -> declarative routes (predicates on path/host/header/method).
 * 2) AUTH             -> a single GLOBAL filter validates JWT once; downstream services
 *                        trust the gateway (or receive forwarded identity headers).
 * 3) RATE LIMITING    -> Redis-backed RequestRateLimiter so the limit is GLOBAL across
 *                        every gateway replica (an in-memory counter would not be).
 * 4) LOAD BALANCING   -> client-side LB (Spring Cloud LoadBalancer) + service discovery,
 *                        addressed via "lb://SERVICE-NAME" instead of fixed IPs.
 * 5) AGGREGATION      -> usually pushed into a BFF or a composition service; the gateway
 *                        itself stays thin. Reactive calls fan out and merge responses.
 * 6) CACHING          -> response caching at the gateway / CDN for hot, cacheable GETs.
 * 7) OBSERVABILITY    -> centralized logging (ELK), metrics (Prometheus/Grafana),
 *                        distributed tracing (OpenTelemetry / Zipkin) with a trace id.
 * 8) RESILIENCE       -> per-route timeouts, retries, and Resilience4j circuit breakers
 *                        with fallbackUri (ties back to the Circuit Breaker pattern).
 *
 * -------------------------------------------------------------------------------------
 * MINIMAL REAL CONFIG (Spring Cloud Gateway) - what you'd actually write
 * -------------------------------------------------------------------------------------
 *   spring:
 *     cloud:
 *       gateway:
 *         routes:
 *           - id: order-service
 *             uri: lb://ORDER-SERVICE            # discovered + load-balanced
 *             predicates:
 *               - Path=/orders/**
 *             filters:
 *               - name: RequestRateLimiter       # distributed, Redis-backed
 *                 args:
 *                   redis-rate-limiter.replenishRate: 5
 *                   redis-rate-limiter.burstCapacity: 10
 *               - name: CircuitBreaker           # resilience + fallback
 *                 args:
 *                   name: orderCB
 *                   fallbackUri: forward:/fallback/orders
 *   # JWT auth is applied globally via Spring Security (a GlobalFilter), not per route.
 *
 * KEY TAKEAWAY: In a big application the gateway is CONFIGURED, not coded. You compose
 * built-in filters (auth, rate limit, circuit breaker) on top of service discovery and
 * Redis, and run MULTIPLE gateway instances behind a load balancer for high availability.
 * =====================================================================================
 */
