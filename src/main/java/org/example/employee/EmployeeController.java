package org.example.employee;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.ListServicesRequest;
import software.amazon.awssdk.services.ecs.model.UpdateServiceRequest;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

@RestController
//@RequiredArgsConstructor
//@CrossOrigin
//@CrossOrigin(methods = {RequestMethod.DELETE, RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.POST})
@Slf4j
public class EmployeeController {

    private static final String ECS_CONTAINER_METADATA_URI_V4 = System.getenv("ECS_CONTAINER_METADATA_URI_V4");
    private static final String ECS_AGENT_URI = System.getenv("ECS_AGENT_URI");
    private static final String LAMBDA_FUNCTION_NAME = System.getenv("LAMBDA_FUNCTION_NAME");

    private final Map<Integer, Employee> employees;
    private final RestTemplate restTemplate;

    private final EcsClient ecsClient;

    private final LambdaClient lambdaClient;

    private final Random random;

    private final ThreadPoolTaskExecutor taskExecutor;

    public EmployeeController(final Map<Integer, Employee> employees, final RestTemplateBuilder restTemplateBuilder, final EcsClient ecsClient,
        final LambdaClient lambdaClient, final Random random, final ThreadPoolTaskExecutor taskExecutor) {
        this.employees = employees;
        this.restTemplate = restTemplateBuilder.build();
        this.ecsClient = ecsClient;
        this.lambdaClient = lambdaClient;
        this.random = random;
        this.taskExecutor = taskExecutor;
    }

    @GetMapping("/employees")
//    @CrossOrigin
    List<Employee> getAllEmployees() {
        final var result = new ArrayList<>(employees.values());
//        final var lib = new Library();
//        log.info("value of imported lib: {}", lib.someLibraryMethod());
        log.info(String.valueOf(result));
        return result;
    }

    @PostMapping("/addItems")
    Map<String, String> addItemsToDynamoDbTable(@RequestBody final Map<String, Integer> request) {
        final var itemCount = request.get("itemCount");
        final var listOfFutures = new ArrayList<Future<InvokeResponse>>();
        log.info("item count: {}", itemCount);

        for (int i = 0; i < itemCount; i++) {

            listOfFutures.add(taskExecutor.submit(() -> {

                final var payload = String.format("{\"id\":%d,\"session-token\":\"%s\"}", random.nextInt(99_999_999), UUID.randomUUID());
//                log.info("payload: {}", payload);

                final var invokeRequest = InvokeRequest.builder()
                    .functionName(LAMBDA_FUNCTION_NAME)
                    .invocationType(InvocationType.EVENT)
                    .payload(SdkBytes.fromString(payload, Charset.defaultCharset()))
                    .build();
                return lambdaClient.invoke(invokeRequest);
            }));
        }
        final var listOfResults = listOfFutures.stream().map(e -> {
            try {
                return e.get(5, TimeUnit.SECONDS).statusCode();
            } catch (InterruptedException | ExecutionException | TimeoutException ex) {
                throw new RuntimeException(ex);
            }
        }).toList();
        return Map.of("result", listOfResults.toString());
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
//    @Cacheable("employee-directory-employees")
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
    GenericApiResponse getMyIp() {
        final var payload = "{\"key\":\"value\"}";

        final var invokeRequest = InvokeRequest.builder()
            .functionName(LAMBDA_FUNCTION_NAME)
            .invocationType(InvocationType.REQUEST_RESPONSE)
            .payload(SdkBytes.fromString(payload, Charset.defaultCharset()))
            .build();
        final var invokeResponse = lambdaClient.invoke(invokeRequest);
        var lambdaResponse = "error";

        if (200 == invokeResponse.statusCode()) {
            lambdaResponse = invokeResponse.payload().asUtf8String();
        }

//        return restTemplate.getForObject("http://checkip.amazonaws.com", String.class);
        return new GenericApiResponse(lambdaResponse, restTemplate.getForObject("http://checkip.amazonaws.com", String.class));
//        return restTemplate.getForObject("https://7s87p0wg7e.execute-api.us-east-1.amazonaws.com/prod/getip", String.class);
//        return String.format("THE_SSM_PARAM: %s", System.getenv("THE_SSM_PARAM"));
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

    // TODO: add AWS scheduled event to make desired # tasks 0 for all services daily at 23:00 AEST
    @Scheduled(initialDelay = 2, fixedDelay = 30, timeUnit = TimeUnit.HOURS)
//    @Scheduled(initialDelay = 3, fixedDelay = 30, timeUnit = TimeUnit.MINUTES)
    void stopAllTasksOfAllServices() {
//        final var requestHeaders = new HttpHeaders();
//        requestHeaders.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
//        restTemplate.exchange("http://localhost:8080/actuator/shutdown", POST, new HttpEntity<>(requestHeaders), String.class);
        final var cluster = System.getenv("CLUSTER_NAME");
        final var listServiceResponse = ecsClient.listServices(ListServicesRequest.builder().cluster(cluster).build());

        for (final var service : listServiceResponse.serviceArns()) {

            final var serviceRequest = UpdateServiceRequest.builder()
                .cluster(cluster)
                .service(service)
                .desiredCount(0)
                .build();

            ecsClient.updateService(serviceRequest);
        }
    }

    @GetMapping(value = "/app-config", produces = APPLICATION_JSON_VALUE)
    String getAwsAppConfig() {
        return restTemplate.getForObject(
            "http://localhost:2772/applications/aws-employee-directory/environments/prod/configurations/aws-employee-directory", String.class);
    }

    @GetMapping(value = "/app-config-via-file", produces = APPLICATION_JSON_VALUE)
    String getAwsAppConfigFromFile() throws IOException {
        return Files.readString(Path.of("/mnt/app-config/app-config.json"), Charset.defaultCharset());
    }

    @GetMapping(value = "/self-call")
    String performSelfCallUsingServiceConnect() {
        return restTemplate.getForObject("http://api-server.aws.employee.directory.local:8080/my-ip", String.class);
    }

    // call a shared microservice
    @GetMapping(value = "/shared-ms-hello", produces = APPLICATION_JSON_VALUE)
    String callSharedMsHello() {
//        return restTemplate.getForObject("http://localhost:8070/hello", String.class);
        return restTemplate.getForObject("http://shared-boot-ms.internal:8080/hello", String.class);
    }
}
