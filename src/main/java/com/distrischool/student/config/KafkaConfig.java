package com.distrischool.student.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Configuração dos tópicos Kafka para o Student Management Service
 */
@Configuration
public class KafkaConfig {

    @Value("${microservice.kafka.topics.student-created:student.created}")
    private String studentCreatedTopic;

    @Value("${microservice.kafka.topics.student-updated:student.updated}")
    private String studentUpdatedTopic;

    @Value("${microservice.kafka.topics.student-deleted:student.deleted}")
    private String studentDeletedTopic;

    @Value("${microservice.kafka.topics.student-status-changed:student.status.changed}")
    private String studentStatusChangedTopic;

    @Bean
    public NewTopic studentCreatedTopic() {
        return TopicBuilder.name(studentCreatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic studentUpdatedTopic() {
        return TopicBuilder.name(studentUpdatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic studentDeletedTopic() {
        return TopicBuilder.name(studentDeletedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic studentStatusChangedTopic() {
        return TopicBuilder.name(studentStatusChangedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
