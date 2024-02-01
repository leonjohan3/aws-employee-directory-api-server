package org.example.employee;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
//@RequiredArgsConstructor
//@CrossOrigin
//@CrossOrigin(methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.POST})
//@Slf4j
public class EmployeeController {

    private static final String ECS_CONTAINER_METADATA_URI_V4 = System.getenv("ECS_CONTAINER_METADATA_URI_V4");
    private static final String ECS_AGENT_URI = System.getenv("ECS_AGENT_URI");

    private final Map<Integer, Employee> employees;
    private final RestTemplate restTemplate;

    public EmployeeController(final Map<Integer, Employee> employees, final RestTemplateBuilder restTemplateBuilder) {
        this.employees = employees;
        this.restTemplate = restTemplateBuilder.build();
    }

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

    @GetMapping("/my-ip")
    String getMyIp() {
        return restTemplate.getForObject("http://checkip.amazonaws.com", String.class);
    }

    @GetMapping(value = "/task", produces = APPLICATION_JSON_VALUE)
    String getTask() {
        return restTemplate.getForObject(ECS_CONTAINER_METADATA_URI_V4 + "/task", String.class);
    }

    @GetMapping(value = "/stats", produces = APPLICATION_JSON_VALUE)
    String getStats() {
        return restTemplate.getForObject(ECS_CONTAINER_METADATA_URI_V4 + "/stats", String.class);
    }

    @GetMapping(value = "/task/stats", produces = APPLICATION_JSON_VALUE)
    String getTaskStats() {
        return restTemplate.getForObject(ECS_CONTAINER_METADATA_URI_V4 + "/task/stats", String.class);
    }

    @GetMapping(value = "/task-protection/v1/state", produces = APPLICATION_JSON_VALUE)
    String getTaskProtectionState() {
        return restTemplate.getForObject(ECS_AGENT_URI + "/task-protection/v1/state", String.class);
    }

    @Scheduled(initialDelay = 2, fixedDelay = 30, timeUnit = TimeUnit.HOURS)
    void quit() throws URISyntaxException {
        final var requestHeaders = new HttpHeaders();
        requestHeaders.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        restTemplate.exchange("http://localhost:8080/actuator/shutdown", POST, new HttpEntity<>(requestHeaders), String.class);
    }

    @GetMapping(value = "/app-config", produces = APPLICATION_JSON_VALUE)
    String getAwsAppConfig() {
        return restTemplate.getForObject(
            "http://localhost:2772/applications/aws-employee-directory/environments/prod/configurations/aws-employee-directory", String.class);
    }
}
