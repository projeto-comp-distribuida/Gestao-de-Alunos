package com.distrischool.student.dto;

import com.distrischool.student.entity.Student.StudentStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequestDTO {

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String fullName;

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    private String cpf;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve conter 10 ou 11 dígitos")
    private String phone;

    @NotNull(message = "Data de nascimento é obrigatória")
    @Past(message = "Data de nascimento deve ser no passado")
    private LocalDate birthDate;

    @NotBlank(message = "Número de matrícula é obrigatório")
    private String registrationNumber;

    @NotBlank(message = "Curso é obrigatório")
    @Size(max = 255, message = "Nome do curso deve ter no máximo 255 caracteres")
    private String course;

    @NotNull(message = "Semestre é obrigatório")
    @Min(value = 1, message = "Semestre deve ser no mínimo 1")
    @Max(value = 20, message = "Semestre deve ser no máximo 20")
    private Integer semester;

    @NotNull(message = "Data de ingresso é obrigatória")
    private LocalDate enrollmentDate;

    private StudentStatus status;

    private String notes;

    // ==================== NOTA IMPORTANTE ====================
    // Endereços, Responsáveis, Contatos de Emergência, Documentos, etc
    // devem ser gerenciados via endpoints específicos após a criação do aluno
    // Isso mantém a API simples e RESTful
}

