package com.zuehlke.pgadmissions.rest.dto.advert;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class AdvertTargetResourceDTO extends AdvertTargetDTO {

    private PrismScope scope;

    public PrismScope getScope() {
        return scope;
    }

    public void setScope(PrismScope scope) {
        this.scope = scope;
    }
    
}
