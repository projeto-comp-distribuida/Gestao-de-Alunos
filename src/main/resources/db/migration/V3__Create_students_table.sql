-- Migration para criar a tabela de alunos (students)
-- Versão 3 - Sistema de Gerenciamento de Alunos

CREATE TABLE students (
    id BIGSERIAL PRIMARY KEY,

    -- Dados Pessoais
    full_name VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(11),
    birth_date DATE NOT NULL,

    -- Dados Acadêmicos
    registration_number VARCHAR(50) NOT NULL UNIQUE,
    course VARCHAR(255) NOT NULL,
    semester INTEGER NOT NULL CHECK (semester >= 1 AND semester <= 20),
    enrollment_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    -- Endereço
    address_street VARCHAR(255),
    address_number VARCHAR(20),
    address_complement VARCHAR(100),
    address_neighborhood VARCHAR(100),
    address_city VARCHAR(100),
    address_state VARCHAR(2),
    address_zipcode VARCHAR(8),

    -- Contato de Emergência
    emergency_contact_name VARCHAR(255),
    emergency_contact_phone VARCHAR(11),
    emergency_contact_relationship VARCHAR(50),

    -- Observações
    notes TEXT,

    -- Auditoria
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    -- Soft Delete
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(255)
);

-- Índices para performance
CREATE INDEX idx_student_registration ON students(registration_number);
CREATE INDEX idx_student_email ON students(email);
CREATE INDEX idx_student_cpf ON students(cpf);
CREATE INDEX idx_student_status ON students(status);
CREATE INDEX idx_student_course ON students(course);
CREATE INDEX idx_student_course_semester ON students(course, semester);
CREATE INDEX idx_student_full_name ON students(full_name);
CREATE INDEX idx_student_deleted_at ON students(deleted_at);

-- Comentários nas colunas
COMMENT ON TABLE students IS 'Tabela de alunos do sistema de gestão da faculdade';
COMMENT ON COLUMN students.status IS 'Status do aluno: ACTIVE, INACTIVE, GRADUATED, SUSPENDED, TRANSFERRED, DROPPED';
COMMENT ON COLUMN students.registration_number IS 'Número de matrícula único do aluno (formato: ANOSMESTRE9999)';
COMMENT ON COLUMN students.deleted_at IS 'Data de exclusão lógica (soft delete)';

