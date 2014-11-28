package com.zuehlke.pgadmissions.rest.representation.resource.application;

import com.zuehlke.pgadmissions.rest.representation.resource.FileRepresentation;

public class ApplicationDocumentRepresentation {

    private FileRepresentation personalStatement;

    private FileRepresentation cv;

    private FileRepresentation coveringLetter;

    public FileRepresentation getPersonalStatement() {
        return personalStatement;
    }

    public void setPersonalStatement(FileRepresentation personalStatement) {
        this.personalStatement = personalStatement;
    }

    public FileRepresentation getCv() {
        return cv;
    }

    public void setCv(FileRepresentation cv) {
        this.cv = cv;
    }

    public FileRepresentation getCoveringLetter() {
        return coveringLetter;
    }

    public void setCoveringLetter(FileRepresentation coveringLetter) {
        this.coveringLetter = coveringLetter;
    }
}
