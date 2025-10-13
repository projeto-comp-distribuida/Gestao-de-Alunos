package com.distrischool.student;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Aplicação principal do Student Management Service (Gestão de Alunos para Faculdade).
 * Sistema completo de gerenciamento de alunos com Hibernate, Kafka, Redis e PostgreSQL.
 */
@SpringBootApplication
@EnableFeignClients
@EnableKafka
@EnableCaching
public class StudentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentServiceApplication.class, args);
    }
}
