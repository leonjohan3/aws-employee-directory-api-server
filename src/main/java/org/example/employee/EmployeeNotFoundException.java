package org.example.employee;

public class EmployeeNotFoundException extends Exception {

    EmployeeNotFoundException(final Integer id) {
        super("Could not find employee " + id);
    }
}
