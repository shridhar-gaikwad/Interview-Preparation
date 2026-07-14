package com.preperation.microservice;

import java.util.List;
import java.util.Map;

/*
 * =====================================================================================
 *                              MICROSERVICE ARCHITECTURE
 * =====================================================================================
 *
 * WHAT IS IT?
 * -----------
 * An architecture where an application is built as SMALL, INDEPENDENT SERVICES, each
 * responsible for a SPECIFIC BUSINESS FUNCTION and communicating over APIs.
 *
 * SIMPLE EXAMPLE (e-commerce app split into services):
 *      User Service | Order Service | Payment Service | Inventory Service
 *
 * >>> NOTE ON THIS FILE <<<
 * The Java code below is a CONCEPTUAL / TEACHING MODEL (plain Java, no Spring, no real
 * network) that simulates services, an API gateway, service discovery and async events
 * so you can SEE how the pieces fit. Real systems use Spring Boot + Spring Cloud,
 * Eureka/Consul, Kafka, Docker/Kubernetes, etc. (see the bottom section).
 *
 * -------------------------------------------------------------------------------------
 * MONOLITH vs MICROSERVICES (VERY IMPORTANT)
 * -------------------------------------------------------------------------------------
 *   Feature      | Monolith            | Microservices
 *   ------------ | --------------------| -----------------------------
 *   Deployment   | one big app         | many services deployed independently
 *   Scaling      | scale the whole app | scale per service (only what's hot)
 *   Failure      | one bug can down all| failures are isolated
 *   Complexity   | simple              | complex (distributed system)
 *
 * -------------------------------------------------------------------------------------
 * KEY CHARACTERISTICS
 * -------------------------------------------------------------------------------------
 *   - Independent deployment
 *   - Loosely coupled services
 *   - OWN database per service (no shared DB)
 *   - API-based communication
 *
 * -------------------------------------------------------------------------------------
 * SERVICE COMMUNICATION
 * -------------------------------------------------------------------------------------
 *   SYNCHRONOUS (caller waits for response):
 *      - REST (most common)
 *      - Feign Client (declarative REST client in Spring)
 *   ASYNCHRONOUS (fire-and-forget via broker):
 *      - Kafka / RabbitMQ
 *   INTERVIEW TIP: prefer ASYNC (events) for high scalability & loose coupling.
 *
 * -------------------------------------------------------------------------------------
 * SUPPORTING BUILDING BLOCKS
 * -------------------------------------------------------------------------------------
 * 1) API GATEWAY (single entry point): routing, authentication, rate limiting, logging.
 *      Tools: Spring Cloud Gateway (modern), Netflix Zuul (older).
 * 2) LOAD BALANCING: distribute traffic so no single server is overloaded.
 *      Client-side: Spring Cloud LoadBalancer | Server-side: Nginx. Works WITH discovery.
 * 3) SERVICE DISCOVERY: services register themselves; callers look them up by NAME
 *      (not hardcoded IPs). Tool: Eureka / Consul / Kubernetes DNS.
 * 4) DATABASE PER SERVICE: each service owns its data -> loose coupling.
 *      Challenge: data consistency across services -> solved with Saga + eventual consistency.
 * 5) FAULT TOLERANCE & RESILIENCE: Circuit Breaker, Retry, Timeout, Bulkhead.
 *      Tool: Resilience4j.
 * 6) CENTRALIZED LOGGING & MONITORING: ELK Stack; Grafana + Prometheus; tracing (Zipkin).
 * 7) SECURITY: OAuth2 / JWT, validated at the API Gateway.
 * 8) CONTAINERIZATION & DEPLOYMENT (trending): Docker, Kubernetes.
 * 9) DISTRIBUTED CACHING: Redis.
 *
 * -------------------------------------------------------------------------------------
 * REAL-WORLD ARCHITECTURE FLOW
 * -------------------------------------------------------------------------------------
 *      Client -> API Gateway -> Service Discovery -> Microservices (User, Order, Payment)
 *             -> Database (separate per service) -> Kafka (events)
 *
 *   PROS: scalability, flexibility, independent teams.
 *   CONS: complexity, network-call overhead, data-consistency issues.
 * -------------------------------------------------------------------------------------
 * BUILDING MICROSERVICES FROM SCRATCH (WHAT YOU NEED + STEP-BY-STEP SETUP)
 * -------------------------------------------------------------------------------------
 * WHAT YOU NEED (HIGH-LEVEL concept  ->  low-level tools in brackets):
 *
 *   - Language & Runtime        (Java, JDK 17+)
 *   - Build tool                (Maven / Gradle)
 *   - Service framework         (Spring Boot)
 *   - Distributed toolkit       (Spring Cloud)
 *   - Database, per service     (PostgreSQL / MySQL / MongoDB)
 *   - Caching                   (Redis)
 *   - Service discovery         (Eureka / Consul / Kubernetes DNS)
 *   - API gateway               (Spring Cloud Gateway / Kong / Nginx)
 *   - Config management         (Spring Cloud Config)
 *   - Communication - SYNC      (REST, OpenFeign, WebClient)
 *   - Communication - ASYNC     (Apache Kafka / RabbitMQ)
 *   - Resilience                (Resilience4j: circuit breaker, retry, timeout, bulkhead)
 *   - Security                  (OAuth2, JWT, Spring Security)
 *   - Logging, centralized      (ELK: Elasticsearch, Logstash, Kibana)
 *   - Monitoring / Metrics      (Prometheus + Grafana)
 *   - Distributed tracing       (Zipkin / OpenTelemetry / Sleuth)
 *   - Containerization          (Docker)
 *   - Orchestration / Deploy    (Kubernetes)
 *   - CI/CD                     (Jenkins / GitHub Actions / GitLab CI)
 *
 * STEP-BY-STEP SETUP GUIDE:
 *   STEP 1 - Design the domain (split by business capability):
 *      Identify bounded contexts. e.g. User, Order, Payment, Inventory.
 *      RULE: one service = one responsibility = one database.
 *
 *   STEP 2 - Create each service (Spring Boot):
 *      Use https://start.spring.io -> pick dependencies: Spring Web, Spring Data JPA,
 *      the DB driver, Actuator. Build a small runnable app that exposes REST endpoints.
 *      Repeat this per service (user-service, order-service, ...).
 *
 *   STEP 3 - Give each service its OWN database:
 *      Separate schema/DB per service. Never let two services share tables.
 *      Configure the connection in each service's application.yml.
 *
 *   STEP 4 - Add a Service Discovery server (Eureka):
 *      Create a new app with 'spring-cloud-starter-netflix-eureka-server' + @EnableEurekaServer.
 *      Add the Eureka CLIENT dependency to every service so they self-register by NAME.
 *      Now services find each other by name, not hardcoded IP/port.
 *
 *   STEP 5 - Add an API Gateway (Spring Cloud Gateway):
 *      Single entry point. Define routes (path -> service name resolved via Eureka).
 *      Put cross-cutting concerns here: authentication, rate limiting, logging.
 *
 *   STEP 6 - Wire inter-service communication:
 *      SYNC  -> OpenFeign / RestTemplate / WebClient (call another service by name).
 *      ASYNC -> publish events to Kafka/RabbitMQ; other services subscribe & react.
 *      Prefer async events for scalability and loose coupling.
 *
 *   STEP 7 - Add resilience (Resilience4j):
 *      Wrap remote calls with Circuit Breaker + Retry + Timeout + Fallback so one
 *      failing service does not cascade and take down the rest.
 *
 *   STEP 8 - Centralize configuration (Spring Cloud Config):
 *      Externalize config into a Git-backed Config Server so all services read
 *      environment-specific settings from one place.
 *
 *   STEP 9 - Secure it:
 *      Add OAuth2 / JWT. Validate tokens at the API Gateway; propagate identity downstream.
 *
 *   STEP 10 - Add observability:
 *      Centralized logging (ELK), metrics (Prometheus + Grafana), and distributed
 *      tracing (Zipkin / OpenTelemetry) so you can debug across service boundaries.
 *
 *   STEP 11 - Containerize & deploy:
 *      Write a Dockerfile per service -> build images -> run with docker-compose locally,
 *      then deploy to Kubernetes (Deployments, Services, ConfigMaps, Secrets, Ingress).
 *
 *   MINIMUM VIABLE ORDER (if short on time):
 *      Services + own DBs  ->  Eureka  ->  API Gateway  ->  Feign/Kafka  ->  Resilience4j
 *      ...then add Config, Security, Observability, Docker/K8s as you mature.
 * -------------------------------------------------------------------------------------
 * QUICK Q&A (INTERVIEW)
 * -------------------------------------------------------------------------------------
 * Q1: What are microservices?
 *     -> Small, independent services communicating via APIs, each handling one business capability.
 * Q2: Advantages vs Monolith?
 *     -> Independent deployment, better scalability, fault isolation, faster development.
 * Q3: Challenges?
 *     -> Complexity, network latency, distributed transactions, harder debugging.
 * Q4: What is an API Gateway?
 *     -> Single entry point that routes requests and handles cross-cutting concerns.
 * Q5: How do services communicate?
 *     -> REST / Feign (sync), Kafka / RabbitMQ (async).
 * Q6: What is a Circuit Breaker?
 *     -> Stops calling a failing service to prevent cascading failures.
 * Q7: What is the Saga Pattern?
 *     -> Manages distributed transactions using a sequence of local transactions + compensation.
 * Q8: How do you handle service discovery?
 *     -> Tools like Eureka (services register; callers resolve by name).
 * Q9: How do you ensure fault tolerance?
 *     -> Retry, Circuit Breaker, Timeout, Bulkhead.
 * Q10: Why database per service?
 *     -> To keep services loosely coupled and independently scalable.
 *
 * ONE-LINER SUMMARY:
 * Microservices split an app into independent services (Spring Boot + Spring Cloud),
 * enabling scalability and resilience, at the cost of distributed-system complexity.
 *
 * -------------------------------------------------------------------------------------
 * EXAMPLE BELOW:
 * A tiny in-memory simulation: services register in a discovery registry, an API gateway
 * routes a request by service NAME (not IP), and an event bus shows async communication.
 * =====================================================================================
 */
