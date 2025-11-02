package com.distrischool.student.controller;

import com.distrischool.student.dto.ApiResponse;
import com.distrischool.student.dto.StudentRequestDTO;
import com.distrischool.student.dto.StudentResponseDTO;
import com.distrischool.student.dto.StudentSummaryDTO;
import com.distrischool.student.entity.Student.StudentStatus;
import com.distrischool.student.service.StudentService;
import io.micrometer.core.annotation.Timed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller REST para gerenciamento de alunos
 */
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Slf4j
public class StudentController {

    private final StudentService studentService;

    /**
     * Cria um novo aluno
     * POST /api/v1/students
     * Requer role ADMIN
     */
    @PostMapping
    @Timed(value = "students.create", description = "Time taken to create a student")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> createStudent(
        @Valid @RequestBody StudentRequestDTO request,
        @RequestHeader(value = "X-User-Id", required = false) String userId,
        @AuthenticationPrincipal Jwt jwt) {

        String effectiveUserId = userId != null ? userId : (jwt != null ? jwt.getSubject() : null);
        
        if (effectiveUserId == null) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Usuário não autenticado"));
        }

        // Verifica se o usuário tem role ADMIN
        boolean isAdmin = studentService.isAdmin(effectiveUserId);
        if (!isAdmin) {
            log.warn("Tentativa de criar aluno sem permissão ADMIN por usuário: {}", effectiveUserId);
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Apenas usuários com role ADMIN podem criar alunos"));
        }

