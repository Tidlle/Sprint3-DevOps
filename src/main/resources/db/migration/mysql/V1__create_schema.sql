-- =========================================================================
-- V1: Schema inicial do VitalPet (clinicas, veterinarios, tutores, pets,
-- consultas, acompanhamentos, alertas e usuarios de acesso ao sistema)
-- Versao MySQL (comentarios de tabela/coluna no formato inline do MySQL)
-- =========================================================================

CREATE TABLE clinicas (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome              VARCHAR(120) NOT NULL,
    endereco          VARCHAR(180) NOT NULL,
    cidade            VARCHAR(80)  NOT NULL,
    estado            VARCHAR(2)   NOT NULL,
    cep               VARCHAR(9)   NOT NULL,
    telefone          VARCHAR(20)  NOT NULL,
    email             VARCHAR(120) NOT NULL,
    cnpj              VARCHAR(14)  NOT NULL COMMENT 'CNPJ sem pontuacao, 14 digitos',
    ativa             BOOLEAN      NOT NULL DEFAULT TRUE COMMENT 'Indica se a clinica esta ativa no sistema',
    data_cadastro     TIMESTAMP    NOT NULL,
    data_atualizacao  TIMESTAMP    NOT NULL,
    CONSTRAINT uk_clinica_cnpj UNIQUE (cnpj)
) COMMENT='Unidades veterinarias que utilizam o sistema';

CREATE TABLE veterinarios (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome              VARCHAR(120) NOT NULL,
    email             VARCHAR(120) NOT NULL,
    telefone          VARCHAR(20)  NOT NULL,
    crmv              VARCHAR(12)  NOT NULL COMMENT 'Registro profissional no formato UF-0000',
    especialidade     VARCHAR(80)  NOT NULL,
    ativo             BOOLEAN      NOT NULL DEFAULT TRUE,
    data_cadastro     TIMESTAMP    NOT NULL,
    data_atualizacao  TIMESTAMP    NOT NULL,
    clinica_id        BIGINT       NOT NULL,
    CONSTRAINT uk_veterinario_crmv UNIQUE (crmv),
    CONSTRAINT uk_veterinario_email UNIQUE (email),
    CONSTRAINT fk_veterinario_clinica FOREIGN KEY (clinica_id) REFERENCES clinicas (id)
) COMMENT='Profissionais responsaveis pelos atendimentos, vinculados a uma clinica';

CREATE TABLE tutores (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome              VARCHAR(120) NOT NULL,
    email             VARCHAR(120) NOT NULL,
    telefone          VARCHAR(20)  NOT NULL,
    cpf               VARCHAR(11)  NOT NULL COMMENT 'CPF sem pontuacao, 11 digitos',
    endereco          VARCHAR(180),
    cidade            VARCHAR(80),
    estado            VARCHAR(2),
    cep               VARCHAR(9),
    ativo             BOOLEAN      NOT NULL DEFAULT TRUE,
    data_cadastro     TIMESTAMP    NOT NULL,
    data_atualizacao  TIMESTAMP    NOT NULL,
    CONSTRAINT uk_tutor_cpf UNIQUE (cpf),
    CONSTRAINT uk_tutor_email UNIQUE (email)
) COMMENT='Responsaveis pelos pets acompanhados no sistema';

CREATE TABLE pets (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome              VARCHAR(80)   NOT NULL,
    especie           VARCHAR(50)   NOT NULL,
    raca              VARCHAR(80),
    data_nascimento   DATE,
    sexo              VARCHAR(20)   NOT NULL,
    peso              NUMERIC(6,2)  NOT NULL COMMENT 'Peso do pet em quilogramas',
    observacoes       VARCHAR(500),
    ativo             BOOLEAN       NOT NULL DEFAULT TRUE,
    data_cadastro     TIMESTAMP     NOT NULL,
    data_atualizacao  TIMESTAMP     NOT NULL,
    tutor_id          BIGINT        NOT NULL,
    CONSTRAINT fk_pet_tutor FOREIGN KEY (tutor_id) REFERENCES tutores (id)
) COMMENT='Animais acompanhados pelo sistema, vinculados a um tutor';