public class MicroserviceArchitecture {

    /** A microservice: has a name, its OWN data store, and a request handler. */
    static class MicroService {
        final String name;
        // "Database per service": each service owns its data, no shared DB.
        private final Map<String, String> ownDatabase = new java.util.HashMap<>();

        MicroService(String name) { this.name = name; }

        void save(String key, String value) { ownDatabase.put(key, value); }

        String handle(String request) {
            return name + " handled '" + request + "' (db=" + ownDatabase + ")";
        }
    }

    /**
     * Service Discovery registry: services REGISTER by name; callers LOOK UP by name
     * instead of hardcoding IPs (this is what Eureka/Consul do).
     */
    static class ServiceRegistry {
        private final Map<String, MicroService> registry = new java.util.HashMap<>();
        void register(MicroService service) {
            registry.put(service.name, service);
            System.out.println("[discovery] registered: " + service.name);
        }
        MicroService lookup(String name) { return registry.get(name); }
    }

    /**
     * API Gateway: single entry point. Maps a URL path -> service NAME, then resolves
     * the actual service via discovery (decoupled from where the service runs).
     */
    static class ApiGateway {
        private final ServiceRegistry discovery;
        private final Map<String, String> pathToService;  // e.g. /orders -> ORDER-SERVICE

        ApiGateway(ServiceRegistry discovery, Map<String, String> pathToService) {
            this.discovery = discovery;
            this.pathToService = pathToService;
        }

