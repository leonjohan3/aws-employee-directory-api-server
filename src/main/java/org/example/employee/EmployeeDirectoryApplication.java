package org.example.employee;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(proxyBeanMethods = false)
@Slf4j
public class EmployeeDirectoryApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeDirectoryApplication.class, args);
    }

    @Override
    public void run(final String... args) throws Exception {
        log.info("command line runner, running...");
    }
}
