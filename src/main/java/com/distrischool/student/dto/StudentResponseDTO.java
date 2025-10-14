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
        // Mapeia endereço do relacionamento Address
        // TODO: Habilitar quando Lombok estiver gerando os getters corretamente
        String addrStreet = null;
        String addrNumber = null;
        String addrComplement = null;
        String addrNeighborhood = null;
        String addrCity = null;
        String addrState = null;
        String addrZipcode = null;
        
        // TEMPORÁRIO: Comentado até Lombok funcionar
        // if (student.getAddress() != null) {
        //     addrStreet = student.getAddress().getStreet();
        //     addrNumber = student.getAddress().getNumber();
        //     addrComplement = student.getAddress().getComplement();
        //     addrNeighborhood = student.getAddress().getNeighborhood();
        //     addrCity = student.getAddress().getCity();
        //     addrState = student.getAddress().getState();
        //     addrZipcode = student.getAddress().getZipcode();
        // }
        
        // Mapeia contato de emergência do relacionamento EmergencyContacts (pega o de maior prioridade)
        // TODO: Habilitar quando Lombok estiver gerando os getters corretamente
        String emergName = null;
        String emergPhone = null;
        String emergRelationship = null;
        
        // TEMPORÁRIO: Comentado até Lombok funcionar
        // if (student.getEmergencyContacts() != null && !student.getEmergencyContacts().isEmpty()) {
        //     var primaryContact = student.getEmergencyContacts().get(0); // Pega o primeiro
        //     emergName = primaryContact.getFullName();
        //     emergPhone = primaryContact.getPhone();
        //     emergRelationship = primaryContact.getRelationship();
        // }
        
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
                .addressStreet(addrStreet)
                .addressNumber(addrNumber)
                .addressComplement(addrComplement)
                .addressNeighborhood(addrNeighborhood)
                .addressCity(addrCity)
                .addressState(addrState)
                .addressZipcode(addrZipcode)
                .emergencyContactName(emergName)
                .emergencyContactPhone(emergPhone)
                .emergencyContactRelationship(emergRelationship)
                .notes(student.getNotes())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .createdBy(student.getCreatedBy())
                .updatedBy(student.getUpdatedBy())
                .build();
    }
}
