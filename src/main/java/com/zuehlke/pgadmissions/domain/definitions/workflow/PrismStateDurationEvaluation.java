package com.zuehlke.pgadmissions.domain.definitions.workflow;

import com.zuehlke.pgadmissions.workflow.resolvers.state.duration.ApplicationClosingDateResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.duration.ApplicationInterviewDateResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.duration.ProgramEndDateResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.duration.ProjectEndDateResolver;
import com.zuehlke.pgadmissions.workflow.resolvers.state.duration.StateDurationResolver;

public enum PrismStateDurationEvaluation {

    APPLICATION_CLOSING_DATE(ApplicationClosingDateResolver.class), //
    APPLICATION_INTERVIEW_DATE(ApplicationInterviewDateResolver.class), //
    PROJECT_END_DATE(ProjectEndDateResolver.class), //
    PROGRAM_END_DATE(ProgramEndDateResolver.class);

    private Class<? extends StateDurationResolver> resolver;

    private PrismStateDurationEvaluation(Class<? extends StateDurationResolver> resolver) {
        this.resolver = resolver;
    }

    public Class<? extends StateDurationResolver> getResolver() {
        return resolver;
    }

}
