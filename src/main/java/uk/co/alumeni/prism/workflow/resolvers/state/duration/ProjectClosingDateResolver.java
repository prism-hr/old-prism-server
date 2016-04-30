package uk.co.alumeni.prism.workflow.resolvers.state.duration;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Project;

@Component
public class ProjectClosingDateResolver implements StateDurationResolver<Project> {

    @Override
    public LocalDate resolve(Project resource, Comment comment) {
        return comment.isRestoreComment() ? null : resource.getAdvert().getClosingDate();
    }

}
