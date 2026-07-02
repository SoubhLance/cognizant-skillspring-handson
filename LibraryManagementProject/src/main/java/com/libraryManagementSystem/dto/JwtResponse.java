package com.libraryManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponse {
    private String token;
    private String refreshToken;
    
    @Builder.Default
    private String type = "Bearer";
    
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private List<String> permissions;
}
