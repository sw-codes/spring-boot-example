package com.swcode.customer;

public record CustomerRegistrationRequest(
        String name,
        String email,
        Integer age
) {
}
