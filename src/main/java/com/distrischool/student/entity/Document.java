package com.distrischool.student.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entidade Document - Documentos do aluno
 * RG, CPF, Certidão de Nascimento, Histórico Escolar, etc
 */
@Entity
@Table(name = "documents", indexes = {
    @Index(name = "idx_document_student", columnList = "student_id"),
    @Index(name = "idx_document_type", columnList = "document_type"),
    @Index(name = "idx_document_number", columnList = "document_number")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"student"})
@ToString(callSuper = true, exclude = {"student"})
public class Document extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull(message = "Tipo de documento é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 30)
    private DocumentType documentType;

    @NotBlank(message = "Número do documento é obrigatório")
    @Size(max = 100, message = "Número do documento deve ter no máximo 100 caracteres")
    @Column(name = "document_number", nullable = false, length = 100)
    private String documentNumber;

    @Size(max = 100, message = "Órgão emissor deve ter no máximo 100 caracteres")
    @Column(name = "issuing_authority", length = 100)
    private String issuingAuthority;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Size(max = 255, message = "URL do arquivo deve ter no máximo 255 caracteres")
    @Column(name = "file_url", length = 255)
    private String fileUrl;

    @Size(max = 100, message = "Nome do arquivo deve ter no máximo 100 caracteres")
    @Column(name = "file_name", length = 100)
    private String fileName;

    @Size(max = 50, message = "Tipo do arquivo deve ter no máximo 50 caracteres")
    @Column(name = "file_type", length = 50)
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "verified_at")
    private LocalDate verifiedAt;

    @Size(max = 255, message = "Nome do verificador deve ter no máximo 255 caracteres")
    @Column(name = "verified_by", length = 255)
    private String verifiedBy;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum DocumentType {
        RG,                     // Registro Geral
        CPF,                    // Cadastro de Pessoa Física
        BIRTH_CERTIFICATE,      // Certidão de Nascimento
        SCHOOL_TRANSCRIPT,      // Histórico Escolar
        VACCINATION_CARD,       // Carteira de Vacinação
        PROOF_OF_RESIDENCE,     // Comprovante de Residência
        PASSPORT,               // Passaporte
        WORK_PERMIT,           // Carteira de Trabalho
        MILITARY_CERTIFICATE,   // Certificado Militar
        VOTER_REGISTRATION,     // Título de Eleitor
        MARRIAGE_CERTIFICATE,   // Certidão de Casamento
        DIPLOMA,               // Diploma
        CERTIFICATE,           // Certificado
        OTHER                  // Outro
    }

    /**
     * Verifica se o documento está verificado
     */
    public boolean isDocumentVerified() {
        return isVerified != null && isVerified;
    }

    /**
     * Verifica se o documento está expirado
     */
    public boolean isExpired() {
        return expirationDate != null && expirationDate.isBefore(LocalDate.now());
    }

    /**
     * Marca o documento como verificado
     */
    public void markAsVerified(String verifiedBy) {
        this.isVerified = true;
        this.verifiedAt = LocalDate.now();
        this.verifiedBy = verifiedBy;
    }
}

