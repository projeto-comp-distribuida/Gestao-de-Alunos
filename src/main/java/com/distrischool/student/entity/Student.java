package com.distrischool.student.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Student para o sistema de gestão de alunos da faculdade.
 * Modelo completo com relacionamentos para Guardian, Address, Documents, etc.
 */
@Entity
@Table(name = "students", indexes = {
    @Index(name = "idx_student_registration", columnList = "registration_number", unique = true),
    @Index(name = "idx_student_email", columnList = "email", unique = true),
    @Index(name = "idx_student_cpf", columnList = "cpf", unique = true),
    @Index(name = "idx_student_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"guardians", "emergencyContacts", "documents", "enrollmentHistory"})
@ToString(callSuper = true, exclude = {"guardians", "emergencyContacts", "documents", "enrollmentHistory", "medicalRecord", "academicRecord"})
public class Student extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    @Column(name = "cpf", nullable = false, unique = true, length = 11)
    private String cpf;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve conter 10 ou 11 dígitos")
    @Column(name = "phone", length = 11)
    private String phone;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @NotBlank(message = "Número de matrícula é obrigatório")
    @Column(name = "registration_number", nullable = false, unique = true, length = 50)
    private String registrationNumber;

    @NotBlank(message = "Curso é obrigatório")
    @Size(max = 255, message = "Nome do curso deve ter no máximo 255 caracteres")
    @Column(name = "course", nullable = false, length = 255)
    private String course;

    @NotNull(message = "Semestre é obrigatório")
    @Min(value = 1, message = "Semestre deve ser no mínimo 1")
    @Max(value = 20, message = "Semestre deve ser no máximo 20")
    @Column(name = "semester", nullable = false)
    private Integer semester;

    @NotNull(message = "Data de ingresso é obrigatória")
    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    @NotNull(message = "Status é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private StudentStatus status = StudentStatus.ACTIVE;

    /**
     * Auth0 User ID (vínculo com o serviço de autenticação)
     * Referência ao usuário criado no auth service quando o aluno é criado
     */
    @Column(name = "auth0_id", unique = true, nullable = true, length = 255)
    private String auth0Id;

    // ==================== RELACIONAMENTOS ====================
    
    /**
     * Endereço principal do aluno
     */
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    /**
     * Responsáveis/Tutores do aluno
     * Um aluno pode ter múltiplos responsáveis (pai, mãe, tutor legal)
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Guardian> guardians = new ArrayList<>();

    /**
     * Contatos de emergência
     * Múltiplos contatos de emergência ordenados por prioridade
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EmergencyContact> emergencyContacts = new ArrayList<>();

    /**
     * Documentos do aluno
     * RG, CPF, Certidão de Nascimento, Histórico Escolar, etc
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Document> documents = new ArrayList<>();

    /**
     * Ficha médica do aluno
     * Informações médicas importantes: alergias, medicamentos, tipo sanguíneo
     */
    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private MedicalRecord medicalRecord;

    /**
     * Registro acadêmico consolidado
     * Performance geral do aluno: GPA, créditos, situação acadêmica
     */
    @OneToOne(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private AcademicRecord academicRecord;

    /**
     * Histórico de matrículas
     * Rastreio de todas as matrículas, transferências e mudanças de curso
     */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EnrollmentHistory> enrollmentHistory = new ArrayList<>();

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum StudentStatus {
        ACTIVE, INACTIVE, GRADUATED, SUSPENDED, TRANSFERRED, DROPPED
    }

    // ==================== MÉTODOS ÚTEIS ====================

    public boolean isActive() {
        return status == StudentStatus.ACTIVE;
    }

    public int getAge() {
        return LocalDate.now().getYear() - birthDate.getYear();
    }

    /**
     * Adiciona um responsável ao aluno
     * NOTA: Os setters das entidades relacionadas serão gerados pelo Lombok
     */
    public void addGuardian(Guardian guardian) {
        if (guardians == null) {
            guardians = new ArrayList<>();
        }
        guardians.add(guardian);
        // guardian.setStudent(this); // Será habilitado quando Lombok estiver funcionando
    }

    /**
     * Remove um responsável do aluno
     */
    public void removeGuardian(Guardian guardian) {
        if (guardians != null) {
            guardians.remove(guardian);
            // guardian.setStudent(null); // Será habilitado quando Lombok estiver funcionando
        }
    }

    /**
     * Adiciona um contato de emergência
     */
    public void addEmergencyContact(EmergencyContact contact) {
        if (emergencyContacts == null) {
            emergencyContacts = new ArrayList<>();
        }
        emergencyContacts.add(contact);
        // contact.setStudent(this); // Será habilitado quando Lombok estiver funcionando
    }

    /**
     * Remove um contato de emergência
     */
    public void removeEmergencyContact(EmergencyContact contact) {
        if (emergencyContacts != null) {
            emergencyContacts.remove(contact);
            // contact.setStudent(null); // Será habilitado quando Lombok estiver funcionando
        }
    }

    /**
     * Adiciona um documento
     */
    public void addDocument(Document document) {
        if (documents == null) {
            documents = new ArrayList<>();
        }
        documents.add(document);
        // document.setStudent(this); // Será habilitado quando Lombok estiver funcionando
    }

    /**
     * Remove um documento
     */
    public void removeDocument(Document document) {
        if (documents != null) {
            documents.remove(document);
            // document.setStudent(null); // Será habilitado quando Lombok estiver funcionando
        }
    }

    /**
     * Adiciona um histórico de matrícula
     */
    public void addEnrollmentHistory(EnrollmentHistory history) {
        if (enrollmentHistory == null) {
            enrollmentHistory = new ArrayList<>();
        }
        enrollmentHistory.add(history);
        // history.setStudent(this); // Será habilitado quando Lombok estiver funcionando
    }

    /**
     * Define a ficha médica
     */
    public void setMedicalRecordDetails(MedicalRecord record) {
        this.medicalRecord = record;
        // if (record != null) {
        //     record.setStudent(this); // Será habilitado quando Lombok estiver funcionando
        // }
    }

    /**
     * Define o registro acadêmico
     */
    public void setAcademicRecordDetails(AcademicRecord record) {
        this.academicRecord = record;
        // if (record != null) {
        //     record.setStudent(this); // Será habilitado quando Lombok estiver funcionando
        // }
    }

    /**
     * Retorna o responsável principal
     */
    public Guardian getPrimaryGuardian() {
        return guardians.stream()
                .filter(Guardian::isPrimary)
                .findFirst()
                .orElse(null);
    }

    /**
     * Retorna o responsável financeiro
     */
    public Guardian getFinancialGuardian() {
        return guardians.stream()
                .filter(Guardian::isFinanciallyResponsible)
                .findFirst()
                .orElse(null);
    }

    /**
     * Verifica se tem condições médicas especiais
     */
    public boolean hasMedicalConditions() {
        return medicalRecord != null && medicalRecord.hasMedicalConditions();
    }

    /**
     * Verifica se está em boa situação acadêmica
     */
    public boolean isInGoodAcademicStanding() {
        return academicRecord != null && academicRecord.isInGoodStanding();
    }
}


