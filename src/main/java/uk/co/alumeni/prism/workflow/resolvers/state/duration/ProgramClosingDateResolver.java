package uk.co.alumeni.prism.workflow.resolvers.state.duration;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Program;

@Component
public class ProgramClosingDateResolver implements StateDurationResolver<Program> {

    @Override
    public LocalDate resolve(Program resource, Comment comment) {
        return comment.isRestoreComment() ? null : resource.getAdvert().getClosingDate();
    }

}
