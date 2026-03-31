package com.project.authservice.controller;

import com.project.authservice.dto.response.ApiResponse;
import com.project.authservice.dto.response.UserResponse;
import com.project.authservice.security.UserPrincipal;
import com.project.authservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "User profile endpoints")
@SecurityRequirement(name = "BearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            @AuthenticationPrincipal UserPrincipal currentUser) {

        UserResponse profile = userService.getCurrentUserProfile(currentUser.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", profile));
    }

    @GetMapping("/profile/{username}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Get user profile by username")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(@PathVariable String username) {
        UserResponse profile = userService.findByUsername(username);
        return ResponseEntity.ok(ApiResponse.success("Profile retrieved", profile));
    }
}