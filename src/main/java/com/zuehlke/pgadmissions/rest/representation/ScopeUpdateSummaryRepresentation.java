package com.zuehlke.pgadmissions.rest.representation;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public class ScopeUpdateSummaryRepresentation {

    private PrismScope scope;

    private Integer resourceCount;

    public PrismScope getScope() {
        return scope;
    }

    public void setScope(PrismScope scope) {
        this.scope = scope;
    }

    public Integer getResourceCount() {
        return resourceCount;
    }

    public void setResourceCount(Integer resourceCount) {
        this.resourceCount = resourceCount;
    }
    
    public ScopeUpdateSummaryRepresentation withScope(PrismScope scope) {
        this.scope = scope;
        return this;
    }

    public ScopeUpdateSummaryRepresentation withResourceCount(Integer resourceCount) {
        this.resourceCount = resourceCount;
        return this;
    }

}
