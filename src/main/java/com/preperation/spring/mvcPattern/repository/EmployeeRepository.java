package com.preperation.spring.mvcPattern.repository;

import com.preperation.spring.mvcPattern.model.Employee;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmployeeRepository {

    static List<Employee> employeeList;

    @PostConstruct
    void initializeData(){
        employeeList = List.of(
                new Employee(1, "John Doe", "Developer", 60000),
                new Employee(2, "Jane Smith", "Manager", 80000),
                new Employee(3, "Alice Johnson", "Designer", 55000),
                new Employee(4, "Bob Brown", "Tester", 50000),
                new Employee(5, "Charlie Davis", "Support", 45000),
                new Employee(6, "Diana Evans", "HR", 70000),
                new Employee(7, "Frank Green", "Sales", 65000),
                new Employee(8, "Grace Harris", "Marketing", 60000),
                new Employee(9, "Henry Jackson", "Finance", 75000),
                new Employee(10, "Ivy King", "Operations", 70000)
        );
    }

    public List<Employee> getAllEmployees() {
        // Logic to retrieve all employees from the data source
        return employeeList;
    }

    public Employee getEmployeeById(Long id) {
        // Logic to retrieve an employee by ID from the data source

        return employeeList.stream()
                .filter(employee -> employee.getId() == id)
                .findFirst()
                .orElse(null);

    }

    public Employee addEmployee(Employee employee) {
        // Logic to add a new employee to the data source

        if(employeeList.add(employee)){
            return employee;
        }
        return null;
    }

    public Employee updateEmployee(Long id, Employee employee) {
        // Logic to update an existing employee in the data source

        employeeList.stream()
                .filter(emp -> emp.getId() == id)
                .findFirst()
                .ifPresent(existingEmployee -> {
                    existingEmployee.setName(employee.getName());
                    existingEmployee.setRole(employee.getRole());
                    existingEmployee.setSalary(employee.getSalary());
                });

        return getEmployeeById(id);
    }

    public boolean deleteEmployee(Long id) {
        // Logic to delete an employee by ID from the data source

        return employeeList.removeIf(employee -> employee.getId() == id);
    }
}
