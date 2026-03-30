package com.example.Fashion.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponseDTO {
    private String token;
    private String email;
    private String fullName;
    private String role;
    private Long userId;
}