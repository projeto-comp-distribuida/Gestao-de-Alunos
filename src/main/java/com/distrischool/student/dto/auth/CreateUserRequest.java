package com.distrischool.student.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para criar um usuário no serviço de autenticação
 * Usado para comunicação entre serviços
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String documentNumber;
    private String role; // ADMIN, TEACHER, STUDENT, PARENT
}

