package com.mycompany.teste;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

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
import javafx.scene.control.TextField;
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

public class App extends Application {

    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int RECORD_PAYLOAD_SIZE = 8192;
    private static final int RECORD_SIZE = 1 + Integer.BYTES + RECORD_PAYLOAD_SIZE;

    private record SubmissionStorage(Path filePath, long recordNumber) {}

    @Override
    public void start(Stage stage) {
        ScrollPane[] rootHolder = new ScrollPane[1];

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

        Label lead = new Label("Preencha o formulario com os seus dados academicos e profissionais. Cada candidatura submetida fica guardada automaticamente em ficheiro.");
        lead.setWrapText(true);
        lead.getStyleClass().add("lead-copy");

        HBox highlights = new HBox(12,
            createInfoCard("Fluxo claro", "Secoes organizadas para preenchimento rapido."),
            createInfoCard("Registo local", "Cada inscricao e guardada numa pasta de ficheiros."),
            createInfoCard("Validacao basica", "Nome, email e consentimento sao obrigatorios.")
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

        bindOptionalField(areaOutra, areaOutraText);
        bindOptionalField(areaInteresseOutro, areaInteresseOutroText);

        dadosPessoais.getStyleClass().add("section-panel");
        habilitacoes.getStyleClass().add("section-panel");
        interesses.getStyleClass().add("section-panel");
        adicionais.getStyleClass().add("section-panel");
        termos.getStyleClass().add("section-panel");

        enviar.setOnAction(evt -> {
            String missingTextField = firstBlankField(
                field("Nome Completo", nomeCompleto.getText()),
                field("Nacionalidade", nacionalidade.getText()),
                field("B.I. / Passaporte", biPassaporte.getText()),
                field("Residencia (Pais)", residenciaPais.getText()),
                field("Provincia", provincia.getText()),
                field("E-mail", email.getText()),
                field("Contacto Telefonico", telefone.getText()),
                field("Curso / Curso Tecnico", curso.getText()),
                field("Instituicao", instituicao.getText()),
                field("Pais da Formacao", paisFormacao.getText()),
                field("Objectivos Profissionais", objectivos.getText()),
                field("Resumo Profissional / Curriculum Vitae", resumo.getText()),
                field("Assinatura do Candidato", assinatura.getText())
            );

            if (missingTextField != null) {
                showRequiredFieldAlert(missingTextField);
                return;
            }

            if (dataNascimento.getValue() == null) {
                showRequiredFieldAlert("Data de Nascimento");
                return;
            }

            if (sexoGroup.getSelectedToggle() == null) {
                showRequiredFieldAlert("Sexo");
                return;
            }

            if (escolaridade.getValue() == null) {
                showRequiredFieldAlert("Nivel de Escolaridade");
                return;
            }

            if (!hasAnySelected(areaTech, areaEco, areaSaude, areaArtes, areaMkt, areaJuridica, areaTurismo, areaAgricultura, areaSecretariado, areaOutra)) {
                showRequiredFieldAlert("Area de Estudo");
                return;
            }

            if (areaOutra.isSelected() && areaOutraText.getText().isBlank()) {
                showRequiredFieldAlert("Especifique a outra area");
                return;
            }

            if (dataFimCurso.getValue() == null) {
                showRequiredFieldAlert("Data de Fim de Curso");
                return;
            }

            if (!hasAnySelected(areaBanca, areaLogistica, areaAdministrativa, areaDireito, areaContabilidade, areaTecnologias, areaMarketing, areaAuditoria, areaProjectos, areaRH, areaCompliance, areaInteresseOutro)) {
                showRequiredFieldAlert("Areas de Interesse");
                return;
            }

            if (areaInteresseOutro.isSelected() && areaInteresseOutroText.getText().isBlank()) {
                showRequiredFieldAlert("Especifique outra area de interesse");
                return;
            }

            if (dataAssinatura.getValue() == null) {
                showRequiredFieldAlert("Data de Assinatura");
                return;
            }

            if (!consentimento.isSelected()) {
                showRequiredFieldAlert("Consentimento de Dados");
                return;
            }

            Map<String, String> submission = new LinkedHashMap<>();
            submission.put("Nome Completo", valueOrDash(nomeCompleto.getText()));
            submission.put("Data de Nascimento", formatDate(dataNascimento.getValue()));
            submission.put("Sexo", selectedToggleText(sexoGroup));
            submission.put("Nacionalidade", valueOrDash(nacionalidade.getText()));
            submission.put("B.I. / Passaporte", valueOrDash(biPassaporte.getText()));
            submission.put("Residencia (Pais)", valueOrDash(residenciaPais.getText()));
            submission.put("Provincia", valueOrDash(provincia.getText()));
            submission.put("E-mail", valueOrDash(email.getText()));
            submission.put("Contacto Telefonico", valueOrDash(telefone.getText()));
            submission.put("Nivel de Escolaridade", valueOrDash(escolaridade.getValue()));
            submission.put("Area de Estudo", joinSelected(areaTech, areaEco, areaSaude, areaArtes, areaMkt, areaJuridica, areaTurismo, areaAgricultura, areaSecretariado, areaOutra));
            submission.put("Outra Area de Estudo", areaOutra.isSelected() ? valueOrDash(areaOutraText.getText()) : "-");
            submission.put("Curso / Curso Tecnico", valueOrDash(curso.getText()));
            submission.put("Instituicao", valueOrDash(instituicao.getText()));
            submission.put("Pais da Formacao", valueOrDash(paisFormacao.getText()));
            submission.put("Data de Fim de Curso", formatDate(dataFimCurso.getValue()));
            submission.put("Areas de Interesse", joinSelected(areaBanca, areaLogistica, areaAdministrativa, areaDireito, areaContabilidade, areaTecnologias, areaMarketing, areaAuditoria, areaProjectos, areaRH, areaCompliance, areaInteresseOutro));
            submission.put("Outra Area de Interesse", areaInteresseOutro.isSelected() ? valueOrDash(areaInteresseOutroText.getText()) : "-");
            submission.put("Objectivos Profissionais", valueOrDash(objectivos.getText()));
            submission.put("Resumo Profissional / Curriculum Vitae", valueOrDash(resumo.getText()));
            submission.put("Consentimento de Dados", consentimento.isSelected() ? "Sim" : "Nao");
            submission.put("Assinatura do Candidato", valueOrDash(assinatura.getText()));
            submission.put("Data de Assinatura", formatDate(dataAssinatura.getValue()));

            try {
                SubmissionStorage storage = saveSubmission(submission, nomeCompleto.getText());
                limpar.fire();
                StackPane thankYouView = createThankYouView(
                    nomeCompleto.getText(),
                    storage,
                    () -> {
                        stage.getScene().setRoot(rootHolder[0]);
                        stage.setTitle("Banco ATLANTICO | Formulario de Candidatura");
                    }
                );
                stage.getScene().setRoot(thankYouView);
                stage.setTitle("Banco ATLANTICO | Obrigado pela Candidatura");
            } catch (IOException exception) {
                showAlert(
                    Alert.AlertType.ERROR,
                    "Falha ao gravar",
                    "Nao foi possivel guardar a candidatura em ficheiro.\nDetalhe: " + exception.getMessage()
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

        Label saveHint = new Label("As inscricoes submetidas sao guardadas automaticamente na pasta inscricoes.");
        saveHint.getStyleClass().add("save-hint");

        Region actionSpacer = new Region();
        HBox.setHgrow(actionSpacer, Priority.ALWAYS);

        HBox actions = new HBox(12, saveHint, actionSpacer, limpar, enviar);
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
        return picker;
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

    private StackPane createThankYouView(String candidateName, SubmissionStorage storage, Runnable onBack) {
        Label badge = new Label("Candidatura Recebida");
        badge.getStyleClass().add("thank-you-badge");

        Label title = new Label("Obrigado por preencher o formulario");
        title.getStyleClass().add("thank-you-title");
        title.setWrapText(true);

        Label message = new Label("A candidatura de " + valueOrDash(candidateName) + " foi registada com sucesso. Os dados foram guardados em ficheiro e a equipa de recrutamento pode agora analisar o seu perfil.");
        message.getStyleClass().add("thank-you-message");
        message.setWrapText(true);

        Label fileInfo = new Label(
            "Ficheiro RandomAccessFile: " + storage.filePath().toAbsolutePath()
                + System.lineSeparator()
                + "Registo: #" + storage.recordNumber()
        );
        fileInfo.getStyleClass().add("thank-you-file");
        fileInfo.setWrapText(true);

        Button backButton = new Button("Voltar e preencher novamente");
        backButton.getStyleClass().add("primary-btn");
        backButton.setOnAction(evt -> onBack.run());

        VBox card = new VBox(16, badge, title, message, fileInfo, backButton);
        card.getStyleClass().add("thank-you-card");
        card.setMaxWidth(680);

        StackPane wrapper = new StackPane(card);
        wrapper.getStyleClass().add("thank-you-screen");
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

    private SubmissionStorage saveSubmission(Map<String, String> submission, String candidateName) throws IOException {
        Path directory = Paths.get(System.getProperty("user.dir"), "inscricoes");
        Files.createDirectories(directory);

        Path targetFile = directory.resolve("candidaturas.dat");

        StringBuilder content = new StringBuilder();
        content.append("BANCO ATLANTICO - CANDIDATURA PROFISSIONAL").append(System.lineSeparator());
        content.append("Candidato: ").append(valueOrDash(candidateName)).append(System.lineSeparator());
        content.append("Gerado em: ").append(FILE_TIMESTAMP.format(LocalDateTime.now())).append(System.lineSeparator());
        content.append(System.lineSeparator());

        for (Map.Entry<String, String> entry : submission.entrySet()) {
            content.append(entry.getKey())
                .append(": ")
                .append(entry.getValue())
                .append(System.lineSeparator());
        }

        byte[] payload = content.toString().getBytes(StandardCharsets.UTF_8);
        if (payload.length > RECORD_PAYLOAD_SIZE) {
            throw new IOException("A candidatura excede o tamanho maximo de registo para RandomAccessFile.");
        }

        try (RandomAccessFile file = new RandomAccessFile(targetFile.toFile(), "rw")) {
            long recordNumber = (file.length() / RECORD_SIZE) + 1;
            file.seek(file.length());
            file.writeBoolean(true);
            file.writeInt(payload.length);
            file.write(payload);

            int padding = RECORD_PAYLOAD_SIZE - payload.length;
            if (padding > 0) {
                file.write(new byte[padding]);
            }

            return new SubmissionStorage(targetFile, recordNumber);
        }
    }

    private String selectedToggleText(ToggleGroup group) {
        if (group.getSelectedToggle() instanceof RadioButton radioButton) {
            return radioButton.getText();
        }
        return "-";
    }

    private boolean hasAnySelected(CheckBox... checkBoxes) {
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                return true;
            }
        }
        return false;
    }

    private String joinSelected(CheckBox... checkBoxes) {
        StringJoiner joiner = new StringJoiner(", ");
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isSelected()) {
                joiner.add(checkBox.getText());
            }
        }
        return joiner.length() == 0 ? "-" : joiner.toString();
    }

    private String formatDate(LocalDate date) {
        return date == null ? "-" : DISPLAY_DATE.format(date);
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "-" : value.trim();
    }

    private Map.Entry<String, String> field(String label, String value) {
        return Map.entry(label, value == null ? "" : value.trim());
    }

    @SafeVarargs
    private String firstBlankField(Map.Entry<String, String>... fields) {
        for (Map.Entry<String, String> field : fields) {
            if (field.getValue().isBlank()) {
                return field.getKey();
            }
        }
        return null;
    }

    private void showRequiredFieldAlert(String fieldName) {
        showAlert(Alert.AlertType.WARNING, "Campo obrigatorio", "Preencha o campo obrigatorio: " + fieldName + ".");
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