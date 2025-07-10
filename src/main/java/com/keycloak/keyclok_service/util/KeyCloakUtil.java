package com.keycloak.keyclok_service.util;

import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.stereotype.Component;

@Component
public class KeyCloakUtil {


    /**
     * This method returns a RealmResource for the specified realm
     * and registration of the Keycloak admin client
     *
     * @return RealmResource for the specified realm
     */

    public  RealmResource getRealmResource() {

        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(KeyCloakConstants.SERVER_URL)
                .realm(KeyCloakConstants.REALM_MASTER)
                .clientId(KeyCloakConstants.ADMIN_CLI)
                .username(KeyCloakConstants.USERNAME)
                .password(KeyCloakConstants.PASSWORD)
                .clientSecret(KeyCloakConstants.CLIENT_SECRET)
                .resteasyClient(new ResteasyClientBuilderImpl()
                        .connectionPoolSize(10)
                        .build())
                .build();
        return keycloak.realm(KeyCloakConstants.REALM_NAME);
    }


    public UsersResource getUsersResource() {
        RealmResource realmResource = this.getRealmResource();
        return realmResource.users();
    }
}
