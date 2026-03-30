package com.example.Fashion.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class RegisterRequestDTO {
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String address;
}