        String route(String path, String request) {
            // Cross-cutting concern spot: auth / rate-limit / logging would go here.
            for (Map.Entry<String, String> e : pathToService.entrySet()) {
                if (path.startsWith(e.getKey())) {
                    MicroService svc = discovery.lookup(e.getValue()); // resolve by NAME
                    if (svc == null) return "503 " + e.getValue() + " unavailable";
                    return svc.handle(request);
                }
            }
            return "404 no route for " + path;
        }
    }

    /**
     * A very small ASYNCHRONOUS event bus (stands in for Kafka/RabbitMQ). Publishers
     * emit events; subscribers react independently -> loose coupling.
     */
    static class EventBus {
        private final Map<String, List<java.util.function.Consumer<String>>> subscribers = new java.util.HashMap<>();
        void subscribe(String topic, java.util.function.Consumer<String> handler) {
            subscribers.computeIfAbsent(topic, k -> new java.util.ArrayList<>()).add(handler);
        }
        void publish(String topic, String event) {
            System.out.println("[event] published to '" + topic + "': " + event);
            subscribers.getOrDefault(topic, List.of()).forEach(h -> h.accept(event));
        }
    }

    public static void main(String[] args) {
        // ---- Build services (each with its own database) ----
        MicroService userService = new MicroService("USER-SERVICE");
        MicroService orderService = new MicroService("ORDER-SERVICE");
        MicroService paymentService = new MicroService("PAYMENT-SERVICE");
        userService.save("u1", "Mahendra");

        // ---- Service discovery: everyone registers ----
        ServiceRegistry discovery = new ServiceRegistry();
        discovery.register(userService);
        discovery.register(orderService);
        discovery.register(paymentService);

        // ---- API gateway routes by path -> service name (resolved via discovery) ----
        ApiGateway gateway = new ApiGateway(discovery, Map.of(
                "/users", "USER-SERVICE",
                "/orders", "ORDER-SERVICE",
                "/payments", "PAYMENT-SERVICE"
        ));

        System.out.println("\n== Synchronous request via API Gateway ==");
        System.out.println(gateway.route("/users/u1", "get user u1"));
        System.out.println(gateway.route("/orders", "create order"));
        System.out.println(gateway.route("/unknown", "??"));

        // ---- Asynchronous communication (event-driven, loosely coupled) ----
        System.out.println("\n== Asynchronous communication (Kafka-style events) ==");
        EventBus bus = new EventBus();
        // Payment & Inventory react to an order being created, independently.
        bus.subscribe("order-created", e -> System.out.println("   PAYMENT-SERVICE processes payment for " + e));
        bus.subscribe("order-created", e -> System.out.println("   INVENTORY-SERVICE reserves stock for " + e));
        bus.publish("order-created", "order#1001");
    }
}

