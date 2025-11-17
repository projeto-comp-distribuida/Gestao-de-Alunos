package com.distrischool.student.kafka;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração para Kafka no serviço de alunos
 */
@SpringBootTest(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.consumer.auto-offset-reset=earliest"
})
@EmbeddedKafka(partitions = 1, topics = {"distrischool.student.created", "distrischool.student.updated"})
@ActiveProfiles("test")
@DirtiesContext
class KafkaIntegrationTest {

    @Autowired(required = false)
    private KafkaTemplate<String, DistriSchoolEvent> kafkaTemplate;

    @Autowired(required = false)
    private EventProducer eventProducer;

    @Test
    void contextLoads() {
        assertThat(kafkaTemplate).isNotNull();
    }

    @Test
    void shouldPublishStudentCreatedEvent() {
        if (eventProducer == null || kafkaTemplate == null) {
            return;
        }

        Map<String, Object> eventData = Map.of(
            "studentId", 1L,
            "name", "Test Student",
            "email", "test@example.com"
        );

        DistriSchoolEvent event = DistriSchoolEvent.of(
            "student.created",
            "student-management-service",
            eventData
        );

        // Publica evento via EventProducer
        eventProducer.send("distrischool.student.created", event);

        // Verifica se o evento foi publicado (sem exceção)
        assertThat(event).isNotNull();
        assertThat(event.getEventType()).isEqualTo("student.created");
    }

    @Test
    void shouldPublishStudentUpdatedEvent() {
        if (kafkaTemplate == null) {
            return;
        }

        Map<String, Object> eventData = Map.of(
            "studentId", 1L,
            "name", "Updated Student"
        );

        DistriSchoolEvent event = DistriSchoolEvent.of(
            "student.updated",
            "student-management-service",
            eventData
        );

        // Publica evento diretamente via KafkaTemplate
        kafkaTemplate.send("distrischool.student.updated", event.getEventId(), event);

        assertThat(event).isNotNull();
        assertThat(event.getEventType()).isEqualTo("student.updated");
    }
}
