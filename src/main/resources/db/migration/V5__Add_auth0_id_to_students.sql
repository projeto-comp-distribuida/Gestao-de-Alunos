-- ===============================================
-- Migração V5: Adiciona coluna auth0_id na tabela students
-- Referência ao usuário criado no serviço de autenticação (Auth0)
-- ===============================================

-- Adiciona coluna auth0_id (nullable, pois pode não existir para alunos criados antes da integração)
ALTER TABLE students 
ADD COLUMN auth0_id VARCHAR(255);

-- Adiciona constraint UNIQUE com nome explícito (cria automaticamente um índice único)
ALTER TABLE students 
ADD CONSTRAINT uk_student_auth0_id UNIQUE (auth0_id);

-- Comentário na coluna
COMMENT ON COLUMN students.auth0_id IS 'Auth0 User ID - Referência ao usuário criado no serviço de autenticação quando o aluno é criado';

