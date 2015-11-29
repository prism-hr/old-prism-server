package uk.co.alumeni.prism.domain.definitions.workflow;

import uk.co.alumeni.prism.workflow.resolvers.state.duration.ApplicationClosingDateResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.duration.ApplicationInterviewDateResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.duration.StateDurationResolver;

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
