package org.example.employee;

import static org.example.employee.EmployeeController.LAMBDA_FUNCTION_NAME;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;
import software.amazon.awssdk.services.lambda.model.InvokeResponse;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final LambdaClient lambdaClient;

    @Async
    public CompletableFuture<InvokeResponse> getLambdaResponse() {
        final var payload = "{\"key\":\"value\"}";
        final var invokeRequest = InvokeRequest.builder()
            .functionName(LAMBDA_FUNCTION_NAME)
            .invocationType(InvocationType.REQUEST_RESPONSE)
            .payload(SdkBytes.fromString(payload, Charset.defaultCharset()))
            .build();
        return CompletableFuture.completedFuture(lambdaClient.invoke(invokeRequest));
    }
}
