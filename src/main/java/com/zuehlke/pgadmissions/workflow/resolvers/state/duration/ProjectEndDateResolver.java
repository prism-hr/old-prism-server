package com.zuehlke.pgadmissions.workflow.resolvers.state.duration;

import static com.zuehlke.pgadmissions.domain.definitions.workflow.PrismState.PROJECT_DISABLED_COMPLETED;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Project;
import com.zuehlke.pgadmissions.services.ResourceService;

@Component
public class ProjectEndDateResolver implements StateDurationResolver<Project> {

    @Inject
    private ResourceService resourceService;

    @Override
    public LocalDate resolve(Project resource, Comment comment) {
        return comment.getTransitionState().getId() == PROJECT_DISABLED_COMPLETED ? null : resourceService.getResourceEndDate(resource.getProject());
    }

}
