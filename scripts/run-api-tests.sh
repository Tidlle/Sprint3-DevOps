#!/usr/bin/env bash
set -e

BASE_URL="${BASE_URL:-http://localhost:8080}"

echo "Testando API em: ${BASE_URL}"
echo "Este script demonstra CRUD com pelo menos 2 inserts."
echo ""

echo "[1] POST - Criando clínica 1"
CLINICA_1=$(curl -s -X POST "${BASE_URL}/api/clinicas" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "VitalPet Vila Mariana",
    "endereco": "Rua das Flores, 100",
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "04000000",
    "telefone": "11999990000",
    "email": "contato@vitalpet.com",
    "cnpj": "12345678000199"
  }' | jq -r '.id')
echo "Clínica 1 criada com ID: ${CLINICA_1}"

echo "[2] POST - Criando clínica 2"
CLINICA_2=$(curl -s -X POST "${BASE_URL}/api/clinicas" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "VitalPet Tatuapé",
    "endereco": "Rua dos Animais, 200",
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "03000000",
    "telefone": "11888880000",
    "email": "tatuape@vitalpet.com",
    "cnpj": "22345678000198"
  }' | jq -r '.id')
echo "Clínica 2 criada com ID: ${CLINICA_2}"

echo "[3] GET - Listando clínicas"
curl -s "${BASE_URL}/api/clinicas?size=10" | jq

echo "[4] GET - Buscando clínica 1 por ID"
curl -s "${BASE_URL}/api/clinicas/${CLINICA_1}" | jq

echo "[5] PUT - Atualizando clínica 1"
curl -s -X PUT "${BASE_URL}/api/clinicas/${CLINICA_1}" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "VitalPet Vila Mariana Atualizada",
    "endereco": "Rua das Flores, 150",
    "cidade": "São Paulo",
    "estado": "SP",
    "cep": "04000000",
    "telefone": "11911112222",
    "email": "contato.atualizado@vitalpet.com",
    "cnpj": "12345678000199"
  }' | jq

echo "[6] DELETE - Desativando clínica 2"
curl -s -i -X DELETE "${BASE_URL}/api/clinicas/${CLINICA_2}"

echo ""
echo "[7] GET - Conferindo clínicas após DELETE lógico"
curl -s "${BASE_URL}/api/clinicas?size=10" | jq

echo ""
echo "Testes finalizados."
echo "No H2 Console, use a URL jdbc:h2:tcp://localhost:1521/./vitalpetdb e rode: SELECT * FROM CLINICAS;"
