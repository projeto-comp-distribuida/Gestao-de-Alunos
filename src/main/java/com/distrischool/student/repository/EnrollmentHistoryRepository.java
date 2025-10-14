package com.distrischool.student.repository;

import com.distrischool.student.entity.EnrollmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para EnrollmentHistory
 */
@Repository
public interface EnrollmentHistoryRepository extends JpaRepository<EnrollmentHistory, Long> {

    /**
     * Busca histórico de matrículas por ID do estudante
     */
    List<EnrollmentHistory> findByStudentId(Long studentId);

    /**
     * Busca histórico de matrículas por ID do estudante ordenado por ano e semestre
     */
    List<EnrollmentHistory> findByStudentIdOrderByAcademicYearDescSemesterDesc(Long studentId);

    /**
     * Busca matrículas por ano acadêmico
     */
    List<EnrollmentHistory> findByAcademicYear(Integer academicYear);

    /**
     * Busca matrículas por ano e semestre
     */
    List<EnrollmentHistory> findByAcademicYearAndSemester(Integer academicYear, Integer semester);

    /**
     * Busca matrículas por status
     */
    List<EnrollmentHistory> findByEnrollmentStatus(EnrollmentHistory.EnrollmentStatus status);

    /**
     * Busca matrícula atual do estudante (ativa)
     */
    Optional<EnrollmentHistory> findByStudentIdAndEnrollmentStatus(Long studentId, EnrollmentHistory.EnrollmentStatus status);

    /**
     * Busca matrículas por curso
     */
    List<EnrollmentHistory> findByCourseName(String courseName);

    /**
     * Busca matrículas por turno
     */
    List<EnrollmentHistory> findByShift(EnrollmentHistory.Shift shift);

    /**
     * Busca estudantes repetindo o ano
     */
    List<EnrollmentHistory> findByIsRepeatingYearTrue();

    /**
     * Busca histórico do estudante por curso
     */
    List<EnrollmentHistory> findByStudentIdAndCourseName(Long studentId, String courseName);

    /**
     * Busca última matrícula do estudante
     */
    @Query("SELECT e FROM EnrollmentHistory e WHERE e.student.id = :studentId ORDER BY e.academicYear DESC, e.semester DESC LIMIT 1")
    Optional<EnrollmentHistory> findLatestByStudentId(@Param("studentId") Long studentId);

    /**
     * Conta total de semestres do estudante
     */
    long countByStudentId(Long studentId);

    /**
     * Conta semestres concluídos do estudante
     */
    long countByStudentIdAndEnrollmentStatus(Long studentId, EnrollmentHistory.EnrollmentStatus status);

    /**
     * Verifica se estudante tem matrícula ativa
     */
    boolean existsByStudentIdAndEnrollmentStatus(Long studentId, EnrollmentHistory.EnrollmentStatus status);
}

