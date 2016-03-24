package uk.co.alumeni.prism.workflow.transition.processors;

import uk.co.alumeni.prism.domain.comment.Comment;
import uk.co.alumeni.prism.domain.resource.Resource;

public interface ResourceProcessor<T extends Resource> {

    void process(T resource, Comment comment);

}
