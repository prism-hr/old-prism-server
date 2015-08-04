package com.zuehlke.pgadmissions.workflow.resolvers.state.duration;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROGRAM_DISABLED_COMPLETED;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Program;
import com.zuehlke.pgadmissions.services.ResourceService;

@Component
public class ProgramEndDateResolver implements StateDurationResolver<Program> {

    @Inject
    private ResourceService resourceService;

    @Override
    public LocalDate resolve(Program resource, Comment comment) {
        return comment.getTransitionState().getId() == PROGRAM_DISABLED_COMPLETED ? null : resourceService.getResourceEndDate(resource.getProgram());
    }

}
