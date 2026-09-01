#!/usr/bin/env bash
set -e

RG_NAME="${RG_NAME:-rg-vitalpet-aci}"

echo "Removendo Resource Group e todos os recursos da entrega ACR + ACI..."
az group delete --name "$RG_NAME" --yes --no-wait

echo "Solicitacao enviada. Aguarde alguns minutos e confira no portal da Azure se o ACR e o ACI foram removidos."
