-- ===============================================
-- Migração V4: Modelo Avançado de Gestão Escolar
-- Cria tabelas: addresses, guardians, emergency_contacts,
-- documents, medical_records, enrollment_history, academic_records
-- ===============================================

-- ===============================================
-- Tabela: addresses
-- Endereços completos (reutilizáveis)
-- ===============================================
CREATE TABLE addresses (
    id BIGSERIAL PRIMARY KEY,
    street VARCHAR(255) NOT NULL,
    number VARCHAR(20) NOT NULL,
    complement VARCHAR(100),
    neighborhood VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(2) NOT NULL,
    zipcode VARCHAR(8) NOT NULL,
    country VARCHAR(100) DEFAULT 'Brasil',
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    is_primary BOOLEAN DEFAULT TRUE,
    address_type VARCHAR(20) DEFAULT 'RESIDENTIAL',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(255)
);

CREATE INDEX idx_address_zipcode ON addresses(zipcode);
CREATE INDEX idx_address_city_state ON addresses(city, state);

-- ===============================================
-- Adiciona coluna address_id na tabela students
-- ===============================================
ALTER TABLE students ADD COLUMN address_id BIGINT;
ALTER TABLE students ADD CONSTRAINT fk_student_address FOREIGN KEY (address_id) REFERENCES addresses(id) ON DELETE SET NULL;
CREATE INDEX idx_student_address ON students(address_id);

-- ===============================================
-- Tabela: guardians
-- Responsáveis/Tutores dos alunos
-- ===============================================
CREATE TABLE guardians (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    rg VARCHAR(20),
    email VARCHAR(255) NOT NULL,
    phone_primary VARCHAR(11) NOT NULL,
    phone_secondary VARCHAR(11),
    birth_date DATE,
    relationship VARCHAR(20) NOT NULL,
    occupation VARCHAR(100),
    workplace VARCHAR(100),
    work_phone VARCHAR(11),
    address_id BIGINT,
    is_primary_guardian BOOLEAN DEFAULT FALSE,
    is_financial_responsible BOOLEAN DEFAULT FALSE,
    can_pick_up_student BOOLEAN DEFAULT TRUE,
    can_authorize_medical_treatment BOOLEAN DEFAULT FALSE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(255),
    CONSTRAINT fk_guardian_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE,
    CONSTRAINT fk_guardian_address FOREIGN KEY (address_id) REFERENCES addresses(id) ON DELETE SET NULL
);

CREATE INDEX idx_guardian_cpf ON guardians(cpf);
CREATE INDEX idx_guardian_email ON guardians(email);
CREATE INDEX idx_guardian_student ON guardians(student_id);
CREATE INDEX idx_guardian_relationship ON guardians(relationship);

