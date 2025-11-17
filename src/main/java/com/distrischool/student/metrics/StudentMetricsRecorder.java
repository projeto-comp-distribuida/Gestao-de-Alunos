package com.distrischool.student.metrics;

import com.distrischool.student.entity.Student.StudentStatus;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/**
 * Helper centralizado para registrar métricas de domínio do serviço de alunos.
 */
@Component
public class StudentMetricsRecorder {

    private static final String METRIC_STUDENT_OPERATIONS = "student_operations_total";
    private static final String METRIC_STUDENT_STATUS_CHANGES = "student_status_changes_total";
    private static final String METRIC_STUDENT_EVENTS = "student_events_total";

    private final MeterRegistry meterRegistry;

    public StudentMetricsRecorder(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void recordOperation(String operation, String outcome) {
        meterRegistry.counter(
            METRIC_STUDENT_OPERATIONS,
            "operation", operation,
            "outcome", outcome
        ).increment();
    }

    public void recordStatusChange(StudentStatus status) {
        meterRegistry.counter(
            METRIC_STUDENT_STATUS_CHANGES,
            "status", status.name().toLowerCase()
        ).increment();
    }

    public void recordKafkaEvent(String direction, String eventType, String outcome) {
        meterRegistry.counter(
            METRIC_STUDENT_EVENTS,
            "direction", direction,
            "event_type", eventType,
            "outcome", outcome
        ).increment();
    }
}


