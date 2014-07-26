package com.zuehlke.pgadmissions.rest.dto.application;

import org.joda.time.DateTime;

public class ApplicationProgramDetailsDTO {

    private Integer studyOption;

    private DateTime startDate;

    private Integer referralSource;

    private ApplicationSupervisorDTO[] suggestedSupervisors;

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

    public ApplicationSupervisorDTO[] getSuggestedSupervisors() {
        return suggestedSupervisors;
    }

    public void setSuggestedSupervisors(ApplicationSupervisorDTO[] suggestedSupervisors) {
        this.suggestedSupervisors = suggestedSupervisors;
    }
}