CREATE TABLE consultas (
    id                BIGINT        AUTO_INCREMENT PRIMARY KEY,
    data_hora         TIMESTAMP     NOT NULL,
    tipo              VARCHAR(60)   NOT NULL,
    sintomas          VARCHAR(1000),
    diagnostico       VARCHAR(1000),
    tratamento        VARCHAR(1000),
    status            VARCHAR(30)   NOT NULL DEFAULT 'AGENDADA' COMMENT 'AGENDADA, CONCLUIDA ou CANCELADA',
    valor             NUMERIC(10,2) NOT NULL,
    data_cadastro     TIMESTAMP     NOT NULL,
    data_atualizacao  TIMESTAMP     NOT NULL,
    pet_id            BIGINT        NOT NULL,
    veterinario_id    BIGINT        NOT NULL,
    CONSTRAINT fk_consulta_pet FOREIGN KEY (pet_id) REFERENCES pets (id),
    CONSTRAINT fk_consulta_veterinario FOREIGN KEY (veterinario_id) REFERENCES veterinarios (id)
) COMMENT='Atendimentos realizados a um pet por um veterinario';

CREATE TABLE acompanhamentos (
    id                BIGINT       AUTO_INCREMENT PRIMARY KEY,
    status            VARCHAR(30)  NOT NULL DEFAULT 'ATIVO' COMMENT 'ATIVO, CONCLUIDO ou CANCELADO',
    data_inicio       TIMESTAMP    NOT NULL,
    data_fim          TIMESTAMP    NULL,
    descricao         VARCHAR(1000) NOT NULL,
    data_cadastro     TIMESTAMP    NOT NULL,
    data_atualizacao  TIMESTAMP    NOT NULL,
    consulta_id       BIGINT       NOT NULL,
    CONSTRAINT uk_acompanhamento_consulta UNIQUE (consulta_id),
    CONSTRAINT fk_acompanhamento_consulta FOREIGN KEY (consulta_id) REFERENCES consultas (id)
) COMMENT='Continuidade clinica criada apos a finalizacao de uma consulta';

CREATE TABLE alertas (
    id                BIGINT       AUTO_INCREMENT PRIMARY KEY,
    tipo              VARCHAR(60)  NOT NULL,
    titulo            VARCHAR(120) NOT NULL,
    descricao         VARCHAR(1000) NOT NULL,
    prioridade        VARCHAR(20)  NOT NULL COMMENT 'BAIXA, MEDIA ou ALTA',
    status            VARCHAR(30)  NOT NULL DEFAULT 'PENDENTE' COMMENT 'PENDENTE, RESOLVIDO ou CANCELADO',
    data_alerta       TIMESTAMP    NOT NULL,
    data_resolucao    TIMESTAMP    NULL,
    data_cadastro     TIMESTAMP    NOT NULL,
    data_atualizacao  TIMESTAMP    NOT NULL,
    pet_id            BIGINT       NOT NULL,
    acompanhamento_id BIGINT,
    CONSTRAINT fk_alerta_pet FOREIGN KEY (pet_id) REFERENCES pets (id),
    CONSTRAINT fk_alerta_acompanhamento FOREIGN KEY (acompanhamento_id) REFERENCES acompanhamentos (id)
) COMMENT='Retornos, vacinas e demais acoes de acompanhamento a serem tratadas';

CREATE TABLE usuarios (
    id                BIGINT       AUTO_INCREMENT PRIMARY KEY,
    nome              VARCHAR(120) NOT NULL,
    email             VARCHAR(120) NOT NULL,
    senha             VARCHAR(100) NOT NULL COMMENT 'Hash BCrypt da senha',
    role              VARCHAR(20)  NOT NULL COMMENT 'Perfil de acesso: ADMIN ou VETERINARIO',
    ativo             BOOLEAN      NOT NULL DEFAULT TRUE,
    data_cadastro     TIMESTAMP    NOT NULL,
    data_atualizacao  TIMESTAMP    NOT NULL,
    veterinario_id    BIGINT,
    CONSTRAINT uk_usuario_email UNIQUE (email),
    CONSTRAINT fk_usuario_veterinario FOREIGN KEY (veterinario_id) REFERENCES veterinarios (id)
) COMMENT='Contas de acesso a aplicacao web (Spring Security)';

-- Indices de apoio as consultas mais frequentes da aplicacao
CREATE INDEX idx_clinica_nome ON clinicas (nome);
CREATE INDEX idx_veterinario_nome ON veterinarios (nome);
CREATE INDEX idx_veterinario_clinica ON veterinarios (clinica_id);
CREATE INDEX idx_pet_nome ON pets (nome);
CREATE INDEX idx_pet_tutor ON pets (tutor_id);
CREATE INDEX idx_consulta_pet ON consultas (pet_id);
CREATE INDEX idx_consulta_veterinario ON consultas (veterinario_id);
CREATE INDEX idx_consulta_status ON consultas (status);
CREATE INDEX idx_alerta_status ON alertas (status);
CREATE INDEX idx_alerta_pet ON alertas (pet_id);
