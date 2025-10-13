package com.distrischool.student.dto;

import com.distrischool.student.entity.Student;
import com.distrischool.student.entity.Student.StudentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSummaryDTO {

    private Long id;
    private String fullName;
    private String email;
    private String registrationNumber;
    private String course;
    private Integer semester;
    private StudentStatus status;

    public static StudentSummaryDTO fromEntity(Student student) {
        return StudentSummaryDTO.builder()
                .id(student.getId())
                .fullName(student.getFullName())
                .email(student.getEmail())
                .registrationNumber(student.getRegistrationNumber())
                .course(student.getCourse())
                .semester(student.getSemester())
                .status(student.getStatus())
                .build();
    }
}
