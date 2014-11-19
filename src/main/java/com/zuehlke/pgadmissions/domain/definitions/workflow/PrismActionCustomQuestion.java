package com.zuehlke.pgadmissions.domain.definitions.workflow;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismScope.APPLICATION;

public enum PrismActionCustomQuestion {

    APPLICATION_COMPLETE_CUSTOM(APPLICATION), //
    APPLICATION_PROVIDE_REVIEW_CUSTOM(APPLICATION), //
    APPLICATION_PROVIDE_INTERVIEW_FEEDBACK_CUSTOM(APPLICATION), //
    APPLICATION_PROVIDE_REFERENCE_CUSTOM(APPLICATION);

    private PrismScope scope;

    private PrismActionCustomQuestion(PrismScope scope) {
        this.scope = scope;
    }

    public final PrismScope getScope() {
        return scope;
    }

}
