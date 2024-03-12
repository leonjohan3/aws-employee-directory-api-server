package org.example.employee;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@Jacksonized
@ToString
public class Employee {
    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
}
