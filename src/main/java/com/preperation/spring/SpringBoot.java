package com.preperation.spring;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/*
 * =====================================================================================
 *                                  SPRING BOOT
 * =====================================================================================
 *
 * WHAT IS IT?
 * -----------
 * Spring Boot is an EXTENSION of the Spring Framework that simplifies application
 * development by providing Auto Configuration, Starter Dependencies, Embedded Servers,
 * and Production-ready features. Goal: "just run" with minimal setup.
 *
 * >>> NOTE ON THIS FILE <<<
 * The Java below is a REAL, runnable Spring Boot application. @SpringBootApplication
 * triggers component scanning + auto configuration, SpringApplication.run() boots the
 * ApplicationContext, an @Service bean is constructor-injected into a @Component
 * CommandLineRunner, and that runner executes once right after startup.
 *
 * -------------------------------------------------------------------------------------
 * ADVANTAGES
 * -------------------------------------------------------------------------------------
 *   - Less configuration        (convention over configuration)
 *   - Auto configuration        (beans wired based on classpath)
 *   - Embedded Tomcat           (no external server to install)
 *   - Starter dependencies      (curated, version-aligned dependency bundles)
 *   - Faster development
 *   - Production ready          (Actuator: health, metrics, info)
 *
 * -------------------------------------------------------------------------------------
 * @SpringBootApplication
 * -------------------------------------------------------------------------------------
 *      @SpringBootApplication
 *      public class Application {
 *          public static void main(String[] args) {
 *              SpringApplication.run(Application.class, args);
 *          }
 *      }
 *
 *   @SpringBootApplication = @Configuration
 *                          + @EnableAutoConfiguration
 *                          + @ComponentScan
 *
 *   @EnableAutoConfiguration automatically configures common beans such as:
 *      DataSource, Tomcat, DispatcherServlet, Jackson.
 *
 * -------------------------------------------------------------------------------------
 * SPRING BOOT INTERNAL WORKING (APPLICATION STARTUP FLOW)
 * -------------------------------------------------------------------------------------
 *      main()
 *        |
 *      SpringApplication.run()
 *        |
 *      Create ApplicationContext
 *        |
 *      Component Scan          (find @Component/@Service/@Repository/@Controller)
 *        |
 *      Create Beans
 *        |
 *      Auto Configuration      (reads META-INF, checks classpath dependencies)
 *        |
 *      Start Embedded Tomcat
 *        |
 *      Application Ready
 *
 * -------------------------------------------------------------------------------------
 * STARTER DEPENDENCIES
 * -------------------------------------------------------------------------------------
 * A starter is a curated bundle of dependencies. Example: spring-boot-starter-web
 * automatically provides: Spring MVC, Jackson, Validation, Embedded Tomcat, Logging.
 *
 *   Popular starters:
 *      - spring-boot-starter-web        (REST / MVC)
 *      - spring-boot-starter-data-jpa   (JPA + Hibernate)
 *      - spring-boot-starter-security   (auth)
 *      - spring-boot-starter-test       (JUnit, Mockito, etc.)
 *      (ADDITIONAL) spring-boot-starter-actuator (health/metrics),
 *                   spring-boot-starter-validation.
 *
 * -------------------------------------------------------------------------------------
 * AUTO CONFIGURATION
 * -------------------------------------------------------------------------------------
 * Automatically configures beans based on the dependencies present on the CLASSPATH.
 *   Example: add spring-boot-starter-data-jpa and Spring Boot auto-creates:
 *      EntityManager, DataSource, TransactionManager.
 *
 *   HOW IT WORKS:
 *      @SpringBootApplication
 *        -> @EnableAutoConfiguration
 *        -> reads META-INF configuration (spring.factories / AutoConfiguration.imports)
 *        -> checks which dependencies exist (@ConditionalOnClass, @ConditionalOnMissingBean)
 *        -> creates the required beans automatically.
 *
 * -------------------------------------------------------------------------------------
 * CommandLineRunner
 * -------------------------------------------------------------------------------------
 * Runs code ONCE right AFTER the application starts. Use cases:
 *      - Initial data load
 *      - Startup validation
 *      - Cache initialization
 *
 *      @Component
 *      public class StartupRunner implements CommandLineRunner {
 *          public void run(String... args) {
 *              System.out.println("App started - loading initial data...");
 *          }
 *      }
 *   (ADDITIONAL) ApplicationRunner is similar but receives parsed ApplicationArguments.
 *
 * -------------------------------------------------------------------------------------
 * SPRING vs SPRING BOOT
 * -------------------------------------------------------------------------------------
 *   Spring                          | Spring Boot
 *   ------------------------------- | ---------------------
 *   Requires manual configuration   | Auto configuration
 *   External Tomcat needed          | Embedded Tomcat
 *   More setup                      | Minimal setup
 *   No starter dependencies         | Starter dependencies
 *   Time consuming                  | Faster development
 *
 * -------------------------------------------------------------------------------------
 * QUICK Q&A (INTERVIEW)
 * -------------------------------------------------------------------------------------
 * Q1: What is Spring Boot? -> An extension of Spring that removes boilerplate via auto
 *                             configuration, starters and an embedded server.
 * Q2: What does @SpringBootApplication do? -> Combines @Configuration +
 *                             @EnableAutoConfiguration + @ComponentScan.
 * Q3: How does Spring Boot start?
 *     -> JVM -> main() -> SpringApplication.run() -> ApplicationContext created ->
 *        beans loaded -> auto configuration -> embedded Tomcat starts -> app ready.
 * Q4: How does auto configuration work? -> @EnableAutoConfiguration reads META-INF,
 *     checks classpath dependencies (conditionals), and creates matching beans.
 * Q5: What is a starter dependency? -> A curated, version-aligned bundle of dependencies
 *     (e.g. spring-boot-starter-web).
 * Q6: What is CommandLineRunner? -> A hook that runs code once right after startup.
 * Q7: Spring vs Spring Boot? -> Spring = manual config + external server; Spring Boot =
 *     auto config + embedded server + starters + faster development.
 * Q8: @Controller vs @RestController?
 *                      -> @Controller returns views;
 *                         @RestController = @Controller + @ResponseBody, returns JSON/XML for REST APIs.
 *
 *
 * ONE-LINER SUMMARY:
 * Spring Boot = Spring + auto configuration + starters + embedded server, so you can
 * build production-ready apps quickly with minimal configuration.
 *
 * -------------------------------------------------------------------------------------
 * EXAMPLE BELOW:
 * A REAL, runnable Spring Boot application. @SpringBootApplication triggers component
 * scanning + auto configuration, SpringApplication.run(...) boots the ApplicationContext,
 * the @Service bean is discovered and constructor-injected into the @Component
 * CommandLineRunner, and that runner executes once right after startup.
 * =====================================================================================
 */
@SpringBootApplication
public class SpringBoot {

    public static void main(String[] args) {
        // Boots the Spring context (component scan + auto configuration), then runs
        // any CommandLineRunner/ApplicationRunner beans once the app is ready.
        SpringApplication.run(SpringBoot.class, args);
    }

    /** A real business-logic bean discovered by component scanning. */
    @Service
    public static class GreetingService {
        public String greet() {
            return "initial data loaded";
        }
    }

    /**
     * Real @Component CommandLineRunner: runs ONCE after startup. The GreetingService
     * is constructor-injected by the container (auto configuration + DI in action).
     * Use cases: initial data load, startup validation, cache initialization.
     */
    @Component
    public static class StartupRunner implements CommandLineRunner {
        private final GreetingService greetingService;

        public StartupRunner(GreetingService greetingService) {
            this.greetingService = greetingService;
        }

        @Override
        public void run(String... args) {
            System.out.println("App started - " + greetingService.greet());
        }
    }

    /**
     * Alternative style: register a CommandLineRunner via an @Bean method instead of a
     * @Component class - handy for quick startup hooks or third-party wiring.
     */
    @Bean
    public CommandLineRunner banner() {
        return args -> System.out.println("SpringBoot example is ready.");
    }
}
