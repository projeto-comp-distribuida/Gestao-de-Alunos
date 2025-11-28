package com.distrischool.student.service;

import com.distrischool.student.dto.StudentRequestDTO;
import com.distrischool.student.dto.StudentResponseDTO;
import com.distrischool.student.dto.StudentSummaryDTO;
import com.distrischool.student.entity.Student;
import com.distrischool.student.entity.Student.StudentStatus;
import com.distrischool.student.exception.BusinessException;
import com.distrischool.student.exception.ResourceNotFoundException;
import com.distrischool.student.feign.AuthServiceClient;
import com.distrischool.student.kafka.DistriSchoolEvent;
import com.distrischool.student.kafka.EventProducer;
import com.distrischool.student.repository.StudentRepository;
import com.distrischool.student.dto.auth.ApiResponse;
import com.distrischool.student.dto.auth.AuthResponse;
import com.distrischool.student.dto.auth.RegisterUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.security.SecureRandom;

/**
 * Service para gerenciamento de alunos
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final EventProducer eventProducer;
    private final AuthServiceClient authServiceClient;

    @Value("${microservice.kafka.topics.student-created}")
    private String studentCreatedTopic;

    @Value("${microservice.kafka.topics.student-updated}")
    private String studentUpdatedTopic;

    @Value("${microservice.kafka.topics.student-deleted}")
    private String studentDeletedTopic;

    @Value("${microservice.kafka.topics.student-status-changed}")
    private String studentStatusChangedTopic;

    /**
     * Cria um novo aluno
     */
    @Transactional
    @CacheEvict(value = "students", allEntries = true)
    public StudentResponseDTO createStudent(StudentRequestDTO request, String createdBy, String authorizationHeader) {
        log.info("Criando novo aluno: {}", request.getEmail());

        // Validações de negócio
        validateStudentUniqueness(request.getCpf(), request.getEmail(), null);
        validateStudentData(request);
        
        // Cria a entidade
        Student student = buildStudentFromRequest(request);
        student.setRegistrationNumber(generateRegistrationNumber());
        // auth0Id será definido após criar o usuário no serviço de auth
        String auth0Id = createAuthUserForStudent(student, authorizationHeader);
        student.setAuth0Id(auth0Id);
        student.setCreatedBy(createdBy);
        student.setUpdatedBy(createdBy);

        // Salva no banco
        Student savedStudent = studentRepository.save(student);
        log.info("Aluno criado com sucesso: ID={}, Matrícula={}",
                 savedStudent.getId(), savedStudent.getRegistrationNumber());

        // Publica evento Kafka
        publishStudentCreatedEvent(savedStudent);

        return StudentResponseDTO.fromEntity(savedStudent);
    }

    /**
     * Busca aluno por ID
     */
    @Cacheable(value = "students", key = "#id")
    public StudentResponseDTO getStudentById(Long id) {
        log.debug("Buscando aluno por ID: {}", id);
        Student student = findStudentByIdOrThrow(id);
        return StudentResponseDTO.fromEntity(student);
    }

    /**
     * Busca aluno por número de matrícula
     */
    @Cacheable(value = "students", key = "#registrationNumber")
    public StudentResponseDTO getStudentByRegistrationNumber(String registrationNumber) {
        log.debug("Buscando aluno por matrícula: {}", registrationNumber);
        Student student = studentRepository.findByRegistrationNumber(registrationNumber)
                .filter(s -> !s.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Aluno não encontrado com matrícula: " + registrationNumber));
        return StudentResponseDTO.fromEntity(student);
    }

    /**
     * Busca aluno por Auth0 ID
     * Retorna apenas o ID do aluno se encontrado, null caso contrário
     */
    public Long getStudentIdByAuth0Id(String auth0Id) {
        log.debug("Buscando aluno por Auth0 ID: {}", auth0Id);
        return studentRepository.findByAuth0Id(auth0Id)
                .filter(s -> !s.isDeleted())
                .map(Student::getId)
                .orElse(null);
    }

    /**
     * Lista todos os alunos com paginação
     */
    public Page<StudentSummaryDTO> getAllStudents(Pageable pageable) {
        log.debug("Listando todos os alunos - Página: {}", pageable.getPageNumber());
        return studentRepository.findAll(pageable)
                .map(StudentSummaryDTO::fromEntity);
    }

    /**
     * Busca alunos com filtros
     */
    public Page<StudentSummaryDTO> searchStudents(
            String name, String course, Integer semester, StudentStatus status, Pageable pageable) {
        log.debug("Buscando alunos com filtros - Nome: {}, Curso: {}, Semestre: {}, Status: {}",
                  name, course, semester, status);
        
        // Normalize null/empty strings: for name, use empty string instead of null to avoid JPQL CONCAT issues
        // For other parameters, keep null as null
        String normalizedName = (name == null || name.trim().isEmpty()) ? "" : name;
        String normalizedCourse = (course != null && course.trim().isEmpty()) ? null : course;
        
        return studentRepository.findByFilters(normalizedName, normalizedCourse, semester, status, pageable)
                .map(StudentSummaryDTO::fromEntity);
    }

    /**
     * Atualiza um aluno
     */
    @Transactional
    @CacheEvict(value = "students", allEntries = true)
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO request, String updatedBy) {
        log.info("Atualizando aluno: ID={}", id);

        Student student = findStudentByIdOrThrow(id);

        // Valida unicidade (exceto para o próprio aluno)
        validateStudentUniqueness(request.getCpf(), request.getEmail(), id);
        validateStudentData(request);

        // Atualiza os campos
        updateStudentFields(student, request);
        student.setUpdatedBy(updatedBy);

        Student updatedStudent = studentRepository.save(student);
        log.info("Aluno atualizado com sucesso: ID={}", updatedStudent.getId());

        // Publica evento Kafka
        publishStudentUpdatedEvent(updatedStudent);

        return StudentResponseDTO.fromEntity(updatedStudent);
    }

    /**
     * Atualiza o status do aluno
     */
    @Transactional
    @CacheEvict(value = "students", allEntries = true)
    public StudentResponseDTO updateStudentStatus(Long id, StudentStatus newStatus, String updatedBy) {
        log.info("Atualizando status do aluno: ID={}, Novo Status={}", id, newStatus);

        Student student = findStudentByIdOrThrow(id);
        StudentStatus oldStatus = student.getStatus();

        student.setStatus(newStatus);
        student.setUpdatedBy(updatedBy);

        Student updatedStudent = studentRepository.save(student);
        log.info("Status do aluno atualizado: ID={}, Status: {} -> {}",
                 id, oldStatus, newStatus);

        // Publica evento Kafka
        publishStudentStatusChangedEvent(updatedStudent, oldStatus, newStatus);

        return StudentResponseDTO.fromEntity(updatedStudent);
    }

    /**
     * Deleta um aluno (soft delete)
     */
    @Transactional
    @CacheEvict(value = "students", allEntries = true)
    public void deleteStudent(Long id, String deletedBy) {
        log.info("Deletando aluno (soft delete): ID={}", id);

        Student student = findStudentByIdOrThrow(id);

        if (student.isDeleted()) {
            throw new BusinessException("Aluno já foi deletado anteriormente");
        }

        student.markAsDeleted(deletedBy);
        studentRepository.save(student);

        log.info("Aluno deletado com sucesso: ID={}", id);

        // Publica evento Kafka
        publishStudentDeletedEvent(student);
    }

    /**
     * Restaura um aluno deletado
     */
    @Transactional
    @CacheEvict(value = "students", allEntries = true)
    public StudentResponseDTO restoreStudent(Long id, String updatedBy) {
        log.info("Restaurando aluno: ID={}", id);

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com ID: " + id));

        if (!student.isDeleted()) {
            throw new BusinessException("Aluno não está deletado");
        }

        student.restore();
        student.setUpdatedBy(updatedBy);

        Student restoredStudent = studentRepository.save(student);
        log.info("Aluno restaurado com sucesso: ID={}", id);

        return StudentResponseDTO.fromEntity(restoredStudent);
    }

    /**
     * Busca alunos por curso
     */
    public Page<StudentSummaryDTO> getStudentsByCourse(String course, Pageable pageable) {
        log.debug("Buscando alunos por curso: {}", course);
        return studentRepository.findByCourse(course, pageable)
                .map(StudentSummaryDTO::fromEntity);
    }

    /**
     * Busca alunos por curso e semestre
     */
    public Page<StudentSummaryDTO> getStudentsByCourseAndSemester(
            String course, Integer semester, Pageable pageable) {
        log.debug("Buscando alunos - Curso: {}, Semestre: {}", course, semester);
        return studentRepository.findByCourseAndSemester(course, semester, pageable)
                .map(StudentSummaryDTO::fromEntity);
    }

    /**
     * Conta alunos por status
     */
    public long countStudentsByStatus(StudentStatus status) {
        return studentRepository.countByStatus(status);
    }

    /**
     * Conta alunos por curso
     */
    public long countStudentsByCourse(String course) {
        return studentRepository.countByCourse(course);
    }

    /**
     * Busca múltiplos alunos por IDs (validação em lote)
     * Retorna uma lista de Maps com os dados dos estudantes
     */
    public List<Map<String, Object>> getStudentsByIds(List<Long> studentIds) {
        log.debug("Buscando múltiplos alunos por IDs: {}", studentIds);
        
        if (studentIds == null || studentIds.isEmpty()) {
            return List.of();
        }
        
        List<Student> students = studentRepository.findByIdsNotDeleted(studentIds);
        
        return students.stream()
                .map(student -> {
                    StudentResponseDTO dto = StudentResponseDTO.fromEntity(student);
                    return convertDtoToMap(dto);
                })
                .toList();
    }

    /**
     * Converte StudentResponseDTO para Map<String, Object>
     */
    private Map<String, Object> convertDtoToMap(StudentResponseDTO dto) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", dto.getId());
        map.put("fullName", dto.getFullName());
        map.put("cpf", dto.getCpf());
        map.put("email", dto.getEmail());
        map.put("phone", dto.getPhone());
        map.put("birthDate", dto.getBirthDate());
        map.put("age", dto.getAge());
        map.put("registrationNumber", dto.getRegistrationNumber());
        map.put("course", dto.getCourse());
        map.put("semester", dto.getSemester());
        map.put("enrollmentDate", dto.getEnrollmentDate());
        map.put("status", dto.getStatus() != null ? dto.getStatus().toString() : null);
        map.put("addressStreet", dto.getAddressStreet());
        map.put("addressNumber", dto.getAddressNumber());
        map.put("addressComplement", dto.getAddressComplement());
        map.put("addressNeighborhood", dto.getAddressNeighborhood());
        map.put("addressCity", dto.getAddressCity());
        map.put("addressState", dto.getAddressState());
        map.put("addressZipcode", dto.getAddressZipcode());
        map.put("emergencyContactName", dto.getEmergencyContactName());
        map.put("emergencyContactPhone", dto.getEmergencyContactPhone());
        map.put("emergencyContactRelationship", dto.getEmergencyContactRelationship());
        map.put("notes", dto.getNotes());
        map.put("createdAt", dto.getCreatedAt());
        map.put("updatedAt", dto.getUpdatedAt());
        map.put("createdBy", dto.getCreatedBy());
        map.put("updatedBy", dto.getUpdatedBy());
        return map;
    }

    /**
     * Verifica se o usuário tem a role ADMIN
     * @deprecated Use Spring Security @PreAuthorize("hasRole('ADMIN')") instead
     * A verificação de role agora é feita diretamente do JWT token via Spring Security
     */
    @Deprecated
    public boolean isAdmin(String auth0Id) {
        log.warn("isAdmin() method is deprecated. Use @PreAuthorize annotation instead.");
        // Este método não é mais usado, mas mantido para compatibilidade
        // A autorização é feita via @PreAuthorize("hasRole('ADMIN')") no controller
        return false;
    }

    // ==================== MÉTODOS PRIVADOS ====================


    private Student findStudentByIdOrThrow(Long id) {
        return studentRepository.findById(id)
                .filter(s -> !s.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com ID: " + id));
    }

    private String createAuthUserForStudent(Student student, String authorizationHeader) {
        try {
            String password = generateSecurePassword();

            RegisterUserRequest registerUserRequest = RegisterUserRequest.builder()
                    .email(student.getEmail())
                    .password(password)
                    .confirmPassword(password)
                    .firstName(extractFirstName(student.getFullName()))
                    .lastName(extractLastName(student.getFullName()))
                    .phone(student.getPhone())
                    .documentNumber(student.getCpf())
                    .roles(Set.of("STUDENT"))
                    .build();

            ApiResponse<AuthResponse> response = authServiceClient.registerUser(authorizationHeader, registerUserRequest);

            if (response == null) {
                throw new BusinessException("Serviço de autenticação não respondeu ao registrar o usuário do aluno");
            }

            if (!Boolean.TRUE.equals(response.getSuccess())) {
                String message = response.getMessage() != null ? response.getMessage() : "Resposta sem sucesso do serviço de autenticação";
                throw new BusinessException("Falha ao registrar usuário no Auth0: " + message);
            }

            AuthResponse data = response.getData();
            if (data == null || data.getUser() == null) {
                throw new BusinessException("Serviço de autenticação não retornou os dados do usuário registrado");
            }

            String auth0Id = data.getUser().getAuth0Id();
            if (auth0Id == null || auth0Id.isBlank()) {
                throw new BusinessException("Serviço de autenticação não retornou o Auth0 ID do usuário");
            }

            log.info("Usuário Auth0 registrado com sucesso para {} - auth0Id={}", student.getEmail(), auth0Id);
            return auth0Id;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Falha ao registrar usuário Auth0 para {}: {}", student.getEmail(), ex.getMessage(), ex);
            throw new BusinessException("Erro ao registrar usuário no Auth0: " + ex.getMessage());
        }
    }

    private String generateSecurePassword() {
        final String lower = "abcdefghijklmnopqrstuvwxyz";
        final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String digits = "0123456789";
        final String special = "@$!%*?&";
        final String allChars = lower + upper + digits + special;
        final int length = 12;

        SecureRandom random = new SecureRandom();
        ArrayList<Character> passwordChars = new ArrayList<>();

        passwordChars.add(lower.charAt(random.nextInt(lower.length())));
        passwordChars.add(upper.charAt(random.nextInt(upper.length())));
        passwordChars.add(digits.charAt(random.nextInt(digits.length())));
        passwordChars.add(special.charAt(random.nextInt(special.length())));

        while (passwordChars.size() < length) {
            passwordChars.add(allChars.charAt(random.nextInt(allChars.length())));
        }

        Collections.shuffle(passwordChars, random);

        StringBuilder password = new StringBuilder();
        for (Character c : passwordChars) {
            password.append(c);
        }
        return password.toString();
    }

    private String extractFirstName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "Student";
        }
        String trimmed = fullName.trim();
        int spaceIndex = trimmed.indexOf(' ');
        if (spaceIndex == -1) {
            return trimmed;
        }
        return trimmed.substring(0, spaceIndex);
    }

    private String extractLastName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "User";
        }
        String trimmed = fullName.trim();
        int spaceIndex = trimmed.indexOf(' ');
        if (spaceIndex == -1) {
            return trimmed;
        }
        return trimmed.substring(spaceIndex + 1);
    }

    private void validateStudentUniqueness(String cpf, String email, Long excludeId) {
        // Valida CPF
        studentRepository.findByCpf(cpf).ifPresent(existing -> {
            if (excludeId == null || !existing.getId().equals(excludeId)) {
                throw new BusinessException("Já existe um aluno cadastrado com o CPF: " + cpf);
            }
        });

        // Valida Email
        studentRepository.findByEmail(email).ifPresent(existing -> {
            if (excludeId == null || !existing.getId().equals(excludeId)) {
                throw new BusinessException("Já existe um aluno cadastrado com o email: " + email);
            }
        });
    }

    private void validateStudentData(StudentRequestDTO request) {
        // Data de ingresso não pode ser futura
        if (request.getEnrollmentDate() != null && request.getEnrollmentDate().isAfter(LocalDate.now())) {
            throw new BusinessException("Data de ingresso não pode ser no futuro");
        }

        // CPF válido pelo algoritmo oficial
        if (request.getCpf() != null && !isValidCpf(request.getCpf())) {
            throw new BusinessException("CPF inválido");
        }
    }

    /**
     * Valida CPF considerando os dígitos verificadores.
     * Aceita apenas 11 dígitos e rejeita sequências repetidas.
     */
    private boolean isValidCpf(String cpf) {
        if (cpf == null) return false;
        String digits = cpf.replaceAll("\\D", "");
        if (digits.length() != 11) return false;

        // Rejeita sequências do mesmo dígito (ex: 000..., 111..., etc)
        if (digits.chars().distinct().count() == 1) return false;

        try {
            int d1 = 0, d2 = 0;
            for (int i = 0; i < 9; i++) {
                int num = digits.charAt(i) - '0';
                d1 += num * (10 - i);
                d2 += num * (11 - i);
            }
            int check1 = d1 % 11;
            check1 = check1 < 2 ? 0 : 11 - check1;

            d2 += check1 * 2;
            int check2 = d2 % 11;
            check2 = check2 < 2 ? 0 : 11 - check2;

            return (digits.charAt(9) - '0') == check1 && (digits.charAt(10) - '0') == check2;
        } catch (Exception e) {
            return false;
        }
    }

    private Student buildStudentFromRequest(StudentRequestDTO request) {
        return Student.builder()
                .fullName(request.getFullName())
                .cpf(request.getCpf())
                .email(request.getEmail())
                .phone(request.getPhone())
                .birthDate(request.getBirthDate())
                .registrationNumber(request.getRegistrationNumber())
                .course(request.getCourse())
                .semester(request.getSemester())
                .enrollmentDate(request.getEnrollmentDate())
                .status(request.getStatus() != null ? request.getStatus() : StudentStatus.ACTIVE)
                .notes(request.getNotes())
                .build();
        // Nota: Endereços, responsáveis, contatos de emergência, etc
        // devem ser adicionados via endpoints específicos após a criação
    }

    private void updateStudentFields(Student student, StudentRequestDTO request) {
        student.setFullName(request.getFullName());
        student.setCpf(request.getCpf());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setBirthDate(request.getBirthDate());
        student.setCourse(request.getCourse());
        student.setSemester(request.getSemester());
        student.setEnrollmentDate(request.getEnrollmentDate());
        student.setStatus(request.getStatus());
        // Nota: Endereços e contatos são gerenciados via endpoints específicos
        student.setNotes(request.getNotes());
    }

    private String generateRegistrationNumber() {
        // Formato: YYYYMM + 6 dígitos sequenciais
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();
        long count = studentRepository.count() + 1;
        return String.format("%d%02d%06d", year, month, count);
    }

    // ==================== KAFKA EVENTS ====================

    private void publishStudentCreatedEvent(Student student) {
        Map<String, Object> data = new HashMap<>();
        data.put("studentId", student.getId());
        data.put("fullName", student.getFullName());
        data.put("email", student.getEmail());
        data.put("registrationNumber", student.getRegistrationNumber());
        data.put("course", student.getCourse());

        DistriSchoolEvent event = DistriSchoolEvent.of(
                "student.created", "student-management-service", data);
        eventProducer.send(studentCreatedTopic, event);
    }

    private void publishStudentUpdatedEvent(Student student) {
        Map<String, Object> data = new HashMap<>();
        data.put("studentId", student.getId());
        data.put("email", student.getEmail());
        // Adicione outros campos relevantes que foram atualizados

        DistriSchoolEvent event = DistriSchoolEvent.of(
                "student.updated", "student-management-service", data);
        eventProducer.send(studentUpdatedTopic, event);
    }

    private void publishStudentDeletedEvent(Student student) {
        Map<String, Object> data = new HashMap<>();
        data.put("studentId", student.getId());
        data.put("fullName", student.getFullName());
        data.put("registrationNumber", student.getRegistrationNumber());

        DistriSchoolEvent event = DistriSchoolEvent.of(
                "student.deleted", "student-management-service", data);
        eventProducer.send(studentDeletedTopic, event);
    }

    private void publishStudentStatusChangedEvent(Student student, StudentStatus oldStatus, StudentStatus newStatus) {
        Map<String, Object> data = new HashMap<>();
        data.put("studentId", student.getId());
        data.put("oldStatus", oldStatus.toString());
        data.put("newStatus", newStatus.toString());
        data.put("registrationNumber", student.getRegistrationNumber());

        DistriSchoolEvent event = DistriSchoolEvent.of(
                "student.status.changed", "student-management-service", data);
        eventProducer.send(studentStatusChangedTopic, event);
    }
}
