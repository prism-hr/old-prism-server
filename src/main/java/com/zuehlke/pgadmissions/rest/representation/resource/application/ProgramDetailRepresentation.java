package com.zuehlke.pgadmissions.rest.representation.resource.application;

import java.util.List;

import com.zuehlke.pgadmissions.domain.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import org.joda.time.LocalDate;

public class ProgramDetailRepresentation {

    private PrismStudyOption studyOption;

    private LocalDate startDate;

    private Integer referralSource;

    private List<ApplicationSuggestedSupervisorRepresentation> supervisors;

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

    public List<ApplicationSuggestedSupervisorRepresentation> getSupervisors() {
        return supervisors;
    }

    public void setSupervisors(List<ApplicationSuggestedSupervisorRepresentation> supervisors) {
        this.supervisors = supervisors;
    }
}
