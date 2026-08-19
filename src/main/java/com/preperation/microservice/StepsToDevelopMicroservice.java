package com.preperation.microservice;

public class StepsToDevelopMicroservice {
    /*
    1. Define the microservice
        First decide exactly what responsibility the service owns. Each service should ideally own a business capability
        rather than simply being a technical layer.
           Ex:  User Service
                Product Service
                Order Service

            Responsibilities:
                - Create order
                - Get order
                - Cancel order
                - Update order status

                Own data:
                - orders
                - order_items

                Dependencies:
                - Product Service
                - Payment Service
    ===============================================================================================
      2. Define the API contract
            Before writing implementation code, define how clients communicate with the service.
            For example:
                POST   /api/v1/orders
                GET    /api/v1/orders/{id}
                GET    /api/v1/orders
                PUT    /api/v1/orders/{id}
                DELETE /api/v1/orders/{id}

            Define:
                Request format
                Response format
                HTTP status codes
                Validation rules
                Error response format
                Authentication requirements
                Pagination
                Versioning
     ===============================================================================================
     3. Create the project structure (The exact structure depends on your technology stack.)
            order-service/
            │
            ├── src/
            │   ├── controller/
            │   ├── service/
            │   ├── repository/
            │   ├── model/
            │   ├── dto/
            │   ├── exception/
            │   ├── config/
            │   └── client/
            │
            ├── test/
            │
            ├── Dockerfile
            ├── docker-compose.yml
            ├── application.yml
            ├── README.md
            └── pom.xml / package.json / build.gradle

    ===============================================================================================

    4. Implement the Controllers, business layer & Repositories
        Follow MVC pattern and Keep everything separate from HTTP handling.
        A controller should primarily handle: HTTP request, Validation, Call business service, HTTP response

    ===============================================================================================
    5. Add database
        A microservice should generally own its data. Single DB creates tight coupling between services. Choose the database and cache.

        Also plan:
            Schema migrations
            Indexes
            Connection pooling
            Backup/recovery
            Database credentials/secrets
    ===============================================================================================

    6. Add validation and error handling
        Define a standard error contract.
        Handle:
            400 → Invalid request
            401 → Authentication required
            403 → Forbidden
            404 → Resource not found
            409 → Conflict
            422 → Business validation failure
            429 → Too many requests
            500 → Internal error
            503 → Dependency unavailable

        Also implement:
            Global exception handling
            Input validation
            Timeout handling
            Dependency failure handling
            Retry where appropriate
            Circuit breaker where appropriate
    ===============================================================================================
    7. Add communication with other microservices
        Synchronous : Rest, gRPC
        Asynchronous : Kafka, AWS SQS/SNS, Google Pub/Sub
    ===============================================================================================
    8. Add authentication and authorization
        OAuth 2.0, JWT
    ===============================================================================================
    9. Add API Gateway
        The gateway can handle:

            Routing
            Authentication
            Authorization
            Rate limiting
            TLS termination
            Request logging
            API versioning
            CORS
            Sometimes response aggregation

            Examples include Kong, NGINX, AWS API Gateway, and cloud-native gateway solutions.
    ===============================================================================================

    11. Add caching
        Caching can reduce database load and latency.
    ===============================================================================================

    12. Add observability
        Logs :
            timestamp
            service
            level
            traceId
            requestId
            message
            error

        Metrics : Monitor things like
            Request count
            Error rate
            Latency
            CPU
            Memory
            Database connections
            Queue depth
            Cache hit ratio
    ===============================================================================================

    13. Add health checks
        Conceptually :
            Liveness
               ↓
            Is the application process alive?

            Readiness
               ↓
            Can this instance receive traffic?
    ===============================================================================================

    14. Add testing
        Unit tests : Test business logic independently.
        End-to-end tests : Test an actual business flow:
    ===============================================================================================
    15. Externalize configuration
            Application
            ↓
        Configuration
            ├── Environment variables
            ├── Config service
            └── Secrets manager
    ===============================================================================================
    16. Build CI/CD
        GitHub Actions, GitLab CI/CD, Jenkins, Azure DevOps, Argo CD
    ===============================================================================================
    17. Deploy to Kubernetes/cloud
        Ex :
                            Internet
                               │
                               ↓
                         Load Balancer
                               │
                               ↓
                          API Gateway
                               │
                     ┌─────────┴─────────┐
                     ↓                   ↓
               Order Service       Product Service
                     │                   │
                     ↓                   ↓
                Order DB             Product DB
                     │
                     ↓
                Message Broker
                     │
               ┌─────┴─────┐
               ↓           ↓
         Payment       Notification
         Service          Service
    ===============================================================================================
    # Recommended implementation order
        1. Define business responsibility
                ↓
        2. Define API/event contracts
                ↓
        3. Create project
                ↓
        4. Implement domain/business logic
                ↓
        5. Add database
                ↓
        6. Add validation + error handling
                ↓
        7. Add unit/integration tests
                ↓
        8. Add service-to-service communication
                ↓
        9. Add timeout/retry/circuit breaker
                ↓
        10. Add authentication/authorization
                ↓
        11. Add logging + metrics + tracing
                ↓
        12. Add health/readiness endpoints
                ↓
        13. Dockerize
                ↓
        14. Create CI/CD pipeline
                ↓
        15. Deploy to staging
                ↓
        16. Load/security testing
                ↓
        17. Deploy to production
                ↓
        18. Add autoscaling + alerts + DR
     */
}
