#!/usr/bin/env bash
set -e

# =========================================================
# Script Azure CLI - VitalPet
# Objetivo:
# 1. Criar uma VM Linux na Azure
# 2. Abrir as portas necessárias para API e H2 Console
# 3. Instalar Docker, Git, nano e ferramentas úteis
# 4. Clonar o projeto do GitHub
# 5. Criar rede e volume nomeado no Docker
# 6. Subir banco H2 e API em background
# =========================================================

RG_NAME="rg-vitalpet-devops"
LOCATION="canadacentral"
VM_NAME="vm-vitalpet"
ADMIN_USER="azureuser"
VM_IMAGE="Ubuntu2204"
VM_SIZE="Standard_B2atsv2"

REPO_URL="https://github.com/Tidlle/Sprint1-Java.git"
PROJECT_DIR="/home/${ADMIN_USER}/clyvo-vitalpet"

IMAGE_NAME="vitalpet-api:1.0.0"
NETWORK_NAME="vitalpet-network"
VOLUME_NAME="vitalpet-h2-data"

echo "Criando Resource Group..."
az group create \
  --name "$RG_NAME" \
  --location "$LOCATION"

echo "Criando Máquina Virtual Linux..."
az vm create \
  --resource-group "$RG_NAME" \
  --name "$VM_NAME" \
  --image "$VM_IMAGE" \
  --size "$VM_SIZE" \
  --admin-username "$ADMIN_USER" \
  --generate-ssh-keys \
  --public-ip-sku Standard

echo "Abrindo portas necessárias..."
az vm open-port --resource-group "$RG_NAME" --name "$VM_NAME" --port 8080 --priority 1001
az vm open-port --resource-group "$RG_NAME" --name "$VM_NAME" --port 8082 --priority 1002

echo "Instalando Docker, Git, nano, curl e jq na VM..."
az vm run-command invoke \
  --resource-group "$RG_NAME" \
  --name "$VM_NAME" \
  --command-id RunShellScript \
  --scripts "
    set -e
    sudo apt-get update -y
    sudo apt-get install -y ca-certificates curl gnupg git nano unzip jq
    sudo install -m 0755 -d /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
    sudo chmod a+r /etc/apt/keyrings/docker.gpg
    echo \"deb [arch=\$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \$(. /etc/os-release && echo \$VERSION_CODENAME) stable\" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    sudo apt-get update -y
    sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin
    sudo systemctl enable docker
    sudo systemctl start docker
    sudo usermod -aG docker ${ADMIN_USER}
  "

echo "Clonando projeto e subindo containers com docker run..."
az vm run-command invoke \
  --resource-group "$RG_NAME" \
  --name "$VM_NAME" \
  --command-id RunShellScript \
  --scripts "
    set -e
    cd /home/${ADMIN_USER}

    if [ -d clyvo-vitalpet ]; then
      cd clyvo-vitalpet
      sudo -u ${ADMIN_USER} git pull
    else
      sudo -u ${ADMIN_USER} git clone ${REPO_URL} clyvo-vitalpet
      cd clyvo-vitalpet
    fi

    docker network create ${NETWORK_NAME} >/dev/null 2>&1 || true
    docker volume create ${VOLUME_NAME} >/dev/null

    docker rm -f vitalpet-api vitalpet-h2 >/dev/null 2>&1 || true

    docker run -d \\
      --name vitalpet-h2 \\
      --restart unless-stopped \\
      --network ${NETWORK_NAME} \\
      -p 8082:81 \\
      -v ${VOLUME_NAME}:/opt/h2-data \\
      -e H2_OPTIONS='-ifNotExists' \\
      oscarfonts/h2:2.2.224

    docker build -t ${IMAGE_NAME} .

    docker run -d \\
      --name vitalpet-api \\
      --restart unless-stopped \\
      --network ${NETWORK_NAME} \\
      -p 8080:8080 \\
      -e SPRING_PROFILES_ACTIVE=docker \\
      -e SPRING_DATASOURCE_URL='jdbc:h2:tcp://vitalpet-h2:1521/./vitalpetdb;DB_CLOSE_DELAY=-1' \\
      -e SPRING_DATASOURCE_USERNAME='sa' \\
      -e SPRING_DATASOURCE_PASSWORD='' \\
      ${IMAGE_NAME}

    docker ps --filter name=vitalpet
    docker volume ls | grep ${VOLUME_NAME} || true
  "

echo "Coletando IP público da VM..."
PUBLIC_IP=$(az vm show \
  --resource-group "$RG_NAME" \
  --name "$VM_NAME" \
  --show-details \
  --query publicIps \
  --output tsv)

echo "Entrega em nuvem criada com sucesso!"
echo "API:      http://${PUBLIC_IP}:8080"
echo "Swagger:  http://${PUBLIC_IP}:8080/swagger-ui.html"
echo "H2 Web:   http://${PUBLIC_IP}:8082"
echo "SSH:      ssh ${ADMIN_USER}@${PUBLIC_IP}"
echo ""
echo "Comandos úteis na VM:"
echo "docker ps"
echo "docker volume ls"
echo "docker exec vitalpet-api whoami"
echo ""
echo "IMPORTANTE: ao final, execute scripts/delete-azure-resources.sh para remover a VM e os recursos em nuvem."
