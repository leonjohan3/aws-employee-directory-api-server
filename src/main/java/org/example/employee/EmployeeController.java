package org.example.employee;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
//@CrossOrigin
//@CrossOrigin(methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.POST})
public class EmployeeController {

    private final Map<Integer, Employee> employees;

    @GetMapping("/employees")
//    @CrossOrigin
    List<Employee> getAllEmployees() {
        return new ArrayList<>(employees.values());
    }

    @PostMapping("/employees")
//    @CrossOrigin
    Employee addOrUpdateEmployee(@RequestBody final Employee employee) {
        var id = employee.getId();

        if (Objects.isNull(id) || 0 == id) {
            id = employees.keySet().stream().reduce(Integer::max).orElse(0) + 1;
        }
        employees.put(id, Employee.builder()
            .id(id)
            .firstName(employee.getFirstName())
            .lastName(employee.getLastName())
            .email(employee.getEmail())
            .build());
        return employees.get(id);
    }

    @GetMapping("/employees/{id}")
    Employee getEmployee(@PathVariable final Integer id) throws EmployeeNotFoundException {
        if (employees.containsKey(id)) {
            return employees.get(id);
        } else {
            throw new EmployeeNotFoundException(id);
        }
    }

    @DeleteMapping("/employees/{id}")
//    @CrossOrigin(methods = {RequestMethod.DELETE})
    void deleteEmployee(@PathVariable final Integer id) throws EmployeeNotFoundException {
        if (employees.containsKey(id)) {
            employees.remove(id);
        } else {
            throw new EmployeeNotFoundException(id);
        }
    }
}
