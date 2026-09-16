package com.preperation.spring;
/*
Q: What is a RESTful API, and what are the key principles?
    A: REST is an architectural style for building APIs around resources. A RESTful API typically uses HTTP methods such as
    GET, POST, PUT, PATCH, and DELETE, meaningful resource-oriented URLs, stateless communication, appropriate HTTP status codes,
    and representations such as JSON.
    The key principles include:
    - Proper HTTP methods/status codes (GET, POST, PUT, DELETE) for CRUD operations
    - Stateless communication between client and server
    - Client-server architecture with a clear separation of concerns
    - Cacheable responses to improve performance
    - Resource-based URLs

    Q: What does stateless mean?
        A: Stateless means that each request from the client to the server must contain all the information needed to understand and process the request.
        The server should not depend on previous requests from the same client.
=====================================================================================================================================
Q: Explain idempotency in REST APIs. Why is it important in microservices?
    A: Idempotency means that making multiple identical requests has the same effect as making a single request.
    In REST APIs, this is important for ensuring that clients can safely retry requests without causing unintended side effects,
    which is crucial in distributed systems and microservices where network failures can occur.
    Typically, below HTTP methods are considered idempotent:
        GET     → Idempotent
        PUT     → Idempotent
        DELETE  → Idempotent
        POST    → Not inherently idempotent
=====================================================================================================================================
Q: How would you design REST APIs for microservices?
    I would design APIs around each service's business/domain ownership.
    For example:
        GET  /api/v1/customers/{id}

        GET  /api/v1/orders/{id}

        POST /api/v1/orders

        GET  /api/v1/products/{id}

        POST /api/v1/payments

    I would also consider:
        API versioning
        Authentication/authorization
        Validation
        Pagination
        Filtering/sorting
        Consistent error responses
        Idempotency
        Timeouts/retries
        Rate limiting
        Correlation IDs
        API documentation using OpenAPI/Swagger
=====================================================================================================================================
Q: How do you handle API versioning?
    URL versioning      /api/v1/customers
    Header versioning   Accept: application/vnd.myapi.v1+json
=====================================================================================================================================
Q: How would you handle pagination in a REST API?
    Pagination can be handled using query parameters such as page and size, or limit and offset.
    For example:
        GET /api/v1/customers?page=2&size=10
        GET /api/v1/customers?limit=10&offset=20
    The response can include metadata about the total number of items, current page, and total pages.
=====================================================================================================================================
Q: How do microservices communicate through REST?
    Microservices communicate through REST by exposing RESTful APIs that other services can consume.
    Each microservice has its own API endpoints, and they communicate over HTTP using standard methods (GET, POST, PUT, DELETE).
    They can exchange data in formats like JSON or XML. Service discovery mechanisms can be used to locate services dynamically,
    and API gateways can manage routing, authentication, and rate limiting.
=====================================================================================================================================
Q: What is the difference between 401 and 403?
    401 Unauthorized - The client has not successfully authenticated.
        No token
        Invalid token
        Expired token

    403 Forbidden - The client is authenticated but doesn't have permission.
        User = authenticated
        Role = CUSTOMER
        Required role = ADMIN

    401 = Who are you?
    403 = I know who you are, but you can't do this.
=====================================================================================================================================
Q: How do you secure REST APIs in a microservices architecture?
        Client
          |
          | HTTPS
          ↓
        API Gateway
          |
          | JWT/OAuth2
          ↓
        Microservices

        Spring Security example :
            http
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/public/**").permitAll()
                    .requestMatchers("/admin/**")
                        .hasRole("ADMIN")
                    .anyRequest()
                        .authenticated()
                )
                .oauth2ResourceServer(
                    oauth -> oauth.jwt()
                );
=====================================================================================================================================
Q: How do you monitor and troubleshoot REST APIs in production?
    Major areas to check :
        Request count
        Error rate
        Response time / Delays
        CPU/memory, Thread count
        Network latency

   Health checks
=====================================================================================================================================
Scenario: Your REST API suddenly becomes slow. How would you troubleshoot it?
    Step 1 — Request count, error rate, response time
    Step 2 — Network latency
    Step 3 — CPU, Memory
    Step 4 — Database query delays
    Step 5 — GC pause
    Step 6 — Thread pools
    Step 7 — Connection pools

Apply the appropriate fix
    Depending on the root cause:
        Slow DB query      → optimize query/index
        Slow dependency    → timeout/circuit breaker
        Traffic spike      → autoscaling
        Thread exhaustion  → tune pool/concurrency
        Large response     → pagination/compression
        Repeated calls     → caching
 */

public class RESTfulAPI {

}



