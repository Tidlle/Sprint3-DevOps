#!/usr/bin/env bash
set -e

# =========================================================================
# Script Azure CLI - VitalPet (Sprint 3 - DevOps Tools & Cloud Computing)
# Opcao escolhida: ACR + ACI (containerizacao completa, App + Banco)
#
# Objetivo:
# 1. Criar um Azure Container Registry (ACR) e publicar a imagem da API
# 2. Criar um Azure Container Instance com DOIS containers no mesmo grupo
#    (api + mysql), compartilhando rede/localhost, com um unico IP publico
# 3. Gerar uma senha aleatoria para o MySQL em tempo de execucao (nunca
#    fica gravada em nenhum arquivo do repositorio)
#
# Por que um unico "container group" (YAML) em vez de dois "az container
# create" separados numa VNet: containers ACI implantados numa VNet nao
# podem receber IP publico (limitacao da plataforma Azure). Um grupo com
# dois containers resolve isso com um unico IP publico, e os containers se
# enxergam via localhost dentro do grupo.
# =========================================================================

RG_NAME="${RG_NAME:-rg-vitalpet-aci}"
# canadacentral e outras regioes ficaram bloqueadas por politica da assinatura
# "Azure for Students" ("best available regions"); eastus foi a regiao validada.
LOCATION="${LOCATION:-eastus}"
ACR_NAME="${ACR_NAME:-acrvitalpet$RANDOM}"
IMAGE_NAME="clyvo-vitalpet-api"
IMAGE_TAG="1.0.0"
CONTAINER_GROUP="vitalpet-aci"
DNS_LABEL="${DNS_LABEL:-vitalpet-$RANDOM}"

echo "Resource Group: $RG_NAME | Regiao: $LOCATION | ACR: $ACR_NAME | DNS: $DNS_LABEL"

echo "Criando Resource Group..."
az group create \
  --name "$RG_NAME" \
  --location "$LOCATION"

echo "Criando Azure Container Registry..."
az acr create \
  --resource-group "$RG_NAME" \
  --name "$ACR_NAME" \
  --sku Basic \
  --admin-enabled true

echo "Buildando e publicando a imagem da API no ACR..."
az acr login --name "$ACR_NAME"
docker build -t "${ACR_NAME}.azurecr.io/${IMAGE_NAME}:${IMAGE_TAG}" .
docker push "${ACR_NAME}.azurecr.io/${IMAGE_NAME}:${IMAGE_TAG}"
# Alternativa sem Docker local (build feito na nuvem pelo proprio ACR):
# az acr build --registry "$ACR_NAME" --image "${IMAGE_NAME}:${IMAGE_TAG}" .

ACR_USERNAME=$(az acr credential show --name "$ACR_NAME" --query username -o tsv)
ACR_PASSWORD=$(az acr credential show --name "$ACR_NAME" --query "passwords[0].value" -o tsv)

echo "Gerando senha aleatoria para o MySQL..."
MYSQL_PASSWORD=$(openssl rand -base64 24 | tr -dc 'A-Za-z0-9' | head -c 24)

MANIFEST_FILE="$(mktemp)"
cat > "$MANIFEST_FILE" <<EOF
apiVersion: '2021-09-01'
location: ${LOCATION}
name: ${CONTAINER_GROUP}
properties:
  osType: Linux
  restartPolicy: OnFailure
  imageRegistryCredentials:
    - server: ${ACR_NAME}.azurecr.io
      username: ${ACR_USERNAME}
      password: ${ACR_PASSWORD}
  ipAddress:
    type: Public
    dnsNameLabel: ${DNS_LABEL}
    ports:
      - protocol: tcp
        port: 8080
  containers:
    - name: mysql
      properties:
        image: mysql:8.0
        resources:
          requests:
            cpu: 1
            memoryInGb: 1.5
        environmentVariables:
          - name: MYSQL_DATABASE
            value: vitalpetdb
          - name: MYSQL_USER
            value: vitalpet
          - name: MYSQL_PASSWORD
            secureValue: ${MYSQL_PASSWORD}
          - name: MYSQL_ROOT_PASSWORD
            secureValue: ${MYSQL_PASSWORD}
        ports:
          - port: 3306
    - name: api
      properties:
        image: ${ACR_NAME}.azurecr.io/${IMAGE_NAME}:${IMAGE_TAG}
        resources:
          requests:
            cpu: 1
            memoryInGb: 1.5
        environmentVariables:
          - name: SPRING_PROFILES_ACTIVE
            value: mysql
          - name: SPRING_DATASOURCE_URL
            value: jdbc:mysql://localhost:3306/vitalpetdb
          - name: SPRING_DATASOURCE_USERNAME
            value: vitalpet
          - name: SPRING_DATASOURCE_PASSWORD
            secureValue: ${MYSQL_PASSWORD}
        ports:
          - port: 8080
EOF

echo "Criando o grupo de containers (mysql + api) na ACI..."
az container create \
  --resource-group "$RG_NAME" \
  --file "$MANIFEST_FILE"

rm -f "$MANIFEST_FILE"

echo "Aguardando a API iniciar (o container pode reiniciar 1-2x enquanto o MySQL sobe, isso e esperado por causa do restartPolicy: OnFailure)..."
sleep 30

FQDN=$(az container show --resource-group "$RG_NAME" --name "$CONTAINER_GROUP" --query ipAddress.fqdn -o tsv)

echo ""
echo "Entrega em nuvem criada com sucesso!"
echo "API:      http://${FQDN}:8080"
echo "Swagger:  http://${FQDN}:8080/swagger-ui.html"
echo ""
echo "Senha gerada para o MySQL (usuarios 'vitalpet' e 'root'): ${MYSQL_PASSWORD}"
echo "Guarde essa senha para a demonstracao em video - ela NAO fica salva em nenhum arquivo do repositorio."
echo ""
echo "Para rodar SELECTs no MySQL durante o video (conecta dentro do proprio container):"
echo "az container exec --resource-group ${RG_NAME} --name ${CONTAINER_GROUP} --container-name mysql --exec-command \"mysql -u vitalpet -p${MYSQL_PASSWORD} vitalpetdb\""
echo ""
echo "Para acompanhar os logs da API em caso de erro:"
echo "az container logs --resource-group ${RG_NAME} --name ${CONTAINER_GROUP} --container-name api"
echo ""
echo "IMPORTANTE: ao final da gravacao, execute scripts/delete-azure-aci-resources.sh para remover os recursos e nao consumir creditos da assinatura."
