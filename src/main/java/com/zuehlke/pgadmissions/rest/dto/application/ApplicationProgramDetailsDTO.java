package com.zuehlke.pgadmissions.rest.dto.application;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

public class ApplicationProgramDetailsDTO {

    @NotNull
    private Integer studyOption;

    @NotNull
    private DateTime startDate;

    @NotNull
    private Integer referralSource;

    private ApplicationSupervisorDTO[] supervisors;

    public Integer getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(Integer studyOption) {
        this.studyOption = studyOption;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public Integer getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(Integer referralSource) {
        this.referralSource = referralSource;
    }

    public ApplicationSupervisorDTO[] getSupervisors() {
        return supervisors;
    }

    public void setSupervisors(ApplicationSupervisorDTO[] supervisors) {
        this.supervisors = supervisors;
    }
}
