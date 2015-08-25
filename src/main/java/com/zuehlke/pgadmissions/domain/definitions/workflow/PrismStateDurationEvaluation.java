package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.zuehlke.pgadmissions.workflow.resolvers.state.duration.ApplicationClosingDateResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.duration.ApplicationInterviewDateResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.duration.StateDurationResolver;

public enum PrismStateDurationEvaluation {

    APPLICATION_CLOSING_DATE(ApplicationClosingDateResolver.class), //
    APPLICATION_INTERVIEW_DATE(ApplicationInterviewDateResolver.class);

    private Class<? extends StateDurationResolver<?>> resolver;

    private PrismStateDurationEvaluation(Class<? extends StateDurationResolver<?>> resolver) {
        this.resolver = resolver;
    }

    public Class<? extends StateDurationResolver<?>> getResolver() {
        return resolver;
    }

}
