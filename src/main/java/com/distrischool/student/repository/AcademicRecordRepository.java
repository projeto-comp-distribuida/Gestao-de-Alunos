package com.distrischool.student.repository;

import com.distrischool.student.entity.AcademicRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository para AcademicRecord
 */
@Repository
public interface AcademicRecordRepository extends JpaRepository<AcademicRecord, Long> {

    /**
     * Busca registro acadêmico por ID do estudante
     */
    Optional<AcademicRecord> findByStudentId(Long studentId);

    /**
     * Busca registros por situação acadêmica
     */
    List<AcademicRecord> findByAcademicStanding(AcademicRecord.AcademicStanding standing);

    /**
     * Busca estudantes em boa situação acadêmica
     */
    @Query("SELECT a FROM AcademicRecord a WHERE a.academicStanding IN ('EXCELLENT', 'GOOD_STANDING')")
    List<AcademicRecord> findStudentsInGoodStanding();

    /**
     * Busca estudantes em risco acadêmico
     */
    @Query("SELECT a FROM AcademicRecord a WHERE a.academicStanding IN ('PROBATION', 'WARNING') OR a.isOnProbation = true")
    List<AcademicRecord> findStudentsAtRisk();

    /**
     * Busca estudantes com honras
     */
    List<AcademicRecord> findByHasHonorsTrue();

    /**
     * Busca estudantes com bolsas
     */
    List<AcademicRecord> findByHasScholarshipsTrue();

    /**
     * Busca estudantes com advertências acadêmicas
     */
    List<AcademicRecord> findByHasAcademicWarningsTrue();

    /**
     * Busca estudantes com ações disciplinares
     */
    List<AcademicRecord> findByHasDisciplinaryActionsTrue();

    /**
     * Busca estudantes em probatório
     */
    List<AcademicRecord> findByIsOnProbationTrue();

    /**
     * Busca estudantes por GPA mínimo
     */
    @Query("SELECT a FROM AcademicRecord a WHERE a.currentGPA >= :minGPA")
    List<AcademicRecord> findByGPAGreaterThanEqual(@Param("minGPA") BigDecimal minGPA);

    /**
     * Busca estudantes com GPA excelente (>= 9.0)
     */
    @Query("SELECT a FROM AcademicRecord a WHERE a.currentGPA >= 9.0")
    List<AcademicRecord> findStudentsWithExcellentGPA();

    /**
     * Busca top N estudantes por GPA
     */
    @Query("SELECT a FROM AcademicRecord a WHERE a.currentGPA IS NOT NULL ORDER BY a.currentGPA DESC LIMIT :limit")
    List<AcademicRecord> findTopStudentsByGPA(@Param("limit") int limit);

    /**
     * Busca estudantes formados
     */
    @Query("SELECT a FROM AcademicRecord a WHERE a.actualGraduationDate IS NOT NULL")
    List<AcademicRecord> findGraduatedStudents();

    /**
     * Busca estudantes próximos da formatura
     */
    @Query("SELECT a FROM AcademicRecord a WHERE " +
           "a.totalCreditsRequired IS NOT NULL AND " +
           "a.totalCreditsEarned IS NOT NULL AND " +
           "a.actualGraduationDate IS NULL AND " +
           "(CAST(a.totalCreditsEarned AS double) / CAST(a.totalCreditsRequired AS double)) >= 0.8")
    List<AcademicRecord> findStudentsNearGraduation();

    /**
     * Calcula média geral dos alunos
     */
    @Query("SELECT AVG(a.currentGPA) FROM AcademicRecord a WHERE a.currentGPA IS NOT NULL")
    Optional<BigDecimal> calculateAverageGPA();

    /**
     * Busca estatísticas de desempenho
     */
    @Query("SELECT " +
           "COUNT(a) as totalStudents, " +
           "AVG(a.currentGPA) as avgGPA, " +
           "MAX(a.currentGPA) as maxGPA, " +
           "MIN(a.currentGPA) as minGPA, " +
           "AVG(a.overallAttendanceRate) as avgAttendance " +
           "FROM AcademicRecord a WHERE a.currentGPA IS NOT NULL")
    Object[] getPerformanceStatistics();

    /**
     * Verifica se estudante tem registro acadêmico
     */
    boolean existsByStudentId(Long studentId);
}

