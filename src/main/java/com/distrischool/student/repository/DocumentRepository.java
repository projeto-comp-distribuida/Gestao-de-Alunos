package com.distrischool.student.repository;

import com.distrischool.student.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para Document
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    /**
     * Busca documentos por ID do estudante
     */
    List<Document> findByStudentId(Long studentId);

    /**
     * Busca documentos por tipo
     */
    List<Document> findByDocumentType(Document.DocumentType documentType);

    /**
     * Busca documentos por ID do estudante e tipo
     */
    List<Document> findByStudentIdAndDocumentType(Long studentId, Document.DocumentType documentType);

    /**
     * Busca documento por número
     */
    Optional<Document> findByDocumentNumber(String documentNumber);

    /**
     * Busca documentos verificados
     */
    List<Document> findByIsVerifiedTrue();

    /**
     * Busca documentos não verificados
     */
    List<Document> findByIsVerifiedFalse();

    /**
     * Busca documentos do estudante não verificados
     */
    List<Document> findByStudentIdAndIsVerifiedFalse(Long studentId);

    /**
     * Busca documentos expirados
     */
    @Query("SELECT d FROM Document d WHERE d.expirationDate IS NOT NULL AND d.expirationDate < :currentDate")
    List<Document> findExpiredDocuments(@Param("currentDate") LocalDate currentDate);

    /**
     * Busca documentos do estudante expirados
     */
    @Query("SELECT d FROM Document d WHERE d.student.id = :studentId AND d.expirationDate IS NOT NULL AND d.expirationDate < :currentDate")
    List<Document> findExpiredDocumentsByStudentId(@Param("studentId") Long studentId, @Param("currentDate") LocalDate currentDate);

    /**
     * Busca documentos que vão expirar em breve
     */
    @Query("SELECT d FROM Document d WHERE d.expirationDate IS NOT NULL AND d.expirationDate BETWEEN :currentDate AND :futureDate")
    List<Document> findDocumentsExpiringBetween(@Param("currentDate") LocalDate currentDate, @Param("futureDate") LocalDate futureDate);

    /**
     * Verifica se documento já existe
     */
    boolean existsByDocumentNumber(String documentNumber);

    /**
     * Verifica se estudante tem documento do tipo especificado
     */
    boolean existsByStudentIdAndDocumentType(Long studentId, Document.DocumentType documentType);

    /**
     * Conta documentos do estudante
     */
    long countByStudentId(Long studentId);

    /**
     * Conta documentos verificados do estudante
     */
    long countByStudentIdAndIsVerifiedTrue(Long studentId);
}

