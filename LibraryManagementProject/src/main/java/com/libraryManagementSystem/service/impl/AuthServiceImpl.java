package com.libraryManagementSystem.service.impl;

import com.libraryManagementSystem.dto.*;
import com.libraryManagementSystem.entity.*;
import com.libraryManagementSystem.enums.UserStatus;
import com.libraryManagementSystem.exception.UserNotFoundException;
import com.libraryManagementSystem.mapper.UserMapper;
import com.libraryManagementSystem.repository.*;
import com.libraryManagementSystem.security.UserDetailsImpl;
import com.libraryManagementSystem.security.jwt.JwtUtils;
import com.libraryManagementSystem.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Value("${app.jwt.refresh-expiration-ms}")
    private Long refreshTokenDurationMs;

    @Override
    @Transactional
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> permissions = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("User account is inactive or suspended");
        }

        // Handle Refresh Token
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElse(null);

        if (refreshToken == null) {
            refreshToken = RefreshToken.builder()
                    .user(user)
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                    .build();
            refreshToken = refreshTokenRepository.save(refreshToken);
        } else if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            refreshToken = refreshTokenRepository.save(refreshToken);
        }

        return JwtResponse.builder()
                .token(jwt)
                .refreshToken(refreshToken.getToken())
                .id(userDetails.getId())
                .firstName(userDetails.getFirstName())
                .lastName(userDetails.getLastName())
                .email(userDetails.getUsername())
                .role(user.getRole().getName())
                .permissions(permissions)
                .build();
    }

    @Override
    @Transactional
    public UserDto registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        Role userRole = roleRepository.findByName(registerRequest.getRole())
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .phone(registerRequest.getPhone())
                .role(userRole)
                .status(UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);

        // Generate email verification token (simulated email verification flow)
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();
        emailVerificationTokenRepository.save(verificationToken);

        // In-app notification
        Notification notification = Notification.builder()
                .user(user)
                .message("Welcome to Enterprise Library Management System! Your registration is successful.")
                .type("GENERAL")
                .build();
        notificationRepository.save(notification);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public JwtResponse refreshAccessToken(TokenRefreshRequest refreshRequest) {
        String requestRefreshToken = refreshRequest.getRefreshToken();

        RefreshToken token = refreshTokenRepository.findByToken(requestRefreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }

        User user = token.getUser();
        String accessToken = jwtUtils.generateTokenFromUsername(user.getEmail());

        List<String> permissions = List.of(user.getRole().getName());

        return JwtResponse.builder()
                .token(accessToken)
                .refreshToken(token.getToken())
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .permissions(permissions)
                .build();
    }

    @Override
    @Transactional
    public void logoutUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            User user = userRepository.findById(userDetails.getId()).orElse(null);
            if (user != null) {
                refreshTokenRepository.deleteByUser(user);
            }
        }
        SecurityContextHolder.clearContext();
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusHours(2))
                .build();
        passwordResetTokenRepository.save(resetToken);

        // In a real app, send email. Here, we log the reset link and insert a system notification.
        System.out.println("Password reset token generated: " + resetToken.getToken());
        
        Notification notification = Notification.builder()
                .user(user)
                .message("A password reset request was initiated. Reset Token: " + resetToken.getToken())
                .type("SECURITY")
                .build();
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid password reset token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new RuntimeException("Password reset token expired");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);

        Notification notification = Notification.builder()
                .user(user)
                .message("Your account password has been reset successfully.")
                .type("SECURITY")
                .build();
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid email verification token"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            emailVerificationTokenRepository.delete(verificationToken);
            throw new RuntimeException("Email verification token expired");
        }

        User user = verificationToken.getUser();
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        emailVerificationTokenRepository.delete(verificationToken);
    }
}
