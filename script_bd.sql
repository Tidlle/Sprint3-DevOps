-- =========================================================================
-- script_bd.sql — Clyvo VitalPet
-- Banco de dados: MySQL 8 (container Azure Container Instances)
-- Sprint 3 — DevOps Tools & Cloud Computing
--
-- Este arquivo reproduz, em um unico script, o schema (tabelas, colunas,
-- chaves primarias/estrangeiras e comentarios) e a massa de dados inicial
-- da aplicacao. E o mesmo conteudo aplicado automaticamente pelo Flyway a
-- partir de src/main/resources/db/migration/mysql/ quando a aplicacao sobe
-- com o profile "mysql".
-- =========================================================================

-- =========================================================================
-- PARTE 1: SCHEMA (tabelas do CORE da aplicacao)
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

-- =========================================================================
-- PARTE 2: MASSA DE DADOS (tabelas do CORE usadas no CRUD: tutores + pets)
-- =========================================================================

INSERT INTO clinicas (nome, endereco, cidade, estado, cep, telefone, email, cnpj, ativa, data_cadastro, data_atualizacao) VALUES
('VitalPet Centro', 'Av. Paulista, 1000', 'Sao Paulo', 'SP', '01310-100', '1130000001', 'contato@vitalpetcentro.com.br', '11222333000181', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('VitalPet Norte',  'Rua das Acacias, 250', 'Sao Paulo', 'SP', '02710-000', '1130000002', 'contato@vitalpetnorte.com.br', '11222333000262', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO veterinarios (nome, email, telefone, crmv, especialidade, ativo, data_cadastro, data_atualizacao, clinica_id) VALUES
('Ana Souza',      'ana.souza@vitalpet.com.br',      '11988880001', 'SP-12345', 'Clinica Geral', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
('Bruno Lima',     'bruno.lima@vitalpet.com.br',      '11988880002', 'SP-23456', 'Dermatologia',  TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
('Carla Nogueira', 'carla.nogueira@vitalpet.com.br',  '11988880003', 'SP-34567', 'Cardiologia',   TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2);

-- tutores: tabela do CORE usada no CRUD (relacionada 1:N com pets)
INSERT INTO tutores (nome, email, telefone, cpf, endereco, cidade, estado, cep, ativo, data_cadastro, data_atualizacao) VALUES
('Maria Oliveira', 'maria.oliveira@example.com', '11977770001', '11122233344', 'Rua das Flores, 45',   'Sao Paulo', 'SP', '01234-000', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Joao Pereira',   'joao.pereira@example.com',   '11977770002', '22233344455', 'Rua dos Girassois, 88', 'Sao Paulo', 'SP', '02345-000', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Fernanda Costa', 'fernanda.costa@example.com', '11977770003', '33344455566', 'Av. Ipiranga, 500',     'Sao Paulo', 'SP', '03456-000', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- pets: tabela do CORE usada no CRUD (relacionada N:1 com tutores via tutor_id)
INSERT INTO pets (nome, especie, raca, data_nascimento, sexo, peso, observacoes, ativo, data_cadastro, data_atualizacao, tutor_id) VALUES
('Rex',  'Cachorro', 'Labrador',        '2021-03-10', 'MACHO',  28.50, 'Sem restricoes alimentares.', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
('Mia',  'Gato',     'Siames',          '2022-07-22', 'FEMEA',  4.20,  'Historico de dermatite.',     TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
('Thor', 'Cachorro', 'Bulldog Frances', '2020-11-05', 'MACHO',  12.80, NULL,                          TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2),
('Luna', 'Gato',     'Persa',           '2023-01-15', 'FEMEA',  3.60,  'Vacinacao em dia.',           TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3);

INSERT INTO consultas (data_hora, tipo, sintomas, diagnostico, tratamento, status, valor, data_cadastro, data_atualizacao, pet_id, veterinario_id) VALUES
('2026-08-05 09:00:00', 'Rotina',     'Nenhum sintoma relatado.',        'Animal saudavel.',                 'Manter rotina de vacinacao.',        'CONCLUIDA', 150.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1),
('2026-08-07 14:30:00', 'Dermatologia','Coceira e vermelhidao na pele.', 'Dermatite alergica.',               'Pomada topica por 10 dias.',         'CONCLUIDA', 220.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 2),
('2026-08-10 11:15:00', 'Emergencia', 'Vomito e apatia.',                'Gastrite aguda.',                   'Dieta restrita e medicacao oral.',   'CONCLUIDA', 320.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 1),
('2026-08-25 10:00:00', 'Rotina',     NULL, NULL, NULL,                                                       'AGENDADA', 150.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4, 3),
('2026-08-20 16:00:00', 'Retorno',    NULL, NULL, NULL,                                                       'AGENDADA', 100.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1);

INSERT INTO acompanhamentos (status, data_inicio, data_fim, descricao, data_cadastro, data_atualizacao, consulta_id) VALUES
('ATIVO',     '2026-08-05 09:20:00', NULL,                    'Observar apetite e disposicao do Rex nos proximos dias.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
('CONCLUIDO', '2026-08-07 15:00:00', '2026-08-14 10:00:00',   'Dermatite da Mia tratada, pele cicatrizada.',             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2);

INSERT INTO alertas (tipo, titulo, descricao, prioridade, status, data_alerta, data_resolucao, data_cadastro, data_atualizacao, pet_id, acompanhamento_id) VALUES
('RETORNO',    'Retorno pos-consulta de Rex',       'Entrar em contato com o tutor para acompanhar a evolucao do pet apos a consulta.', 'MEDIA', 'PENDENTE', '2026-08-12 09:00:00', NULL,                    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1),
('MEDICACAO',  'Termino do tratamento de Mia',      'Confirmar com o tutor a finalizacao do uso da pomada topica.',                     'BAIXA', 'RESOLVIDO', '2026-08-14 09:00:00', '2026-08-14 10:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 2),
('VACINACAO',  'Vacina antirrabica de Thor vencendo','Vacina antirrabica de Thor vence em breve, agendar aplicacao.',                    'ALTA',  'PENDENTE', '2026-08-22 09:00:00', NULL,                    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, NULL);

-- usuarios de acesso a aplicacao web
-- senha para ambos (ambiente de demonstracao): VitalPet@123
INSERT INTO usuarios (nome, email, senha, role, ativo, data_cadastro, data_atualizacao, veterinario_id) VALUES
('Administrador Clyvo', 'admin@vitalpet.com.br',     '$2a$10$c2s2Kt.7cx3mtQtngGnM5ede0mjmJpPgN/w7qDExKlcdOMvZj8DTe', 'ADMIN',       TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
('Ana Souza',            'ana.souza@vitalpet.com.br', '$2a$10$c2s2Kt.7cx3mtQtngGnM5ede0mjmJpPgN/w7qDExKlcdOMvZj8DTe', 'VETERINARIO', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
