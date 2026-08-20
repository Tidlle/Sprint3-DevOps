# Roteiro do vídeo - DevOps com Dockerfile, sem Docker Compose

1. Mostrar o GitHub público do projeto.
2. Mostrar que existe Dockerfile e que não será usado docker-compose.yml.
3. Mostrar o script `scripts/azure-vm-deploy.sh`.
4. Executar `az login`.
5. Executar `chmod +x scripts/azure-vm-deploy.sh`.
6. Executar `./scripts/azure-vm-deploy.sh`.
7. Mostrar a criação da VM Linux na Azure.
8. Mostrar as portas abertas: 8080 para API e 8082 para H2 Console.
9. Mostrar Docker instalado na VM.
10. Mostrar containers em background com `docker ps`.
11. Mostrar o volume nomeado com `docker volume ls`.
12. Mostrar que a aplicação não roda como root com `docker exec vitalpet-api whoami`; o resultado esperado é `appuser`.
13. Acessar `http://IP_PUBLICO:8080/swagger-ui.html`.
14. Fazer o CRUD: POST com dois inserts, GET, PUT e DELETE.
15. Acessar `http://IP_PUBLICO:8082` e mostrar o banco H2.
16. Executar `SELECT * FROM CLINICAS;` no H2 Console para comprovar os dados.
17. Executar `scripts/delete-azure-resources.sh`.
18. Mostrar a evidência de remoção da VM e dos recursos.
