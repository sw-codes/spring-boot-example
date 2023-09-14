package com.swcode.auth;

public record AuthenticationRequest(
        String username,
        String password
) {
}
