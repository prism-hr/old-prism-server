package com.zuehlke.pgadmissions.rest.representation.resource.application;

import com.zuehlke.pgadmissions.rest.representation.FileRepresentation;

public class ApplicationDocumentRepresentation extends ApplicationSectionRepresentation {

    private FileRepresentation personalStatement;

    private FileRepresentation cv;

    private FileRepresentation coveringLetter;

    private FileRepresentation researchStatement;

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

    public final FileRepresentation getResearchStatement() {
        return researchStatement;
    }

    public final void setResearchStatement(FileRepresentation researchStatement) {
        this.researchStatement = researchStatement;
    }

    public ApplicationDocumentRepresentation withPersonalStatement(FileRepresentation personalStatement) {
        this.personalStatement = personalStatement;
        return this;
    }

    public ApplicationDocumentRepresentation withCv(FileRepresentation cv) {
        this.cv = cv;
        return this;
    }

    public ApplicationDocumentRepresentation withCoveringLetter(FileRepresentation coveringLetter) {
        this.coveringLetter = coveringLetter;
        return this;
    }

}
