package com.zuehlke.pgadmissions.rest.representation.resource.application;

import org.joda.time.LocalDate;

import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;

public class ProgramDetailRepresentation extends ApplicationSectionRepresentation {

    private PrismStudyOption studyOption;

    private LocalDate startDate;

    private Integer referralSource;

    public PrismStudyOption getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(PrismStudyOption studyOption) {
        this.studyOption = studyOption;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Integer getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(Integer referralSource) {
        this.referralSource = referralSource;
    }

}
