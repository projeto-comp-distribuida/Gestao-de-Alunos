package com.distrischool.student.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistriSchoolEvent {
    private String eventId;
    private String eventType;
    private String source;
    private Map<String, Object> data;

    public static DistriSchoolEvent of(String eventType, String source, Map<String, Object> data) {
        return DistriSchoolEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .source(source)
                .data(data)
                .build();
    }
}
