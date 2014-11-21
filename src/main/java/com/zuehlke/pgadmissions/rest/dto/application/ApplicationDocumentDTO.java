package com.zuehlke.pgadmissions.rest.dto.application;

import com.zuehlke.pgadmissions.rest.dto.FileDTO;

public class ApplicationDocumentDTO {

    private FileDTO personalStatement;

    private FileDTO cv;

    private FileDTO coveringLetter;

    public FileDTO getPersonalStatement() {
        return personalStatement;
    }

    public void setPersonalStatement(FileDTO personalStatement) {
        this.personalStatement = personalStatement;
    }

    public FileDTO getCv() {
        return cv;
    }

    public void setCv(FileDTO cv) {
        this.cv = cv;
    }

    public FileDTO getCoveringLetter() {
        return coveringLetter;
    }

    public void setCoveringLetter(FileDTO coveringLetter) {
        this.coveringLetter = coveringLetter;
    }
}
