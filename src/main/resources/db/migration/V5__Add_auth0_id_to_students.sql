-- Migration: Adiciona coluna auth0_id à tabela students
-- Descrição: Adiciona o campo auth0_id para vincular alunos ao serviço de autenticação

ALTER TABLE students
ADD COLUMN IF NOT EXISTS auth0_id VARCHAR(255);

-- Cria índice único para auth0_id
CREATE UNIQUE INDEX IF NOT EXISTS idx_student_auth0_id ON students(auth0_id);

-- Comentário na coluna
COMMENT ON COLUMN students.auth0_id IS 'Auth0 User ID - vínculo com o serviço de autenticação';

