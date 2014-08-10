package com.zuehlke.pgadmissions.rest.dto;

import javax.validation.constraints.NotNull;

public class ProjectDTO {

    @NotNull
    private Integer programId;

    public final Integer getProgramId() {
        return programId;
    }

    public final void setProgramId(Integer programId) {
        this.programId = programId;
    }
    
}
