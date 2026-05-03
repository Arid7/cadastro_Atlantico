package com.mycompany.teste.persistence;

public class RecordEntry {

    private final long recordNumber;
    private final String candidateName;
    private final String content;

    public RecordEntry(long recordNumber, String candidateName, String content) {
        this.recordNumber = recordNumber;
        this.candidateName = candidateName;
        this.content = content;
    }

    public long getRecordNumber() {
        return recordNumber;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public String getContent() {
        return content;
    }
}
