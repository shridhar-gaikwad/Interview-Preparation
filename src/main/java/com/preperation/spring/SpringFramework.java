package com.preperation.spring;

/*
 * =====================================================================================
 *                              SPRING FRAMEWORK
 * =====================================================================================
 *
 * WHAT IS IT?
 * -----------
 * Spring is a lightweight, open-source Java framework used for developing enterprise
 * applications. It provides:
 *      - IoC (Inversion of Control)
 *      - Dependency Injection (DI)
 *      - Transaction Management
 *      - MVC (web framework)
 *      - Security
 *      - Data Access
 *      - AOP (Aspect Oriented Programming)
 *
 * >>> NOTE ON THIS FILE <<<
 * The Java below is a CONCEPTUAL / TEACHING MODEL (plain Java, no real Spring container)
 * that simulates IoC, Dependency Injection and bean wiring so you can SEE how the pieces
 * fit. In a real app Spring's IoC container does all of this for you via annotations.
 *
 * -------------------------------------------------------------------------------------
 * MAIN MODULES IN SPRING
 * -------------------------------------------------------------------------------------
 *   - Spring Core        (IoC container, beans)
 *   - Spring Context     (ApplicationContext, events, i18n)
 *   - Spring Beans       (bean definition & wiring)
 *   - Spring AOP         (cross-cutting concerns: logging, security, txns)
 *   - Spring JDBC        (JDBC abstraction)
 *   - Spring ORM         (Hibernate/JPA integration)
 *   - Spring MVC         (web layer)
 *   - Spring Security     (authentication / authorization)
 *   - Spring Data JPA    (repositories, less boilerplate)
 *
 * -------------------------------------------------------------------------------------
 * 1) IoC, DEPENDENCY INJECTION & BEAN WIRING
 * -------------------------------------------------------------------------------------
 * IoC (Inversion of Control):
 *   A design principle where the responsibility of CREATING and MANAGING objects is
 *   transferred FROM the application code TO the Spring Container.
 *
 * Dependency Injection (DI):
 *   Providing a dependency from OUTSIDE instead of creating it inside the class.
 *   The Spring container creates/manages beans and injects dependencies.
 *
 *      @Service
 *      public class OrderService {
 *          private final PaymentService paymentService;   // final = mandatory & immutable
 *
 *          // @Autowired optional since Spring 4.3 for a single constructor
 *          public OrderService(PaymentService paymentService) {
 *              this.paymentService = paymentService;
 *          }
 *      }
 *
 *   Types of injection:
 *      1. Constructor Injection   (RECOMMENDED - final fields, testable, no NPE,
 *                                  catches circular deps early)
 *      2. Setter Injection        (for optional dependencies)
 *      3. Field Injection         (@Autowired on field - concise but hard to test)
 *
 * -------------------------------------------------------------------------------------
 * SPRING CONTAINER
 * -------------------------------------------------------------------------------------
 * Heart of the Spring Framework. Responsible for:
 *      - Creating beans
 *      - Managing beans
 *      - Injecting dependencies
 *      - Managing the bean lifecycle
 *
 *   Types of containers:
 *      1. BeanFactory        -> basic container, LAZY initialization
 *      2. ApplicationContext -> advanced container (most used), EAGER initialization.
 *                               Adds: event handling, internationalization (i18n),
 *                               AOP support, enterprise features.
 *
 * -------------------------------------------------------------------------------------
 * BEAN CREATION (annotations)
 * -------------------------------------------------------------------------------------
 *      @Component   -> generic Spring-managed bean
 *      @Service     -> business logic layer
 *      @Repository  -> persistence/DAO layer
 *      @Controller  -> web MVC layer
 *
 * AUTOWIRING:
 *   Automatic dependency injection by Spring.
 *      @Autowired
 *      private Engine engine;
 *
 * -------------------------------------------------------------------------------------
 * RESOLVING MULTIPLE BEANS (@Primary vs @Qualifier)
 * -------------------------------------------------------------------------------------
 *      public interface PaymentService {}
 *
 *      @Service @Primary               // default choice when multiple candidates exist
 *      class UpiPayment implements PaymentService {}
 *
 *      @Service @Qualifier("card")     // selected explicitly by name
 *      class CardPayment implements PaymentService {}
 *
 *      @Service
 *      class Checkout {
 *          Checkout(@Qualifier("card") PaymentService p) { } // picks CardPayment
 *      }
 *
 *   @Primary  -> the default bean when several match.
 *   @Qualifier-> explicitly pick a specific bean by name.
 *
 *   NoUniqueBeanDefinitionException:
 *      Thrown when Spring "expected a single matching bean but found more than one"
 *      and no @Primary / @Qualifier was given to break the tie.
 *
 * -------------------------------------------------------------------------------------
 * 2) CORE STEREOTYPE ANNOTATIONS
 * -------------------------------------------------------------------------------------
 *   Annotation        | Layer         | Purpose                        | Special behavior
 *   ----------------- | ------------- | ------------------------------ | -----------------------------
 *   @Component        | Any           | Generic Spring-managed bean    | none
 *   @Service          | Service       | Business logic                 | semantic specialization
 *   @Repository       | Persistence   | DAO / database access          | translates DB exceptions
 *   @Controller       | Web MVC       | Handles HTTP, returns views    | used in MVC apps
 *   @RestController   | REST API      | Returns JSON/XML               | @Controller + @ResponseBody
 *   @Configuration    | Configuration | Defines bean configuration     | can contain @Bean methods
 *
 *   Why @Service instead of @Component? Technically both work, but @Service immediately
 *   tells developers "this class belongs to the Service layer" (self-documenting).
 *
 * -------------------------------------------------------------------------------------
 * 3) BEAN SCOPES & LIFECYCLE
 * -------------------------------------------------------------------------------------
 * Bean lifecycle:
 *      1. Instantiate bean
 *      2. Inject dependencies
 *      3. @PostConstruct   (init callback)
 *      4. Bean ready (in use)
 *      5. @PreDestroy      (cleanup callback)
 *      6. Destroy bean
 *   (ADDITIONAL) Same hooks via interfaces: InitializingBean.afterPropertiesSet()
 *                and DisposableBean.destroy().
 *
 * Bean scopes (how many instances and how long they live):
 *      1. singleton (DEFAULT) -> one object per Spring container
 *      2. prototype           -> a NEW object every time it's requested
 *      3. request             -> one bean per HTTP request  (web)
 *      4. session             -> one bean per user session  (web)
 *      (ADDITIONAL) application, websocket.
 *
 *      @Service
 *      @Scope("singleton")
 *      public class PaymentService { }
 *
 * -------------------------------------------------------------------------------------
 * 4) GLOBAL EXCEPTION HANDLING
 * -------------------------------------------------------------------------------------
 *      @RestControllerAdvice  -> global exception handler for REST APIs
 *      @ControllerAdvice      -> used for MVC applications
 *      @ExceptionHandler      -> handles specific exception(s)
 *
 *      @RestControllerAdvice
 *      public class GlobalExceptionHandler {
 *          @ExceptionHandler(EmployeeNotFoundException.class)
 *          public ResponseEntity<String> handle(EmployeeNotFoundException ex) {
 *              return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
 *          }
 *      }
 *
 *   Multiple exceptions in one handler:
 *      @ExceptionHandler({ NullPointerException.class, RuntimeException.class })
 *
 *   FLOW: Client Request -> Controller -> Service -> exception thrown
 *         -> @RestControllerAdvice -> @ExceptionHandler -> JSON response
 *
 * -------------------------------------------------------------------------------------
 * 5) SPRING MVC
 * -------------------------------------------------------------------------------------
 * Spring MVC is a web framework based on the Model-View-Controller pattern for building
 * web applications and REST APIs. It separates:
 *      Model      -> business/data (container to carry data Controller -> View)
 *      View       -> UI (JSP, Thymeleaf, HTML)
 *      Controller -> request handling layer
 *
 *   REQUEST FLOW:
 *      Client Request -> DispatcherServlet -> Controller -> Service -> Repository
 *                     -> Database -> back to Controller -> View Resolver
 *                     -> View generated -> Response sent
 *
 * -------------------------------------------------------------------------------------
 * 6) @Component vs @Bean
 * -------------------------------------------------------------------------------------
 * @Component: "Spring, this CLASS is a bean." (applied on a class, found by @ComponentScan)
 *      @Component
 *      class PaymentService {}
 *
 * @Bean: "Spring, call this METHOD and register whatever it returns as a bean."
 *      (applied on a method inside a @Configuration class)
 *      @Bean
 *      public PaymentService paymentService() { return new PaymentService(); }
 *
 *   WHEN IS @Bean USEFUL? For THIRD-PARTY classes you cannot annotate, or custom
 *   object-creation logic:
 *      @Configuration
 *      public class AppConfig {
 *          @Bean public ObjectMapper objectMapper() { return new ObjectMapper(); }
 *          @Bean public RestTemplate restTemplate() { return new RestTemplate(); }
 *      }
 *
 *   Feature                    | @Component                 | @Bean
 *   -------------------------- | -------------------------- | --------------------------------
 *   Applied on                 | class                      | method (in @Configuration)
 *   Bean creation              | auto via component scan    | manual via method invocation
 *   Control over instantiation | limited                    | full control
 *   Best for                   | classes you OWN            | third-party / custom creation
 *   Bean name                  | derived from class name    | derived from method name
 *
 * -------------------------------------------------------------------------------------
 * QUICK Q&A (INTERVIEW)
 * -------------------------------------------------------------------------------------
 * Q1: What is Spring? -> Lightweight framework providing IoC, DI, AOP, MVC, security, etc.
 * Q2: What is IoC?     -> Container (not your code) creates and manages object lifecycle.
 * Q3: What is DI?      -> Dependencies are injected from outside; prefer constructor injection.
 * Q4: What is a Spring Bean? -> An object whose creation, config, DI and lifecycle are
 *                               managed by the Spring IoC container.
 * Q5: BeanFactory vs ApplicationContext?
 *                      -> BeanFactory = lazy, basic. ApplicationContext = eager, enterprise.
 * Q6: @Component vs @Bean? -> @Component on a class (auto-scanned); @Bean on a method for
 *                             third-party/custom objects.
 * Q7: @Controller vs @RestController?
 *                      -> @Controller returns views; @RestController = @Controller +
 *                         @ResponseBody, returns JSON/XML for REST APIs.
 * Q8: @Primary vs @Qualifier? -> @Primary is the default match; @Qualifier picks explicitly.
 * Q9: How to handle exceptions globally? -> @RestControllerAdvice + @ExceptionHandler.
 * Q10: What are bean scopes? -> singleton (default), prototype, request, session.
 *
 * ONE-LINER SUMMARY:
 * Spring is an IoC/DI-driven framework: the container creates and wires beans for you,
 * letting you focus on business logic while it handles object lifecycle and cross-cutting
 * concerns (AOP, transactions, MVC, security).
 *
 * -------------------------------------------------------------------------------------
 * EXAMPLE BELOW:
 * A tiny in-memory "IoC container" that registers beans and injects a dependency by type,
 * mimicking what Spring does with @Component/@Autowired.
 * =====================================================================================
 */