/*
 * =====================================================================================
 *          HOW THIS IS BUILT IN A BIG (REAL) APPLICATION  -- CONCEPTUAL
 * =====================================================================================
 *
 * The simulation above is for understanding. In a real system you assemble PROVEN TOOLS
 * rather than coding the plumbing yourself:
 *
 *   CONCERN                REAL TOOL / TECH
 *   --------------------   --------------------------------
 *   Service framework      Spring Boot
 *   Service discovery      Eureka / Consul / Kubernetes DNS
 *   API gateway            Spring Cloud Gateway / Kong / Nginx
 *   Sync communication     REST / OpenFeign
 *   Async communication    Apache Kafka / RabbitMQ
 *   Load balancing         Spring Cloud LoadBalancer / Nginx
 *   Resilience             Resilience4j (circuit breaker/retry)
 *   Config management      Spring Cloud Config
 *   Security               OAuth2 / JWT at the gateway
 *   Data per service       separate DB per service (Postgres, etc.)
 *   Distributed txns       Saga Pattern + eventual consistency
 *   Caching                Redis
 *   Observability          ELK, Prometheus+Grafana, Zipkin/OTel
 *   Packaging & deploy     Docker + Kubernetes
 *
 * TYPICAL REQUEST PATH IN PRODUCTION:
 *   Client -> Load Balancer -> API Gateway (auth, rate limit, routing, circuit breaker)
 *          -> Service Discovery (resolve service instances)
 *          -> Microservice instance (own DB)
 *          -> publishes events to Kafka -> other services react asynchronously
 *   Cross-cutting: centralized logging + metrics + distributed tracing throughout.
 *
 * KEY TAKEAWAY: Microservices = many small Spring Boot services, each independently
 * deployable and scalable, wired together by an API gateway, service discovery, async
 * messaging and resilience tooling - trading simplicity for scalability and isolation.
 * =====================================================================================
 */
