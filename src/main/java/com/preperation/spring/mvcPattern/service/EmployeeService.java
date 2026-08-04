package com.preperation.spring.mvcPattern.service;

import com.preperation.spring.mvcPattern.model.Employee;
import com.preperation.spring.mvcPattern.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        // Logic to retrieve all employees from the repository

        return employeeRepository.getAllEmployees();
    }

    public Employee getEmployeeById(Long id) {
        // Logic to retrieve an employee by ID from the repository

        return employeeRepository.getEmployeeById(id);
    }

    public Employee addEmployee(Employee employee) {
        // Logic to add a new employee to the repository

        return employeeRepository.addEmployee(employee);
    }

    public Employee updateEmployee(Long id, Employee employee) {
        // Logic to update an existing employee in the repository

        return employeeRepository.updateEmployee(id, employee);
    }

    public boolean deleteEmployee(Long id) {
        // Logic to delete an employee by ID from the repository

        return employeeRepository.deleteEmployee(id);
    }
}
