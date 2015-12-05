package uk.co.alumeni.prism.domain.definitions.workflow;

import uk.co.alumeni.prism.workflow.resolvers.state.duration.ApplicationInterviewDateResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.duration.ProgramClosingDateResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.duration.ProjectClosingDateResolver;
import uk.co.alumeni.prism.workflow.resolvers.state.duration.StateDurationResolver;

public enum PrismStateDurationEvaluation {

    APPLICATION_INTERVIEW_DATE(ApplicationInterviewDateResolver.class), //
    PROJECT_CLOSING_DATE(ProjectClosingDateResolver.class), //
    PROGRAM_CLOSING_DATE(ProgramClosingDateResolver.class);

    private Class<? extends StateDurationResolver<?>> resolver;

    private PrismStateDurationEvaluation(Class<? extends StateDurationResolver<?>> resolver) {
        this.resolver = resolver;
    }

    public Class<? extends StateDurationResolver<?>> getResolver() {
        return resolver;
    }

}
