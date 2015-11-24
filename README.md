# gefi-server
Servidor web para o serviço de gerenciamento de filas.

## Propósito

Sistema de controle de senhas para atendimento bancário. O sistema será utilizado por dois perfis de usuários: GERENTE e CLIENTE. O Gerente será responsável pela administração do sistema, chamando novas senhas e reiniciando a contagem de senhas quando for necessário. Ao cliente caberá apenas a retirada e acompanhamento das senhas.


## Dependências

* JDK 1.8 ou mais recente
* Maven 3.0 ou mais recente
* Git 

## Obtendo o projeto

	$ mkdir -p /workspace/gefi-server
	$ cd /workspace/gefi-server
	$ git clone https://github.com/tioback/gefi-server.git
	
## Executando no modo standalone

	$ mvn spring-boot:run
	
## Empacotando para deploy

	$ mvn clean package
	# Utilizar o arquivo target/gefi-server*.war 
	
## Utilizando

O sistema pode ser acessado pela url http://localhost:8090/gefi-server (considerado como BASE_URL daqui em diante).
As operações disponíveis são:

### Cliente

As ações permitidas ao cliente são:

* Gerar Senha Normal

	$ curl -i -X POST BASE_URL/senha/N
	# Exemplo de retorno esperado (cabeçalhos omitidos)
	{
	  "tipo": "NORMAL",
	  "sequencial": 1,
	  "prioridade": 10001,
	  "codigo": "N0001"
	}

* Gerar Senha Preferencial

	$ curl -i -X POST BASE_URL/senha/P
	# Exemplo de retorno esperado (cabeçalhos omitidos)
	{
	  "tipo": "PREFERENCIAL",
	  "sequencial": 1,
	  "prioridade": 1,
	  "codigo": "P0001"
	}

* Conferir Última Senha Chamada - via requisição direta

	$ curl -i -X GET BASE_URL/senha
	# Exemplo de retorno esperado quando alguma senha já foi chamada (cabeçalhos omitidos)
	{
	  "tipo": "NORMAL",
	  "sequencial": 1,
	  "prioridade": 10001,
	  "codigo": "N0001"
	}
	
	# Exemplo de retorno esperado quando ainda não houve senha chamada (cabeçalhos omitidos)
	{
	  "timestamp": 1448333042207,
	  "status": 404,
	  "error": "Not Found",
	  "exception": "renatoback.paripassu.teste.analistaIII.gefi.server.exception.NenhumaSenhaChamada",
	  "message": "Nenhuma senha chamada até o momento.",
	  "path": "/gefi-server/senha"
	}
	
* Conferir Última Senha Chamada - via WebSocket / subscription

O servidor disponibiliza um WebSocket endpoint no endereço *BASE_URL/ultimaSenha* com a fila */topic/senha*.
	
### Gerente

O Gerente pode realizar as seguintes operações:

## Testes

O sistema possui testes para os serviços disponibilizados e são sempre executados ao compilar a aplicação através do Maven.

* Reiniciar Senha Normal

	$ curl -i -X PUT BASE_URL/senha/N
	# O retorno de sucesso é um response status HTTP/1.1 200 OK (cabeçalhos omitidos)

* Reiniciar Senha Preferencial

	$ curl -i -X PUT BASE_URL/senha/P
	# O retorno de sucesso é um response status HTTP/1.1 200 OK (cabeçalhos omitidos)

* Chamar Próxima Senha

	$ curl -i -X POST BASE_URL/senha
	# Exemplo de retorno esperado quando alguma senha já foi chamada (cabeçalhos omitidos)
	{
	  "tipo": "PREFERENCIAL",
	  "sequencial": 1,
	  "prioridade": 1,
	  "codigo": "P0001"
	}
	
	# Exemplo de retorno esperado quando ainda não houve senha chamada (cabeçalhos omitidos)
	{
	  "timestamp": 1448333604458,
	  "status": 404,
	  "error": "Not Found",
	  "exception": "renatoback.paripassu.teste.analistaIII.gefi.server.exception.SemSenhasParaChamar",
	  "message": "Nenhuma senha para chamar.",
	  "path": "/gefi-server/senha"
	}