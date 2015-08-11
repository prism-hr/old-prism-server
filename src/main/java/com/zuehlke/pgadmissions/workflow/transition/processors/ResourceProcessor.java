package com.zuehlke.pgadmissions.workflow.transition.processors;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;

public interface ResourceProcessor<T extends Resource<?>> {

    void process(T resource, Comment comment);

}
