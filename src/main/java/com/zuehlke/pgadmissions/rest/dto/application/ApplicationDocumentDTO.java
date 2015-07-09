package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.Valid;

import com.zuehlke.pgadmissions.rest.dto.FileDTO;

public class ApplicationDocumentDTO {

    @Valid
    private FileDTO personalStatement;

    @Valid
    private FileDTO cv;

    @Valid
    private FileDTO researchStatement;

    @Valid
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

    public final FileDTO getResearchStatement() {
        return researchStatement;
    }

    public final void setResearchStatement(FileDTO researchStatement) {
        this.researchStatement = researchStatement;
    }

    public FileDTO getCoveringLetter() {
        return coveringLetter;
    }

    public void setCoveringLetter(FileDTO coveringLetter) {
        this.coveringLetter = coveringLetter;
    }

}
