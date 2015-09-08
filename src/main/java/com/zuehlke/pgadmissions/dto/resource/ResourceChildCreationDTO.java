package com.zuehlke.pgadmissions.dto.resource;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ResourceChildCreationDTO extends ResourceStandardDTO {

    private Boolean externalMode;

    public Boolean getExternalMode() {
        return externalMode;
    }

    public void setExternalMode(Boolean externalMode) {
        this.externalMode = externalMode;
    }

    @Override
    public ResourceChildCreationDTO getParentResource() {
        return super.getParentResource(ResourceChildCreationDTO.class);
    }

    @Override
    public ResourceChildCreationDTO getEnclosingResource(PrismScope scope) {
        return super.getEnclosingResource(scope, ResourceChildCreationDTO.class);
    }

}
