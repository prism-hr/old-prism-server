package com.zuehlke.pgadmissions.workflow.transition.processors;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;

public interface ResourceProcessor {

    void process(Resource resource, Comment comment) throws Exception;

}
