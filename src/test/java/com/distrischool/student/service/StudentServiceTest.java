package com.distrischool.student.service;

import com.distrischool.student.dto.StudentRequestDTO;
import com.distrischool.student.dto.StudentResponseDTO;
import com.distrischool.student.dto.auth.ApiResponse;
import com.distrischool.student.dto.auth.AuthResponse;
import com.distrischool.student.dto.auth.UserResponse;
import com.distrischool.student.entity.Student;
import com.distrischool.student.entity.Student.StudentStatus;
import com.distrischool.student.exception.BusinessException;
import com.distrischool.student.exception.ResourceNotFoundException;
import com.distrischool.student.feign.AuthServiceClient;
import com.distrischool.student.kafka.EventProducer;
import com.distrischool.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para StudentService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService - Testes Unitários")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private EventProducer eventProducer;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private StudentService studentService;

    private StudentRequestDTO validRequest;
    private Student validStudent;

    @BeforeEach
    void setUp() {
        validRequest = StudentRequestDTO.builder()
                .fullName("João Silva Santos")
                .cpf("12345678901")
                .email("joao.silva@faculdade.edu.br")
                .phone("11987654321")
                .birthDate(LocalDate.of(2000, 5, 15))
                .course("Ciência da Computação")
                .semester(3)
                .enrollmentDate(LocalDate.of(2023, 2, 1))
                .build();

        validStudent = Student.builder()
                .id(1L)
                .fullName("João Silva Santos")
                .cpf("12345678901")
                .email("joao.silva@faculdade.edu.br")
                .phone("11987654321")
                .birthDate(LocalDate.of(2000, 5, 15))
                .registrationNumber("20241001")
                .course("Ciência da Computação")
                .semester(3)
                .enrollmentDate(LocalDate.of(2023, 2, 1))
                .status(StudentStatus.ACTIVE)
                .build();
        AuthResponse authResponse = AuthResponse.builder()
                .user(UserResponse.builder().auth0Id("auth0|student-123").build())
                .build();
        ApiResponse<AuthResponse> apiResponse = ApiResponse.<AuthResponse>builder()
                .success(true)
                .data(authResponse)
                .build();
        when(authServiceClient.createUser(anyString(), any())).thenReturn(apiResponse);
    }

    @Test
    @DisplayName("Deve criar um aluno com sucesso")
    void shouldCreateStudentSuccessfully() {
        // Arrange
        when(studentRepository.findByCpf(any())).thenReturn(Optional.empty());
        when(studentRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(studentRepository.save(any(Student.class))).thenReturn(validStudent);

        // Act
        StudentResponseDTO result = studentService.createStudent(validRequest, "admin", "Bearer mock-token");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFullName()).isEqualTo("João Silva Santos");
        assertThat(result.getEmail()).isEqualTo("joao.silva@faculdade.edu.br");
        verify(studentRepository, times(1)).save(any(Student.class));
        verify(eventProducer, times(1)).send(anyString(), any());
    }

    @Test
    @DisplayName("Não deve criar aluno com CPF duplicado")
    void shouldNotCreateStudentWithDuplicateCpf() {
        // Arrange
        when(studentRepository.findByCpf(validRequest.getCpf()))
                .thenReturn(Optional.of(validStudent));

        // Act & Assert
        assertThatThrownBy(() -> studentService.createStudent(validRequest, "admin", "Bearer mock-token"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("CPF");

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Não deve criar aluno com email duplicado")
    void shouldNotCreateStudentWithDuplicateEmail() {
        // Arrange
        when(studentRepository.findByCpf(any())).thenReturn(Optional.empty());
        when(studentRepository.findByEmail(validRequest.getEmail()))
                .thenReturn(Optional.of(validStudent));

        // Act & Assert
        assertThatThrownBy(() -> studentService.createStudent(validRequest, "admin", "Bearer mock-token"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("email");

        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Deve buscar aluno por ID com sucesso")
    void shouldGetStudentByIdSuccessfully() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(validStudent));

        // Act
        StudentResponseDTO result = studentService.getStudentById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFullName()).isEqualTo("João Silva Santos");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar aluno inexistente")
    void shouldThrowExceptionWhenStudentNotFound() {
        // Arrange
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> studentService.getStudentById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("não encontrado");
    }

    @Test
    @DisplayName("Deve atualizar aluno com sucesso")
    void shouldUpdateStudentSuccessfully() {
        // Arrange
        StudentRequestDTO updateRequest = StudentRequestDTO.builder()
                .fullName("João Silva Santos Jr")
                .cpf("12345678901")
                .email("joao.silva@faculdade.edu.br")
                .phone("11987654321")
                .birthDate(LocalDate.of(2000, 5, 15))
                .course("Ciência da Computação")
                .semester(4)
                .enrollmentDate(LocalDate.of(2023, 2, 1))
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(validStudent));
        when(studentRepository.findByCpf(any())).thenReturn(Optional.of(validStudent));
        when(studentRepository.findByEmail(any())).thenReturn(Optional.of(validStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(validStudent);

        // Act
        StudentResponseDTO result = studentService.updateStudent(1L, updateRequest, "admin");

        // Assert
        assertThat(result).isNotNull();
        verify(studentRepository, times(1)).save(any(Student.class));
        verify(eventProducer, times(1)).send(anyString(), any());
    }

    @Test
    @DisplayName("Deve atualizar status do aluno")
    void shouldUpdateStudentStatus() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(validStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(validStudent);

        // Act
        StudentResponseDTO result = studentService.updateStudentStatus(1L, StudentStatus.GRADUATED, "admin");

        // Assert
        assertThat(result).isNotNull();
        verify(studentRepository, times(1)).save(any(Student.class));
        verify(eventProducer, times(1)).send(anyString(), any());
    }

    @Test
    @DisplayName("Deve deletar aluno (soft delete)")
    void shouldDeleteStudent() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(validStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(validStudent);

        // Act
        studentService.deleteStudent(1L, "admin");

        // Assert
        verify(studentRepository, times(1)).save(any(Student.class));
        verify(eventProducer, times(1)).send(anyString(), any());
    }

    @Test
    @DisplayName("Deve listar alunos com paginação")
    void shouldListStudentsWithPagination() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Student> studentPage = new PageImpl<>(Arrays.asList(validStudent));
        when(studentRepository.findAll(pageable)).thenReturn(studentPage);

        // Act
        Page<?> result = studentService.getAllStudents(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("Deve contar alunos por status")
    void shouldCountStudentsByStatus() {
        // Arrange
        when(studentRepository.countByStatus(StudentStatus.ACTIVE)).thenReturn(10L);

        // Act
        long count = studentService.countStudentsByStatus(StudentStatus.ACTIVE);

        // Assert
        assertThat(count).isEqualTo(10L);
    }
}
