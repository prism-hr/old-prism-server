package com.zuehlke.pgadmissions.domain.definitions;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public enum PrismDisplayCategory {

    SYSTEM_RUNNING(PrismScope.SYSTEM),
    APPLICATION_REJECTED(PrismScope.APPLICATION);
    
    private PrismScope scope;
    
    private PrismDisplayCategory(PrismScope scope) {
        this.scope = scope;
    }

    public final PrismScope getScope() {
        return scope;
    }
    
}
