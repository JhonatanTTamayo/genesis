package com.breaze.genesis.services;

import com.breaze.genesis.dto.auth.responses.UserProfileResponse;
import com.breaze.genesis.dto.users.requests.UpdateUserStatusRequest;
import com.breaze.genesis.dto.users.responses.UserPageResponse;
import com.breaze.genesis.dto.users.responses.UserStatusResponse;
import org.springframework.data.domain.Pageable;

public interface IUserService {
    UserPageResponse listUsers(Pageable pageable);
    UserProfileResponse getMyProfile(String email);
    UserStatusResponse updateUserStatus(Long id, UpdateUserStatusRequest request);
}
