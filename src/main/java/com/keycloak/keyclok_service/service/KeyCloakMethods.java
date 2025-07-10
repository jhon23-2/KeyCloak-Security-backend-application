package com.keycloak.keyclok_service.service;

import com.keycloak.keyclok_service.dto.UserDto;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Optional;

public interface KeyCloakMethods {
    List<UserRepresentation> findAllUsers();
    Optional<List<UserRepresentation>> findUserByUsername(String username);
    String save(UserDto userDto);
    void update(String userId, UserDto userDto);
    void delete(String userId);
}
