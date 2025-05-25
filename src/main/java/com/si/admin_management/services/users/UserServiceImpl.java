package com.si.admin_management.services.users;

import com.si.admin_management.dtos.keycloak.KcUserDto;
import com.si.admin_management.dtos.keycloak.KcUserDtoRequest;
import com.si.admin_management.dtos.keycloak.Permission;
import com.si.admin_management.dtos.keycloak.UserInfos;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final Keycloak keycloakAdmin;
    @Value("${keycloak.realm}")
    private String realm;

    public KcUserDto createUser(KcUserDtoRequest kcUserDtoRequest) {
        UserRepresentation user = new UserRepresentation();

        user.setFirstName(kcUserDtoRequest.getFirstName());
        user.setLastName(kcUserDtoRequest.getLastName());
        user.setUsername(kcUserDtoRequest.getEmail());
        user.setEmail(kcUserDtoRequest.getEmail());
        user.setEmailVerified(true);
        user.setEnabled(true);
        user.setRequiredActions(Collections.emptyList());



        Response response = keycloakAdmin.realm(realm).users().create(user);

        if (response.getStatus() == 201) {
            String userId = keycloakAdmin.realm(realm).users()
                    .search(kcUserDtoRequest.getEmail())
                    .get(0).getId();

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(false);
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(kcUserDtoRequest.getPassword());
            keycloakAdmin.realm(realm).users().get(userId).resetPassword(credential);

            RoleRepresentation realmRole = keycloakAdmin.realm(realm).roles().get(kcUserDtoRequest.getRole()).toRepresentation();
            keycloakAdmin.realm(realm).users().get(userId).roles().realmLevel().add(List.of(realmRole));

            KcUserDto kcUserDto = new KcUserDto();
            kcUserDto.setId(userId);
            kcUserDto.setFirstName(kcUserDtoRequest.getFirstName());
            kcUserDto.setLastName(kcUserDtoRequest.getLastName());
            kcUserDto.setEmail(kcUserDtoRequest.getEmail());

            logger.info("User created : {}", kcUserDto);

            return kcUserDto;

        }

        return null;
    }


    public Page<KcUserDto> getUsers (int pageNumber, int pageSize) {
        int firstResult = pageNumber * pageSize;

        Pageable pagedRequest = PageRequest.of(pageNumber,pageSize);
        List<KcUserDto> users = keycloakAdmin.realm(realm).users().list(firstResult,pageSize).stream().map(user -> {
            KcUserDto kcUserDto = new KcUserDto();
            kcUserDto.setId(user.getId());
            kcUserDto.setUsername(user.getUsername());
            kcUserDto.setFirstName(user.getFirstName());
            kcUserDto.setLastName(user.getLastName());
            kcUserDto.setEmail(user.getEmail());
            kcUserDto.setEnabled(user.isEnabled());
//            keycloakAdmin.realm(realm).roles().list().stream().forEach(role -> {
//
//            });
            //kcUserDto.setRealmRoles(user.getRealmRoles().stream().toList());
            return kcUserDto;
        }).toList() ;
        long totalUsers = keycloakAdmin.realm(realm).users().count();
        return new PageImpl<>(users, pagedRequest, totalUsers) ;
    }

    public void updateUser(String userId, KcUserDtoRequest kcUserDtoRequest) {
        UserRepresentation user = keycloakAdmin.realm(realm).users().get(userId).toRepresentation();
        user.setEmail(kcUserDtoRequest.getEmail());
        user.setFirstName(kcUserDtoRequest.getFirstName());
        user.setLastName(kcUserDtoRequest.getLastName());
        user.setUsername(kcUserDtoRequest.getEmail());
        keycloakAdmin.realm(realm).users().get(userId).update(user);

    }

    public void deleteUser(String username) {
        UsersResource usersResource = keycloakAdmin.realm(realm).users();
        List<UserRepresentation> users = usersResource.search(username);

        if (users.isEmpty()) {
            System.out.println("Utilisateur non trouvé !");
            return;
        }
        String userId = users.get(0).getId();
        usersResource.get(userId).remove();
    }

    public UserInfos getMe() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Permission> permissions = Optional.ofNullable(jwt.getClaimAsMap("authorization"))
                .map(auth -> (List<Permission>) auth.get("permissions"))
                .orElse(Collections.emptyList());


        UserInfos userInfos = new UserInfos();
        userInfos.setUsername(jwt.getClaimAsString("preferred_username"));
        userInfos.setEmail(jwt.getClaimAsString("email"));
        userInfos.setFirstName(jwt.getClaimAsString("given_name"));
        userInfos.setLastName(jwt.getClaimAsString("family_name"));
        userInfos.setPermissions(permissions);

        return userInfos;
    }

}
