package com.mycompany.teste.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.regex.Pattern;

import com.mycompany.teste.model.Candidatura;
import com.mycompany.teste.persistence.RandomAccessCandidaturaRepository;
import com.mycompany.teste.persistence.RecordEntry;
import com.mycompany.teste.persistence.SubmissionStorage;

public class CandidaturaService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{9}$");
    private static final Pattern PERSON_NAME_PATTERN = Pattern.compile("^[\\p{L}][\\p{L} .'-]{2,59}$");
    private static final Pattern PLACE_NAME_PATTERN = Pattern.compile("^[\\p{L}][\\p{L} '-]{1,59}$");
    private static final Pattern DOCUMENT_PATTERN = Pattern.compile("^(?=.*[A-Za-z0-9])[A-Za-z0-9/-]{6,20}$");

    private final RandomAccessCandidaturaRepository repository;

    public CandidaturaService(RandomAccessCandidaturaRepository repository) {
        this.repository = repository;
    }

    public SubmissionStorage submeter(Candidatura candidatura) throws IOException, ValidationException {
        validate(candidatura);
        return repository.save(candidatura);
    }

    public String consultarResumoPorRegisto(long recordNumber) throws IOException {
        return repository.readRecord(recordNumber);
    }

    public String listarTodasAsCandidaturas() throws IOException {
        return formatEntries(repository.listAllRecords(), "Ainda nao existem candidaturas gravadas.");
    }

    public String pesquisarCandidaturasPorNome(String candidateName) throws IOException, ValidationException {
        require("Nome do candidato", candidateName);
        return formatEntries(
            repository.searchByCandidateName(candidateName),
            "Nenhuma candidatura encontrada para o nome informado."
        );
    }

    public int getRecordSize() {
        return repository.getRecordSize();
    }

    private void validate(Candidatura candidatura) throws ValidationException {
        require("Nome Completo", candidatura.getNomeCompleto());
        require("Data de Nascimento", candidatura.getDataNascimento());
        require("Sexo", candidatura.getSexo());
        require("Nacionalidade", candidatura.getNacionalidade());
        require("B.I. / Passaporte", candidatura.getBiPassaporte());
        require("Pais de Residencia", candidatura.getResidenciaPais());
        require("Provincia", candidatura.getProvincia());
        require("E-mail", candidatura.getEmail());
        require("Contacto Telefonico", candidatura.getContactoTelefonico());
        require("Nivel de Escolaridade", candidatura.getNivelEscolaridade());
        require("Area de Estudo", candidatura.getAreaEstudo());
        if (candidatura.isOutraAreaEstudoSelecionada()) {
            require("Especifique a outra area", candidatura.getOutraAreaEstudo());
        }
        require("Curso / Curso Tecnico", candidatura.getCursoTecnico());
        require("Instituicao", candidatura.getInstituicao());
        require("Pais da Formacao", candidatura.getPaisFormacao());
        require("Areas de Interesse", candidatura.getAreasInteresse());
        if (candidatura.isOutraAreaInteresseSelecionada()) {
            require("Especifique outra area de interesse", candidatura.getOutraAreaInteresse());
        }
        require("Objectivos Profissionais", candidatura.getObjectivosProfissionais());
        require("Resumo Profissional / Curriculum Vitae", candidatura.getResumoProfissional());
        require("Assinatura do Candidato", candidatura.getAssinaturaCandidato());
        require("Data de Assinatura", candidatura.getDataAssinatura());

        validatePersonName("Nome Completo", candidatura.getNomeCompleto());
        validatePersonName("Assinatura do Candidato", candidatura.getAssinaturaCandidato());
        validatePlaceName("Nacionalidade", candidatura.getNacionalidade());
        validatePlaceName("Pais de Residencia", candidatura.getResidenciaPais());
        validatePlaceName("Provincia", candidatura.getProvincia());
        validatePlaceName("Pais da Formacao", candidatura.getPaisFormacao());
        validateDocument(candidatura.getBiPassaporte());
        validateEmail(candidatura.getEmail());
        validatePhone(candidatura.getContactoTelefonico());

        LocalDate dataNascimento = validatePastOrPresentDate("Data de Nascimento", candidatura.getDataNascimento());
        LocalDate dataAssinatura = validatePastOrPresentDate("Data de Assinatura", candidatura.getDataAssinatura());

        if (containsText(candidatura.getDataFimCurso())) {
            LocalDate dataFimCurso = validatePastOrPresentDate("Data de Conclusao do Curso", candidatura.getDataFimCurso());
            if (dataFimCurso.isBefore(dataNascimento)) {
                throw new ValidationException("Data de Conclusao do Curso", "A data de conclusao do curso nao pode ser anterior a data de nascimento.");
            }
        }

        if (dataAssinatura.isBefore(dataNascimento)) {
            throw new ValidationException("Data de Assinatura", "A data de assinatura nao pode ser anterior a data de nascimento.");
        }

        if (!candidatura.isConsentimentoAceite()) {
            throw new ValidationException("Consentimento de Dados", "Marque o consentimento de dados antes de submeter.");
        }
    }

    private void require(String fieldName, String value) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName, "Preencha o campo obrigatorio: " + fieldName + ".");
        }
    }

    private void validateEmail(String email) throws ValidationException {
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("E-mail", "Introduza um endereco de e-mail valido.");
        }
    }

    private void validatePersonName(String fieldName, String value) throws ValidationException {
        if (!PERSON_NAME_PATTERN.matcher(value.trim()).matches()) {
            throw new ValidationException(fieldName, "O campo " + fieldName + " deve conter apenas letras e ter pelo menos 3 caracteres.");
        }
    }

    private void validatePlaceName(String fieldName, String value) throws ValidationException {
        if (!PLACE_NAME_PATTERN.matcher(value.trim()).matches()) {
            throw new ValidationException(fieldName, "O campo " + fieldName + " deve conter apenas letras, espacos, apostrofos ou hifens.");
        }
    }

    private void validateDocument(String document) throws ValidationException {
        if (!DOCUMENT_PATTERN.matcher(document.trim()).matches()) {
            throw new ValidationException("B.I. / Passaporte", "O campo B.I. / Passaporte deve ter entre 6 e 20 caracteres alfanumericos e pode incluir '/' ou '-'.");
        }
    }

    private void validatePhone(String phone) throws ValidationException {
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new ValidationException("Contacto Telefonico", "O numero de telefone deve conter exatamente 9 digitos.");
        }
    }

    private LocalDate validatePastOrPresentDate(String fieldName, String value) throws ValidationException {
        try {
            LocalDate parsedDate = LocalDate.parse(value.trim(), DATE_FORMATTER);
            if (parsedDate.isAfter(LocalDate.now())) {
                throw new ValidationException(fieldName, "A data informada em " + fieldName + " nao pode ser futura.");
            }
            return parsedDate;
        } catch (DateTimeParseException exception) {
            throw new ValidationException(fieldName, "A data informada em " + fieldName + " deve estar no formato dd/MM/yyyy.");
        }
    }

    private boolean containsText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String formatEntries(List<RecordEntry> entries, String emptyMessage) {
        if (entries.isEmpty()) {
            return emptyMessage;
        }

        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < entries.size(); index++) {
            RecordEntry entry = entries.get(index);
            builder.append("REGISTO #")
                .append(entry.getRecordNumber())
                .append(" - ")
                .append(entry.getCandidateName())
                .append(System.lineSeparator())
                .append(entry.getContent());

            if (index < entries.size() - 1) {
                builder.append(System.lineSeparator())
                    .append(System.lineSeparator())
                    .append("------------------------------------------------------------")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
            }
        }
        return builder.toString();
    }
}
