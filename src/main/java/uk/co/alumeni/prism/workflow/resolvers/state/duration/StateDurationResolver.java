package uk.co.alumeni.prism.workflow.resolvers.state.duration;

import org.joda.time.LocalDate;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Resource;

public interface StateDurationResolver<T extends Resource> {

    LocalDate resolve(T resource, Comment comment);

}
