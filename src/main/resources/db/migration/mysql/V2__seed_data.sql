-- =========================================================================
-- V2: Massa de dados inicial para demonstracao da aplicacao (MySQL)
-- IDs nao sao informados explicitamente: o MySQL gera 1, 2, 3... na ordem
-- de insercao, o que e usado para montar os relacionamentos abaixo.
-- =========================================================================

-- clinicas (1, 2)
INSERT INTO clinicas (nome, endereco, cidade, estado, cep, telefone, email, cnpj, ativa, data_cadastro, data_atualizacao) VALUES
('VitalPet Centro', 'Av. Paulista, 1000', 'Sao Paulo', 'SP', '01310-100', '1130000001', 'contato@vitalpetcentro.com.br', '11222333000181', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('VitalPet Norte',  'Rua das Acacias, 250', 'Sao Paulo', 'SP', '02710-000', '1130000002', 'contato@vitalpetnorte.com.br', '11222333000262', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- veterinarios (1, 2, 3)
INSERT INTO veterinarios (nome, email, telefone, crmv, especialidade, ativo, data_cadastro, data_atualizacao, clinica_id) VALUES
('Ana Souza',      'ana.souza@vitalpet.com.br',      '11988880001', 'SP-12345', 'Clinica Geral', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
('Bruno Lima',     'bruno.lima@vitalpet.com.br',      '11988880002', 'SP-23456', 'Dermatologia',  TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
('Carla Nogueira', 'carla.nogueira@vitalpet.com.br',  '11988880003', 'SP-34567', 'Cardiologia',   TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2);

-- tutores (1, 2, 3)
INSERT INTO tutores (nome, email, telefone, cpf, endereco, cidade, estado, cep, ativo, data_cadastro, data_atualizacao) VALUES
('Maria Oliveira', 'maria.oliveira@example.com', '11977770001', '11122233344', 'Rua das Flores, 45',   'Sao Paulo', 'SP', '01234-000', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Joao Pereira',   'joao.pereira@example.com',   '11977770002', '22233344455', 'Rua dos Girassois, 88', 'Sao Paulo', 'SP', '02345-000', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Fernanda Costa', 'fernanda.costa@example.com', '11977770003', '33344455566', 'Av. Ipiranga, 500',     'Sao Paulo', 'SP', '03456-000', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- pets (1 Rex, 2 Mia, 3 Thor, 4 Luna)
INSERT INTO pets (nome, especie, raca, data_nascimento, sexo, peso, observacoes, ativo, data_cadastro, data_atualizacao, tutor_id) VALUES
('Rex',  'Cachorro', 'Labrador',        '2021-03-10', 'MACHO',  28.50, 'Sem restricoes alimentares.', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
('Mia',  'Gato',     'Siames',          '2022-07-22', 'FEMEA',  4.20,  'Historico de dermatite.',     TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
('Thor', 'Cachorro', 'Bulldog Frances', '2020-11-05', 'MACHO',  12.80, NULL,                          TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2),
('Luna', 'Gato',     'Persa',           '2023-01-15', 'FEMEA',  3.60,  'Vacinacao em dia.',           TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3);

-- consultas (1 a 5)
INSERT INTO consultas (data_hora, tipo, sintomas, diagnostico, tratamento, status, valor, data_cadastro, data_atualizacao, pet_id, veterinario_id) VALUES
('2026-08-05 09:00:00', 'Rotina',     'Nenhum sintoma relatado.',        'Animal saudavel.',                 'Manter rotina de vacinacao.',        'CONCLUIDA', 150.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1),
('2026-08-07 14:30:00', 'Dermatologia','Coceira e vermelhidao na pele.', 'Dermatite alergica.',               'Pomada topica por 10 dias.',         'CONCLUIDA', 220.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 2),
('2026-08-10 11:15:00', 'Emergencia', 'Vomito e apatia.',                'Gastrite aguda.',                   'Dieta restrita e medicacao oral.',   'CONCLUIDA', 320.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, 1),
('2026-08-25 10:00:00', 'Rotina',     NULL, NULL, NULL,                                                       'AGENDADA', 150.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 4, 3),
('2026-08-20 16:00:00', 'Retorno',    NULL, NULL, NULL,                                                       'AGENDADA', 100.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1);

-- acompanhamentos (1, 2) - gerados ao finalizar as consultas 1 e 2
INSERT INTO acompanhamentos (status, data_inicio, data_fim, descricao, data_cadastro, data_atualizacao, consulta_id) VALUES
('ATIVO',     '2026-08-05 09:20:00', NULL,                    'Observar apetite e disposicao do Rex nos proximos dias.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1),
('CONCLUIDO', '2026-08-07 15:00:00', '2026-08-14 10:00:00',   'Dermatite da Mia tratada, pele cicatrizada.',             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2);

-- alertas (1, 2, 3)
INSERT INTO alertas (tipo, titulo, descricao, prioridade, status, data_alerta, data_resolucao, data_cadastro, data_atualizacao, pet_id, acompanhamento_id) VALUES
('RETORNO',    'Retorno pos-consulta de Rex',       'Entrar em contato com o tutor para acompanhar a evolucao do pet apos a consulta.', 'MEDIA', 'PENDENTE', '2026-08-12 09:00:00', NULL,                    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1, 1),
('MEDICACAO',  'Termino do tratamento de Mia',      'Confirmar com o tutor a finalizacao do uso da pomada topica.',                     'BAIXA', 'RESOLVIDO', '2026-08-14 09:00:00', '2026-08-14 10:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 2, 2),
('VACINACAO',  'Vacina antirrabica de Thor vencendo','Vacina antirrabica de Thor vence em breve, agendar aplicacao.',                    'ALTA',  'PENDENTE', '2026-08-22 09:00:00', NULL,                    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 3, NULL);

-- usuarios de acesso a aplicacao web
-- senha para ambos (ambiente de demonstracao): VitalPet@123
INSERT INTO usuarios (nome, email, senha, role, ativo, data_cadastro, data_atualizacao, veterinario_id) VALUES
('Administrador Clyvo', 'admin@vitalpet.com.br',     '$2a$10$c2s2Kt.7cx3mtQtngGnM5ede0mjmJpPgN/w7qDExKlcdOMvZj8DTe', 'ADMIN',       TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, NULL),
('Ana Souza',            'ana.souza@vitalpet.com.br', '$2a$10$c2s2Kt.7cx3mtQtngGnM5ede0mjmJpPgN/w7qDExKlcdOMvZj8DTe', 'VETERINARIO', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 1);