public class SpringFramework {

    /** A dependency (like a @Service bean). */
    static class PaymentService {
        String pay(String order) { return "Paid for " + order; }
    }

    /** A bean that DEPENDS on PaymentService (injected via constructor). */
    static class OrderService {
        private final PaymentService paymentService;   // final -> mandatory & immutable

        OrderService(PaymentService paymentService) {  // constructor injection
            this.paymentService = paymentService;
        }

        String placeOrder(String order) {
            return "OrderService -> " + paymentService.pay(order);
        }
    }

    /**
     * A minimal IoC container: stores singleton beans by type and hands them out.
     * This is the essence of what Spring's ApplicationContext does (in ~15 lines).
     */
    static class MiniContainer {
        private final java.util.Map<Class<?>, Object> beans = new java.util.HashMap<>();

        <T> void register(Class<T> type, T bean) {
            beans.put(type, bean);
            System.out.println("[container] registered bean: " + type.getSimpleName());
        }

        @SuppressWarnings("unchecked")
        <T> T getBean(Class<T> type) { return (T) beans.get(type); }
    }

    public static void main(String[] args) {
        // ---- Inversion of Control: the container creates & manages beans ----
        MiniContainer context = new MiniContainer();

        PaymentService paymentService = new PaymentService();
        context.register(PaymentService.class, paymentService);

        // ---- Dependency Injection: dependency supplied from OUTSIDE the class ----
        OrderService orderService = new OrderService(context.getBean(PaymentService.class));
        context.register(OrderService.class, orderService);

        // ---- Use the wired bean (as a Controller/Service would) ----
        System.out.println("\n== Bean wiring result ==");
        System.out.println(context.getBean(OrderService.class).placeOrder("order#1001"));
    }
}
