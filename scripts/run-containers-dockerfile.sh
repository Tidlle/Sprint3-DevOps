#!/usr/bin/env bash
set -e

# =========================================================
# Execução local/VM usando Dockerfile, SEM Docker Compose
# Sobe dois containers:
# 1. vitalpet-h2  -> banco H2 conteinerizado
# 2. vitalpet-api -> aplicação Java criada pelo Dockerfile
# =========================================================

IMAGE_NAME="vitalpet-api:1.0.0"
NETWORK_NAME="vitalpet-network"
VOLUME_NAME="vitalpet-h2-data"

printf "Criando rede Docker...\n"
docker network create "$NETWORK_NAME" >/dev/null 2>&1 || true

printf "Criando volume nomeado para o banco H2...\n"
docker volume create "$VOLUME_NAME" >/dev/null

printf "Removendo containers antigos, se existirem...\n"
docker rm -f vitalpet-api vitalpet-h2 >/dev/null 2>&1 || true

printf "Subindo banco H2 em container...\n"
docker run -d \
  --name vitalpet-h2 \
  --restart unless-stopped \
  --network "$NETWORK_NAME" \
  -p 8082:81 \
  -v "$VOLUME_NAME":/opt/h2-data \
  -e H2_OPTIONS="-ifNotExists" \
  oscarfonts/h2:2.2.224

printf "Construindo imagem da API pelo Dockerfile...\n"
docker build -t "$IMAGE_NAME" .

printf "Subindo API em container, conectada ao H2...\n"
docker run -d \
  --name vitalpet-api \
  --restart unless-stopped \
  --network "$NETWORK_NAME" \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e SPRING_DATASOURCE_URL="jdbc:h2:tcp://vitalpet-h2:1521/./vitalpetdb;DB_CLOSE_DELAY=-1" \
  -e SPRING_DATASOURCE_USERNAME="sa" \
  -e SPRING_DATASOURCE_PASSWORD="" \
  "$IMAGE_NAME"

printf "\nContainers em execução:\n"
docker ps --filter "name=vitalpet"

printf "\nVolume criado:\n"
docker volume ls | grep "$VOLUME_NAME" || true

printf "\nAcesse:\n"
printf "API:      http://localhost:8080\n"
printf "Swagger:  http://localhost:8080/swagger-ui.html\n"
printf "H2 Web:   http://localhost:8082\n"
printf "\nH2 Console:\n"
printf "JDBC URL: jdbc:h2:tcp://localhost:1521/./vitalpetdb\n"
printf "User: sa\n"
printf "Password: deixe vazio\n"
