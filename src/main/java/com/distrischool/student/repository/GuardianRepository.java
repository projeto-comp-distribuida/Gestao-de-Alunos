package com.distrischool.student.repository;

import com.distrischool.student.entity.Guardian;
import com.distrischool.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para Guardian
 */
@Repository
public interface GuardianRepository extends JpaRepository<Guardian, Long> {

    /**
     * Busca responsáveis por CPF
     */
    Optional<Guardian> findByCpf(String cpf);

    /**
     * Busca responsáveis por estudante
     */
    List<Guardian> findByStudent(Student student);

    /**
     * Busca responsáveis por ID do estudante
     */
    List<Guardian> findByStudentId(Long studentId);

    /**
     * Busca responsável principal do estudante
     */
    Optional<Guardian> findByStudentIdAndIsPrimaryGuardianTrue(Long studentId);

    /**
     * Busca responsável financeiro do estudante
     */
    Optional<Guardian> findByStudentIdAndIsFinancialResponsibleTrue(Long studentId);

    /**
     * Busca responsáveis por relacionamento
     */
    List<Guardian> findByRelationship(Guardian.Relationship relationship);

    /**
     * Busca responsáveis autorizados a buscar o aluno
     */
    List<Guardian> findByStudentIdAndCanPickUpStudentTrue(Long studentId);

    /**
     * Busca responsáveis por email
     */
    Optional<Guardian> findByEmail(String email);

    /**
     * Busca responsáveis por nome
     */
    @Query("SELECT g FROM Guardian g WHERE LOWER(g.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Guardian> searchByName(@Param("name") String name);

    /**
     * Busca responsáveis que podem autorizar tratamento médico
     */
    List<Guardian> findByStudentIdAndCanAuthorizeMedicalTreatmentTrue(Long studentId);

    /**
     * Verifica se CPF já está cadastrado
     */
    boolean existsByCpf(String cpf);

    /**
     * Verifica se email já está cadastrado
     */
    boolean existsByEmail(String email);
}

