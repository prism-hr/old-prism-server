package com.zuehlke.pgadmissions.dto.resource;

import org.apache.commons.lang.BooleanUtils;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ResourceChildCreationDTO extends ResourceAncestryDTO {

    private Boolean partnerMode;

    public Boolean getPartnerMode() {
        return BooleanUtils.isTrue(partnerMode);
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
