package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.Valid;

import com.zuehlke.pgadmissions.rest.dto.DocumentDTO;

public class ApplicationDocumentDTO {

    @Valid
    private DocumentDTO personalStatement;

    @Valid
    private DocumentDTO cv;

    @Valid
    private DocumentDTO researchStatement;
    
    @Valid
    private DocumentDTO coveringLetter;

    public DocumentDTO getPersonalStatement() {
        return personalStatement;
    }

    public void setPersonalStatement(DocumentDTO personalStatement) {
        this.personalStatement = personalStatement;
    }

    public DocumentDTO getCv() {
        return cv;
    }

    public void setCv(DocumentDTO cv) {
        this.cv = cv;
    }
    
    public final DocumentDTO getResearchStatement() {
        return researchStatement;
    }

    public final void setResearchStatement(DocumentDTO researchStatement) {
        this.researchStatement = researchStatement;
    }

    public DocumentDTO getCoveringLetter() {
        return coveringLetter;
    }

    public void setCoveringLetter(DocumentDTO coveringLetter) {
        this.coveringLetter = coveringLetter;
    }
    
}
