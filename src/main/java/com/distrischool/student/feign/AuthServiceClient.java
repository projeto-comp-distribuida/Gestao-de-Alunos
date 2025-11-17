package com.distrischool.student.feign;

import com.distrischool.student.dto.auth.CreateUserRequest;
import com.distrischool.student.dto.auth.ApiResponse;
import com.distrischool.student.dto.auth.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

/**
 * Feign client para comunicação com o serviço de autenticação
 */
@FeignClient(name = "auth-service", url = "${microservice.auth.url:http://microservice-auth-dev:8080}")
public interface AuthServiceClient {

    /**
     * Cria um novo usuário no serviço de autenticação
     * Endpoint interno para uso entre serviços
     */
    @PostMapping("/api/v1/auth/internal/users")
    ApiResponse<com.distrischool.student.dto.auth.AuthResponse> createUser(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody CreateUserRequest request);

    /**
     * Busca um usuário por Auth0 ID
     */
    @GetMapping("/api/v1/users/auth0/{auth0Id}")
    ApiResponse<UserResponse> getUserByAuth0Id(@PathVariable String auth0Id);

    /**
     * Verifica se o usuário tem uma role específica
     * Retorna true se o usuário tem a role ADMIN
     */
    @GetMapping("/api/v1/users/{userId}/has-role")
    ApiResponse<Boolean> hasRole(@PathVariable Long userId, @RequestParam String role);
}

