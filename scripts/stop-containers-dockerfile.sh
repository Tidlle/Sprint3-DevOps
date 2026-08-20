#!/usr/bin/env bash
set -e

echo "Parando e removendo containers VitalPet..."
docker rm -f vitalpet-api vitalpet-h2 >/dev/null 2>&1 || true

echo "Containers removidos. O volume vitalpet-h2-data foi mantido para preservar os dados."
echo "Para apagar o volume também, execute: docker volume rm vitalpet-h2-data"
