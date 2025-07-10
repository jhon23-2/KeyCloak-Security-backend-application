package com.keycloak.keyclok_service.service;

import com.keycloak.keyclok_service.dto.UserDto;
import com.keycloak.keyclok_service.util.KeyCloakUtil;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class KeyCloakService implements KeyCloakMethods{

    private final KeyCloakUtil keyCloakUtil;

    public KeyCloakService(KeyCloakUtil keyCloakUtil) {
        this.keyCloakUtil = keyCloakUtil;
    }

    /**
     * Retrieves all users from the Keycloak realm.
     * This method uses the KeyCloakUtil to access the realm resource and fetches a list of all users.
     *
     * @return a list of UserRepresentation objects representing all users.
     */
    @Override
    public List<UserRepresentation> findAllUsers() {
        return this.keyCloakUtil.getRealmResource().users().list();
    }

    /**
     * Searches for users by their username in the Keycloak realm.
     * This method uses the KeyCloakUtil to access the realm resource and searches for users by their username.
     * @param username the username to search for.
     * @return an Optional containing a list of UserRepresentation objects if found, otherwise an empty Optional.
     */
    @Override
    public Optional<List<UserRepresentation>> findUserByUsername(String username) {
        return Optional.of(
                this.keyCloakUtil.getRealmResource()
                        .users().searchByUsername(username, true)
        );
    }


    /**
     * Saves a new user in the Keycloak realm.
     * This method uses the KeyCloakUtil to access the users resource and creates a new user with the provided UserDto.
     * @param userDto the UserDto containing user details to be saved.
     * @return a String message indicating the result of the operation.
     */
    @Override
    public String save(UserDto userDto) {

        UsersResource userResource = this.keyCloakUtil.getUsersResource();

        UserRepresentation userRepresentation = new UserRepresentation();

        // Set user details from UserDto
        userRepresentation.setFirstName(userDto.firstName());
        userRepresentation.setLastName(userDto.lastName());
        userRepresentation.setEmail(userDto.email());
        userRepresentation.setUsername(userDto.username());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);

        Response response = userResource.create(userRepresentation);// Create the user in Keycloak

        if(response.getStatus() == 201){
            String location = response.getLocation().getPath();
            String userId = location.substring(location.lastIndexOf("/") + 1); // Extract the user ID from the response location

            CredentialRepresentation credentialRepresentation = new CredentialRepresentation();

            // Set the user's credentials
            credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
            credentialRepresentation.setTemporary(false);
            credentialRepresentation.setValue(userDto.password());

            userResource.get(userId).resetPassword(credentialRepresentation); // Set the user's password


            // we're going to add the user to the roles

            RealmResource realmResource = this.keyCloakUtil.getRealmResource();
            List<RoleRepresentation> rolesRepresentations = null;

            if (userDto.roles().isEmpty()) {
                rolesRepresentations = realmResource // Default role
                        .roles()
                        .list()
                        .stream()
                        .filter(role -> role.getName().equals("user"))
                        .toList();
            }else {
              rolesRepresentations = realmResource.roles()
                        .list()
                        .stream()
                      .filter(role -> userDto.roles()
                              .stream()
                              .map(String::toLowerCase)
                              .collect(Collectors.toSet())
                              .contains(role.getName())
                      )
                      .toList();
            }

            realmResource.users().get(userId).roles().realmLevel().add(rolesRepresentations); // Add the user to the roles

            log.info("User created successfully with ID: {}", userId);

            return "User created successfully with ID: " + userId;

        }

        // If the response status is not 201 so the user was not created successfully
        log.error("Failed to create user: {}", response.getStatusInfo().getReasonPhrase());

        return "Failed to create user: " + response.getStatusInfo().getReasonPhrase();
    }

    /**
     * Retrieves a user by their ID from the Keycloak realm.
     * This method uses the KeyCloakUtil to access the realm resource and fetches a user by their ID.
     * @param userId the ID of the user to retrieve
     * @param userDto the UserDto object containing new user details
     */
    @Override
    public void update(String userId, UserDto userDto) {

        // Set the user's credentials
        CredentialRepresentation newCredentialRepresentation = new CredentialRepresentation();

        newCredentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        newCredentialRepresentation.setTemporary(false);
        newCredentialRepresentation.setValue(userDto.password());

        // Set user details from UserDto
        UserRepresentation newUserRepresentation = new UserRepresentation();

        newUserRepresentation.setFirstName(userDto.firstName());
        newUserRepresentation.setLastName(userDto.lastName());
        newUserRepresentation.setEmail(userDto.email());
        newUserRepresentation.setUsername(userDto.username());
        newUserRepresentation.setEnabled(true);
        newUserRepresentation.setEmailVerified(true);
        newUserRepresentation.setCredentials(Collections.singletonList(newCredentialRepresentation));


        this.keyCloakUtil.getUsersResource().get(userId)
                .update(newUserRepresentation); // Update the user in Keycloak
    }

    /**
     * Deletes a user from the Keycloak realm by their ID.
     * This method uses the KeyCloakUtil to access the users resource and removes the user.
     * @param userId the ID of the user to delete
     */

    @Override
    public void delete(String userId) {
        this.keyCloakUtil.getUsersResource()
                .get(userId)
                .remove();
        log.info("User with ID: {} deleted successfully", userId);
    }
}
