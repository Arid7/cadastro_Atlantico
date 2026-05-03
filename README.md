# Cadastro Atlantico

Aplicacao de candidatura profissional em JavaFX com persistencia por ficheiro de acesso aleatorio, validacao obrigatoria dos campos e consulta direta por numero de registo.

Este README foi escrito para o grupo conseguir entender a arquitetura, explicar o projeto na defesa e demonstrar com clareza como a aplicacao funciona do inicio ao fim.

## 1. Objetivo do projeto

O projeto simula um formulario de candidatura profissional para o Banco Atlantico.

O utilizador pode:

- preencher todos os campos do formulario
- submeter a candidatura
- guardar os dados num ficheiro de acesso aleatorio
- receber uma tela de agradecimento com o numero do registo criado
- consultar uma candidatura depois, usando apenas o numero do registo

O ponto principal para a defesa e mostrar que o sistema nao grava apenas texto qualquer, mas organiza a informacao em registos fixos para permitir leitura direta com `RandomAccessFile`.

## 2. Tecnologias e configuracao atual

- GUI: JavaFX
- Linguagem: Java
- Projeto: Maven / NetBeans
- Persistencia: `RandomAccessFile`
- Estrutura modular: `module-info.java`

Observacao importante:

- O projeto esta configurado no `pom.xml` com Java 21 e JavaFX 22 para conseguir correr no ambiente atual.
- A camada de negocio e a camada de persistencia foram escritas com estilo simples e compativel com Java 8: classes normais, getters/setters, `StringBuilder`, `RandomAccessFile`, excecoes personalizadas e sem dependencia de recursos modernos desnecessarios.

## 3. Estrutura do projeto

### 3.1 Camadas principais

- GUI JavaFX: [src/main/java/com/mycompany/teste/App.java](src/main/java/com/mycompany/teste/App.java)
- Modelo de dados: [src/main/java/com/mycompany/teste/model/Candidatura.java](src/main/java/com/mycompany/teste/model/Candidatura.java)
- Camada de negocio: [src/main/java/com/mycompany/teste/service/CandidaturaService.java](src/main/java/com/mycompany/teste/service/CandidaturaService.java)
- Excecao de validacao: [src/main/java/com/mycompany/teste/service/ValidationException.java](src/main/java/com/mycompany/teste/service/ValidationException.java)
- Persistencia com acesso aleatorio: [src/main/java/com/mycompany/teste/persistence/RandomAccessCandidaturaRepository.java](src/main/java/com/mycompany/teste/persistence/RandomAccessCandidaturaRepository.java)
- Resultado da gravacao: [src/main/java/com/mycompany/teste/persistence/SubmissionStorage.java](src/main/java/com/mycompany/teste/persistence/SubmissionStorage.java)
- Estilos da interface: [src/main/resources/com/mycompany/teste/atlantico-theme.css](src/main/resources/com/mycompany/teste/atlantico-theme.css)

### 3.2 Responsabilidade de cada ficheiro

#### `App.java`

Responsavel apenas por:

- montar a interface JavaFX
- recolher os dados digitados no formulario
- converter esses dados para um objeto `Candidatura`
- chamar a camada de negocio
- trocar entre a tela do formulario, a tela de agradecimento e a tela de consulta

Nao deve conter a regra principal de persistencia. A GUI apenas pede servicos a outras camadas.

#### `Candidatura.java`

Representa uma candidatura completa. E o modelo de dados da aplicacao.

Contem:

- os atributos da candidatura
- getters e setters
- um metodo que converte o objeto num `Map<String, String>` para a persistencia

#### `CandidaturaService.java`

Representa a camada de negocio.

E a classe que decide:

- se a candidatura pode ou nao ser submetida
- se todos os campos obrigatorios estao preenchidos
- se o consentimento foi aceite
- quando deve chamar o repositorio para gravar
- quando deve chamar o repositorio para ler um registo

#### `ValidationException.java`

Serve para avisar a GUI que um campo obrigatorio nao foi preenchido corretamente.

Vantagem:

- a validacao fica centralizada na camada de negocio
- a GUI apenas mostra a mensagem ao utilizador

#### `RandomAccessCandidaturaRepository.java`

Representa a camada de persistencia.

Responsavel por:

- abrir o ficheiro `candidaturas.dat`
- escrever novos registos no fim do ficheiro
- calcular offsets para leitura direta
- ler um registo especifico usando `seek()`

#### `SubmissionStorage.java`

Objeto simples que devolve para a GUI:

- o caminho do ficheiro usado
- o numero do registo criado

Isto e importante para a tela de agradecimento e para a demonstracao da consulta por registo.

## 4. Fluxo funcional da aplicacao

### 4.1 Submissao do formulario

O fluxo de submissao e este:

