package com.zuehlke.pgadmissions.rest.representation;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ResourceRepresentation {

    private Integer id;

    private PrismScope resourceScope;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PrismScope getResourceScope() {
        return resourceScope;
    }

    public void setResourceScope(PrismScope resourceScope) {
        this.resourceScope = resourceScope;
    }

}
