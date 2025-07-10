package com.keycloak.keyclok_service.converter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtAuthenticationConverter = new JwtGrantedAuthoritiesConverter();

    @Value("${keycloak.jwt.token.claim.client-id}")
    private String clientId;

    @Value("${keycloak.jwt.token.claim.username}")
    private String subjectClaim;

    @Override
    public AbstractAuthenticationToken convert(Jwt keycloakJwt) {

        Collection<GrantedAuthority> authorities = Stream
                .concat(jwtAuthenticationConverter.convert(keycloakJwt).stream(), extractRolesFromKeyCloakToken(keycloakJwt).stream())
                .toList();

        return new JwtAuthenticationToken(keycloakJwt, authorities, getSubjectClaim(keycloakJwt));
    }

    /**
     * Extracts roles from the Keycloak JWT token.
     *
     * @param keycloakJwt the JWT token from Keycloak
     * @return a collection of granted authorities representing the roles
     * value expected in the JWT token:
        "resource_access": {
            "spring-boot-application": {
                "roles": ["any_role"]
                    }
                }
        * If the roles are not present, an empty collection is returned.
     */

    private Collection<? extends GrantedAuthority> extractRolesFromKeyCloakToken(Jwt keycloakJwt){

        Map<String, Object> resourceAccess;
        Map<String, Object> clientIdAccess;
        Collection<String> roles;

        if(!keycloakJwt.getClaims().containsKey("resource_access")){
            return List.of();
        }

        resourceAccess = keycloakJwt.getClaim("resource_access");

        if(!resourceAccess.containsKey(clientId)){
            return List.of();
        }
        clientIdAccess = (Map<String, Object>) resourceAccess.get(clientId);

        if(!clientIdAccess.containsKey("roles")){
            return List.of();
        }

        roles = (Collection<String>) clientIdAccess.get("roles");
        return roles.stream()
                .map(role-> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    // This method is used to get the subject claim from the JWT token.

    private String getSubjectClaim(Jwt keycloakJwt) {
        return keycloakJwt.getClaimAsString(subjectClaim) != null ?
                keycloakJwt.getClaimAsString(subjectClaim):
                keycloakJwt.getClaimAsString(JwtClaimNames.SUB);
    }
}
