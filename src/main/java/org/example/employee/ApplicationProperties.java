package org.example.employee;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(value = "app.config", ignoreUnknownFields = false)
@RequiredArgsConstructor
@Getter
@Validated
public class ApplicationProperties {

//    @NotNull(message = "mySecret must not be null")
//    @Pattern(regexp = "^[a-z]{8}$", message = "mySecret must be 8 lowercase characters")
//    private final String mySecret;
}
