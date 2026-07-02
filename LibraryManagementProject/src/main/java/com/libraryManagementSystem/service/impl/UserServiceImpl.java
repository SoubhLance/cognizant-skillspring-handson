package com.libraryManagementSystem.service.impl;

import com.libraryManagementSystem.dto.UserDto;
import com.libraryManagementSystem.entity.User;
import com.libraryManagementSystem.enums.UserStatus;
import com.libraryManagementSystem.exception.UserNotFoundException;
import com.libraryManagementSystem.mapper.UserMapper;
import com.libraryManagementSystem.repository.UserRepository;
import com.libraryManagementSystem.security.UserDetailsImpl;
import com.libraryManagementSystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        return userMapper.toDto(user);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return userMapper.toDto(user);
    }

    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    @Override
    @Transactional
    public UserDto updateUserStatus(Long id, UserStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        user.setStatus(status);
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserDto getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            return getUserById(userDetails.getId());
        }
        throw new RuntimeException("No authenticated user session found");
    }
}
