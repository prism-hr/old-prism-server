package com.zuehlke.pgadmissions.dto.resource;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ResourceChildCreationDTO extends ResourceStandardDTO {

    private Boolean partnerMode;

    public Boolean getPartnerMode() {
        return partnerMode;
    }

    public void setPartnerMode(Boolean partnerMode) {
        this.partnerMode = partnerMode;
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
