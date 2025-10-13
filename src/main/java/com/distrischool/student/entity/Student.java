package com.distrischool.student.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entidade Student para o sistema de gestão de alunos da faculdade.
 */
@Entity
@Table(name = "students", indexes = {
    @Index(name = "idx_student_registration", columnList = "registration_number", unique = true),
    @Index(name = "idx_student_email", columnList = "email", unique = true),
    @Index(name = "idx_student_cpf", columnList = "cpf", unique = true),
    @Index(name = "idx_student_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Student extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    @Column(name = "cpf", nullable = false, unique = true, length = 11)
    private String cpf;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve conter 10 ou 11 dígitos")
    @Column(name = "phone", length = 11)
    private String phone;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @NotBlank(message = "Número de matrícula é obrigatório")
    @Column(name = "registration_number", nullable = false, unique = true, length = 50)
    private String registrationNumber;

    @NotBlank(message = "Curso é obrigatório")
    @Size(max = 255, message = "Nome do curso deve ter no máximo 255 caracteres")
    @Column(name = "course", nullable = false, length = 255)
    private String course;

    @NotNull(message = "Semestre é obrigatório")
    @Min(value = 1, message = "Semestre deve ser no mínimo 1")
    @Max(value = 20, message = "Semestre deve ser no máximo 20")
    @Column(name = "semester", nullable = false)
    private Integer semester;

    @NotNull(message = "Data de ingresso é obrigatória")
    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    @NotNull(message = "Status é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private StudentStatus status = StudentStatus.ACTIVE;

    @Column(name = "address_street", length = 255)
    private String addressStreet;

    @Column(name = "address_number", length = 20)
    private String addressNumber;

    @Column(name = "address_complement", length = 100)
    private String addressComplement;

    @Column(name = "address_neighborhood", length = 100)
    private String addressNeighborhood;

    @Column(name = "address_city", length = 100)
    private String addressCity;

    @Column(name = "address_state", length = 2)
    private String addressState;

    @Pattern(regexp = "\\d{8}", message = "CEP deve conter 8 dígitos")
    @Column(name = "address_zipcode", length = 8)
    private String addressZipcode;

    @Column(name = "emergency_contact_name", length = 255)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 11)
    private String emergencyContactPhone;

    @Column(name = "emergency_contact_relationship", length = 50)
    private String emergencyContactRelationship;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum StudentStatus {
        ACTIVE, INACTIVE, GRADUATED, SUSPENDED, TRANSFERRED, DROPPED
    }

    public boolean isActive() {
        return status == StudentStatus.ACTIVE;
    }

    public int getAge() {
        return LocalDate.now().getYear() - birthDate.getYear();
    }
}

