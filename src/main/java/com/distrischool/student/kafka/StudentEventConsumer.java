package com.distrischool.student.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer Kafka para processar eventos de alunos
 * Este é um exemplo de como outros serviços podem consumir eventos de alunos
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StudentEventConsumer {

    /**
     * Exemplo de consumer que escuta eventos de aluno criado
     * Outros serviços podem usar este padrão para reagir a eventos
     */
    @KafkaListener(
        topics = "${microservice.kafka.topics.student-created}",
        groupId = "${spring.application.name}-consumer-group"
    )
    public void handleStudentCreated(DistriSchoolEvent event) {
        log.info("Evento recebido - student.created: Tipo={}, Source={}",
                 event.getEventType(),
                 event.getSource());

        // Extrair dados do evento
        var data = event.getData();
        log.info("  Student ID: {}", data.get("studentId"));
        log.info("  Nome: {}", data.get("fullName"));
        log.info("  Matrícula: {}", data.get("registrationNumber"));

        // Aqui você pode adicionar lógica para processar o evento
        // Por exemplo: enviar email de boas-vindas, criar usuário no sistema, etc.
    }

    /**
     * Exemplo de consumer que escuta mudanças de status
     */
    @KafkaListener(
        topics = "${microservice.kafka.topics.student-status-changed}",
        groupId = "${spring.application.name}-consumer-group"
    )
    public void handleStudentStatusChanged(DistriSchoolEvent event) {
        log.info("Evento recebido - student.status.changed: Tipo={}, Source={}",
                 event.getEventType(),
                 event.getSource());

        var data = event.getData();
        log.info("  Student ID: {}", data.get("studentId"));
        log.info("  Status: {} -> {}", data.get("oldStatus"), data.get("newStatus"));

        // Aqui você pode adicionar lógica para processar mudanças de status
        // Por exemplo: notificar o aluno, atualizar sistemas externos, etc.
    }

    /**
     * Exemplo de consumer que escuta atualizações de aluno
     */
    @KafkaListener(
        topics = "${microservice.kafka.topics.student-updated}",
        groupId = "${spring.application.name}-consumer-group"
    )
    public void handleStudentUpdated(DistriSchoolEvent event) {
        log.info("Evento recebido - student.updated: Tipo={}, Source={}",
                 event.getEventType(),
                 event.getSource());

        var data = event.getData();
        log.info("  Student ID: {}", data.get("studentId"));
        log.info("  Email: {}", data.get("email"));
    }

    /**
     * Exemplo de consumer que escuta exclusão de aluno
     */
    @KafkaListener(
        topics = "${microservice.kafka.topics.student-deleted}",
        groupId = "${spring.application.name}-consumer-group"
    )
    public void handleStudentDeleted(DistriSchoolEvent event) {
        log.info("Evento recebido - student.deleted: Tipo={}, Source={}",
                 event.getEventType(),
                 event.getSource());

        var data = event.getData();
        log.info("  Student ID: {}", data.get("studentId"));
        log.info("  Nome: {}", data.get("fullName"));

        // Aqui você pode adicionar lógica para processar a exclusão
        // Por exemplo: arquivar dados, notificar sistemas externos, etc.
    }
}
