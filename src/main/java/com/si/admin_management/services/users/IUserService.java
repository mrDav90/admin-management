package com.si.admin_management.services.users;

import com.si.admin_management.dtos.keycloak.KcUserDto;
import com.si.admin_management.dtos.keycloak.KcUserDtoRequest;
import com.si.admin_management.dtos.keycloak.UserInfos;
import org.springframework.data.domain.Page;

public interface IUserService {
    KcUserDto createUser(KcUserDtoRequest kcUserDtoRequest);
    Page<KcUserDto> getUsers (int pageNumber, int pageSize);
    void updateUser(String userId, KcUserDtoRequest kcUserDtoRequest);
    void deleteUser(String userId);
    UserInfos getMe();
}
