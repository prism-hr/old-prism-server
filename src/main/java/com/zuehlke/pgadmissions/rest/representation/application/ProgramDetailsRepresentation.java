package com.zuehlke.pgadmissions.rest.representation.application;

import java.util.List;

import org.joda.time.LocalDate;

public class ProgramDetailsRepresentation {

    private Integer studyOption;

    private LocalDate startDate;

    private Integer referralSource;

    private List<SupervisorRepresentation> supervisors;

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

    public List<SupervisorRepresentation> getSupervisors() {
        return supervisors;
    }

    public void setSupervisors(List<SupervisorRepresentation> supervisors) {
        this.supervisors = supervisors;
    }
}
