package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;

public class ProgramDTO extends OpportunityDTO {

    @NotNull
    private Integer institutionId;

    public Integer getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

}
