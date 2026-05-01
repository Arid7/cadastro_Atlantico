package com.mycompany.teste;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.StringJoiner;
import java.util.function.UnaryOperator;

import com.mycompany.teste.model.Candidatura;
import com.mycompany.teste.persistence.RandomAccessCandidaturaRepository;
import com.mycompany.teste.persistence.SubmissionStorage;
import com.mycompany.teste.service.CandidaturaService;
import com.mycompany.teste.service.ValidationException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class App extends Application {

    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final CandidaturaService candidaturaService = new CandidaturaService(
        new RandomAccessCandidaturaRepository(Paths.get(System.getProperty("user.dir"), "inscricoes"))
    );

    @Override
    public void start(Stage stage) {
        final ScrollPane[] rootHolder = new ScrollPane[1];

        VBox page = new VBox(26);
        page.getStyleClass().add("page");
        page.setAlignment(Pos.TOP_CENTER);

        StackPane hero = createHero();
        hero.setMaxWidth(Double.MAX_VALUE);

        VBox formCard = new VBox(18);
        formCard.getStyleClass().add("form-card");
        formCard.setMaxWidth(1040);

        Label eyebrow = new Label("Portal de Recrutamento Digital");
        eyebrow.getStyleClass().add("eyebrow-label");

        Label title = new Label("FORMULARIO DE CANDIDATURA PROFISSIONAL");
        title.getStyleClass().add("form-title");

        Label subtitle = new Label("Banco ATLANTICO | Recrutamento e Talento");
        subtitle.getStyleClass().add("form-subtitle");

        Label lead = new Label("Preencha o formulario com os seus dados academicos e profissionais. A validacao fica na camada de negocio, os registos sao guardados em RandomAccessFile e podem ser consultados diretamente pelo numero do registo.");
        lead.setWrapText(true);
        lead.getStyleClass().add("lead-copy");

        HBox highlights = new HBox(12,
            createInfoCard("Fluxo claro", "Secoes organizadas para preenchimento rapido."),
            createInfoCard("Persistencia RAF", "Registos fixos em RandomAccessFile para acesso direto."),
            createInfoCard("Negocio separado", "Validacao e submissao isoladas da camada JavaFX.")
        );
        highlights.getStyleClass().add("info-strip");

        TextField nomeCompleto = createTextField("Nome Completo");
        DatePicker dataNascimento = createDatePicker("Data de Nascimento");

        ToggleGroup sexoGroup = new ToggleGroup();
        RadioButton sexoMasculino = new RadioButton("Masculino");
        RadioButton sexoFeminino = new RadioButton("Feminino");
        sexoMasculino.setToggleGroup(sexoGroup);
        sexoFeminino.setToggleGroup(sexoGroup);

        TextField nacionalidade = createTextField("Nacionalidade");
        TextField biPassaporte = createTextField("B.I. / Passaporte");
        TextField residenciaPais = createTextField("Residencia (Pais)");
        TextField provincia = createTextField("Provincia");
        TextField email = createTextField("E-mail");
        TextField telefone = createTextField("Contacto Telefonico");

        applyLettersFilter(nomeCompleto, true);
        applyLettersFilter(nacionalidade, false);
        applyLettersFilter(residenciaPais, false);
        applyLettersFilter(provincia, false);
        applyDocumentFilter(biPassaporte);
        applyEmailFilter(email);
        applyPhoneFilter(telefone);

        GridPane dadosPessoais = createGrid();
        dadosPessoais.add(sectionHeader("1. DADOS PESSOAIS"), 0, 0, 4, 1);
        dadosPessoais.add(nomeCompleto, 0, 1, 4, 1);
        dadosPessoais.add(dataNascimento, 0, 2, 2, 1);
        dadosPessoais.add(sexBox(sexoMasculino, sexoFeminino), 2, 2, 2, 1);
        dadosPessoais.add(nacionalidade, 0, 3, 2, 1);
        dadosPessoais.add(biPassaporte, 2, 3, 2, 1);
        dadosPessoais.add(residenciaPais, 0, 4, 2, 1);
        dadosPessoais.add(provincia, 2, 4, 2, 1);
        dadosPessoais.add(email, 0, 5, 2, 1);
        dadosPessoais.add(telefone, 2, 5, 2, 1);

        ComboBox<String> escolaridade = new ComboBox<>();
        escolaridade.getItems().addAll(
            "Ensino Medio",
            "Curso Tecnico",
            "Licenciatura",
            "Pos-Graduacao",
            "Mestrado",
            "Doutoramento"
        );
        escolaridade.setPromptText("Nivel de Escolaridade");
        escolaridade.getStyleClass().add("input-field");
        escolaridade.setMaxWidth(Double.MAX_VALUE);

        CheckBox areaTech = new CheckBox("Tecnologia e Engenharias");
        CheckBox areaEco = new CheckBox("Economia, Gestao, Contabilidade e Financas");
        CheckBox areaSaude = new CheckBox("Ciencias e Saude");
        CheckBox areaArtes = new CheckBox("Arquitectura e Artes");
        CheckBox areaMkt = new CheckBox("Marketing, Comunicacao e Design");
        CheckBox areaJuridica = new CheckBox("Ciencias Juridicas");
        CheckBox areaTurismo = new CheckBox("Turismo");
        CheckBox areaAgricultura = new CheckBox("Agricultura e Recursos Naturais");
        CheckBox areaSecretariado = new CheckBox("Secretariado e Traducao");
        CheckBox areaOutra = new CheckBox("Outra");
        TextField areaOutraText = createTextField("Especifique a outra area");

        FlowPane areaEstudoPane = new FlowPane(12, 10,
            areaTech, areaEco, areaSaude, areaArtes, areaMkt,
            areaJuridica, areaTurismo, areaAgricultura, areaSecretariado, areaOutra
        );
        areaEstudoPane.getStyleClass().add("choice-flow");

        TextField curso = createTextField("Curso / Curso Tecnico");
        TextField instituicao = createTextField("Instituicao");
        TextField paisFormacao = createTextField("Pais");
        DatePicker dataFimCurso = createDatePicker("Data de Fim de Curso");

        applyLettersFilter(paisFormacao, false);

        GridPane habilitacoes = createGrid();
        habilitacoes.add(sectionHeader("2. HABILITACOES ACADEMICAS E AREA DE ESTUDO"), 0, 0, 4, 1);
        habilitacoes.add(escolaridade, 0, 1, 4, 1);
        Label areaEstudoLabel = new Label("Area de Estudo");
        areaEstudoLabel.getStyleClass().add("field-label");
        habilitacoes.add(areaEstudoLabel, 0, 2, 4, 1);
        habilitacoes.add(areaEstudoPane, 0, 3, 4, 1);
        habilitacoes.add(areaOutraText, 0, 4, 4, 1);
        habilitacoes.add(curso, 0, 5, 2, 1);
        habilitacoes.add(instituicao, 2, 5, 2, 1);
        habilitacoes.add(paisFormacao, 0, 6, 2, 1);
        habilitacoes.add(dataFimCurso, 2, 6, 2, 1);

        CheckBox areaBanca = new CheckBox("Banca Comercial");
        CheckBox areaLogistica = new CheckBox("Logistica");
        CheckBox areaAdministrativa = new CheckBox("Administrativa");
        CheckBox areaDireito = new CheckBox("Direito");
        CheckBox areaContabilidade = new CheckBox("Contabilidade");
        CheckBox areaTecnologias = new CheckBox("Tecnologias");
        CheckBox areaMarketing = new CheckBox("Marketing");
        CheckBox areaAuditoria = new CheckBox("Auditoria");
        CheckBox areaProjectos = new CheckBox("Gestao de Projectos");
        CheckBox areaRH = new CheckBox("Recursos Humanos");
        CheckBox areaCompliance = new CheckBox("Compliance");
        CheckBox areaInteresseOutro = new CheckBox("Outro");
        TextField areaInteresseOutroText = createTextField("Especifique outra area de interesse");

        FlowPane interessesPane = new FlowPane(12, 10,
            areaBanca, areaLogistica, areaAdministrativa, areaDireito, areaContabilidade,
            areaTecnologias, areaMarketing, areaAuditoria, areaProjectos, areaRH,
            areaCompliance, areaInteresseOutro
        );
        interessesPane.getStyleClass().add("choice-flow");

        GridPane interesses = createGrid();
        interesses.add(sectionHeader("3. AREAS DE INTERESSE PROFISSIONAL"), 0, 0, 4, 1);
        Label interessesLabel = new Label("Assinale as areas de preferencia");
        interessesLabel.getStyleClass().add("field-label");
        interesses.add(interessesLabel, 0, 1, 4, 1);
        interesses.add(interessesPane, 0, 2, 4, 1);
        interesses.add(areaInteresseOutroText, 0, 3, 4, 1);

        TextArea objectivos = new TextArea();
        objectivos.setPromptText("Explique-nos melhor o seu pedido (Objectivos Profissionais, etc.)");
        objectivos.getStyleClass().add("input-area");
        objectivos.setPrefRowCount(4);
        objectivos.setMaxWidth(Double.MAX_VALUE);

        TextArea resumo = new TextArea();
        resumo.setPromptText("Resumo Profissional / Curriculum Vitae");
        resumo.getStyleClass().add("input-area");
        resumo.setPrefRowCount(5);
        resumo.setMaxWidth(Double.MAX_VALUE);

        GridPane adicionais = createGrid();
        adicionais.add(sectionHeader("4. INFORMACOES ADICIONAIS"), 0, 0, 4, 1);
        adicionais.add(objectivos, 0, 1, 4, 1);
        adicionais.add(resumo, 0, 2, 4, 1);

        CheckBox consentimento = new CheckBox("Autorizo que os meus dados sejam utilizados pelo Banco ATLANTICO.");
        Label termosTexto = new Label("Li, entendi e concordo com os termos e condicoes acima apresentados.");
        termosTexto.setWrapText(true);
        termosTexto.getStyleClass().add("terms-label");
        TextField assinatura = createTextField("Assinatura do Candidato");
        DatePicker dataAssinatura = createDatePicker("Data");

        applyLettersFilter(assinatura, true);

        GridPane termos = createGrid();
        termos.add(sectionHeader("5. TERMOS E CONSENTIMENTO"), 0, 0, 4, 1);
        termos.add(consentimento, 0, 1, 4, 1);
        termos.add(termosTexto, 0, 2, 4, 1);
        termos.add(assinatura, 0, 3, 2, 1);
        termos.add(dataAssinatura, 2, 3, 2, 1);

        Button enviar = new Button("Submeter Candidatura");
        enviar.getStyleClass().add("primary-btn");

        Button limpar = new Button("Limpar");
        limpar.getStyleClass().add("secondary-btn");

        Button consultarRegisto = new Button("Consultar Registo");
        consultarRegisto.getStyleClass().add("secondary-btn");
        consultarRegisto.setOnAction(evt -> openConsultationScreen(
            stage,
            "",
            () -> {
                stage.getScene().setRoot(rootHolder[0]);
                stage.setTitle("Banco ATLANTICO | Formulario de Candidatura");
            }
        ));

        bindOptionalField(areaOutra, areaOutraText);
        bindOptionalField(areaInteresseOutro, areaInteresseOutroText);

        dadosPessoais.getStyleClass().add("section-panel");
        habilitacoes.getStyleClass().add("section-panel");
        interesses.getStyleClass().add("section-panel");
        adicionais.getStyleClass().add("section-panel");
        termos.getStyleClass().add("section-panel");

        enviar.setOnAction(evt -> {
            Candidatura candidatura = new Candidatura();
            candidatura.setNomeCompleto(normalize(nomeCompleto.getText()));
            candidatura.setDataNascimento(readDateInput(dataNascimento));
            candidatura.setSexo(selectedToggleText(sexoGroup));
            candidatura.setNacionalidade(normalize(nacionalidade.getText()));
            candidatura.setBiPassaporte(normalize(biPassaporte.getText()));
            candidatura.setResidenciaPais(normalize(residenciaPais.getText()));
            candidatura.setProvincia(normalize(provincia.getText()));
            candidatura.setEmail(normalize(email.getText()));
            candidatura.setContactoTelefonico(normalize(telefone.getText()));
            candidatura.setNivelEscolaridade(normalize(escolaridade.getValue()));
            candidatura.setAreaEstudo(joinSelected(areaTech, areaEco, areaSaude, areaArtes, areaMkt, areaJuridica, areaTurismo, areaAgricultura, areaSecretariado, areaOutra));
            candidatura.setOutraAreaEstudo(normalize(areaOutraText.getText()));
            candidatura.setOutraAreaEstudoSelecionada(areaOutra.isSelected());
            candidatura.setCursoTecnico(normalize(curso.getText()));
            candidatura.setInstituicao(normalize(instituicao.getText()));
            candidatura.setPaisFormacao(normalize(paisFormacao.getText()));
            candidatura.setDataFimCurso(readDateInput(dataFimCurso));
            candidatura.setAreasInteresse(joinSelected(areaBanca, areaLogistica, areaAdministrativa, areaDireito, areaContabilidade, areaTecnologias, areaMarketing, areaAuditoria, areaProjectos, areaRH, areaCompliance, areaInteresseOutro));
            candidatura.setOutraAreaInteresse(normalize(areaInteresseOutroText.getText()));
            candidatura.setOutraAreaInteresseSelecionada(areaInteresseOutro.isSelected());
            candidatura.setObjectivosProfissionais(normalize(objectivos.getText()));
            candidatura.setResumoProfissional(normalize(resumo.getText()));
            candidatura.setConsentimentoAceite(consentimento.isSelected());
            candidatura.setAssinaturaCandidato(normalize(assinatura.getText()));
            candidatura.setDataAssinatura(readDateInput(dataAssinatura));

            try {
                SubmissionStorage storage = candidaturaService.submeter(candidatura);
                limpar.fire();
                final StackPane[] thankYouHolder = new StackPane[1];
                StackPane thankYouView = createThankYouView(
                    candidatura.getNomeCompleto(),
                    storage,
                    () -> {
                        stage.getScene().setRoot(rootHolder[0]);
                        stage.setTitle("Banco ATLANTICO | Formulario de Candidatura");
                    },
                    () -> openConsultationScreen(
                        stage,
                        String.valueOf(storage.getRecordNumber()),
                        () -> {
                            stage.getScene().setRoot(thankYouHolder[0]);
                            stage.setTitle("Banco ATLANTICO | Obrigado pela Candidatura");
                        }
                    )
                );
                thankYouHolder[0] = thankYouView;
                stage.getScene().setRoot(thankYouView);
                stage.setTitle("Banco ATLANTICO | Obrigado pela Candidatura");
            } catch (ValidationException validationException) {
                showAlert(Alert.AlertType.WARNING, "Campo obrigatorio", validationException.getMessage());
            } catch (IOException exception) {
                showAlert(
                    Alert.AlertType.ERROR,
                    "Falha ao gravar",
                    "Nao foi possivel guardar a candidatura em ficheiro de acesso aleatorio.\nDetalhe: " + exception.getMessage()
                );
            }
        });

        limpar.setOnAction(evt -> {
            nomeCompleto.clear();
            dataNascimento.setValue(null);
            sexoGroup.selectToggle(null);
            nacionalidade.clear();
            biPassaporte.clear();
            residenciaPais.clear();
            provincia.clear();
            email.clear();
            telefone.clear();
            escolaridade.getSelectionModel().clearSelection();
            curso.clear();
            instituicao.clear();
            paisFormacao.clear();
            dataFimCurso.setValue(null);
            objectivos.clear();
            resumo.clear();
            consentimento.setSelected(false);
            assinatura.clear();
            dataAssinatura.setValue(null);
            areaOutraText.clear();
            areaInteresseOutroText.clear();

            areaTech.setSelected(false);
            areaEco.setSelected(false);
            areaSaude.setSelected(false);
            areaArtes.setSelected(false);
            areaMkt.setSelected(false);
            areaJuridica.setSelected(false);
            areaTurismo.setSelected(false);
            areaAgricultura.setSelected(false);
            areaSecretariado.setSelected(false);
            areaOutra.setSelected(false);

            areaBanca.setSelected(false);
            areaLogistica.setSelected(false);
            areaAdministrativa.setSelected(false);
            areaDireito.setSelected(false);
            areaContabilidade.setSelected(false);
            areaTecnologias.setSelected(false);
            areaMarketing.setSelected(false);
            areaAuditoria.setSelected(false);
            areaProjectos.setSelected(false);
            areaRH.setSelected(false);
            areaCompliance.setSelected(false);
            areaInteresseOutro.setSelected(false);
        });

        Label saveHint = new Label("Persistencia em ficheiro de acesso aleatorio com registos fixos para leitura direta por numero.");
        saveHint.getStyleClass().add("save-hint");

        Region actionSpacer = new Region();
        HBox.setHgrow(actionSpacer, Priority.ALWAYS);

        HBox actions = new HBox(12, saveHint, actionSpacer, consultarRegisto, limpar, enviar);
        actions.setAlignment(Pos.CENTER_LEFT);
        actions.getStyleClass().add("action-bar");

        formCard.getChildren().addAll(
            eyebrow,
            title,
            subtitle,
            lead,
            highlights,
            dadosPessoais,
            habilitacoes,
            interesses,
            adicionais,
            termos,
            actions
        );

        Label footer = new Label("Banco ATLANTICO - Talento com excelencia e impacto.");
        footer.getStyleClass().add("footer-label");

        page.getChildren().addAll(hero, formCard, footer);

        ScrollPane root = new ScrollPane(page);
        root.setFitToWidth(true);
        root.getStyleClass().add("form-scroll");
        rootHolder[0] = root;

        Scene scene = new Scene(root, 1180, 860);
        scene.getStylesheets().add(
            getClass().getResource("/com/mycompany/teste/atlantico-theme.css").toExternalForm()
        );

        stage.setTitle("Banco ATLANTICO | Formulario de Candidatura");
        stage.setScene(scene);
        stage.setMinWidth(980);
        stage.setMinHeight(720);
        stage.show();
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(12);
        grid.setMaxWidth(Double.MAX_VALUE);

        for (int index = 0; index < 4; index++) {
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setPercentWidth(25);
            constraints.setHgrow(Priority.ALWAYS);
            constraints.setFillWidth(true);
            grid.getColumnConstraints().add(constraints);
        }

        return grid;
    }

    private Label sectionHeader(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("section-title");
        return label;
    }

    private TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("input-field");
        field.setMaxWidth(Double.MAX_VALUE);
        return field;
    }

    private DatePicker createDatePicker(String prompt) {
        DatePicker picker = new DatePicker();
        picker.setPromptText(prompt);
        picker.getStyleClass().add("input-field");
        picker.setMaxWidth(Double.MAX_VALUE);
        picker.setConverter(createDateConverter());
        picker.getEditor().setTextFormatter(new TextFormatter<String>(createDateFilter()));
        return picker;
    }

    private void applyLettersFilter(TextField field, boolean allowDots) {
        String pattern = allowDots ? "[\\p{L} .'-]*" : "[\\p{L} '-]*";
        field.setTextFormatter(new TextFormatter<String>(createPatternFilter(pattern, 60)));
    }

    private void applyDocumentFilter(TextField field) {
        field.setTextFormatter(new TextFormatter<String>(createPatternFilter("[A-Za-z0-9/-]*", 20)));
    }

    private void applyEmailFilter(TextField field) {
        field.setTextFormatter(new TextFormatter<String>(change -> {
            String newText = change.getControlNewText();
            if (newText.length() > 80) {
                return null;
            }
            return newText.matches("[A-Za-z0-9@._+\\-]*") ? change : null;
        }));
    }

    private void applyPhoneFilter(TextField field) {
        field.setTextFormatter(new TextFormatter<String>(createPatternFilter("\\d*", 9)));
    }

    private UnaryOperator<TextFormatter.Change> createPatternFilter(String pattern, int maxLength) {
        return change -> {
            String newText = change.getControlNewText();
            if (newText.length() > maxLength) {
                return null;
            }
            return newText.matches(pattern) ? change : null;
        };
    }

    private UnaryOperator<TextFormatter.Change> createDateFilter() {
        return change -> {
            String newText = change.getControlNewText();
            if (newText.length() > 10) {
                return null;
            }
            return newText.matches("[0-9/]*") ? change : null;
        };
    }

    private StringConverter<LocalDate> createDateConverter() {
        return new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date == null ? "" : DISPLAY_DATE.format(date);
            }

            @Override
            public LocalDate fromString(String value) {
                if (value == null || value.trim().isEmpty()) {
                    return null;
                }
                try {
                    return LocalDate.parse(value.trim(), DISPLAY_DATE);
                } catch (DateTimeParseException exception) {
                    return null;
                }
            }
        };
    }

    private HBox sexBox(RadioButton masculino, RadioButton feminino) {
        Label sexoLabel = new Label("Sexo");
        sexoLabel.getStyleClass().add("sub-label");

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        HBox box = new HBox(14, sexoLabel, masculino, feminino, gap);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getStyleClass().add("radio-box");
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    private VBox createInfoCard(String title, String text) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("info-title");

        Label textLabel = new Label(text);
        textLabel.setWrapText(true);
        textLabel.getStyleClass().add("info-copy");

        VBox card = new VBox(6, titleLabel, textLabel);
        card.getStyleClass().add("info-card");
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private void bindOptionalField(CheckBox toggle, TextField field) {
        field.disableProperty().bind(toggle.selectedProperty().not());
        field.setOpacity(1);
    }

    private StackPane createThankYouView(String candidateName, SubmissionStorage storage, Runnable onBack, Runnable onOpenConsultation) {
        Label badge = new Label("Candidatura Recebida");
        badge.getStyleClass().add("thank-you-badge");

        Label title = new Label("Obrigado por preencher o formulario");
        title.getStyleClass().add("thank-you-title");
        title.setWrapText(true);

        Label message = new Label("A candidatura de " + valueOrDash(candidateName) + " foi registada com sucesso. A camada de persistencia guardou o registo em RandomAccessFile e o numero abaixo pode ser usado para acesso direto ao conteudo.");
        message.getStyleClass().add("thank-you-message");
        message.setWrapText(true);

        Label fileInfo = new Label(
            "Ficheiro RandomAccessFile: " + storage.getFilePath().toAbsolutePath()
                + System.lineSeparator()
                + "Registo: #" + storage.getRecordNumber()
                + System.lineSeparator()
                + "Tamanho do registo: " + candidaturaService.getRecordSize() + " bytes"
        );
        fileInfo.getStyleClass().add("thank-you-file");
        fileInfo.setWrapText(true);

        Button backButton = new Button("Voltar e preencher novamente");
        backButton.getStyleClass().add("primary-btn");
        backButton.setOnAction(evt -> onBack.run());

        Button consultButton = new Button("Consultar este registo agora");
        consultButton.getStyleClass().add("secondary-btn");
        consultButton.setOnAction(evt -> onOpenConsultation.run());

        HBox actionRow = new HBox(12, backButton, consultButton);
        actionRow.getStyleClass().add("screen-action-row");

        VBox card = new VBox(16, badge, title, message, fileInfo, actionRow);
        card.getStyleClass().add("thank-you-card");
        card.setMaxWidth(720);

        StackPane wrapper = new StackPane(card);
        wrapper.getStyleClass().add("thank-you-screen");
        wrapper.setPadding(new Insets(36));
        return wrapper;
    }

    private void openConsultationScreen(Stage stage, String initialRecordNumber, Runnable onBack) {
        StackPane consultationView = createConsultationView(initialRecordNumber, onBack);
        stage.getScene().setRoot(consultationView);
        stage.setTitle("Banco ATLANTICO | Consulta de Registos");
    }

    private StackPane createConsultationView(String initialRecordNumber, Runnable onBack) {
        Label badge = new Label("Consulta RandomAccessFile");
        badge.getStyleClass().add("thank-you-badge");

        Label title = new Label("Consultar candidatura por numero de registo");
        title.getStyleClass().add("consult-title");
        title.setWrapText(true);

        Label description = new Label("Esta tela demonstra o acesso aleatorio: introduza um numero de registo, a aplicacao calcula o offset e le apenas aquele bloco no ficheiro candidaturas.dat.");
        description.getStyleClass().add("consult-help");
        description.setWrapText(true);

        Label formula = new Label("Formula usada: offset = (numero do registo - 1) x " + candidaturaService.getRecordSize() + " bytes");
        formula.getStyleClass().add("consult-formula");
        formula.setWrapText(true);

        TextField recordField = createTextField("Numero do registo");
        recordField.setText(initialRecordNumber == null ? "" : initialRecordNumber);

        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setWrapText(true);
        resultArea.setPrefRowCount(18);
        resultArea.setPromptText("O conteudo do registo aparecera aqui.");
        resultArea.getStyleClass().addAll("input-area", "consult-output");

        Button consultarButton = new Button("Ler Registo");
        consultarButton.getStyleClass().add("primary-btn");
        consultarButton.setOnAction(evt -> {
            String rawValue = normalize(recordField.getText());
            if (rawValue.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Numero obrigatorio", "Informe o numero do registo que pretende consultar.");
                return;
            }

            try {
                long recordNumber = Long.parseLong(rawValue);
                String result = candidaturaService.consultarResumoPorRegisto(recordNumber);
                resultArea.setText(result);
            } catch (NumberFormatException exception) {
                showAlert(Alert.AlertType.WARNING, "Numero invalido", "Introduza um numero inteiro positivo para consultar o registo.");
            } catch (IOException exception) {
                showAlert(Alert.AlertType.ERROR, "Falha na consulta", exception.getMessage());
            }
        });

        Button backButton = new Button("Voltar");
        backButton.getStyleClass().add("secondary-btn");
        backButton.setOnAction(evt -> onBack.run());

        HBox actionRow = new HBox(12, backButton, consultarButton);
        actionRow.getStyleClass().add("screen-action-row");

        VBox card = new VBox(16, badge, title, description, formula, recordField, actionRow, resultArea);
        card.getStyleClass().add("consult-card");
        card.setMaxWidth(820);
        VBox.setVgrow(resultArea, Priority.ALWAYS);

        StackPane wrapper = new StackPane(card);
        wrapper.getStyleClass().add("consult-screen");
        wrapper.setPadding(new Insets(36));
        return wrapper;
    }

    private StackPane createHero() {
        Label bank = new Label("BANCO ATLANTICO");
        bank.getStyleClass().add("hero-bank");

        Label desc = new Label("Candidatura Profissional | Perfil, Competencias e Ambicao");
        desc.getStyleClass().add("hero-desc");

        VBox heroContent = new VBox(8, bank, desc);
        heroContent.setPadding(new Insets(28, 30, 28, 30));

        StackPane hero = new StackPane(heroContent);
        hero.getStyleClass().add("hero");
        return hero;
    }

    private String selectedToggleText(ToggleGroup group) {
        Toggle selectedToggle = group.getSelectedToggle();
        if (selectedToggle instanceof RadioButton) {
            return ((RadioButton) selectedToggle).getText();
        }
        return "";
    }

    private String joinSelected(CheckBox... checkBoxes) {
        StringJoiner joiner = new StringJoiner(", ");
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                joiner.add(checkBox.getText());
            }
        }
        return joiner.toString();
    }

    private String formatDate(LocalDate date) {
        return date == null ? "" : DISPLAY_DATE.format(date);
    }

    private String readDateInput(DatePicker picker) {
        String editorText = normalize(picker.getEditor().getText());
        return editorText.isEmpty() ? formatDate(picker.getValue()) : editorText;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String valueOrDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
