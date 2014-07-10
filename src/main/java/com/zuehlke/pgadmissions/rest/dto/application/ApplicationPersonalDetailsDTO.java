package com.zuehlke.pgadmissions.rest.dto.application;

import org.joda.time.LocalDate;

public class ApplicationPersonalDetailsDTO {

    private Integer applicationId;

    private String studyOption;

    private LocalDate startDate;

    private String referralSource;

    private ApplicationSupervisorDTO[] suggestedSupervisors;

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

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

    public ApplicationSupervisorDTO[] getSuggestedSupervisors() {
        return suggestedSupervisors;
    }

    public void setSuggestedSupervisors(ApplicationSupervisorDTO[] suggestedSupervisors) {
        this.suggestedSupervisors = suggestedSupervisors;
    }
}
