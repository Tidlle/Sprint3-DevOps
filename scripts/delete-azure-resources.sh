#!/usr/bin/env bash
set -e

RG_NAME="rg-vitalpet-devops"

echo "Removendo Resource Group e todos os recursos da entrega..."
az group delete --name "$RG_NAME" --yes --no-wait

echo "Solicitação enviada. Aguarde alguns minutos e confira no portal da Azure se a VM foi removida."
