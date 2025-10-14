package com.distrischool.student.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidade AcademicRecord - Registro Acadêmico do aluno
 * Consolidação do desempenho acadêmico geral do aluno
 */
@Entity
@Table(name = "academic_records", indexes = {
    @Index(name = "idx_academic_student", columnList = "student_id", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"student"})
@ToString(callSuper = true, exclude = {"student"})
public class AcademicRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private Student student;

    @Column(name = "total_credits_earned")
    @Builder.Default
    private Integer totalCreditsEarned = 0;

    @Column(name = "total_credits_required")
    private Integer totalCreditsRequired;

    @Column(name = "current_gpa", precision = 4, scale = 2)
    private BigDecimal currentGPA;

    @Column(name = "cumulative_gpa", precision = 4, scale = 2)
    private BigDecimal cumulativeGPA;

    @Column(name = "total_subjects_taken")
    @Builder.Default
    private Integer totalSubjectsTaken = 0;

    @Column(name = "total_subjects_passed")
    @Builder.Default
    private Integer totalSubjectsPassed = 0;

    @Column(name = "total_subjects_failed")
    @Builder.Default
    private Integer totalSubjectsFailed = 0;

    @Column(name = "total_semesters_completed")
    @Builder.Default
    private Integer totalSemestersCompleted = 0;

    @Column(name = "overall_attendance_rate", precision = 5, scale = 2)
    private BigDecimal overallAttendanceRate;

    @Column(name = "has_academic_warnings")
    @Builder.Default
    private Boolean hasAcademicWarnings = false;

    @Column(name = "warning_count")
    @Builder.Default
    private Integer warningCount = 0;

    @Column(name = "last_warning_date")
    private LocalDate lastWarningDate;

    @Column(name = "has_disciplinary_actions")
    @Builder.Default
    private Boolean hasDisciplinaryActions = false;

    @Column(name = "disciplinary_actions_count")
    @Builder.Default
    private Integer disciplinaryActionsCount = 0;

    @Column(name = "last_disciplinary_action_date")
    private LocalDate lastDisciplinaryActionDate;

    @Column(name = "is_on_probation")
    @Builder.Default
    private Boolean isOnProbation = false;

    @Column(name = "probation_reason", columnDefinition = "TEXT")
    private String probationReason;

    @Column(name = "has_honors")
    @Builder.Default
    private Boolean hasHonors = false;

    @Column(name = "honors_list", columnDefinition = "TEXT")
    private String honorsList;

    @Column(name = "has_scholarships")
    @Builder.Default
    private Boolean hasScholarships = false;

    @Column(name = "scholarship_details", columnDefinition = "TEXT")
    private String scholarshipDetails;

    @Column(name = "expected_graduation_date")
    private LocalDate expectedGraduationDate;

    @Column(name = "actual_graduation_date")
    private LocalDate actualGraduationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "academic_standing", length = 20)
    @Builder.Default
    private AcademicStanding academicStanding = AcademicStanding.GOOD_STANDING;

    @Column(name = "extracurricular_activities", columnDefinition = "TEXT")
    private String extracurricularActivities;

    @Column(name = "achievements", columnDefinition = "TEXT")
    private String achievements;

    @Column(name = "research_projects", columnDefinition = "TEXT")
    private String researchProjects;

    @Column(name = "publications", columnDefinition = "TEXT")
    private String publications;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum AcademicStanding {
        EXCELLENT,          // Excelente
        GOOD_STANDING,      // Bom
        SATISFACTORY,       // Satisfatório
        PROBATION,          // Probatório
        WARNING,            // Advertência
        SUSPENSION          // Suspensão
    }

    /**
     * Calcula a porcentagem de conclusão do curso
     */
    public BigDecimal getCompletionPercentage() {
        if (totalCreditsRequired == null || totalCreditsRequired == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(totalCreditsEarned != null ? totalCreditsEarned : 0)
                .divide(BigDecimal.valueOf(totalCreditsRequired), 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Calcula a taxa de aprovação geral
     */
    public BigDecimal getOverallPassRate() {
        if (totalSubjectsTaken == null || totalSubjectsTaken == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(totalSubjectsPassed != null ? totalSubjectsPassed : 0)
                .divide(BigDecimal.valueOf(totalSubjectsTaken), 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    /**
     * Verifica se está em boa situação acadêmica
     */
    public boolean isInGoodStanding() {
        return academicStanding == AcademicStanding.EXCELLENT || 
               academicStanding == AcademicStanding.GOOD_STANDING;
    }

    /**
     * Verifica se está em situação de risco
     */
    public boolean isAtRisk() {
        return academicStanding == AcademicStanding.PROBATION || 
               academicStanding == AcademicStanding.WARNING ||
               (isOnProbation != null && isOnProbation);
    }

    /**
     * Verifica se já se formou
     */
    public boolean hasGraduated() {
        return actualGraduationDate != null;
    }
}

