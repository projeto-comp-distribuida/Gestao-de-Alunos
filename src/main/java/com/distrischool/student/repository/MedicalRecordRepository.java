package com.distrischool.student.repository;

import com.distrischool.student.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para MedicalRecord
 */
@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    /**
     * Busca ficha médica por ID do estudante
     */
    Optional<MedicalRecord> findByStudentId(Long studentId);

    /**
     * Busca estudantes com alergias
     */
    List<MedicalRecord> findByHasAllergiesTrue();

    /**
     * Busca estudantes com doenças crônicas
     */
    List<MedicalRecord> findByHasChronicDiseasesTrue();

    /**
     * Busca estudantes com deficiências
     */
    List<MedicalRecord> findByHasDisabilitiesTrue();

    /**
     * Busca estudantes que usam medicação contínua
     */
    List<MedicalRecord> findByUsesContinuousMedicationTrue();

    /**
     * Busca estudantes com restrições alimentares
     */
    List<MedicalRecord> findByHasDietaryRestrictionsTrue();

    /**
     * Busca estudantes com necessidades especiais
     */
    List<MedicalRecord> findByHasSpecialNeedsTrue();

    /**
     * Busca estudantes que requerem cuidados especiais
     */
    List<MedicalRecord> findByRequiresSpecialCareTrue();

    /**
     * Busca estudantes por tipo sanguíneo
     */
    List<MedicalRecord> findByBloodType(MedicalRecord.BloodType bloodType);

    /**
     * Busca estudantes com carteira de vacinação em dia
     */
    List<MedicalRecord> findByVaccinationCardUpToDateTrue();

    /**
     * Busca estudantes com carteira de vacinação desatualizada
     */
    List<MedicalRecord> findByVaccinationCardUpToDateFalse();

    /**
     * Busca estudantes com qualquer condição médica relevante
     */
    @Query("SELECT m FROM MedicalRecord m WHERE " +
           "m.hasAllergies = true OR " +
           "m.hasChronicDiseases = true OR " +
           "m.hasDisabilities = true OR " +
           "m.usesContinuousMedication = true OR " +
           "m.requiresSpecialCare = true")
    List<MedicalRecord> findStudentsWithMedicalConditions();

    /**
     * Verifica se estudante tem ficha médica
     */
    boolean existsByStudentId(Long studentId);
}

