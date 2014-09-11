package com.zuehlke.pgadmissions.rest.dto.application;

import com.zuehlke.pgadmissions.domain.ProgramStudyOption;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import org.joda.time.LocalDate;

import javax.validation.constraints.NotNull;
import java.util.List;

public class ApplicationProgramDetailDTO {

    @NotNull
    private PrismStudyOption studyOption;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private Integer referralSource;

    private List<ApplicationSupervisorDTO> supervisors;

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

    public List<ApplicationSupervisorDTO> getSupervisors() {
        return supervisors;
    }

    public void setSupervisors(List<ApplicationSupervisorDTO> supervisors) {
        this.supervisors = supervisors;
    }
}
