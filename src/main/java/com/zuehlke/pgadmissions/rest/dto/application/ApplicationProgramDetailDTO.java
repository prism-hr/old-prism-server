package com.zuehlke.pgadmissions.rest.dto.application;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;

public class ApplicationProgramDetailDTO {

    @NotNull
    private Integer studyOption;

    @NotNull
    private DateTime startDate;

    @NotNull
    private Integer referralSource;

    private List<ApplicationSupervisorDTO> supervisors;

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

    public List<ApplicationSupervisorDTO> getSupervisors() {
        return supervisors;
    }

    public void setSupervisors(List<ApplicationSupervisorDTO> supervisors) {
        this.supervisors = supervisors;
    }
}
