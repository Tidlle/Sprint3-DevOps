# Roteiro do vídeo — Sprint 3 DevOps Tools & Cloud Computing (ACR + ACI)

Sem cortes durante as etapas de teste/CRUD (exigência do enunciado). Mínimo 720p, com
áudio claro e explicação por voz, sem legendas.

1. Mostrar o repositório no GitHub (código público, histórico de commits).
2. Mostrar o `README.md`: descrição da solução, benefícios para o negócio e a seção
   "Docker e Azure — Sprint 3 DevOps Tools & Cloud Computing (ACR + ACI)".
3. Mostrar `script_bd.sql` na raiz do repositório.
4. Mostrar o `Dockerfile` e o `docker-compose.yml`.
5. **Clonar o repositório do zero** a partir do GitHub — início obrigatório dos testes.
6. Rodar `az account show` para confirmar a assinatura autenticada (sem digitar senha em
   tela).
7. Executar `scripts/azure-acr-aci-deploy.sh`, seguindo exatamente os passos do README:
   - Mostrar a criação do Resource Group.
   - Mostrar a criação do Azure Container Registry.
   - Mostrar `docker build` e `docker push` da imagem para o ACR.
   - Mostrar a criação do grupo de containers (ACI) com `api` + `mysql`.
8. Mostrar os recursos criados no Portal Azure (Resource Group, ACR, Container Instance).
9. Acessar `http://<FQDN>:8080/swagger-ui.html` publicamente (não localhost).
10. Rodar `docker exec`/`az container exec` mostrando `whoami` dentro do container `api`
    — evidenciar que a aplicação não roda como root (resultado esperado: `appuser`).
11. **Demonstração individual e sem cortes do CRUD em Tutores e Pets** (tabelas do CORE
    relacionadas por `tutor_id`), usando `scripts/run-api-tests-tutores-pets.sh` ou o
    Swagger/Postman diretamente:
    - Inserção de um tutor → `SELECT * FROM tutores;` no MySQL mostrando o registro.
    - Inserção de um pet vinculado ao tutor → `SELECT * FROM pets;` mostrando o registro.
    - Atualização do tutor e do pet → novo `SELECT` mostrando os dados atualizados.
    - Exclusão do pet e do tutor → novo `SELECT` mostrando a remoção/inativação.
    - Consulta geral (`GET` com filtros) evidenciando a integração completa entre app e
      banco.
12. Para os `SELECT`s, abrir uma sessão interativa no container do MySQL (não dá para
    passar o `SELECT` direto no `--exec-command`, pois ele não passa por um shell):
    `az container exec --resource-group rg-vitalpet-aci --name vitalpet-aci --container-name mysql --exec-command "/bin/sh"`,
    depois `mysql -u vitalpet -p vitalpetdb` e digitar os `SELECT`s no prompt.
13. Executar `scripts/delete-azure-aci-resources.sh` ao final, mostrando a remoção dos
    recursos (Resource Group, ACR e ACI) para não consumir créditos da assinatura.
