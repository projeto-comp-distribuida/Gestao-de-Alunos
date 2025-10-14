package com.distrischool.student.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade EnrollmentHistory - Histórico de Matrícula do aluno
 * Rastreia todas as matrículas, transferências e mudanças de curso
 */
@Entity
@Table(name = "enrollment_history", indexes = {
    @Index(name = "idx_enrollment_student", columnList = "student_id"),
    @Index(name = "idx_enrollment_period", columnList = "academic_year, semester"),
    @Index(name = "idx_enrollment_status", columnList = "enrollment_status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"student"})
@ToString(callSuper = true, exclude = {"student"})
public class EnrollmentHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull(message = "Ano acadêmico é obrigatório")
    @Min(value = 2000, message = "Ano acadêmico deve ser maior que 2000")
    @Max(value = 2100, message = "Ano acadêmico deve ser menor que 2100")
    @Column(name = "academic_year", nullable = false)
    private Integer academicYear;

    @NotNull(message = "Semestre é obrigatório")
    @Min(value = 1, message = "Semestre deve ser no mínimo 1")
    @Max(value = 2, message = "Semestre deve ser no máximo 2")
    @Column(name = "semester", nullable = false)
    private Integer semester;

    @NotBlank(message = "Curso é obrigatório")
    @Size(max = 255, message = "Nome do curso deve ter no máximo 255 caracteres")
    @Column(name = "course_name", nullable = false, length = 255)
    private String courseName;

    @Size(max = 50, message = "Código do curso deve ter no máximo 50 caracteres")
    @Column(name = "course_code", length = 50)
    private String courseCode;

    @Size(max = 100, message = "Nome da turma deve ter no máximo 100 caracteres")
    @Column(name = "class_name", length = 100)
    private String className;

    @Size(max = 50, message = "Código da turma deve ter no máximo 50 caracteres")
    @Column(name = "class_code", length = 50)
    private String classCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "shift", length = 20)
    private Shift shift;

    @NotNull(message = "Data de início é obrigatória")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @NotNull(message = "Status da matrícula é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "enrollment_status", nullable = false, length = 20)
    @Builder.Default
    private EnrollmentStatus enrollmentStatus = EnrollmentStatus.ACTIVE;

    @Column(name = "total_subjects")
    private Integer totalSubjects;

    @Column(name = "passed_subjects")
    private Integer passedSubjects;

    @Column(name = "failed_subjects")
    private Integer failedSubjects;

    @Column(name = "average_grade", precision = 5, scale = 2)
    private BigDecimal averageGrade;

    @Column(name = "attendance_percentage", precision = 5, scale = 2)
    private BigDecimal attendancePercentage;

    @Column(name = "credits_earned")
    private Integer creditsEarned;

    @Column(name = "total_credits")
    private Integer totalCredits;

    @Column(name = "is_repeating_year")
    @Builder.Default
    private Boolean isRepeatingYear = false;

    @Column(name = "transfer_reason", columnDefinition = "TEXT")
    private String transferReason;

    @Column(name = "dropout_reason", columnDefinition = "TEXT")
    private String dropoutReason;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum Shift {
        MORNING,     // Manhã
        AFTERNOON,   // Tarde
        EVENING,     // Noite
        FULL_TIME    // Integral
    }

    public enum EnrollmentStatus {
        ACTIVE,         // Ativo
        COMPLETED,      // Concluído
        SUSPENDED,      // Suspenso
        CANCELLED,      // Cancelado
        TRANSFERRED,    // Transferido
        DROPPED         // Abandonado
    }

    /**
     * Verifica se a matrícula está ativa
     */
    public boolean isActive() {
        return enrollmentStatus == EnrollmentStatus.ACTIVE;
    }

    /**
     * Calcula a taxa de aprovação
     */
    public BigDecimal getPassRate() {
        if (totalSubjects == null || totalSubjects == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(passedSubjects != null ? passedSubjects : 0)
                .divide(BigDecimal.valueOf(totalSubjects), 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Verifica se foi aprovado no período
     */
    public boolean isPassed() {
        return enrollmentStatus == EnrollmentStatus.COMPLETED && 
               averageGrade != null && averageGrade.compareTo(BigDecimal.valueOf(6.0)) >= 0;
    }
}

