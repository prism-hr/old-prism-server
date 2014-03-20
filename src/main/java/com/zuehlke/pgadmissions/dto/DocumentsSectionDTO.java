package com.zuehlke.pgadmissions.dto;

import com.zuehlke.pgadmissions.domain.ApplicationForm;
import com.zuehlke.pgadmissions.domain.Document;
import com.zuehlke.pgadmissions.domain.FormSectionObject;

public class DocumentsSectionDTO implements FormSectionObject {

    private Document cv;

    private Document personalStatement;

    private boolean acceptedTerms;

    private ApplicationForm application;

    public Document getCv() {
        return cv;
    }

    public void setCv(Document cv) {
        this.cv = cv;
    }

    public Document getPersonalStatement() {
        return personalStatement;
    }

    public void setPersonalStatement(Document personalStatement) {
        this.personalStatement = personalStatement;
    }

    public boolean isAcceptedTerms() {
        return acceptedTerms;
    }

    public void setAcceptedTerms(boolean acceptedTerms) {
        this.acceptedTerms = acceptedTerms;
    }

    public ApplicationForm getApplication() {
        return application;
    }

    public void setApplication(ApplicationForm application) {
        this.application = application;
    }

}
