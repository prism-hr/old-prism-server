package com.zuehlke.pgadmissions.domain.definitions;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public enum PrismDisplayCategory {

    GLOBAL(PrismScope.SYSTEM);
    
    private PrismScope scope;
    
    private PrismDisplayCategory(PrismScope scope) {
        this.scope = scope;
    }

    public final PrismScope getScope() {
        return scope;
    }
    
}