1. o utilizador preenche o formulario JavaFX
2. a GUI recolhe os valores e cria um objeto `Candidatura`
3. a GUI chama `candidaturaService.submeter(candidatura)`
4. a camada de negocio valida os campos obrigatorios
5. se algum campo estiver vazio, a camada de negocio lanca `ValidationException`
6. se a candidatura estiver valida, a camada de negocio chama o repositorio
7. o repositorio grava o registo em `candidaturas.dat`
8. o repositorio devolve o numero do registo gravado
9. a GUI mostra a tela de agradecimento com o numero desse registo

### 4.2 Consulta por numero de registo

Agora a aplicacao tem tambem uma tela de consulta por registo.

O fluxo da consulta e este:

1. o utilizador abre a tela `Consultar Registo`
2. pode escolher entre tres formas de consulta
3. ler por numero do registo
4. pesquisar por nome do candidato
5. listar todos os registos gravados
6. a camada de negocio delega ao repositorio
7. o repositorio percorre os registos e devolve apenas o que foi pedido
8. o conteudo encontrado e mostrado no ecran

Esta tela existe para demonstrar, na pratica, que o ficheiro foi desenhado para acesso aleatorio e nao apenas para leitura sequencial.

## 5. Como o ficheiro de acesso aleatorio esta organizado

O ficheiro principal e:

- `inscricoes/candidaturas.dat`

Cada candidatura e guardada como um registo fixo com esta estrutura:

1. `1 byte` para indicar se o registo esta ativo
2. `4 bytes` com o tamanho real do texto guardado
3. `8192 bytes` reservados para o conteudo da candidatura

Logo, o tamanho total de cada registo e:

$$
RECORD\_SIZE = 1 + 4 + 8192 = 8197\ bytes
$$

## 6. Formula de acesso direto

Se queremos ler o registo `n`, nao precisamos ler o ficheiro todo desde o inicio.

Calculamos diretamente a posicao:

$$
offset = (n - 1) \times RECORD\_SIZE
$$

Exemplo:

- registo 1: offset `0`
- registo 2: offset `8197`
- registo 3: offset `16394`

Depois disso, o codigo faz:

```java
file.seek(offset);
```

Esse e o ponto mais importante da persistencia para a defesa.

## 7. Porque isto e acesso aleatorio e nao apenas sequencial

Num ficheiro sequencial simples, para chegar ao registo 50 normalmente seria preciso ler os registos anteriores um a um.

Aqui, como cada bloco tem tamanho fixo, a aplicacao consegue:

- calcular onde o registo comeca
- saltar diretamente para essa posicao
- ler apenas aquele bloco

Isto cumpre o criterio de ficheiros de acesso aleatorio pedido para a defesa.

## 8. Validacao de negocio

Todos os campos do formulario sao obrigatorios.

A regra principal de validacao fica em `CandidaturaService`, nao na persistencia.

Existe tambem uma validacao preventiva na GUI, para evitar que o utilizador digite caracteres claramente invalidos antes mesmo de submeter.

Em resumo:

- a GUI previne entradas absurdas
- a camada de negocio confirma se os dados sao mesmo validos
- a persistencia so grava quando tudo estiver correto

Sao validados, entre outros:

- dados pessoais
- escolaridade
- area de estudo
- area de interesse
- objectivos profissionais
- resumo profissional
- assinatura
- data de assinatura
- consentimento de dados

### 8.1 Regras novas aplicadas na GUI

No `App.java`, alguns campos passaram a ter filtros de digitacao:

- `Contacto Telefonico`: aceita apenas digitos e limita a 9 caracteres
- `Nome Completo` e `Assinatura do Candidato`: aceitam apenas letras, espacos, apostrofos, hifens e ponto
- `Nacionalidade`, `Residencia (Pais)`, `Provincia` e `Pais da Formacao`: aceitam apenas letras, espacos, apostrofos e hifens
- `B.I. / Passaporte`: aceita apenas letras, numeros, `/` e `-`
- `E-mail`: nao aceita espacos nem caracteres claramente invalidos
- `DatePicker`: o editor passou a usar o formato `dd/MM/yyyy`, com filtro de digitos e barra

Isto melhora a experiencia do utilizador porque reduz erros logo durante o preenchimento.

### 8.2 Regras novas aplicadas na camada de negocio

Mesmo que a GUI deixe passar algo por engano, a camada de negocio ainda valida tudo antes de gravar.

Agora o servico verifica tambem:

- telefone com exatamente 9 digitos
- e-mail com formato valido
- nome completo sem numeros e com comprimento minimo coerente
- assinatura sem numeros e com comprimento minimo coerente
- nacionalidade, pais de residencia, provincia e pais da formacao sem numeros
- B.I. / Passaporte com 6 a 20 caracteres alfanumericos, podendo incluir `/` ou `-`
- datas no formato `dd/MM/yyyy`
- datas impossiveis, como `31/02/2026`
- datas futuras nao sao aceites
- data de fim de curso nao pode ser anterior a data de nascimento
- data de assinatura nao pode ser anterior a data de nascimento

Casos especiais:

