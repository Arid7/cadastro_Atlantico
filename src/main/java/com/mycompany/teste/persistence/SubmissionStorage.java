package com.mycompany.teste.persistence;

import java.nio.file.Path;

public class SubmissionStorage {

    private final Path filePath;
    private final long recordNumber;

    public SubmissionStorage(Path filePath, long recordNumber) {
        this.filePath = filePath;
        this.recordNumber = recordNumber;
    }

    public Path getFilePath() {
        return filePath;
    }

    public long getRecordNumber() {
        return recordNumber;
    }
}