-- ===============================================
-- Tabela: emergency_contacts
-- Contatos de emergência dos alunos
-- ===============================================
CREATE TABLE emergency_contacts (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone_primary VARCHAR(11) NOT NULL,
    phone_secondary VARCHAR(11),
    email VARCHAR(255),
    relationship VARCHAR(50) NOT NULL,
    priority_order INTEGER NOT NULL,
    is_authorized_to_pick_up BOOLEAN DEFAULT FALSE,
    address VARCHAR(500),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(255),
    CONSTRAINT fk_emergency_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE INDEX idx_emergency_student ON emergency_contacts(student_id);
CREATE INDEX idx_emergency_priority ON emergency_contacts(priority_order);

-- ===============================================
-- Tabela: documents
-- Documentos dos alunos (RG, CPF, Histórico, etc)
-- ===============================================
CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    document_type VARCHAR(30) NOT NULL,
    document_number VARCHAR(100) NOT NULL,
    issuing_authority VARCHAR(100),
    issue_date DATE,
    expiration_date DATE,
    file_url VARCHAR(255),
    file_name VARCHAR(100),
    file_type VARCHAR(50),
    file_size BIGINT,
    is_verified BOOLEAN DEFAULT FALSE,
    verified_at DATE,
    verified_by VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(255),
    CONSTRAINT fk_document_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE INDEX idx_document_student ON documents(student_id);
CREATE INDEX idx_document_type ON documents(document_type);
CREATE INDEX idx_document_number ON documents(document_number);
CREATE INDEX idx_document_verified ON documents(is_verified);
CREATE INDEX idx_document_expiration ON documents(expiration_date);

-- ===============================================
-- Tabela: medical_records
-- Fichas médicas dos alunos
-- ===============================================
CREATE TABLE medical_records (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL UNIQUE,
    blood_type VARCHAR(5),
    has_allergies BOOLEAN DEFAULT FALSE,
    allergies TEXT,
    has_chronic_diseases BOOLEAN DEFAULT FALSE,
    chronic_diseases TEXT,
    has_disabilities BOOLEAN DEFAULT FALSE,
    disabilities TEXT,
    uses_continuous_medication BOOLEAN DEFAULT FALSE,
    medications TEXT,
    has_dietary_restrictions BOOLEAN DEFAULT FALSE,
    dietary_restrictions TEXT,
    has_special_needs BOOLEAN DEFAULT FALSE,
    special_needs TEXT,
    primary_doctor_name VARCHAR(255),
    primary_doctor_phone VARCHAR(11),
    preferred_hospital VARCHAR(255),
    health_insurance_number VARCHAR(100),
    health_insurance_provider VARCHAR(100),
    vaccination_card_up_to_date BOOLEAN DEFAULT FALSE,
    requires_special_care BOOLEAN DEFAULT FALSE,
    special_care_instructions TEXT,
    emergency_procedures TEXT,
    additional_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(255),
    CONSTRAINT fk_medical_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE INDEX idx_medical_student ON medical_records(student_id);
CREATE INDEX idx_medical_allergies ON medical_records(has_allergies);
CREATE INDEX idx_medical_special_needs ON medical_records(has_special_needs);

-- ===============================================
-- Tabela: enrollment_history
-- Histórico de matrículas dos alunos
-- ===============================================
CREATE TABLE enrollment_history (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL,
    academic_year INTEGER NOT NULL,
    semester INTEGER NOT NULL,
    course_name VARCHAR(255) NOT NULL,
    course_code VARCHAR(50),
    class_name VARCHAR(100),
    class_code VARCHAR(50),
    shift VARCHAR(20),
    start_date DATE NOT NULL,
    end_date DATE,
    enrollment_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    total_subjects INTEGER,
    passed_subjects INTEGER,
    failed_subjects INTEGER,
    average_grade DECIMAL(5, 2),
    attendance_percentage DECIMAL(5, 2),
    credits_earned INTEGER,
    total_credits INTEGER,
    is_repeating_year BOOLEAN DEFAULT FALSE,
    transfer_reason TEXT,
    dropout_reason TEXT,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(255),
    CONSTRAINT fk_enrollment_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE INDEX idx_enrollment_student ON enrollment_history(student_id);
CREATE INDEX idx_enrollment_period ON enrollment_history(academic_year, semester);
CREATE INDEX idx_enrollment_status ON enrollment_history(enrollment_status);
CREATE INDEX idx_enrollment_course ON enrollment_history(course_name);

-- ===============================================
-- Tabela: academic_records
-- Registros acadêmicos consolidados dos alunos
-- ===============================================
CREATE TABLE academic_records (
    id BIGSERIAL PRIMARY KEY,
    student_id BIGINT NOT NULL UNIQUE,
    total_credits_earned INTEGER DEFAULT 0,
    total_credits_required INTEGER,
    current_gpa DECIMAL(4, 2),
    cumulative_gpa DECIMAL(4, 2),
    total_subjects_taken INTEGER DEFAULT 0,
    total_subjects_passed INTEGER DEFAULT 0,
    total_subjects_failed INTEGER DEFAULT 0,
    total_semesters_completed INTEGER DEFAULT 0,
    overall_attendance_rate DECIMAL(5, 2),
    has_academic_warnings BOOLEAN DEFAULT FALSE,
    warning_count INTEGER DEFAULT 0,
    last_warning_date DATE,
    has_disciplinary_actions BOOLEAN DEFAULT FALSE,
    disciplinary_actions_count INTEGER DEFAULT 0,
    last_disciplinary_action_date DATE,
    is_on_probation BOOLEAN DEFAULT FALSE,
    probation_reason TEXT,
    has_honors BOOLEAN DEFAULT FALSE,
    honors_list TEXT,
    has_scholarships BOOLEAN DEFAULT FALSE,
    scholarship_details TEXT,
    expected_graduation_date DATE,
    actual_graduation_date DATE,
    academic_standing VARCHAR(20) DEFAULT 'GOOD_STANDING',
    extracurricular_activities TEXT,
    achievements TEXT,
    research_projects TEXT,
    publications TEXT,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(255),
    CONSTRAINT fk_academic_student FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

CREATE INDEX idx_academic_student ON academic_records(student_id);
CREATE INDEX idx_academic_standing ON academic_records(academic_standing);
CREATE INDEX idx_academic_gpa ON academic_records(current_gpa);
CREATE INDEX idx_academic_probation ON academic_records(is_on_probation);
CREATE INDEX idx_academic_honors ON academic_records(has_honors);

-- ===============================================
-- Comentários nas tabelas
-- ===============================================
COMMENT ON TABLE addresses IS 'Endereços completos reutilizáveis para estudantes e responsáveis';
COMMENT ON TABLE guardians IS 'Responsáveis/Tutores dos alunos com múltiplos relacionamentos possíveis';
COMMENT ON TABLE emergency_contacts IS 'Contatos de emergência ordenados por prioridade';
COMMENT ON TABLE documents IS 'Documentos dos alunos (RG, CPF, Certidão, Histórico Escolar, etc)';
COMMENT ON TABLE medical_records IS 'Fichas médicas com informações de saúde dos alunos';
COMMENT ON TABLE enrollment_history IS 'Histórico completo de matrículas e desempenho por período';
COMMENT ON TABLE academic_records IS 'Registro acadêmico consolidado com performance geral do aluno';

