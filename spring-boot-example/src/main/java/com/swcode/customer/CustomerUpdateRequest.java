package com.swcode.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
) {
}
