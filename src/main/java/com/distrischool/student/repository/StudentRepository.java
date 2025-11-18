package com.distrischool.student.repository;

import com.distrischool.student.entity.Student;
import com.distrischool.student.entity.Student.StudentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByRegistrationNumber(String registrationNumber);
    Optional<Student> findByCpf(String cpf);
    Optional<Student> findByEmail(String email);

    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    boolean existsByRegistrationNumber(String registrationNumber);

    Page<Student> findByStatus(StudentStatus status, Pageable pageable);
    Page<Student> findByCourse(String course, Pageable pageable);
    Page<Student> findByCourseAndSemester(String course, Integer semester, Pageable pageable);
    Page<Student> findByFullNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT s FROM Student s WHERE s.deletedAt IS NULL AND s.status = :status")
    Page<Student> findActiveStudents(@Param("status") StudentStatus status, Pageable pageable);

    List<Student> findByEnrollmentDateBetween(LocalDate startDate, LocalDate endDate);

    long countByCourse(String course);
    long countByStatus(StudentStatus status);

    @Query("SELECT s FROM Student s WHERE s.deletedAt IS NULL")
    Page<Student> findAllNotDeleted(Pageable pageable);

    @Query(value = "SELECT s FROM Student s WHERE s.deletedAt IS NULL " +
           "AND (:name = '' OR LOWER(s.fullName) LIKE CONCAT('%', LOWER(:name), '%')) " +
           "AND (:course IS NULL OR s.course = :course) " +
           "AND (:semester IS NULL OR s.semester = :semester) " +
           "AND (:status IS NULL OR s.status = :status)",
           countQuery = "SELECT COUNT(s) FROM Student s WHERE s.deletedAt IS NULL " +
           "AND (:name = '' OR LOWER(s.fullName) LIKE CONCAT('%', LOWER(:name), '%')) " +
           "AND (:course IS NULL OR s.course = :course) " +
           "AND (:semester IS NULL OR s.semester = :semester) " +
           "AND (:status IS NULL OR s.status = :status)")
    Page<Student> findByFilters(
        @Param("name") String name,
        @Param("course") String course,
        @Param("semester") Integer semester,
        @Param("status") StudentStatus status,
        Pageable pageable
    );

    /**
     * Busca múltiplos estudantes por IDs, excluindo os deletados
     */
    @Query("SELECT s FROM Student s WHERE s.id IN :ids AND s.deletedAt IS NULL")
    List<Student> findByIdsNotDeleted(@Param("ids") List<Long> ids);
}

