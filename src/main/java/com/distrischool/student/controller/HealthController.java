package com.distrischool.student.controller;

import com.distrischool.student.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller base para demonstrar a estrutura do DistriSchool.
 * Este controller serve como exemplo de como implementar endpoints REST
 * seguindo os padrões do sistema de gestão escolar.
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @Value("${spring.application.name}")
    private String appName;

    @GetMapping
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", appName);
        return ApiResponse.success(status);
    }

    @GetMapping("/info")
    public ApiResponse<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", appName);
        info.put("description", "DistriSchool Student Service");
        return ApiResponse.success(info);
    }
}
