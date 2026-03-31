package com.project.authservice.service.impl;

import com.project.authservice.dto.request.LoginRequest;
import com.project.authservice.dto.request.RegisterRequest;
import com.project.authservice.dto.response.AuthResponse;
import com.project.authservice.dto.response.UserResponse;
import com.project.authservice.entity.Role;
import com.project.authservice.entity.User;
import com.project.authservice.exception.InvalidCredentialsException;
import com.project.authservice.exception.RoleNotFoundException;
import com.project.authservice.exception.UserAlreadyExistsException;
import com.project.authservice.repository.RoleRepository;
import com.project.authservice.repository.UserRepository;
import com.project.authservice.security.UserPrincipal;
import com.project.authservice.service.AuditLogService;
import com.project.authservice.service.AuthService;
import com.project.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already registered: " + request.getEmail());
        }

        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new RoleNotFoundException(Role.RoleName.ROLE_USER.name()));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(userRole))
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        auditLogService.log(
                "REGISTRATION",
                savedUser.getUsername(),
                "New user registered: " + savedUser.getEmail(),
                "SYSTEM",
                "SUCCESS"
        );

        log.info("New user registered: {}", savedUser.getUsername());
        return toUserResponse(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );

            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            String token = jwtUtil.generateToken(principal);

            Set<String> roles = principal.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.toSet());

            auditLogService.log(
                    "LOGIN_SUCCESS",
                    principal.getUsername(),
                    "User logged in successfully",
                    "SYSTEM",
                    "SUCCESS"
            );

            log.info("User logged in: {}", principal.getUsername());

            return AuthResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .userId(principal.getId())
                    .username(principal.getUsername())
                    .email(principal.getEmail())
                    .roles(roles)
                    .build();

        } catch (BadCredentialsException ex) {
            auditLogService.log(
                    "LOGIN_FAILURE",
                    request.getUsernameOrEmail(),
                    "Login failed — invalid credentials",
                    "SYSTEM",
                    "FAILURE"
            );
            log.warn("Failed login attempt for: {}", request.getUsernameOrEmail());
            throw new InvalidCredentialsException();
        }
    }

    private UserResponse toUserResponse(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .enabled(user.isEnabled())
                .roles(roleNames)
                .createdAt(user.getCreatedAt())
                .build();
    }
}