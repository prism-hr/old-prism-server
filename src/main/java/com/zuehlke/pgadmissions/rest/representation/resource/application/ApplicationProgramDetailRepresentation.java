package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

import uk.co.alumeni.prism.api.model.imported.response.ImportedEntityResponse;

public class ApplicationProgramDetailRepresentation extends ApplicationSectionRepresentation {

    private ImportedEntityResponse studyOption;

    private LocalDate startDate;

    private ImportedEntityResponse referralSource;

    public ImportedEntityResponse getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(ImportedEntityResponse studyOption) {
        this.studyOption = studyOption;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public ImportedEntityResponse getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(ImportedEntityResponse referralSource) {
        this.referralSource = referralSource;
    }

    public ApplicationProgramDetailRepresentation withStudyOption(ImportedEntityResponse studyOption) {
        this.studyOption = studyOption;
        return this;
    }

    public ApplicationProgramDetailRepresentation withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public ApplicationProgramDetailRepresentation withReferralSource(ImportedEntityResponse referralSource) {
        this.referralSource = referralSource;
        return this;
    }

}
