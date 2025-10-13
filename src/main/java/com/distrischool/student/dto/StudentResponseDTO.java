package com.distrischool.student.dto;

import com.distrischool.student.entity.Student;
import com.distrischool.student.entity.Student.StudentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDTO {

    private Long id;
    private String fullName;
    private String cpf;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private Integer age;
    private String registrationNumber;
    private String course;
    private Integer semester;
    private LocalDate enrollmentDate;
    private StudentStatus status;

    private String addressStreet;
    private String addressNumber;
    private String addressComplement;
    private String addressNeighborhood;
    private String addressCity;
    private String addressState;
    private String addressZipcode;

    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;

    private String notes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    public static StudentResponseDTO fromEntity(Student student) {
        return StudentResponseDTO.builder()
                .id(student.getId())
                .fullName(student.getFullName())
                .cpf(student.getCpf())
                .email(student.getEmail())
                .phone(student.getPhone())
                .birthDate(student.getBirthDate())
                .age(student.getAge())
                .registrationNumber(student.getRegistrationNumber())
                .course(student.getCourse())
                .semester(student.getSemester())
                .enrollmentDate(student.getEnrollmentDate())
                .status(student.getStatus())
                .addressStreet(student.getAddressStreet())
                .addressNumber(student.getAddressNumber())
                .addressComplement(student.getAddressComplement())
                .addressNeighborhood(student.getAddressNeighborhood())
                .addressCity(student.getAddressCity())
                .addressState(student.getAddressState())
                .addressZipcode(student.getAddressZipcode())
                .emergencyContactName(student.getEmergencyContactName())
                .emergencyContactPhone(student.getEmergencyContactPhone())
                .emergencyContactRelationship(student.getEmergencyContactRelationship())
                .notes(student.getNotes())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .createdBy(student.getCreatedBy())
                .updatedBy(student.getUpdatedBy())
                .build();
    }
}
