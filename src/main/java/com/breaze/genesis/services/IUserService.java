package com.breaze.genesis.services;

import com.breaze.genesis.dto.auth.responses.UserProfileResponse;

public interface IUserService {
    UserProfileResponse getMyProfile(String email);   
}
