package com.distrischool.student.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Entidade MedicalRecord - Ficha Médica do aluno
 * Informações médicas importantes: alergias, medicamentos, tipo sanguíneo, etc
 */
@Entity
@Table(name = "medical_records", indexes = {
    @Index(name = "idx_medical_student", columnList = "student_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"student"})
@ToString(callSuper = true, exclude = {"student"})
public class MedicalRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type", length = 5)
    private BloodType bloodType;

    @Column(name = "has_allergies")
    @Builder.Default
    private Boolean hasAllergies = false;

    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "has_chronic_diseases")
    @Builder.Default
    private Boolean hasChronicDiseases = false;

    @Column(name = "chronic_diseases", columnDefinition = "TEXT")
    private String chronicDiseases;

    @Column(name = "has_disabilities")
    @Builder.Default
    private Boolean hasDisabilities = false;

    @Column(name = "disabilities", columnDefinition = "TEXT")
    private String disabilities;

    @Column(name = "uses_continuous_medication")
    @Builder.Default
    private Boolean usesContinuousMedication = false;

    @Column(name = "medications", columnDefinition = "TEXT")
    private String medications;

    @Column(name = "has_dietary_restrictions")
    @Builder.Default
    private Boolean hasDietaryRestrictions = false;

    @Column(name = "dietary_restrictions", columnDefinition = "TEXT")
    private String dietaryRestrictions;

    @Column(name = "has_special_needs")
    @Builder.Default
    private Boolean hasSpecialNeeds = false;

    @Column(name = "special_needs", columnDefinition = "TEXT")
    private String specialNeeds;

    @Size(max = 255, message = "Nome do médico deve ter no máximo 255 caracteres")
    @Column(name = "primary_doctor_name", length = 255)
    private String primaryDoctorName;

    @Pattern(regexp = "\\d{10,11}", message = "Telefone do médico deve conter 10 ou 11 dígitos")
    @Column(name = "primary_doctor_phone", length = 11)
    private String primaryDoctorPhone;

    @Size(max = 255, message = "Nome da clínica/hospital deve ter no máximo 255 caracteres")
    @Column(name = "preferred_hospital", length = 255)
    private String preferredHospital;

    @Size(max = 100, message = "Número do plano de saúde deve ter no máximo 100 caracteres")
    @Column(name = "health_insurance_number", length = 100)
    private String healthInsuranceNumber;

    @Size(max = 100, message = "Nome do plano de saúde deve ter no máximo 100 caracteres")
    @Column(name = "health_insurance_provider", length = 100)
    private String healthInsuranceProvider;

    @Column(name = "vaccination_card_up_to_date")
    @Builder.Default
    private Boolean vaccinationCardUpToDate = false;

    @Column(name = "requires_special_care")
    @Builder.Default
    private Boolean requiresSpecialCare = false;

    @Column(name = "special_care_instructions", columnDefinition = "TEXT")
    private String specialCareInstructions;

    @Column(name = "emergency_procedures", columnDefinition = "TEXT")
    private String emergencyProcedures;

    @Column(name = "additional_notes", columnDefinition = "TEXT")
    private String additionalNotes;

    public enum BloodType {
        A_POSITIVE("A+"),
        A_NEGATIVE("A-"),
        B_POSITIVE("B+"),
        B_NEGATIVE("B-"),
        AB_POSITIVE("AB+"),
        AB_NEGATIVE("AB-"),
        O_POSITIVE("O+"),
        O_NEGATIVE("O-");

        private final String displayName;

        BloodType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Verifica se há alguma condição médica relevante
     */
    public boolean hasMedicalConditions() {
        return (hasAllergies != null && hasAllergies) ||
               (hasChronicDiseases != null && hasChronicDiseases) ||
               (hasDisabilities != null && hasDisabilities) ||
               (usesContinuousMedication != null && usesContinuousMedication);
    }

    /**
     * Verifica se requer cuidados especiais
     */
    public boolean needsSpecialCare() {
        return requiresSpecialCare != null && requiresSpecialCare;
    }
}

