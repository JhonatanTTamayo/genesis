package com.breaze.genesis.services;

import com.breaze.genesis.dto.auth.responses.UserProfileResponse;
import com.breaze.genesis.dto.users.requests.UpdateUserStatusRequest;
import com.breaze.genesis.dto.users.responses.UserStatusResponse;

public interface IUserService {
    UserProfileResponse getMyProfile(String email);
    UserStatusResponse updateUserStatus(Long id, UpdateUserStatusRequest request);
}
