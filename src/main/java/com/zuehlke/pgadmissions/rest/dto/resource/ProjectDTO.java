package com.zuehlke.pgadmissions.rest.dto.resource;

import org.apache.commons.lang3.ObjectUtils;

public class ProjectDTO extends ResourceOpportunityDTO {

    private ResourceOpportunityDTO newProgram;

    public ResourceOpportunityDTO getNewProgram() {
        return newProgram;
    }

    public void setNewProgram(ResourceOpportunityDTO newProgram) {
        this.newProgram = newProgram;
    }

    @Override
    public ResourceParentDTO getNewParentResource() {
        return ObjectUtils.firstNonNull(newProgram, super.getNewParentResource());
    }

}
