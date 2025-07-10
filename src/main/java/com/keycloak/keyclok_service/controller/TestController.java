package com.keycloak.keyclok_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping("/admin")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('admin_client')") // Here the role makes matches the one defined in Keycloak
    public String adminTest() {
        return "ADMIN  - access granted";
    }

    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('user_client') or hasRole('admin_client')") // Here the role makes matches the one defined in Keycloak
    public String userTest() {
        return "USER  - access granted";
    }


    /** NOTE
     * Now We're going to create a class for to set the jwt role because from keycloak token come like
     * admin_client. However, inside our spring boot application, we want to use the role like
     * ROLE_admin_client and ROLE_user_client because spring security works with this prefix
     */

}
