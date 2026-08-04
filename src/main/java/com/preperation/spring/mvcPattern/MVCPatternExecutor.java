package com.preperation.spring.mvcPattern;

import com.preperation.spring.mvcPattern.model.Employee;
import com.preperation.spring.mvcPattern.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MVCPatternExecutor {

    public static void main(String[] args) {

        SpringApplication.run(MVCPatternExecutor.class, args);
    }

    CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

        };
    }
}
