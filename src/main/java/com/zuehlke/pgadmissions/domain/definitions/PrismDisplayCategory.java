package com.zuehlke.pgadmissions.domain.definitions;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.INSTITUTION;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROGRAM;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.PROJECT;
import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.SYSTEM;

import com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope;

public enum PrismDisplayCategory {

    SYSTEM_GLOBAL(SYSTEM), //
    INSTITUTION_GLOBAL(INSTITUTION), //
    PROGRAM_GLOBAL(PROGRAM), //
    PROJECT_GLOBAL(PROJECT), //
    APPLICATION_GLOBAL(APPLICATION), //
    APPLICATION_PROGRAM_DETAIL(APPLICATION), //
    APPLICATION_SUPERVISOR(APPLICATION), //
    APPLICATION_PERSONAL_DETAIL(APPLICATION), //
    APPLICATION_PASSPORT(APPLICATION), //
    APPLICATION_LANGUAGE_QUALIFICATION(APPLICATION), //
    APPLICATION_ADDRESS(APPLICATION), //
    APPLICATION_QUALIFICATION(APPLICATION), //
    APPLICATION_EMPLOYMENT_POSITION(APPLICATION), //
    APPLICATION_FUNDING(APPLICATION), //
    APPLICATION_REFEREE(APPLICATION), //
    APPLICATION_DOCUMENT(APPLICATION), //
    APPLICATION_ADDITIONAL_INFORMATION(APPLICATION), //
    APPLICATION_COMMENT(APPLICATION);

    private PrismScope scope;

    private PrismDisplayCategory(PrismScope scope) {
        this.scope = scope;
    }

    public final PrismScope getScope() {
        return scope;
    }

}
