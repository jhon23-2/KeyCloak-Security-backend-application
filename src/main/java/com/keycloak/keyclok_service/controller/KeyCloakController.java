package com.keycloak.keyclok_service.controller;


import com.keycloak.keyclok_service.dto.UserDto;
import com.keycloak.keyclok_service.service.KeyCloakMethods;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('admin_client')")
@RequestMapping("/api/keycloak")
public class KeyCloakController {

    private final KeyCloakMethods keyCloakMethods;

    public KeyCloakController(KeyCloakMethods keyCloakMethods) {
        this.keyCloakMethods = keyCloakMethods;
    }

    /**
     * Retrieves all users from the Keycloak realm.
     * @return a ResponseEntity containing a list of UserRepresentation objects.
     */

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<UserRepresentation> users = this.keyCloakMethods.findAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a user by their username.
     * @param username the username of the user to retrieve.
     * @return a ResponseEntity containing the UserRepresentation if found, or a 404 Not Found status if not found.
     */
    @GetMapping("/users/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        return this.keyCloakMethods.findUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    /**
     * Saves a new user in the Keycloak realm.
     * @param userDto the UserDto containing user details to be saved.
     * @return a ResponseEntity with the created user details and a status of 201 Created.
     */
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody UserDto userDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(this.keyCloakMethods.save(userDto));
    }


    /**
     * Updates an existing user in the Keycloak realm.
     * @param userId the ID of the user to update.
     * @param userDto the UserDto containing updated user details.
     * @return a ResponseEntity with a success message and a status of 200 OK.
     */
    @PutMapping("/update/{userId}")
    public ResponseEntity<?> update(@PathVariable String userId, @RequestBody UserDto userDto){
        this.keyCloakMethods.update(userId, userDto);
        return ResponseEntity.ok("User updated successfully");
    }


    /**
     * Deletes a user from the Keycloak realm.
     * @param userId the ID of the user to delete.
     */
    @DeleteMapping("/delete/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String userId){
        this.keyCloakMethods.delete(userId);
    }



}
