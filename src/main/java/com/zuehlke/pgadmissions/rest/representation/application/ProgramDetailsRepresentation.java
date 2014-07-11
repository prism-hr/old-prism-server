package com.zuehlke.pgadmissions.rest.representation.application;

import java.util.List;

import org.joda.time.LocalDate;

public class ProgramDetailsRepresentation {

    private String studyOption;

    private LocalDate startDate;

    private String referralSource;

    private List<SupervisorRepresentation> suggestedSupervisors;

    public String getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(String studyOption) {
        this.studyOption = studyOption;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(String referralSource) {
        this.referralSource = referralSource;
    }

    public List<SupervisorRepresentation> getSuggestedSupervisors() {
        return suggestedSupervisors;
    }

    public void setSuggestedSupervisors(List<SupervisorRepresentation> suggestedSupervisors) {
        this.suggestedSupervisors = suggestedSupervisors;
    }
}
