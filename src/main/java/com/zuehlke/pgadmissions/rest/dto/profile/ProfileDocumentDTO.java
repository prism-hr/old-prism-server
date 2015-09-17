package com.zuehlke.pgadmissions.rest.dto.profile;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

import com.zuehlke.pgadmissions.rest.dto.DocumentDTO;

public class ProfileDocumentDTO {

    @NotEmpty
    private String personalSummary;

    @Valid
    private DocumentDTO cv;

    @Valid
    private DocumentDTO coveringLetter;

    public String getPersonalSummary() {
        return personalSummary;
    }

    public void setPersonalSummary(String personalSummary) {
        this.personalSummary = personalSummary;
    }

    public DocumentDTO getCv() {
        return cv;
    }

    public void setCv(DocumentDTO cv) {
        this.cv = cv;
    }

    public DocumentDTO getCoveringLetter() {
        return coveringLetter;
    }

    public void setCoveringLetter(DocumentDTO coveringLetter) {
        this.coveringLetter = coveringLetter;
    }

}