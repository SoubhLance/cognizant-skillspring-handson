package com.libraryManagementSystem.service;

import com.libraryManagementSystem.dto.UserDto;
import com.libraryManagementSystem.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserDto getUserById(Long id);
    UserDto getUserByEmail(String email);
    Page<UserDto> getAllUsers(Pageable pageable);
    UserDto updateUserStatus(Long id, UserStatus status);
    UserDto getCurrentUser();
}
