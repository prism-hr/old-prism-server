package com.zuehlke.pgadmissions.domain.definitions;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public enum PrismDisplayCategory {

    SYSTEM_GLOBAL(PrismScope.SYSTEM), //
    APPLICATION_GLOBAL(PrismScope.APPLICATION), //
    APPLICATION_PROGRAM_DETAIL(PrismScope.APPLICATION), //
    APPLICATION_SUPERVISOR(PrismScope.APPLICATION), //
    APPLICATION_PERSONAL_DETAIL(PrismScope.APPLICATION), //
    APPLICATION_PERSONAL_DETAIL_PASSPORT(PrismScope.APPLICATION), //
    APPLICATION_REJECTION(PrismScope.APPLICATION);

    private PrismScope scope;

    private PrismDisplayCategory(PrismScope scope) {
        this.scope = scope;
    }

    public final PrismScope getScope() {
        return scope;
    }

}
