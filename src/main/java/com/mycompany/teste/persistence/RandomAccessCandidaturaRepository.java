package com.mycompany.teste.persistence;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.mycompany.teste.model.Candidatura;

public class RandomAccessCandidaturaRepository {

    private static final Charset STORAGE_CHARSET = StandardCharsets.UTF_8;
    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final int RECORD_PAYLOAD_SIZE = 8192;
    private static final int RECORD_SIZE = 1 + Integer.BYTES + RECORD_PAYLOAD_SIZE;

    private final Path dataFile;

    public RandomAccessCandidaturaRepository(Path directory) {
        this.dataFile = directory.resolve("candidaturas.dat");
    }

    public SubmissionStorage save(Candidatura candidatura) throws IOException {
        Files.createDirectories(dataFile.getParent());

        byte[] payload = serialize(candidatura).getBytes(STORAGE_CHARSET);
        if (payload.length > RECORD_PAYLOAD_SIZE) {
            throw new IOException("A candidatura excede o tamanho maximo do registo de acesso aleatorio.");
        }

        RandomAccessFile file = new RandomAccessFile(dataFile.toFile(), "rw");
        try {
            long recordNumber = (file.length() / RECORD_SIZE) + 1;
            file.seek(file.length());
            file.writeBoolean(true);
            file.writeInt(payload.length);
            file.write(payload);

            int padding = RECORD_PAYLOAD_SIZE - payload.length;
            if (padding > 0) {
                file.write(new byte[padding]);
            }

            return new SubmissionStorage(dataFile, recordNumber);
        } finally {
            file.close();
        }
    }

    public String readRecord(long recordNumber) throws IOException {
        if (recordNumber <= 0) {
            throw new IOException("O numero do registo deve ser maior do que zero.");
        }

        RandomAccessFile file = new RandomAccessFile(dataFile.toFile(), "r");
        try {
            long offset = (recordNumber - 1) * RECORD_SIZE;
            if (offset >= file.length()) {
                throw new IOException("O registo pedido nao existe no ficheiro.");
            }

            file.seek(offset);
            boolean active = file.readBoolean();
            int payloadLength = file.readInt();
            if (!active) {
                throw new IOException("O registo pedido encontra-se marcado como removido.");
            }
            if (payloadLength < 0 || payloadLength > RECORD_PAYLOAD_SIZE) {
                throw new IOException("O registo encontrado esta corrompido.");
            }

            byte[] payload = new byte[RECORD_PAYLOAD_SIZE];
            file.readFully(payload);
            return new String(payload, 0, payloadLength, STORAGE_CHARSET);
        } finally {
            file.close();
        }
    }

    public List<RecordEntry> listAllRecords() throws IOException {
        return readMatchingRecords(null);
    }

    public List<RecordEntry> searchByCandidateName(String candidateName) throws IOException {
        return readMatchingRecords(candidateName);
    }

    public Path getDataFile() {
        return dataFile;
    }

    public int getRecordSize() {
        return RECORD_SIZE;
    }

    private List<RecordEntry> readMatchingRecords(String candidateNameFilter) throws IOException {
        if (!Files.exists(dataFile)) {
            return Collections.emptyList();
        }

        List<RecordEntry> matches = new ArrayList<RecordEntry>();
        String normalizedFilter = candidateNameFilter == null ? null : candidateNameFilter.trim().toLowerCase(Locale.ROOT);

        RandomAccessFile file = new RandomAccessFile(dataFile.toFile(), "r");
        try {
            long totalRecords = file.length() / RECORD_SIZE;
            for (long index = 0; index < totalRecords; index++) {
                long offset = index * RECORD_SIZE;
                file.seek(offset);

                boolean active = file.readBoolean();
                int payloadLength = file.readInt();
                if (payloadLength < 0 || payloadLength > RECORD_PAYLOAD_SIZE) {
                    throw new IOException("Foi encontrado um registo corrompido no ficheiro de candidaturas.");
                }

                byte[] payload = new byte[RECORD_PAYLOAD_SIZE];
                file.readFully(payload);
                if (!active) {
                    continue;
                }

                String content = new String(payload, 0, payloadLength, STORAGE_CHARSET);
                String candidateName = extractCandidateName(content);
                if (normalizedFilter == null || candidateName.toLowerCase(Locale.ROOT).contains(normalizedFilter)) {
                    matches.add(new RecordEntry(index + 1, candidateName, content));
                }
            }
            return matches;
        } finally {
            file.close();
        }
    }

    private String extractCandidateName(String content) {
        String[] lines = content.split("\\R");
        for (String line : lines) {
            if (line.startsWith("Candidato: ")) {
                return line.substring("Candidato: ".length()).trim();
            }
        }
        return "Nao identificado";
    }

    private String serialize(Candidatura candidatura) {
        StringBuilder content = new StringBuilder();
        content.append("BANCO ATLANTICO - CANDIDATURA PROFISSIONAL").append(System.lineSeparator());
        content.append("Candidato: ").append(safeValue(candidatura.getNomeCompleto())).append(System.lineSeparator());
        content.append("Gerado em: ").append(FILE_TIMESTAMP.format(LocalDateTime.now())).append(System.lineSeparator());
        content.append(System.lineSeparator());

        for (Map.Entry<String, String> entry : candidatura.toPersistenceMap().entrySet()) {
            content.append(entry.getKey())
                .append(": ")
                .append(entry.getValue())
                .append(System.lineSeparator());
        }
        return content.toString();
    }

    private String safeValue(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}
