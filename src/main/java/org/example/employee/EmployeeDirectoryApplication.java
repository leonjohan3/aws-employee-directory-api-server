package org.example.employee;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(proxyBeanMethods = false)
@Slf4j
@EnableAsync
@EnableConfigurationProperties(ApplicationProperties.class)
public class EmployeeDirectoryApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeDirectoryApplication.class, args);
    }

    @Override
    public void run(final String... args) throws Exception {
        log.info("command line runner, running...");
    }
}
