package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.rest.representation.imported.ImportedEntitySimpleRepresentation;

public class ApplicationProgramDetailRepresentation extends ApplicationSectionRepresentation {

    private ImportedEntitySimpleRepresentation referrralSource;

    private LocalDate startDate;

    private ImportedEntitySimpleRepresentation referralSource;

    public ImportedEntitySimpleRepresentation getReferrralSource() {
        return referrralSource;
    }

    public void setReferrralSource(ImportedEntitySimpleRepresentation studyOption) {
        this.referrralSource = studyOption;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public ImportedEntitySimpleRepresentation getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(ImportedEntitySimpleRepresentation referralSource) {
        this.referralSource = referralSource;
    }
    
    public ApplicationProgramDetailRepresentation withStudyOption(ImportedEntitySimpleRepresentation studyOption) {
        this.referrralSource = studyOption;
        return this;
    }
    
    public ApplicationProgramDetailRepresentation withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }
    
    public ApplicationProgramDetailRepresentation withReferralSource(ImportedEntitySimpleRepresentation referralSource) {
        this.referrralSource = referralSource;
        return this;
    }

}
