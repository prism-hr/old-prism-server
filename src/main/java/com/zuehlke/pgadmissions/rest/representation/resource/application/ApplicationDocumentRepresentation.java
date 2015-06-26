package com.zuehlke.pgadmissions.rest.representation.resource.application;

import com.zuehlke.pgadmissions.rest.representation.resource.DocumentRepresentation;

public class ApplicationDocumentRepresentation extends ApplicationSectionRepresentation {

    private DocumentRepresentation personalStatement;

    private DocumentRepresentation cv;

    private DocumentRepresentation coveringLetter;
    
    private DocumentRepresentation researchStatement;

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

    public DocumentRepresentation getCoveringLetter() {
        return coveringLetter;
    }

    public void setCoveringLetter(DocumentRepresentation coveringLetter) {
        this.coveringLetter = coveringLetter;
    }

    public final DocumentRepresentation getResearchStatement() {
        return researchStatement;
    }

    public final void setResearchStatement(DocumentRepresentation researchStatement) {
        this.researchStatement = researchStatement;
    }
    
}
