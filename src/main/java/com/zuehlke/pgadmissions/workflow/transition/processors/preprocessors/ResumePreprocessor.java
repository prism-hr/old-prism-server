package com.zuehlke.pgadmissions.workflow.transition.processors.preprocessors;

import org.springframework.stereotype.Component;

import com.zuehlke.pgadmissions.domain.comment.Comment;
import com.zuehlke.pgadmissions.domain.resource.Resume;
import com.zuehlke.pgadmissions.workflow.transition.processors.ResourceProcessor;

@Component
public class ResumePreprocessor implements ResourceProcessor<Resume> {

    @Override
    public void process(Resume resource, Comment comment) {
        resource.setSubmittedTimestamp(comment.getCreatedTimestamp());
    }

}
