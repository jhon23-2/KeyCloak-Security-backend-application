package com.keycloak.keyclok_service.dto;

import lombok.Builder;

import java.util.Set;


@Builder
public record UserDto(
        String username,
        String firstName,
        String lastName,
        String email,
        String password,
        Set<String> roles
        ) {
}
