package com.distrischool.student.repository;

import com.distrischool.student.entity.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para EmergencyContact
 */
@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {

    /**
     * Busca contatos de emergência por ID do estudante
     */
    List<EmergencyContact> findByStudentId(Long studentId);

    /**
     * Busca contatos de emergência por ID do estudante ordenados por prioridade
     */
    List<EmergencyContact> findByStudentIdOrderByPriorityOrderAsc(Long studentId);

    /**
     * Busca contatos autorizados a buscar o aluno
     */
    List<EmergencyContact> findByStudentIdAndIsAuthorizedToPickUpTrue(Long studentId);

    /**
     * Busca contatos por prioridade
     */
    List<EmergencyContact> findByPriorityOrder(Integer priorityOrder);

    /**
     * Busca contatos por telefone
     */
    List<EmergencyContact> findByPhonePrimary(String phone);

    /**
     * Busca primeiro contato de emergência (maior prioridade)
     */
    @Query("SELECT e FROM EmergencyContact e WHERE e.student.id = :studentId ORDER BY e.priorityOrder ASC LIMIT 1")
    EmergencyContact findPrimaryContactByStudentId(Long studentId);

    /**
     * Conta contatos de emergência do estudante
     */
    long countByStudentId(Long studentId);
}

