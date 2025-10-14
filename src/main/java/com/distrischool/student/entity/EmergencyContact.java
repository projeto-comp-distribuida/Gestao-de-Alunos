package com.distrischool.student.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Entidade EmergencyContact - Contato de Emergência
 * Múltiplos contatos de emergência por aluno
 */
@Entity
@Table(name = "emergency_contacts", indexes = {
    @Index(name = "idx_emergency_student", columnList = "student_id"),
    @Index(name = "idx_emergency_priority", columnList = "priority_order")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"student"})
@ToString(callSuper = true, exclude = {"student"})
public class EmergencyContact extends BaseEntity {

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

    @NotBlank(message = "Telefone principal é obrigatório")
    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve conter 10 ou 11 dígitos")
    @Column(name = "phone_primary", nullable = false, length = 11)
    private String phonePrimary;

    @Pattern(regexp = "\\d{10,11}", message = "Telefone secundário deve conter 10 ou 11 dígitos")
    @Column(name = "phone_secondary", length = 11)
    private String phoneSecondary;

    @Email(message = "Email deve ser válido")
    @Column(name = "email", length = 255)
    private String email;

    @NotBlank(message = "Relacionamento é obrigatório")
    @Size(max = 50, message = "Relacionamento deve ter no máximo 50 caracteres")
    @Column(name = "relationship", nullable = false, length = 50)
    private String relationship;

    @NotNull(message = "Ordem de prioridade é obrigatória")
    @Min(value = 1, message = "Ordem de prioridade deve ser no mínimo 1")
    @Column(name = "priority_order", nullable = false)
    private Integer priorityOrder;

    @Column(name = "is_authorized_to_pick_up")
    @Builder.Default
    private Boolean isAuthorizedToPickUp = false;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * Verifica se está autorizado a buscar o aluno
     */
    public boolean canPickUpStudent() {
        return isAuthorizedToPickUp != null && isAuthorizedToPickUp;
    }
}

