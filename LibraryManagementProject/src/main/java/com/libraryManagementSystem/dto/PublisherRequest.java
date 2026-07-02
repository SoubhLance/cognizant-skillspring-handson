package com.libraryManagementSystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PublisherRequest {
    @NotBlank(message = "Publisher name is required")
    @Size(max = 150)
    private String name;

    private String address;

    @Email(message = "Invalid email format")
    private String contactEmail;
}
