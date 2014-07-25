package com.zuehlke.pgadmissions.rest.dto.application;

import org.joda.time.DateTime;

public class ApplicationProgramDetailsDTO {

    private String studyOption;

    private DateTime startDate;

    private String referralSource;

    private ApplicationSupervisorDTO[] suggestedSupervisors;

    public String getStudyOption() {
        return studyOption;
    }

    public void setStudyOption(String studyOption) {
        this.studyOption = studyOption;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public String getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(String referralSource) {
        this.referralSource = referralSource;
    }

    public ApplicationSupervisorDTO[] getSuggestedSupervisors() {
        return suggestedSupervisors;
    }

    public void setSuggestedSupervisors(ApplicationSupervisorDTO[] suggestedSupervisors) {
        this.suggestedSupervisors = suggestedSupervisors;
    }
}
