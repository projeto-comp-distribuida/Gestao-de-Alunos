package com.distrischool.student.controller;

import com.distrischool.student.StudentServiceApplication;
import com.distrischool.student.config.TestContainersConfiguration;
import com.distrischool.student.dto.StudentRequestDTO;
import com.distrischool.student.dto.auth.AuthResponse;
import com.distrischool.student.dto.auth.CreateUserRequest;
import com.distrischool.student.dto.auth.UserResponse;
import com.distrischool.student.entity.Student;
import com.distrischool.student.entity.Student.StudentStatus;
import com.distrischool.student.feign.AuthServiceClient;
import com.distrischool.student.kafka.EventProducer;
import com.distrischool.student.repository.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for StudentController.
 * Tests the full stack: Controller -> Service -> Repository -> Database
 */
@SpringBootTest(classes = StudentServiceApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(TestContainersConfiguration.class)
@Transactional
@DisplayName("StudentController - Integration Tests")
class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    @MockBean
    private AuthServiceClient authServiceClient;

    @MockBean
    private EventProducer eventProducer;

    private StudentRequestDTO validStudentRequest;
    private com.distrischool.student.dto.auth.ApiResponse<AuthResponse> mockAuthResponse;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();

        // Setup valid student request (using valid CPF: 11144477735)
        validStudentRequest = StudentRequestDTO.builder()
                .fullName("João Silva Santos")
                .cpf("11144477735")
                .email("joao.silva@faculdade.edu.br")
                .phone("11987654321")
                .birthDate(LocalDate.of(2000, 5, 15))
                .course("Ciência da Computação")
                .semester(3)
                .enrollmentDate(LocalDate.of(2023, 2, 1))
                .status(StudentStatus.ACTIVE)
                .build();

        // Setup mock auth service response
        UserResponse mockUser = UserResponse.builder()
                .id(1L)
                .auth0Id("auth0|123456")
                .email("joao.silva@faculdade.edu.br")
                .build();

        AuthResponse mockAuth = AuthResponse.builder()
                .user(mockUser)
                .build();

        mockAuthResponse = com.distrischool.student.dto.auth.ApiResponse.<AuthResponse>builder()
                .success(true)
                .data(mockAuth)
                .build();

        // Mock auth service to return success for user creation
        when(authServiceClient.createUser(any(CreateUserRequest.class)))
                .thenReturn(mockAuthResponse);

        // Mock getUserByAuth0Id - return user response for admin users
        com.distrischool.student.dto.auth.ApiResponse<UserResponse> adminUserResponse = 
                com.distrischool.student.dto.auth.ApiResponse.<UserResponse>builder()
                        .success(true)
                        .data(UserResponse.builder()
                                .id(1L)
                                .auth0Id("auth0|admin123")
                                .email("admin@faculdade.edu.br")
                                .build())
                        .build();
        
        // Mock hasRole - return true for admin users
        com.distrischool.student.dto.auth.ApiResponse<Boolean> adminRoleResponse = 
                com.distrischool.student.dto.auth.ApiResponse.<Boolean>builder()
                        .success(true)
                        .data(true)
                        .build();

        // Default response for other auth0Ids - must be defined FIRST
        when(authServiceClient.getUserByAuth0Id(anyString()))
                .thenReturn(com.distrischool.student.dto.auth.ApiResponse.<UserResponse>builder()
                        .success(false)
                        .data(null)
                        .build());
        // Return admin user for admin auth0Id - must be defined AFTER anyString() to override
        when(authServiceClient.getUserByAuth0Id("auth0|admin123"))
                .thenReturn(adminUserResponse);
        
        // Default response for other users - must be defined FIRST
        when(authServiceClient.hasRole(anyLong(), eq("ADMIN")))
                .thenReturn(com.distrischool.student.dto.auth.ApiResponse.<Boolean>builder()
                        .success(true)
                        .data(false)
                        .build());
        // Mock hasRole - return true for admin user (ID 1L) - must be defined AFTER anyLong() to override
        when(authServiceClient.hasRole(1L, "ADMIN"))
                .thenReturn(adminRoleResponse);
    }

    @Test
    @DisplayName("Deve criar um aluno com sucesso - Integração completa")
    void shouldCreateStudentSuccessfully() throws Exception {
        // Given: valid student request
        String requestBody = objectMapper.writeValueAsString(validStudentRequest);

        // When & Then: POST request to create student
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("X-User-Id", "auth0|admin123"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("João Silva Santos"));
    }

    @Test
    @DisplayName("Deve buscar aluno por ID - Integração completa")
    void shouldGetStudentById() throws Exception {
        // Given: create a student directly in database (using valid CPF: 12345678909)
        Student student = Student.builder()
                .fullName("Maria Silva")
                .cpf("12345678909")
                .email("maria.silva@faculdade.edu.br")
                .phone("11987654322")
                .birthDate(LocalDate.of(1999, 3, 10))
                .registrationNumber("20231001")
                .course("Engenharia")
                .semester(2)
                .enrollmentDate(LocalDate.of(2023, 2, 1))
                .status(StudentStatus.ACTIVE)
                .auth0Id("auth0|test123")
                .build();
        Student savedStudent = studentRepository.save(student);

        // When & Then: GET request to retrieve student
        mockMvc.perform(get("/api/v1/students/{id}", savedStudent.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(savedStudent.getId()))
                .andExpect(jsonPath("$.data.fullName").value("Maria Silva"))
                .andExpect(jsonPath("$.data.email").value("maria.silva@faculdade.edu.br"))
                .andExpect(jsonPath("$.data.registrationNumber").value("20231001"));
    }

    @Test
    @DisplayName("Deve buscar aluno por número de matrícula - Integração completa")
    void shouldGetStudentByRegistrationNumber() throws Exception {
        // Given: create a student directly in database (using valid CPF: 98765432100)
        Student student = Student.builder()
                .fullName("Pedro Oliveira")
                .cpf("98765432100")
                .email("pedro.oliveira@faculdade.edu.br")
                .phone("11987654333")
                .birthDate(LocalDate.of(2001, 7, 20))
                .registrationNumber("20231002")
                .course("Medicina")
                .semester(1)
                .enrollmentDate(LocalDate.of(2023, 2, 1))
                .status(StudentStatus.ACTIVE)
                .auth0Id("auth0|test456")
                .build();
        studentRepository.save(student);

        // When & Then: GET request to retrieve student by registration
        mockMvc.perform(get("/api/v1/students/registration/{registrationNumber}", "20231002")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.registrationNumber").value("20231002"))
                .andExpect(jsonPath("$.data.fullName").value("Pedro Oliveira"));
    }

    @Test
    @DisplayName("Deve listar alunos com paginação - Integração completa")
    void shouldListStudentsWithPagination() throws Exception {
        // Given: create multiple students
        for (int i = 1; i <= 5; i++) {
            Student student = Student.builder()
                    .fullName("Aluno " + i)
                    .cpf("111222333" + String.format("%02d", i))
                    .email("aluno" + i + "@faculdade.edu.br")
                    .phone("1198765433" + i)
                    .birthDate(LocalDate.of(2000, 1, 1))
                    .registrationNumber("2023100" + i)
                    .course("Curso Teste")
                    .semester(1)
                    .enrollmentDate(LocalDate.of(2023, 2, 1))
                    .status(StudentStatus.ACTIVE)
                    .auth0Id("auth0|test" + i)
                    .build();
            studentRepository.save(student);
        }

        // When & Then: GET request to list students
        mockMvc.perform(get("/api/v1/students")
                        .param("page", "0")
                        .param("size", "3")
                        .param("sortBy", "id")
                        .param("direction", "ASC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(3))
                .andExpect(jsonPath("$.data.totalElements").value(5))
                .andExpect(jsonPath("$.data.totalPages").value(2));
    }

    @Test
    @DisplayName("Deve buscar alunos com filtros - Integração completa")
    void shouldSearchStudentsWithFilters() throws Exception {
        // Given: create students with different attributes
        Student student1 = Student.builder()
                .fullName("Ana Silva")
                .cpf("11122233355")
                .email("ana.silva@faculdade.edu.br")
                .phone("11987654344")
                .birthDate(LocalDate.of(2000, 5, 10))
                .registrationNumber("20231010")
                .course("Ciência da Computação")
                .semester(3)
                .enrollmentDate(LocalDate.of(2023, 2, 1))
                .status(StudentStatus.ACTIVE)
                .auth0Id("auth0|ana123")
                .build();

        Student student2 = Student.builder()
                .fullName("Bruno Costa")
                .cpf("11122233366")
                .email("bruno.costa@faculdade.edu.br")
                .phone("11987654355")
                .birthDate(LocalDate.of(2001, 8, 15))
                .registrationNumber("20231011")
                .course("Ciência da Computação")
                .semester(3)
                .enrollmentDate(LocalDate.of(2023, 2, 1))
                .status(StudentStatus.ACTIVE)
                .auth0Id("auth0|bruno123")
                .build();

        Student student3 = Student.builder()
                .fullName("Carla Mendes")
                .cpf("11122233377")
                .email("carla.mendes@faculdade.edu.br")
                .phone("11987654366")
                .birthDate(LocalDate.of(1999, 11, 20))
                .registrationNumber("20231012")
                .course("Engenharia")
                .semester(2)
                .enrollmentDate(LocalDate.of(2023, 2, 1))
                .status(StudentStatus.INACTIVE)
                .auth0Id("auth0|carla123")
                .build();

        studentRepository.save(student1);
        studentRepository.save(student2);
        studentRepository.save(student3);

        // When & Then: GET request with filters
        mockMvc.perform(get("/api/v1/students/search")
                        .param("course", "Ciência da Computação")
                        .param("semester", "3")
                        .param("status", "ACTIVE")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "fullName")
                        .param("direction", "ASC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.content[0].course").value("Ciência da Computação"))
                .andExpect(jsonPath("$.data.content[0].semester").value(3));
    }

    @Test
    @DisplayName("Deve buscar alunos por curso - Integração completa")
    void shouldGetStudentsByCourse() throws Exception {
        // Given: create students in different courses
        Student student1 = Student.builder()
                .fullName("João Engenharia")
                .cpf("11122233388")
                .email("joao.eng@faculdade.edu.br")
                .phone("11987654377")
                .birthDate(LocalDate.of(2000, 1, 1))
                .registrationNumber("20231020")
                .course("Engenharia")
                .semester(1)
                .enrollmentDate(LocalDate.of(2023, 2, 1))
                .status(StudentStatus.ACTIVE)
                .auth0Id("auth0|joao123")
                .build();

        Student student2 = Student.builder()
                .fullName("Maria Engenharia")
                .cpf("11122233399")
                .email("maria.eng@faculdade.edu.br")
                .phone("11987654388")
                .birthDate(LocalDate.of(1999, 2, 2))
                .registrationNumber("20231021")
                .course("Engenharia")
                .semester(2)
                .enrollmentDate(LocalDate.of(2023, 2, 1))
                .status(StudentStatus.ACTIVE)
                .auth0Id("auth0|maria123")
                .build();

        studentRepository.save(student1);
        studentRepository.save(student2);

        // When & Then: GET request by course
        mockMvc.perform(get("/api/v1/students/course/{course}", "Engenharia")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.content[0].course").value("Engenharia"));
    }

    @Test
    @DisplayName("Deve atualizar aluno - Integração completa")
    void shouldUpdateStudent() throws Exception {
        // Given: create a student
        Student student = Student.builder()
                .fullName("Aluno Original")
                .cpf("22233344405")
                .email("original@faculdade.edu.br")
                .phone("11987654399")
                .birthDate(LocalDate.of(2000, 1, 1))
                .registrationNumber("20231030")
                .course("Curso Original")
                .semester(1)
                .enrollmentDate(LocalDate.of(2023, 2, 1))
                .status(StudentStatus.ACTIVE)
                .auth0Id("auth0|original123")
                .build();
        Student savedStudent = studentRepository.save(student);

        // Prepare update request
        StudentRequestDTO updateRequest = StudentRequestDTO.builder()
                .fullName("Aluno Atualizado")
                .cpf("22233344405") // Same CPF
                .email("atualizado@faculdade.edu.br")
                .phone("11987654400")
                .birthDate(LocalDate.of(2000, 1, 1))
                .registrationNumber("20231030")
                .course("Curso Atualizado")
                .semester(2)
                .enrollmentDate(LocalDate.of(2023, 2, 1))
                .status(StudentStatus.ACTIVE)
                .build();

        String requestBody = objectMapper.writeValueAsString(updateRequest);

        // When & Then: PUT request to update student
        mockMvc.perform(put("/api/v1/students/{id}", savedStudent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("X-User-Id", "admin123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Aluno Atualizado"))
                .andExpect(jsonPath("$.data.email").value("atualizado@faculdade.edu.br"))
                .andExpect(jsonPath("$.data.course").value("Curso Atualizado"))
                .andExpect(jsonPath("$.data.semester").value(2));

        // Verify database was updated
        Student updatedStudent = studentRepository.findById(savedStudent.getId()).orElseThrow();
        assertThat(updatedStudent.getFullName()).isEqualTo("Aluno Atualizado");
        assertThat(updatedStudent.getCourse()).isEqualTo("Curso Atualizado");
        assertThat(updatedStudent.getSemester()).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve atualizar status do aluno - Integração completa")
    void shouldUpdateStudentStatus() throws Exception {
        // Given: create a student with ACTIVE status
        Student student = Student.builder()
                .fullName("Aluno Status")
                .cpf("33344455566")
                .email("status@faculdade.edu.br")
                .phone("11987654500")
                .birthDate(LocalDate.of(2000, 1, 1))
                .registrationNumber("20231040")
                .course("Curso Status")
                .semester(1)
                .enrollmentDate(LocalDate.of(2023, 2, 1))
                .status(StudentStatus.ACTIVE)
                .auth0Id("auth0|status123")
                .build();
        Student savedStudent = studentRepository.save(student);

        // When & Then: PATCH request to update status
        mockMvc.perform(patch("/api/v1/students/{id}/status", savedStudent.getId())
                        .param("status", "GRADUATED")
                        .header("X-User-Id", "admin123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("GRADUATED"));

        // Verify database was updated
        Student updatedStudent = studentRepository.findById(savedStudent.getId()).orElseThrow();
        assertThat(updatedStudent.getStatus()).isEqualTo(StudentStatus.GRADUATED);
    }

    @Test
    @DisplayName("Deve deletar aluno (soft delete) - Integração completa")
    void shouldDeleteStudent() throws Exception {
        // Given: create a student
        Student student = Student.builder()
                .fullName("Aluno Para Deletar")
                .cpf("44455566677")
                .email("deletar@faculdade.edu.br")
                .phone("11987654600")
                .birthDate(LocalDate.of(2000, 1, 1))
                .registrationNumber("20231050")
                .course("Curso Delete")
                .semester(1)
                .enrollmentDate(LocalDate.of(2023, 2, 1))
                .status(StudentStatus.ACTIVE)
                .auth0Id("auth0|delete123")
                .build();
        Student savedStudent = studentRepository.save(student);

        // When & Then: DELETE request
        mockMvc.perform(delete("/api/v1/students/{id}", savedStudent.getId())
                        .header("X-User-Id", "admin123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Verify soft delete - student should still exist but be marked as deleted
        Student deletedStudent = studentRepository.findById(savedStudent.getId()).orElseThrow();
        assertThat(deletedStudent.isDeleted()).isTrue();
        assertThat(deletedStudent.getDeletedAt()).isNotNull();

        // Verify student cannot be retrieved by normal queries
        mockMvc.perform(get("/api/v1/students/{id}", savedStudent.getId()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve restaurar aluno deletado - Integração completa")
    void shouldRestoreStudent() throws Exception {
        // Given: create and delete a student
        Student student = Student.builder()
                .fullName("Aluno Restaurar")
                .cpf("55566677788")
                .email("restaurar@faculdade.edu.br")
                .phone("11987654700")
                .birthDate(LocalDate.of(2000, 1, 1))
                .registrationNumber("20231060")
                .course("Curso Restore")
                .semester(1)
                .enrollmentDate(LocalDate.of(2023, 2, 1))
                .status(StudentStatus.ACTIVE)
                .auth0Id("auth0|restore123")
                .build();
        Student savedStudent = studentRepository.save(student);
        savedStudent.markAsDeleted("admin");
        studentRepository.save(savedStudent);

        // When & Then: POST request to restore
        mockMvc.perform(post("/api/v1/students/{id}/restore", savedStudent.getId())
                        .header("X-User-Id", "admin123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Verify student is restored
        Student restoredStudent = studentRepository.findById(savedStudent.getId()).orElseThrow();
        assertThat(restoredStudent.isDeleted()).isFalse();
        assertThat(restoredStudent.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("Deve obter estatísticas de alunos - Integração completa")
    void shouldGetStatistics() throws Exception {
        // Given: create students with different statuses
        Student active1 = createStudentWithStatus("stats1@faculdade.edu.br", "11111111111", StudentStatus.ACTIVE);
        Student active2 = createStudentWithStatus("stats2@faculdade.edu.br", "22222222222", StudentStatus.ACTIVE);
        Student inactive = createStudentWithStatus("stats3@faculdade.edu.br", "33333333333", StudentStatus.INACTIVE);
        Student graduated = createStudentWithStatus("stats4@faculdade.edu.br", "44444444444", StudentStatus.GRADUATED);
        Student suspended = createStudentWithStatus("stats5@faculdade.edu.br", "55555555555", StudentStatus.SUSPENDED);

        studentRepository.save(active1);
        studentRepository.save(active2);
        studentRepository.save(inactive);
        studentRepository.save(graduated);
        studentRepository.save(suspended);

        // When & Then: GET request for statistics
        mockMvc.perform(get("/api/v1/students/statistics")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.active").value(2))
                .andExpect(jsonPath("$.data.inactive").value(1))
                .andExpect(jsonPath("$.data.graduated").value(1))
                .andExpect(jsonPath("$.data.suspended").value(1))
                .andExpect(jsonPath("$.data.total").value(5));
    }

    @Test
    @DisplayName("Deve contar alunos por curso - Integração completa")
    void shouldCountStudentsByCourse() throws Exception {
        // Given: create students in different courses
        Student student1 = createStudentInCourse("count1@faculdade.edu.br", "66666666666", "Engenharia");
        Student student2 = createStudentInCourse("count2@faculdade.edu.br", "77777777777", "Engenharia");
        Student student3 = createStudentInCourse("count3@faculdade.edu.br", "88888888888", "Medicina");

        studentRepository.save(student1);
        studentRepository.save(student2);
        studentRepository.save(student3);

        // When & Then: GET request to count by course
        mockMvc.perform(get("/api/v1/students/count/course/{course}", "Engenharia")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(2));
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar aluno inexistente - Integração completa")
    void shouldReturn404WhenStudentNotFound() throws Exception {
        // When & Then: GET request for non-existent student
        mockMvc.perform(get("/api/v1/students/{id}", 99999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar erro de validação ao criar aluno com dados inválidos - Integração completa")
    void shouldReturnValidationError() throws Exception {
        // Given: invalid student request (missing required fields)
        StudentRequestDTO invalidRequest = StudentRequestDTO.builder()
                .fullName("AB") // Too short
                .email("invalid-email") // Invalid email
                .build();

        String requestBody = objectMapper.writeValueAsString(invalidRequest);

        // When & Then: POST request should fail validation
        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("X-User-Id", "admin123"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // Helper methods
    private Student createStudentWithStatus(String email, String cpf, StudentStatus status) {
        return Student.builder()
                .fullName("Student " + status)
                .cpf(cpf)
                .email(email)
                .phone("11987654000")
                .birthDate(LocalDate.of(2000, 1, 1))
                .registrationNumber(cpf.substring(0, 8))
                .course("Test Course")
                .semester(1)
                .enrollmentDate(LocalDate.of(2023, 2, 1))
                .status(status)
                .auth0Id("auth0|" + email.replace("@", ""))
                .build();
    }

    private Student createStudentInCourse(String email, String cpf, String course) {
        return Student.builder()
                .fullName("Student " + course)
                .cpf(cpf)
                .email(email)
                .phone("11987654000")
                .birthDate(LocalDate.of(2000, 1, 1))
                .registrationNumber(cpf.substring(0, 8))
                .course(course)
                .semester(1)
                .enrollmentDate(LocalDate.of(2023, 2, 1))
                .status(StudentStatus.ACTIVE)
                .auth0Id("auth0|" + email.replace("@", ""))
                .build();
    }
}

