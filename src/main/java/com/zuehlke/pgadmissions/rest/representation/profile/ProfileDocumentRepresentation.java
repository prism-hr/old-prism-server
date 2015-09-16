package com.zuehlke.pgadmissions.rest.representation.profile;

import com.zuehlke.pgadmissions.rest.representation.DocumentRepresentation;
import com.zuehlke.pgadmissions.rest.representation.resource.application.ApplicationSectionRepresentation;

public class ProfileDocumentRepresentation extends ApplicationSectionRepresentation {

    private String personalSummary;

    private DocumentRepresentation cv;

    private DocumentRepresentation coveringLetter;

    private String linkedinProfileUrl;

    public String getPersonalSummary() {
        return personalSummary;
    }

    public void setPersonalSummary(String personalSummary) {
        this.personalSummary = personalSummary;
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

    public String getLinkedinProfileUrl() {
        return linkedinProfileUrl;
    }

    public void setLinkedinProfileUrl(String linkedinProfileUrl) {
        this.linkedinProfileUrl = linkedinProfileUrl;
    }

}
