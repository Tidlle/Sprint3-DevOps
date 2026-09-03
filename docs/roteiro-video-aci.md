# Roteiro do vídeo — Sprint 3 DevOps Tools & Cloud Computing (ACR + ACI)

Sem cortes durante as etapas de teste/CRUD (exigência do enunciado). Mínimo 720p, com
áudio claro e explicação por voz, sem legendas. Cada passo abaixo traz uma sugestão de
fala (🎙) — ajuste com suas palavras, o importante é explicar o que está acontecendo.

1. Mostrar o repositório no GitHub (código público, histórico de commits).
   🎙 "Esse é o repositório do Clyvo VitalPet, com a entrega da Sprint 3 de DevOps."
2. Mostrar o `README.md`: descrição da solução, benefícios para o negócio e a seção
   "Docker e Azure — Sprint 3 DevOps Tools & Cloud Computing (ACR + ACI)".
   🎙 "Aqui no README estão a descrição da solução, os benefícios para o negócio e o
   anexo com todos os comandos usados na entrega de DevOps."
3. Mostrar `script_bd.sql` na raiz do repositório.
   🎙 "Esse é o script_bd.sql, com o DDL completo e comentado das tabelas do CORE da
   aplicação."
4. Mostrar o `Dockerfile` e o `docker-compose.yml`.
   🎙 "O Dockerfile é multi-stage e roda com um usuário sem privilégios administrativos;
   o docker-compose eu uso pra validar tudo localmente antes de ir pra nuvem."
5. **Clonar o repositório do zero** a partir do GitHub — início obrigatório dos testes.
   🎙 "Vou clonar o repositório do zero, começando os testes exatamente como um
   avaliador faria."
6. Rodar `az account show` para confirmar a assinatura autenticada (sem digitar senha em
   tela).
   🎙 "Confirmando que estou autenticado na assinatura correta antes de criar qualquer
   recurso na Azure."
7. Executar `scripts/azure-acr-aci-deploy.sh`, seguindo exatamente os passos do README:
   - Mostrar a criação do Resource Group.
   - Mostrar a criação do Azure Container Registry.
   - Mostrar `docker build` e `docker push` da imagem para o ACR.
   - Mostrar a criação do grupo de containers (ACI) com `api` + `mysql`.
   🎙 "Aqui o script está criando o Resource Group... agora o Azure Container
   Registry... agora buildando e publicando a imagem da API... e por fim criando o
   grupo de containers com a API e o MySQL."
8. Mostrar os recursos criados no Portal Azure (Resource Group, ACR, Container Instance).
   🎙 "Aqui no portal já dá pra ver o Resource Group, o Container Registry com a imagem
   publicada, e o grupo de containers com a API e o MySQL rodando."
9. Acessar `http://<FQDN>:8080/swagger-ui.html` publicamente (não localhost).
   🎙 "Esse é o Swagger acessível publicamente pelo endereço do container, não é
   localhost — qualquer pessoa com o link consegue acessar."
10. Rodar `docker exec`/`az container exec` mostrando `whoami` dentro do container `api`
    — evidenciar que a aplicação não roda como root (resultado esperado: `appuser`).
    🎙 "E aqui a prova de que o container da API não roda como root: o usuário é
    appuser, definido no Dockerfile."
11. **Demonstração individual e sem cortes do CRUD em Tutores e Pets** (tabelas do CORE
    relacionadas por `tutor_id`), usando `scripts/run-api-tests-tutores-pets.sh` ou o
    Swagger/Postman diretamente:
    - Inserção de um tutor → `SELECT * FROM tutores;` no MySQL mostrando o registro.
      🎙 "Criei um tutor pela API. Voltando pro terminal do MySQL, dá pra ver o
      registro recém-inserido direto no banco."
    - Inserção de um pet vinculado ao tutor → `SELECT * FROM pets;` mostrando o registro.
      🎙 "Agora um pet vinculado a esse tutor — a mesma conferência no banco, mostrando
      a relação entre as duas tabelas."
    - Atualização do tutor e do pet → novo `SELECT` mostrando os dados atualizados.
      🎙 "Atualizando o tutor e o pet pela API. Rodando o SELECT de novo, dá pra
      confirmar que os dados mudaram no banco."
    - Exclusão do pet e do tutor → novo `SELECT` mostrando a remoção/inativação.
      🎙 "Excluindo o pet e o tutor pela API. Como é uma exclusão lógica, o campo ativo
      vai pra zero — é exatamente isso que aparece agora no SELECT."
    - Consulta geral (`GET` com filtros) evidenciando a integração completa entre app e
      banco.
      🎙 "Por fim, uma consulta com filtro pela API, fechando o ciclo completo de CRUD
      com a integração entre a aplicação e o banco na nuvem funcionando de ponta a
      ponta."
12. Para os `SELECT`s, abrir uma sessão interativa no container do MySQL (não dá para
    passar o `SELECT` direto no `--exec-command`, pois ele não passa por um shell):
    `az container exec --resource-group rg-vitalpet-aci --name vitalpet-aci --container-name mysql --exec-command "/bin/sh"`,
    depois `mysql -u vitalpet -p vitalpetdb` e digitar os `SELECT`s no prompt.
    🎙 "Vou abrir uma sessão dentro do container do MySQL pra acompanhar os dados em
    tempo real enquanto testo a API."
13. Executar `scripts/delete-azure-aci-resources.sh` ao final, mostrando a remoção dos
    recursos (Resource Group, ACR e ACI) para não consumir créditos da assinatura.
    🎙 "Para não consumir mais créditos da assinatura, removo o Resource Group inteiro
    ao final."
