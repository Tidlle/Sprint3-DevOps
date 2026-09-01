#!/usr/bin/env bash
set -e

# Demonstra CRUD completo (Inclusao, Alteracao, Exclusao, Consulta) sobre as
# duas tabelas do CORE usadas na entrega da Sprint 3 (DevOps): tutores e
# pets, relacionadas entre si por pets.tutor_id -> tutores.id.

BASE_URL="${BASE_URL:-http://localhost:8080}"

echo "Testando API em: ${BASE_URL}"
echo ""

echo "[1] POST - Criando tutor"
TUTOR_ID=$(curl -s -X POST "${BASE_URL}/api/tutores" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Ricardo Alves",
    "email": "ricardo.alves@example.com",
    "telefone": "11966665555",
    "cpf": "44455566677",
    "endereco": "Rua Nova, 123",
    "cidade": "Sao Paulo",
    "estado": "SP",
    "cep": "05000000"
  }' | jq -r '.id')
echo "Tutor criado com ID: ${TUTOR_ID}"

echo "[2] POST - Criando pet vinculado ao tutor"
PET_ID=$(curl -s -X POST "${BASE_URL}/api/pets" \
  -H "Content-Type: application/json" \
  -d "{
    \"nome\": \"Nina\",
    \"especie\": \"Cachorro\",
    \"raca\": \"Vira-lata\",
    \"dataNascimento\": \"2022-02-14\",
    \"sexo\": \"FEMEA\",
    \"peso\": 9.4,
    \"observacoes\": \"Pet criado via script de teste da Sprint 3 DevOps\",
    \"tutorId\": ${TUTOR_ID}
  }" | jq -r '.id')
echo "Pet criado com ID: ${PET_ID}"

echo "[3] GET - Consultando tutor criado"
curl -s "${BASE_URL}/api/tutores/${TUTOR_ID}" | jq

echo "[4] GET - Consultando pet criado"
curl -s "${BASE_URL}/api/pets/${PET_ID}" | jq

echo "[5] PUT - Atualizando o tutor"
curl -s -X PUT "${BASE_URL}/api/tutores/${TUTOR_ID}" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Ricardo Alves Junior",
    "email": "ricardo.alves.jr@example.com",
    "telefone": "11966665599",
    "cpf": "44455566677",
    "endereco": "Rua Nova, 456",
    "cidade": "Sao Paulo",
    "estado": "SP",
    "cep": "05000000"
  }' | jq

echo "[6] PUT - Atualizando o pet"
curl -s -X PUT "${BASE_URL}/api/pets/${PET_ID}" \
  -H "Content-Type: application/json" \
  -d "{
    \"nome\": \"Nina\",
    \"especie\": \"Cachorro\",
    \"raca\": \"Vira-lata\",
    \"dataNascimento\": \"2022-02-14\",
    \"sexo\": \"FEMEA\",
    \"peso\": 10.1,
    \"observacoes\": \"Pet atualizado via script de teste da Sprint 3 DevOps\",
    \"tutorId\": ${TUTOR_ID}
  }" | jq

echo "[7] DELETE - Removendo o pet"
curl -s -i -X DELETE "${BASE_URL}/api/pets/${PET_ID}"

echo ""
echo "[8] DELETE - Removendo o tutor"
curl -s -i -X DELETE "${BASE_URL}/api/tutores/${TUTOR_ID}"

echo ""
echo "[9] GET - Listando tutores apos exclusao"
curl -s "${BASE_URL}/api/tutores?size=10" | jq

echo ""
echo "Testes finalizados."
echo "No MySQL, conecte e rode:"
echo "SELECT * FROM tutores; SELECT * FROM pets;"
