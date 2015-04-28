package com.zuehlke.pgadmissions.workflow.resourcer.processors;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resource;

public interface ResourceProcessor {

    public void process(Resource resource, Comment comment) throws Exception;
    
}