        log.info("Requisição para criar aluno: {} (by {})", request.getEmail(), effectiveUserId);
        StudentResponseDTO student = studentService.createStudent(request, effectiveUserId);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(student, "Aluno criado com sucesso"));
    }

    /**
     * Busca aluno por ID
     * GET /api/v1/students/{id}
     */
    @GetMapping("/{id}")
    @Timed(value = "students.get", description = "Time taken to get a student")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudentById(@PathVariable Long id) {
        log.info("Requisição para buscar aluno por ID: {}", id);
        StudentResponseDTO student = studentService.getStudentById(id);
        return ResponseEntity.ok(ApiResponse.success(student));
    }

    /**
     * Busca aluno por número de matrícula
     * GET /api/v1/students/registration/{registrationNumber}
     */
    @GetMapping("/registration/{registrationNumber}")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> getStudentByRegistration(
        @PathVariable String registrationNumber) {
        log.info("Requisição para buscar aluno por matrícula: {}", registrationNumber);
        StudentResponseDTO student = studentService.getStudentByRegistrationNumber(registrationNumber);
        return ResponseEntity.ok(ApiResponse.success(student));
    }

    /**
     * Lista todos os alunos com paginação
     * GET /api/v1/students
     */
    @GetMapping
    @Timed(value = "students.list", description = "Time taken to list students")
    public ResponseEntity<ApiResponse<Page<StudentSummaryDTO>>> getAllStudents(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "ASC") Sort.Direction direction) {

        log.info("Requisição para listar alunos - Página: {}, Tamanho: {}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<StudentSummaryDTO> students = studentService.getAllStudents(pageable);

        return ResponseEntity.ok(ApiResponse.success(students));
    }

    /**
     * Busca alunos com filtros
     * GET /api/v1/students/search
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<StudentSummaryDTO>>> searchStudents(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String course,
        @RequestParam(required = false) Integer semester,
        @RequestParam(required = false) StudentStatus status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "fullName") String sortBy,
        @RequestParam(defaultValue = "ASC") Sort.Direction direction) {

        log.info("Requisição para buscar alunos - Filtros: name={}, course={}, semester={}, status={}",
            name, course, semester, status);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<StudentSummaryDTO> students = studentService.searchStudents(name, course, semester, status, pageable);

        return ResponseEntity.ok(ApiResponse.success(students));
    }

    /**
     * Busca alunos por curso
     * GET /api/v1/students/course/{course}
     */
    @GetMapping("/course/{course}")
    public ResponseEntity<ApiResponse<Page<StudentSummaryDTO>>> getStudentsByCourse(
        @PathVariable String course,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {

        log.info("Requisição para buscar alunos por curso: {}", course);
        Pageable pageable = PageRequest.of(page, size, Sort.by("fullName"));
        Page<StudentSummaryDTO> students = studentService.getStudentsByCourse(course, pageable);

        return ResponseEntity.ok(ApiResponse.success(students));
    }

    /**
     * Busca alunos por curso e semestre
     * GET /api/v1/students/course/{course}/semester/{semester}
     */
    @GetMapping("/course/{course}/semester/{semester}")
    public ResponseEntity<ApiResponse<Page<StudentSummaryDTO>>> getStudentsByCourseAndSemester(
        @PathVariable String course,
        @PathVariable Integer semester,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {

        log.info("Requisição para buscar alunos - Curso: {}, Semestre: {}", course, semester);
        Pageable pageable = PageRequest.of(page, size, Sort.by("fullName"));
        Page<StudentSummaryDTO> students = studentService.getStudentsByCourseAndSemester(course, semester, pageable);

        return ResponseEntity.ok(ApiResponse.success(students));
    }

    /**
     * Atualiza um aluno
     * PUT /api/v1/students/{id}
     */
    @PutMapping("/{id}")
    @Timed(value = "students.update", description = "Time taken to update a student")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> updateStudent(
        @PathVariable Long id,
        @Valid @RequestBody StudentRequestDTO request,
        @RequestHeader(value = "X-User-Id", required = false) String userId,
        @AuthenticationPrincipal Jwt jwt) {

        String effectiveUserId = userId != null ? userId : (jwt != null ? jwt.getSubject() : "system");
        log.info("Requisição para atualizar aluno: ID={} (by {})", id, effectiveUserId);
        StudentResponseDTO student = studentService.updateStudent(id, request, effectiveUserId);

        return ResponseEntity.ok(ApiResponse.success(student, "Aluno atualizado com sucesso"));
    }

    /**
     * Atualiza o status do aluno
     * PATCH /api/v1/students/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> updateStudentStatus(
        @PathVariable Long id,
        @RequestParam StudentStatus status,
        @RequestHeader(value = "X-User-Id", required = false) String userId,
        @AuthenticationPrincipal Jwt jwt) {

        String effectiveUserId = userId != null ? userId : (jwt != null ? jwt.getSubject() : "system");
        log.info("Requisição para atualizar status do aluno: ID={}, Status={} (by {})", id, status, effectiveUserId);
        StudentResponseDTO student = studentService.updateStudentStatus(id, status, effectiveUserId);

        return ResponseEntity.ok(ApiResponse.success(student, "Status do aluno atualizado com sucesso"));
    }

    /**
     * Deleta um aluno (soft delete)
     * DELETE /api/v1/students/{id}
     */
    @DeleteMapping("/{id}")
    @Timed(value = "students.delete", description = "Time taken to delete a student")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(
        @PathVariable Long id,
        @RequestHeader(value = "X-User-Id", required = false) String userId,
        @AuthenticationPrincipal Jwt jwt) {

        String effectiveUserId = userId != null ? userId : (jwt != null ? jwt.getSubject() : "system");
        log.info("Requisição para deletar aluno: ID={} (by {})", id, effectiveUserId);
        studentService.deleteStudent(id, effectiveUserId);

        return ResponseEntity.ok(ApiResponse.success(null, "Aluno deletado com sucesso"));
    }

    /**
     * Restaura um aluno deletado
     * POST /api/v1/students/{id}/restore
     */
    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<StudentResponseDTO>> restoreStudent(
        @PathVariable Long id,
        @RequestHeader(value = "X-User-Id", required = false) String userId,
        @AuthenticationPrincipal Jwt jwt) {

        String effectiveUserId = userId != null ? userId : (jwt != null ? jwt.getSubject() : "system");
        log.info("Requisição para restaurar aluno: ID={} (by {})", id, effectiveUserId);
        StudentResponseDTO student = studentService.restoreStudent(id, effectiveUserId);

        return ResponseEntity.ok(ApiResponse.success(student, "Aluno restaurado com sucesso"));
    }

    /**
     * Estatísticas gerais
     * GET /api/v1/students/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics() {
        log.info("Requisição para obter estatísticas de alunos");

        long activeStudents = studentService.countStudentsByStatus(StudentStatus.ACTIVE);
        long inactiveStudents = studentService.countStudentsByStatus(StudentStatus.INACTIVE);
        long graduatedStudents = studentService.countStudentsByStatus(StudentStatus.GRADUATED);
        long suspendedStudents = studentService.countStudentsByStatus(StudentStatus.SUSPENDED);

        Map<String, Object> statistics = Map.of(
            "active", activeStudents,
            "inactive", inactiveStudents,
            "graduated", graduatedStudents,
            "suspended", suspendedStudents,
            "total", activeStudents + inactiveStudents + graduatedStudents + suspendedStudents
        );

        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    /**
     * Conta alunos por curso
     * GET /api/v1/students/count/course/{course}
     */
    @GetMapping("/count/course/{course}")
    public ResponseEntity<ApiResponse<Long>> countStudentsByCourse(@PathVariable String course) {
        log.info("Requisição para contar alunos por curso: {}", course);
        long count = studentService.countStudentsByCourse(course);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
