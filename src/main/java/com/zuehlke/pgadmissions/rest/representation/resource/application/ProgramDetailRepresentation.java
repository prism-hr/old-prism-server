package com.zuehlke.pgadmissions.rest.representation.resource.application;

import java.util.List;

import org.joda.time.LocalDate;

public class ProgramDetailRepresentation {

    private Integer studyOption;

    private LocalDate startDate;

    private Integer referralSource;

    private List<ApplicationSuggestedSupervisorRepresentation> supervisors;

    public Integer getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(Integer studyOption) {
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

    public List<ApplicationSuggestedSupervisorRepresentation> getSupervisors() {
        return supervisors;
    }

    public void setSupervisors(List<ApplicationSuggestedSupervisorRepresentation> supervisors) {
        this.supervisors = supervisors;
    }
}
