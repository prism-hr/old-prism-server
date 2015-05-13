package com.zuehlke.pgadmissions.rest.dto;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.zuehlke.pgadmissions.domain.definitions.PrismOpportunityType;
import com.zuehlke.pgadmissions.domain.definitions.PrismStudyOption;
import com.zuehlke.pgadmissions.dto.DepartmentDTO;

public class OpportunityDTO extends ResourceParentDTO {

    @Valid
    private InstitutionPartnerDTO partner;

    @Valid
    private DepartmentDTO department;

    @NotNull
    private PrismOpportunityType opportunityType;

    private Integer durationMinimum;

    private Integer durationMaximum;

    private List<PrismStudyOption> studyOptions;

    public InstitutionPartnerDTO getPartner() {
        return partner;
    }

    public void setPartner(InstitutionPartnerDTO partner) {
        this.partner = partner;
    }

    public DepartmentDTO getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentDTO department) {
        this.department = department;
    }

    public PrismOpportunityType getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(PrismOpportunityType opportunityType) {
        this.opportunityType = opportunityType;
    }

    public Integer getDurationMinimum() {
        return durationMinimum;
    }

    public void setDurationMinimum(Integer durationMinimum) {
        this.durationMinimum = durationMinimum;
    }

    public Integer getDurationMaximum() {
        return durationMaximum;
    }

    public void setDurationMaximum(Integer durationMaximum) {
        this.durationMaximum = durationMaximum;
    }

    public List<PrismStudyOption> getStudyOptions() {
        return studyOptions;
    }

    public void setStudyOptions(List<PrismStudyOption> studyOptions) {
        this.studyOptions = studyOptions;
    }

}
