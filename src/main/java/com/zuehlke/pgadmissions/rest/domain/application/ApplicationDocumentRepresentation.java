package com.zuehlke.pgadmissions.rest.domain.application;

public class ApplicationDocumentRepresentation {

    private DocumentRepresentation personalStatement;

    private DocumentRepresentation cv;

    public DocumentRepresentation getPersonalStatement() {
        return personalStatement;
    }

    public void setPersonalStatement(DocumentRepresentation personalStatement) {
        this.personalStatement = personalStatement;
    }

    public DocumentRepresentation getCv() {
        return cv;
    }

    public void setCv(DocumentRepresentation cv) {
        this.cv = cv;
    }
}
