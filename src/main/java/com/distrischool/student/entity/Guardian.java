package com.distrischool.student.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entidade Guardian - Responsável/Tutor do aluno
 * Um aluno pode ter múltiplos responsáveis (pai, mãe, tutor legal, etc)
 */
@Entity
@Table(name = "guardians", indexes = {
    @Index(name = "idx_guardian_cpf", columnList = "cpf", unique = true),
    @Index(name = "idx_guardian_email", columnList = "email"),
    @Index(name = "idx_guardian_student", columnList = "student_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"student"})
@ToString(callSuper = true, exclude = {"student"})
public class Guardian extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    @Column(name = "cpf", nullable = false, unique = true, length = 11)
    private String cpf;

    @Size(max = 20, message = "RG deve ter no máximo 20 caracteres")
    @Column(name = "rg", length = 20)
    private String rg;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @NotBlank(message = "Telefone principal é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve conter 10 ou 11 dígitos")
    @Column(name = "phone_primary", nullable = false, length = 11)
    private String phonePrimary;

    @Pattern(regexp = "\\d{10,11}", message = "Telefone secundário deve conter 10 ou 11 dígitos")
    @Column(name = "phone_secondary", length = 11)
    private String phoneSecondary;

    @Past(message = "Data de nascimento deve ser no passado")
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @NotNull(message = "Relacionamento é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "relationship", nullable = false, length = 20)
    private Relationship relationship;

    @Size(max = 100, message = "Profissão deve ter no máximo 100 caracteres")
    @Column(name = "occupation", length = 100)
    private String occupation;

    @Size(max = 100, message = "Local de trabalho deve ter no máximo 100 caracteres")
    @Column(name = "workplace", length = 100)
    private String workplace;

    @Pattern(regexp = "\\d{10,11}", message = "Telefone de trabalho deve conter 10 ou 11 dígitos")
    @Column(name = "work_phone", length = 11)
    private String workPhone;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "is_primary_guardian")
    @Builder.Default
    private Boolean isPrimaryGuardian = false;

    @Column(name = "is_financial_responsible")
    @Builder.Default
    private Boolean isFinancialResponsible = false;

    @Column(name = "can_pick_up_student")
    @Builder.Default
    private Boolean canPickUpStudent = true;

    @Column(name = "can_authorize_medical_treatment")
    @Builder.Default
    private Boolean canAuthorizeMedicalTreatment = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum Relationship {
        FATHER,          // Pai
        MOTHER,          // Mãe
        LEGAL_GUARDIAN,  // Tutor Legal
        GRANDFATHER,     // Avô
        GRANDMOTHER,     // Avó
        UNCLE,           // Tio
        AUNT,            // Tia
        BROTHER,         // Irmão
        SISTER,          // Irmã
        OTHER            // Outro
    }

    /**
     * Verifica se o responsável pode buscar o aluno
     */
    public boolean canPickUp() {
        return canPickUpStudent != null && canPickUpStudent;
    }

    /**
     * Verifica se é o responsável financeiro
     */
    public boolean isFinanciallyResponsible() {
        return isFinancialResponsible != null && isFinancialResponsible;
    }

    /**
     * Verifica se é o responsável principal
     */
    public boolean isPrimary() {
        return isPrimaryGuardian != null && isPrimaryGuardian;
    }
}

