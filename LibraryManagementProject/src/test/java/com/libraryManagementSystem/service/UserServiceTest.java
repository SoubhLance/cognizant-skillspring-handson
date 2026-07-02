package com.libraryManagementSystem.service;

import com.libraryManagementSystem.dto.UserDto;
import com.libraryManagementSystem.entity.Role;
import com.libraryManagementSystem.entity.User;
import com.libraryManagementSystem.enums.UserStatus;
import com.libraryManagementSystem.mapper.UserMapper;
import com.libraryManagementSystem.repository.UserRepository;
import com.libraryManagementSystem.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        Role role = Role.builder().id(1L).name("ROLE_STUDENT").build();
        testUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();

        testUserDto = UserDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@test.com")
                .role("ROLE_STUDENT")
                .status("ACTIVE")
                .build();
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("john.doe@test.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }
}
