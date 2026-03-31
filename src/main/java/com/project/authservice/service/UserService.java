package com.project.authservice.service;

import com.project.authservice.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse findById(Long id);

    UserResponse findByUsername(String username);

    Page<UserResponse> findAll(Pageable pageable);

    UserResponse getCurrentUserProfile(String username);
}