- se a opcao `Outra` for marcada em area de estudo, o texto explicativo tambem passa a ser obrigatorio
- se a opcao `Outro` for marcada em area de interesse, o texto explicativo tambem passa a ser obrigatorio
- a `Data de Conclusao do Curso` passou a ser opcional
- se essa data for preenchida, continua a ser validada e nao pode ser futura nem anterior a data de nascimento

### 8.3 Exemplo pratico de validacao

Exemplos de entradas rejeitadas:

- telefone `92345` porque nao tem 9 digitos
- nome `Joao123` porque contem numeros
- data `31/02/2026` porque a data nao existe
- data `12/12/2035` porque e futura
- BI `A1` porque nao atinge o tamanho minimo esperado

## 9. Ecras da aplicacao

### 9.1 Formulario principal

Mostra todas as secoes da candidatura:

- dados pessoais
- habilitacoes academicas
- areas de interesse
- informacoes adicionais
- termos e consentimento

Tambem tem um botao `Consultar Registo`, para demonstrar a leitura direta do ficheiro mesmo sem voltar a submeter outro formulario.

### 9.2 Tela de agradecimento

Depois da submissao bem sucedida, o sistema mostra:

- confirmacao da candidatura
- caminho do ficheiro `.dat`
- numero do registo criado
- tamanho do registo fixo

Esta tela tem dois objetivos:

- mostrar ao utilizador que a gravacao foi bem sucedida
- dar ao grupo um ponto visual forte para explicar a persistencia na defesa

### 9.3 Tela de consulta

Permite:

- informar um numero de registo
- pesquisar candidaturas por nome
- listar todos os registos existentes
- localizar o conteudo gravado no ficheiro
- mostrar o conteudo no ecran

Esta e a melhor parte da demo tecnica do projeto.

## 10. Como demonstrar na defesa

Sugestao de roteiro simples:

1. abrir o formulario principal
2. explicar rapidamente as camadas do sistema
3. preencher e submeter uma candidatura
4. mostrar a tela de agradecimento com o numero do registo
5. abrir a consulta por registo
6. usar o numero recebido para ler o registo ao vivo
7. explicar a formula do offset
8. mostrar no codigo o metodo `readRecord()` do repositorio

Se o professor perguntar onde esta a separacao de responsabilidades, a resposta e:

- a GUI recolhe e mostra dados
- a camada de negocio valida e coordena
- a persistencia grava e le o ficheiro de acesso aleatorio

## 11. Ficheiros mais importantes para mostrar ao professor

Se houver pouco tempo na defesa, estes sao os ficheiros que valem mais a pena abrir:

1. [src/main/java/com/mycompany/teste/App.java](src/main/java/com/mycompany/teste/App.java)
2. [src/main/java/com/mycompany/teste/service/CandidaturaService.java](src/main/java/com/mycompany/teste/service/CandidaturaService.java)
3. [src/main/java/com/mycompany/teste/persistence/RandomAccessCandidaturaRepository.java](src/main/java/com/mycompany/teste/persistence/RandomAccessCandidaturaRepository.java)
4. [src/main/java/com/mycompany/teste/model/Candidatura.java](src/main/java/com/mycompany/teste/model/Candidatura.java)

## 12. Como validar no ambiente atual

O Maven nao estava disponivel no ambiente usado durante o desenvolvimento, por isso a validacao foi feita com `javac` e os jars JavaFX existentes no cache local.

Exemplo de validacao completa:

```bash
JAVAFX_MP="$HOME/.m2/repository/org/openjfx/javafx-base/22/javafx-base-22.jar:$HOME/.m2/repository/org/openjfx/javafx-base/22/javafx-base-22-linux.jar:$HOME/.m2/repository/org/openjfx/javafx-controls/22/javafx-controls-22.jar:$HOME/.m2/repository/org/openjfx/javafx-controls/22/javafx-controls-22-linux.jar:$HOME/.m2/repository/org/openjfx/javafx-fxml/22/javafx-fxml-22.jar:$HOME/.m2/repository/org/openjfx/javafx-fxml/22/javafx-fxml-22-linux.jar:$HOME/.m2/repository/org/openjfx/javafx-graphics/22/javafx-graphics-22.jar:$HOME/.m2/repository/org/openjfx/javafx-graphics/22/javafx-graphics-22-linux.jar"

javac --module-path "$JAVAFX_MP" \
	--add-modules javafx.controls,javafx.fxml,javafx.graphics \
	-d /tmp/cadastro-atlantico-app-check \
	$(find src/main/java -name '*.java')
```

## 13. Resumo final para o grupo

Em linguagem simples, o projeto funciona assim:

- o formulario recolhe os dados
- os dados viram um objeto `Candidatura`
- o servico valida tudo
- o repositorio grava a candidatura num bloco fixo dentro de um ficheiro `.dat`
- o sistema devolve o numero do registo criado
- esse numero pode ser usado depois para consultar diretamente a candidatura

Se o grupo souber explicar bem estas seis ideias, a defesa fica tecnicamente forte e coerente com os criterios de avaliacao.
